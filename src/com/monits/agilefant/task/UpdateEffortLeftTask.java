/**
 *
 */
package com.monits.agilefant.task;

import roboguice.util.RoboAsyncTask;
import android.content.Context;

import com.google.inject.Inject;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.MetricsService;

/**
 * @author gmuniz
 *
 */
public class UpdateEffortLeftTask extends RoboAsyncTask<Task> {

	@Inject
	private MetricsService metricsService;

	private double effortLeft;
	private com.monits.agilefant.model.Task task;
	private TaskCallback<com.monits.agilefant.model.Task> callback;

	private boolean isSuccess;
	private com.monits.agilefant.model.Task fallbackTask;

	@Inject
	protected UpdateEffortLeftTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();
		fallbackTask = task.clone();
		task.setEffortLeft(Double.valueOf(effortLeft * 60).longValue());
	}

	@Override
	public com.monits.agilefant.model.Task call() throws Exception {
		return metricsService.changeEffortLeft(effortLeft, task.getId());
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		isSuccess = false;

	}

	@Override
	protected void onSuccess(com.monits.agilefant.model.Task t) throws Exception {
		super.onSuccess(t);

		task.updateValues(t);
		isSuccess = true;
	}

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();

		if (isSuccess) {
			if (callback != null) {
				callback.onSuccess(task);
			}
		} else {
			task.updateValues(fallbackTask);

			if (callback != null) {
				callback.onError();
			}
		}

	}

	public void configure(com.monits.agilefant.model.Task task, double effortLeft, TaskCallback<com.monits.agilefant.model.Task> callback) {
		this.effortLeft = effortLeft;
		this.task = task;
		this.callback = callback;
	}
}
