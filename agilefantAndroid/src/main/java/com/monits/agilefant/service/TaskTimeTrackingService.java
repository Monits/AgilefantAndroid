package com.monits.agilefant.service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import roboguice.service.RoboService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.monits.agilefant.R;
import com.monits.agilefant.activity.SavingTaskTimeDialogActivity;
import com.monits.agilefant.model.NotificationHolder;
import com.monits.agilefant.model.Task;

public class TaskTimeTrackingService extends RoboService {

	public static final String ACTION_START_TRACKING = "com.monits.agilefant.intent.action.START_TRACKING";
	public static final String ACTION_PAUSE_TRACKING = "com.monits.agilefant.intent.action.PAUSE_TRACKING";
	public static final String ACTION_STOP_TRACKING  = "com.monits.agilefant.intent.action.STOP_TRACKING";
	public static final String ACTION_QUIT_TRACKING_TASK = "com.monits.agilefant.intent.action.QUIT_TRACKING_TASK";

	public static final String EXTRA_NOTIFICATION_ID = "com.monits.agilefant.intent.extra.NOTIFICATION_ID";
	public static final String EXTRA_TASK = "com.monits.agilefant.intent.extra.TASK";

	private final Map<Long, NotificationHolder> notificationMap = new ConcurrentHashMap<Long, NotificationHolder>();

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();

