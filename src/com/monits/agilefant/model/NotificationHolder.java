package com.monits.agilefant.model;

import android.os.SystemClock;


public class NotificationHolder {

	private final Task trackedTask;

	private boolean isChronometerRunning;
	private long elapsedTime;
	private long chronometerBaseTime;

	public NotificationHolder(final Task trackedTask) {
		this.trackedTask = trackedTask;
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
