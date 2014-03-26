package com.monits.agilefant.model;

import java.util.concurrent.atomic.AtomicInteger;

import android.os.SystemClock;


public class NotificationHolder {

	private final static AtomicInteger generatedId = new AtomicInteger(0);

	private final Task trackedTask;

	private final int notificationId;
	private boolean isChronometerRunning;
	private long elapsedTime;
	private long chronometerBaseTime;

	public NotificationHolder(final Task trackedTask) {
		this.notificationId = generatedId.incrementAndGet();
		this.trackedTask = trackedTask;
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

	public void resume() {
		if (!isChronometerRunning) {
			isChronometerRunning = true;
			chronometerBaseTime = SystemClock.elapsedRealtime() + elapsedTime;
		}
	}

	public void pause() {
		if (isChronometerRunning) {
			isChronometerRunning = false;
			elapsedTime = chronometerBaseTime - SystemClock.elapsedRealtime();
		}
	}

}
