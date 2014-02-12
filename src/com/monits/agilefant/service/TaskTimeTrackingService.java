package com.monits.agilefant.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import com.monits.agilefant.R;

public class TaskTimeTrackingService extends Service implements PropertyChangeListener {

	public static final String ACTION_START_TRACKING = "com.monits.agilefant.intent.action.START_TRACKING";
	public static final String ACTION_PAUSE_TRACKING = "com.monits.agilefant.intent.action.PAUSE_TRACKING";
	public static final String ACTION_STOP_TRACKING  = "com.monits.agilefant.intent.action.STOP_TRACKING";

	private boolean isTracking;

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (action.equals(ACTION_START_TRACKING)) {

			} else if (action.equals(ACTION_PAUSE_TRACKING)) {

			} else if (action.equals(ACTION_STOP_TRACKING)) {

			}
		}
	};

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
			isTracking = true;
			sendBroadcast(new Intent(ACTION_START_TRACKING));
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



}
