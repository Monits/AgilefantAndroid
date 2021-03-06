package com.monits.agilefant.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.monits.agilefant.R;
import com.monits.agilefant.activity.SavingTaskTimeDialogActivity;
import com.monits.agilefant.model.NotificationHolder;
import com.monits.agilefant.model.Task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class TaskTimeTrackingService extends Service {

	public static final String ACTION_START_TRACKING = "com.monits.agilefant.intent.action.START_TRACKING";
	public static final String ACTION_PAUSE_TRACKING = "com.monits.agilefant.intent.action.PAUSE_TRACKING";
	public static final String ACTION_STOP_TRACKING = "com.monits.agilefant.intent.action.STOP_TRACKING";
	public static final String ACTION_QUIT_TRACKING_TASK = "com.monits.agilefant.intent.action.QUIT_TRACKING_TASK";

	public static final String EXTRA_NOTIFICATION_ID = "com.monits.agilefant.intent.extra.NOTIFICATION_ID";
	public static final String EXTRA_TASK = "com.monits.agilefant.intent.extra.TASK";

	private final Map<Long, NotificationHolder> notificationMap = new ConcurrentHashMap<>();

	private final BroadcastReceiver broadcastReceiver = new TaskTimeTrackingServiceBroadcastReceiver();

	private NotificationManager mNotificationManager;

	/**
	* This factory method returns an intent of this class with it's necessary extra values
	*
	* @param context A Context of the application package implementing this class
	* @param task A Task Object for being sent to the returned intent
	* @return An intent that contains sent Task as extra values
	*/
	public static Intent getIntent(@NonNull final Context context, @NonNull final Task task) {
		final Intent intent = new Intent(context, TaskTimeTrackingService.class);
		intent.putExtra(EXTRA_TASK, task);
		return intent;
	}

	/**
	* This factory method returns an intent of this class with it's necessary extra values
	*
	* @param task A Task Object for being sent to the returned intent
	* @return An intent that contains sent data as extra values
	*/
	public static Intent getIntent(@NonNull final Task task) {
		final Intent intent = new Intent(ACTION_QUIT_TRACKING_TASK);
		intent.putExtra(EXTRA_NOTIFICATION_ID, task.getId());
		return intent;
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_START_TRACKING);
		intentFilter.addAction(ACTION_STOP_TRACKING);
		intentFilter.addAction(ACTION_PAUSE_TRACKING);
		intentFilter.addAction(ACTION_QUIT_TRACKING_TASK);

		registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		if (intent != null && intent.hasExtra(EXTRA_TASK)) {
			final Task taskToTrack = (Task) intent.getSerializableExtra(EXTRA_TASK);

			final long id = taskToTrack.getId();
			if (notificationMap.containsKey(id)) {
				Toast.makeText(
					this, R.string.already_tracking_task_error, Toast.LENGTH_SHORT).show();
			} else {
				final NotificationHolder notificationHolder = new NotificationHolder(this, taskToTrack);
				notificationMap.put(id, notificationHolder);

				startChronometer(notificationHolder);

				final Intent startIntent = new Intent(ACTION_START_TRACKING);
				startIntent.putExtra(EXTRA_NOTIFICATION_ID, id);
				sendBroadcast(startIntent);
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
		super.onDestroy();
	}

	/**
	 * Creates or updates current notification.
	 */
	@SuppressLint("NewApi")
	private void displayNotification(final NotificationHolder notificationHolder) {
		final Task trackedTask = notificationHolder.getTrackedTask();
		final long extraNotifId = trackedTask.getId(); // Make sure it's a long
		final int notifId = notificationHolder.getNotificationId();
		final boolean isChronometerRunning = notificationHolder.isChronometerRunning();
		final RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.task_tracking_notification);

		contentView.setTextViewText(R.id.chronometer_description, trackedTask.getName());
		contentView.setChronometer(R.id.trackChronometer, notificationHolder.getChronometerBaseTime(),
				getString(R.string.chronometer_format), isChronometerRunning);

		final NotificationCompat.Builder mNotificationBuilder = notificationHolder.getNotificationBuilder()
			.setSmallIcon(R.drawable.ic_small_icon)
			.setOnlyAlertOnce(true)
			.setDefaults(Notification.DEFAULT_LIGHTS)
			.setOngoing(true)
			.setContentTitle(getString(R.string.app_name))
			.setContentText(getString(R.string.notification_content_text, trackedTask.getName()));

		final PendingIntent changeStateIntent;
		if (isChronometerRunning) {
			final Intent pauseTrackingIntent = new Intent(ACTION_PAUSE_TRACKING);
			pauseTrackingIntent.putExtra(EXTRA_NOTIFICATION_ID, extraNotifId);
			changeStateIntent = PendingIntent.getBroadcast(this, notifId, pauseTrackingIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			final Intent resumeTrackingIntent = new Intent(ACTION_START_TRACKING);
			resumeTrackingIntent.putExtra(EXTRA_NOTIFICATION_ID, extraNotifId);
			changeStateIntent = PendingIntent.getBroadcast(this, notifId, resumeTrackingIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		}

		final Intent stopTrackingIntent = new Intent(ACTION_STOP_TRACKING);
		stopTrackingIntent.putExtra(EXTRA_NOTIFICATION_ID, extraNotifId);
		final PendingIntent stopIntent = PendingIntent.getBroadcast(this, notifId,
				stopTrackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		final Intent quitTrackingIntent = new Intent(ACTION_QUIT_TRACKING_TASK);
		quitTrackingIntent.putExtra(EXTRA_NOTIFICATION_ID, extraNotifId);
		final PendingIntent quitIntent = PendingIntent.getBroadcast(this, notifId, quitTrackingIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		contentView.setOnClickPendingIntent(R.id.chronometer_status, changeStateIntent);
		contentView.setOnClickPendingIntent(R.id.chronometer_stop, stopIntent);
		contentView.setOnClickPendingIntent(R.id.chronometer_close, quitIntent);


		final Notification notification;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			contentView.setImageViewResource(R.id.chronometer_status,
					isChronometerRunning ? R.drawable.ic_notification_pause : R.drawable.ic_notification_play);

			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) { //Set content description in 15
				contentView.setContentDescription(R.id.chronometer_status, isChronometerRunning
						? getString(R.string.notification_pause) : getString(R.string.notification_play));
			}

			mNotificationBuilder.setContent(contentView);
			notification = mNotificationBuilder.build();
		} else {
			contentView.setTextViewCompoundDrawables(R.id.chronometer_status,
					isChronometerRunning ? R.drawable.ic_notification_pause : R.drawable.ic_notification_play, 0, 0, 0);
			contentView.setContentDescription(R.id.chronometer_status,
					isChronometerRunning ? getString(R.string.notification_pause)
							: getString(R.string.notification_play));

			contentView.setTextViewText(R.id.chronometer_status,
				isChronometerRunning ? getString(R.string.notification_pause)
						: getString(R.string.notification_play));

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


	@SuppressWarnings("ResourceType")
	private void collapseStatusBar() {
		final Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		getApplicationContext().sendBroadcast(it);
	}

	private class TaskTimeTrackingServiceBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();

			final long notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, -1);
			if (notificationMap.containsKey(notificationId)) {
				final NotificationHolder notificationHolder = notificationMap.get(notificationId);

				if (ACTION_START_TRACKING.equals(action)) {
					startChronometer(notificationHolder);
				} else if (ACTION_PAUSE_TRACKING.equals(action)) {
					pauseChronometer(notificationHolder);
				} else if (ACTION_QUIT_TRACKING_TASK.equals(action)) {
					notificationMap.remove(notificationId);

					mNotificationManager.cancel(notificationHolder.getNotificationId());

					if (notificationMap.isEmpty()) {
						stopSelf();
					}
				} else if (ACTION_STOP_TRACKING.equals(action)) {

					if (notificationHolder.isChronometerRunning()) {
						pauseChronometer(notificationHolder);
					}

					collapseStatusBar();

					startActivity(SavingTaskTimeDialogActivity.getIntent(context, notificationHolder.getTrackedTask(),
									Math.abs(notificationHolder.getElapsedTime())));
				}
			}
		}
	}
}
