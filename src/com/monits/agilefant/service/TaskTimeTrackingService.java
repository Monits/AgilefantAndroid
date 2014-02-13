package com.monits.agilefant.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.monits.agilefant.R;
import com.monits.agilefant.model.Task;

public class TaskTimeTrackingService extends Service implements PropertyChangeListener {

	private static final int NOTIFICATION_ID = 1;

	public static final String ACTION_START_TRACKING = "com.monits.agilefant.intent.action.START_TRACKING";
	public static final String ACTION_PAUSE_TRACKING = "com.monits.agilefant.intent.action.PAUSE_TRACKING";
	public static final String ACTION_STOP_TRACKING  = "com.monits.agilefant.intent.action.STOP_TRACKING";

	public static final String EXTRA_TASK = "com.monits.agilefant.intent.extra.TASK";

	private Task trackedTask;

	private boolean isTracking;
	private boolean isChronometerRunning;

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (action.equals(ACTION_START_TRACKING)) {

				displayNotification();
			} else if (action.equals(ACTION_PAUSE_TRACKING)) {

				displayNotification();
			} else if (action.equals(ACTION_STOP_TRACKING)) {

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

				trackedTask = intent.getParcelableExtra(EXTRA_TASK);

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
				SystemClock.elapsedRealtime(), // TODO: save this time on pause.
				getString(R.string.chronometer_format),
				isChronometerRunning = !isChronometerRunning); // TODO: Change this according to current state (play / pause)

		mNotificationBuilder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setOnlyAlertOnce(true)
			.setDefaults(Notification.DEFAULT_ALL)
			.setOngoing(true)
			.setContentTitle(getString(R.string.app_name))
			.setContentText(getString(R.string.notification_content_text, trackedTask.getName()));

		final PendingIntent changeStateIntent =
				PendingIntent.getBroadcast(
						TaskTimeTrackingService.this,
						0,
						new Intent(ACTION_START_TRACKING),
						0);

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
			mNotificationBuilder.setContent(contentView);
			notification = mNotificationBuilder.build();
		} else {
			notification = mNotificationBuilder.build();
			notification.bigContentView = contentView;
		}

		startForeground(NOTIFICATION_ID, notification);
	}
}