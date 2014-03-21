package com.monits.agilefant.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

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

public class TaskTimeTrackingService extends RoboService implements PropertyChangeListener {

	private static final int NOTIFICATION_ID = 1;

	public static final String ACTION_START_TRACKING = "com.monits.agilefant.intent.action.START_TRACKING";
	public static final String ACTION_PAUSE_TRACKING = "com.monits.agilefant.intent.action.PAUSE_TRACKING";
	public static final String ACTION_STOP_TRACKING  = "com.monits.agilefant.intent.action.STOP_TRACKING";

	public static final String EXTRA_TASK = "com.monits.agilefant.intent.extra.TASK";

	private Task trackedTask;

	private boolean isTracking;

	private NotificationHolder notificationHolder;

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (action.equals(ACTION_START_TRACKING)) {
				resumeChronometer(notificationHolder);
			} else if (action.equals(ACTION_PAUSE_TRACKING)) {
				pauseChronometer(notificationHolder);
			} else if (action.equals(ACTION_STOP_TRACKING)) {

				if (notificationHolder.isChronometerRunning()) {
					pauseChronometer(notificationHolder);

					displayNotification();
				}

				collapseStatusBar();

				final Intent dialogActivityIntent = new Intent(context, SavingTaskTimeDialogActivity.class);
				dialogActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_MULTIPLE_TASK
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				dialogActivityIntent.putExtra(
						SavingTaskTimeDialogActivity.EXTRA_TASK, trackedTask);
				dialogActivityIntent.putExtra(
						SavingTaskTimeDialogActivity.EXTRA_ELAPSED_MILLIS, Math.abs(notificationHolder.getElapsedTime()));

				startActivity(dialogActivityIntent);
			}
		}
	};

	private NotificationCompat.Builder mNotificationBuilder;

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_START_TRACKING);
		intentFilter.addAction(ACTION_STOP_TRACKING);
		intentFilter.addAction(ACTION_PAUSE_TRACKING);

		registerReceiver(broadcastReceiver, intentFilter);

		super.onCreate();
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		if (!isTracking) {
			if (intent != null && intent.hasExtra(EXTRA_TASK)) {
				isTracking = true;

				trackedTask = (Task) intent.getSerializableExtra(EXTRA_TASK);
				notificationHolder = new NotificationHolder(trackedTask);

				displayNotification();

				sendBroadcast(new Intent(ACTION_START_TRACKING));
			} else {
				isTracking = false;

				stopSelf();

				Log.e(getClass().getName(), "Failed to start tracking, no extra provided!");
			}
		} else {
			Toast.makeText(TaskTimeTrackingService.this, R.string.already_tracking_task_error, Toast.LENGTH_SHORT).show();
		}

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		final NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATION_ID);

		unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {

	}

	/**
	 * Creates or updates current notification.
	 */
	@SuppressLint("NewApi")
	private void displayNotification() {
		final RemoteViews contentView =
				new RemoteViews(getPackageName(), R.layout.task_tracking_notification);

		contentView.setTextViewText(R.id.chronometer_description, trackedTask.getName());
		contentView.setChronometer(R.id.chronometer,
				notificationHolder.getChronometerBaseTime(),
				getString(R.string.chronometer_format),
				notificationHolder.isChronometerRunning());

		mNotificationBuilder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setOnlyAlertOnce(true)
			.setDefaults(Notification.DEFAULT_LIGHTS)
			.setOngoing(true)
			.setContentTitle(getString(R.string.app_name))
			.setContentText(getString(R.string.notification_content_text, trackedTask.getName()));

		final PendingIntent changeStateIntent;
		if (notificationHolder.isChronometerRunning()) {
			changeStateIntent = PendingIntent.getBroadcast(
					TaskTimeTrackingService.this, 0, new Intent(ACTION_PAUSE_TRACKING), 0);
		} else {
			changeStateIntent = PendingIntent.getBroadcast(
					TaskTimeTrackingService.this, 0, new Intent(ACTION_START_TRACKING), 0);
		}

		final PendingIntent stopIntent =
				PendingIntent.getBroadcast(
						TaskTimeTrackingService.this,
						0,
						new Intent(ACTION_STOP_TRACKING),
						0);

		contentView.setOnClickPendingIntent(R.id.chronometer_status, changeStateIntent);
		contentView.setOnClickPendingIntent(R.id.chronometer_stop, stopIntent);

		final Notification notification;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			contentView.setImageViewResource(R.id.chronometer_status,
					notificationHolder.isChronometerRunning() ? R.drawable.ic_notification_pause : R.drawable.ic_notification_play);

			mNotificationBuilder.setContent(contentView);
			notification = mNotificationBuilder.build();
		} else {
			contentView.setTextViewCompoundDrawables(R.id.chronometer_status,
					notificationHolder.isChronometerRunning() ? R.drawable.ic_notification_pause : R.drawable.ic_notification_play,
							0,
							0,
							0);
			contentView.setTextViewText(R.id.chronometer_status,
					notificationHolder.isChronometerRunning() ? getString(R.string.notification_pause) : getString(R.string.notification_play));

			notification = mNotificationBuilder.build();
			notification.bigContentView = contentView;
		}

		startForeground(NOTIFICATION_ID, notification);
	}

	/**
	 * Resumes chronometer, updates elapsed millis and updates notification state.
	 */
	private void resumeChronometer(final NotificationHolder notificationHolder) {
		notificationHolder.resume();
		displayNotification();
	}

	/**
	 * Stops the chronometer, stores the time when it was stopped and updates notification state.
	 */
	private void pauseChronometer(final NotificationHolder notificationHolder) {
		notificationHolder.pause();
		displayNotification();
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