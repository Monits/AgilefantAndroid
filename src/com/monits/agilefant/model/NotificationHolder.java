package com.monits.agilefant.model;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;


public class NotificationHolder {

	private final static AtomicInteger generatedId = new AtomicInteger(0);

	private final Task trackedTask;

	private final int notificationId;
	private boolean isChronometerRunning;
	private long elapsedTime;
	private long chronometerBaseTime;

	private final NotificationCompat.Builder notifBuilder;

	public NotificationHolder(final Context context, final Task trackedTask) {
		this.notificationId = generatedId.incrementAndGet();
		this.trackedTask = trackedTask;
		notifBuilder = new NotificationCompat.Builder(context);
	}

	public int getNotificationId() {
		return notificationId;
	}

	public Task getTrackedTask() {
		return trackedTask;
	}

	public boolean isChronometerRunning() {
		return isChronometerRunning;
	}

	public long getChronometerBaseTime() {
		return chronometerBaseTime;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public NotificationCompat.Builder getNotificationBuilder() {
		return notifBuilder;
	}

	public void resume() {
		if (!isChronometerRunning) {
			isChronometerRunning = true;
			updateBase();
		}
	}

	public void pause() {
		if (isChronometerRunning) {
			isChronometerRunning = false;
			elapsedTime = chronometerBaseTime - SystemClock.elapsedRealtime();
		}
	}

	public void updateBase() {
		chronometerBaseTime = SystemClock.elapsedRealtime() + elapsedTime;
	}
}
