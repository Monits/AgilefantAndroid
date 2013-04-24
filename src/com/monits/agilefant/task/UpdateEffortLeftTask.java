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

	private long effortLeft;
	private long taskId;

	private TaskCallback<com.monits.agilefant.model.Task> callback;

	@Inject
	protected UpdateEffortLeftTask(Context context) {
		super(context);
	}

	@Override
	public com.monits.agilefant.model.Task call() throws Exception {
		return metricsService.changeEffortLeft(effortLeft, taskId);
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		if (callback != null) {
			callback.onError();
		}
	}

	@Override
	protected void onSuccess(com.monits.agilefant.model.Task t) throws Exception {
		super.onSuccess(t);

		if (callback != null) {
			callback.onSuccess(t);
		}
	}

	public void configure(long taskId, long effortLeft, TaskCallback<com.monits.agilefant.model.Task> callback) {
		this.effortLeft = effortLeft;
		this.taskId = taskId;
		this.callback = callback;
	}
}
