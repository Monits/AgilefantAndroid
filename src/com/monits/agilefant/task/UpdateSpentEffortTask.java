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
	private long userId;
	private TaskCallback<Boolean> callback;
	private com.monits.agilefant.model.Task task;

	private boolean isSuccess;
	private com.monits.agilefant.model.Task mFallbackTask;

	@Inject
	protected UpdateSpentEffortTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();
		mFallbackTask = task.clone();
		task.setEffortSpent(task.getEffortSpent() + minuteSpent);
	}

	@Override
	public Boolean call() {
		try {
			metricsService.taskChangeSpentEffort(date, minuteSpent, description, task.getId(), userId);

			return true;

		} catch (Exception e) {
			onException(e);
		}

		return false;
	}

	@Override
	protected void onSuccess(Boolean t) throws Exception {
		super.onSuccess(t);
		isSuccess = t;
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);
		isSuccess = false;
	}

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();

		if (isSuccess) {
			if (callback != null) {
				callback.onSuccess(true);
			}
		} else {
			task.updateValues(mFallbackTask);

			if (callback != null) {
				callback.onError();
			}
		}
	}

	public void configure(Date date, long minutesSpent, String description, com.monits.agilefant.model.Task task, long userId, TaskCallback<Boolean> callback) {
		this.callback = callback;
		this.date = date;
		this.minuteSpent = minutesSpent;
		this.description = description;
		this.task = task;
		this.userId = userId;
	}
}
