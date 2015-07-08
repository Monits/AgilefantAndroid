package com.monits.agilefant.model;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;


public class NotificationHolder {

	private final static AtomicInteger GENERATED_ID = new AtomicInteger(0);

	private final Task trackedTask;

	private final int notificationId;
	private boolean isChronometerRunning;
	private long elapsedTime;
	private long chronometerBaseTime;

	private final NotificationCompat.Builder notifBuilder;

	/**
	 * Constructor
	 * @param context The context
	 * @param trackedTask The tracked task.
	 */
	public NotificationHolder(final Context context, final Task trackedTask) {
		this.notificationId = GENERATED_ID.incrementAndGet();
		this.trackedTask = trackedTask;
		notifBuilder = new NotificationCompat.Builder(context);
	}

	/**
	 * @return The notification id
	 */
	public int getNotificationId() {
		return notificationId;
	}

	/**
	 * @return The tracked task
	 */
	public Task getTrackedTask() {
		return trackedTask;
	}

	/**
	 * @return true if the chronometer is running
	 */
	public boolean isChronometerRunning() {
		return isChronometerRunning;
	}

	/**
	 * @return the chronometer base time
	 */
	public long getChronometerBaseTime() {
		return chronometerBaseTime;
	}

	/**
	 * @return the chronometer base time
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * @return the notification builder
	 */
	public NotificationCompat.Builder getNotificationBuilder() {
		return notifBuilder;
	}

	/**
	 * Set the chronometer to active
	 */
	public void resume() {
		if (!isChronometerRunning) {
			isChronometerRunning = true;
			updateBase();
		}
	}

	/**
	 * Set the chronometer to paused
	 */
	public void pause() {
		if (isChronometerRunning) {
			isChronometerRunning = false;
			elapsedTime = chronometerBaseTime - SystemClock.elapsedRealtime();
		}
	}

	/**
	 * Update the chronometer base time
	 */
	public void updateBase() {
		chronometerBaseTime = SystemClock.elapsedRealtime() + elapsedTime;
	}
}
