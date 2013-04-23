/**
 * 
 */
package com.monits.agilefant.task;

import java.util.Date;

import roboguice.util.RoboAsyncTask;
import android.content.Context;

import com.google.inject.Inject;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.service.MetricsService;

/**
 * @author gmuniz
 *
 */
public class UpdateSpentEffortTask extends RoboAsyncTask<Boolean> {

	@Inject
	private MetricsService metricsService;

	private Date date;
	private long minuteSpent;
	private String description;
	private long taskId;
	private long userId;
	private TaskCallback<Boolean> callback;

	@Inject
	protected UpdateSpentEffortTask(Context context) {
		super(context);
	}

	@Override
	public Boolean call() {
		try {
			metricsService.taskChangeSpentEffort(date, minuteSpent, description, taskId, userId);

			return true;
		} catch (Exception e) {
			onException(e);
		}

		return false;
	}

	@Override
	protected void onSuccess(Boolean t) throws Exception {
		super.onSuccess(t);
		callback.onSuccess(t);
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);
		callback.onError();
	}

	public void configure(Date date, long minutesSpent, String description, long taskId, long userId, TaskCallback<Boolean> callback) {
		this.callback = callback;
		this.date = date;
		this.minuteSpent = minutesSpent;
		this.description = description;
		this.taskId = taskId;
		this.userId = userId;
	}
}