			final long notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, -1);
			if (notificationMap.containsKey(notificationId)) {
				final NotificationHolder notificationHolder = notificationMap.get(notificationId);

				if (action.equals(ACTION_START_TRACKING)) {
					startChronometer(notificationHolder);
				} else if (action.equals(ACTION_PAUSE_TRACKING)) {
					pauseChronometer(notificationHolder);
				} else if (action.equals(ACTION_QUIT_TRACKING_TASK)) {
					notificationMap.remove(notificationId);

					mNotificationManager.cancel(notificationHolder.getNotificationId());

					if (notificationMap.isEmpty()) {
						stopSelf();
					}
				} else if (action.equals(ACTION_STOP_TRACKING)) {

					if (notificationHolder.isChronometerRunning()) {
						pauseChronometer(notificationHolder);
					}

					collapseStatusBar();

					final Intent dialogActivityIntent = new Intent(context, SavingTaskTimeDialogActivity.class);
					dialogActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_MULTIPLE_TASK
							| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					dialogActivityIntent.putExtra(
							SavingTaskTimeDialogActivity.EXTRA_TASK, notificationHolder.getTrackedTask());
					dialogActivityIntent.putExtra(
							SavingTaskTimeDialogActivity.EXTRA_ELAPSED_MILLIS, Math.abs(notificationHolder.getElapsedTime()));

					startActivity(dialogActivityIntent);
				}
			}
		}
	};

	private NotificationManager mNotificationManager;

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_START_TRACKING);
		intentFilter.addAction(ACTION_STOP_TRACKING);
		intentFilter.addAction(ACTION_PAUSE_TRACKING);
		intentFilter.addAction(ACTION_QUIT_TRACKING_TASK);

		registerReceiver(broadcastReceiver, intentFilter);

		super.onCreate();
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		if (intent != null && intent.hasExtra(EXTRA_TASK)) {
			final Task taskToTrack = (Task) intent.getSerializableExtra(EXTRA_TASK);

			final long id = taskToTrack.getId();
			if (!notificationMap.containsKey(id)) {

				final NotificationHolder notificationHolder = new NotificationHolder(this, taskToTrack);
				notificationMap.put(id, notificationHolder);

				startChronometer(notificationHolder);

				final Intent startIntent = new Intent(ACTION_START_TRACKING);
				startIntent.putExtra(EXTRA_NOTIFICATION_ID, id);
				sendBroadcast(startIntent);
			} else {
				Toast.makeText(TaskTimeTrackingService.this, R.string.already_tracking_task_error, Toast.LENGTH_SHORT).show();
			}


		} else if (notificationMap.isEmpty()) {
			stopForeground(true);

			stopSelf();

			Log.e(getClass().getName(), "Failed to start tracking, no extra provided!");
		}

		return START_STICKY_COMPATIBILITY;
	}

	@Override
	public void onDestroy() {
		mNotificationManager.cancelAll();

		unregisterReceiver(broadcastReceiver);
	}

	/**
	 * Creates or updates current notification.
	 */
	@SuppressLint("NewApi")
	private void displayNotification(final NotificationHolder notificationHolder) {

		final Task trackedTask = notificationHolder.getTrackedTask();
		final long extraNotifId = trackedTask.getId();		// Make sure it's a long

		final int notifId = notificationHolder.getNotificationId();
		final boolean isChronometerRunning = notificationHolder.isChronometerRunning();

		final RemoteViews contentView =
				new RemoteViews(getPackageName(), R.layout.task_tracking_notification);

		contentView.setTextViewText(R.id.chronometer_description, trackedTask.getName());
		contentView.setChronometer(R.id.chronometer,
				notificationHolder.getChronometerBaseTime(),
				getString(R.string.chronometer_format),
				isChronometerRunning);

		final NotificationCompat.Builder mNotificationBuilder = notificationHolder.getNotificationBuilder()
			.setSmallIcon(R.drawable.ic_launcher)
			.setOnlyAlertOnce(true)
			.setDefaults(Notification.DEFAULT_LIGHTS)
			.setOngoing(true)
			.setContentTitle(getString(R.string.app_name))
			.setContentText(getString(R.string.notification_content_text, trackedTask.getName()));

		final PendingIntent changeStateIntent;
		if (isChronometerRunning) {
			final Intent pauseTrackingIntent = new Intent(ACTION_PAUSE_TRACKING);
			pauseTrackingIntent.putExtra(EXTRA_NOTIFICATION_ID, extraNotifId);
			changeStateIntent = PendingIntent.getBroadcast(
					TaskTimeTrackingService.this, notifId, pauseTrackingIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			final Intent resumeTrackingIntent = new Intent(ACTION_START_TRACKING);
			resumeTrackingIntent.putExtra(EXTRA_NOTIFICATION_ID, extraNotifId);
			changeStateIntent = PendingIntent.getBroadcast(
					TaskTimeTrackingService.this, notifId, resumeTrackingIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		}

		final Intent stopTrackingIntent = new Intent(ACTION_STOP_TRACKING);
		stopTrackingIntent.putExtra(EXTRA_NOTIFICATION_ID, extraNotifId);
		final PendingIntent stopIntent =
				PendingIntent.getBroadcast(
						TaskTimeTrackingService.this, notifId, stopTrackingIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		contentView.setOnClickPendingIntent(R.id.chronometer_status, changeStateIntent);
		contentView.setOnClickPendingIntent(R.id.chronometer_stop, stopIntent);

		final Notification notification;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			contentView.setImageViewResource(R.id.chronometer_status,
					isChronometerRunning ? R.drawable.ic_notification_pause : R.drawable.ic_notification_play);

			mNotificationBuilder.setContent(contentView);
			notification = mNotificationBuilder.build();
		} else {
			contentView.setTextViewCompoundDrawables(R.id.chronometer_status,
					isChronometerRunning ? R.drawable.ic_notification_pause : R.drawable.ic_notification_play,
							0,
							0,
							0);
			contentView.setTextViewText(R.id.chronometer_status,
					isChronometerRunning ? getString(R.string.notification_pause) : getString(R.string.notification_play));

			notification = mNotificationBuilder.build();
			notification.bigContentView = contentView;
		}

		if (isChronometerRunning) {
			startForeground(notifId, notification);
		} else {
			mNotificationManager.notify(notifId, notification);
		}
	}

	/**
	 * Resumes chronometer, updates elapsed millis and updates notification state.
	 */
	private void startChronometer(final NotificationHolder notificationHolder) {
		stopForeground(false);

		notificationHolder.resume();

		/*
		 * This multiple calls to displayNotification() were needed to avoid our notification to be overlapped.
		 */
		displayNotification(notificationHolder);

		for (final Entry<Long, NotificationHolder> entry : notificationMap.entrySet()) {
			if (entry.getKey().longValue() != notificationHolder.getTrackedTask().getId()) {
				pauseChronometer(entry.getValue());
			}
		}

		displayNotification(notificationHolder);
	}

	/**
	 * Stops the chronometer, stores the time when it was stopped and updates notification state.
	 */
	private void pauseChronometer(final NotificationHolder notificationHolder) {
		notificationHolder.pause();

		displayNotification(notificationHolder);

		/*
		 * This is required to update each notification chronometer's base.
		 *
		 * From Android Doc:
		 * Stop counting up. This does not affect the base as set from setBase(long), just the view display.
		 * This stops the messages to the handler, effectively releasing resources that
		 * would be held as the chronometer is running, via start().
		 */
		for (final NotificationHolder holder : notificationMap.values()) {
			holder.updateBase();
			displayNotification(holder);
		}
	}

	private void collapseStatusBar() {
		final Object sbservice = getSystemService("statusbar");

		try {
			final Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
				final Method collapse = statusbarManager.getMethod("collapse");
				collapse.invoke(sbservice);
			} else {
				final Method collapse2 = statusbarManager.getMethod("collapsePanels");
				collapse2.invoke(sbservice);
			}
		} catch (final Exception e) {
			Log.e(getClass().getName(), "Failed to close status bar", e);
		}
	}
}