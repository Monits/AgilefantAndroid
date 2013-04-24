/**
 * 
 */
package com.monits.agilefant.task;

import roboguice.util.RoboAsyncTask;
import android.content.Context;

import com.google.inject.Inject;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.MetricsService;

/**
 * @author gmuniz
 *
 */
public class UpdateStateTask extends RoboAsyncTask<Task> {

	@Inject
	private MetricsService metricsService;

	private StateKey state;
	private long taskId;
	private TaskCallback<com.monits.agilefant.model.Task> callback;

	@Inject
	protected UpdateStateTask(Context context) {
		super(context);
	}

	@Override
	public com.monits.agilefant.model.Task call() throws Exception {
		return metricsService.taskChangeState(state, taskId);
	}

	@Override
	protected void onSuccess(com.monits.agilefant.model.Task t)
			throws Exception {
		super.onSuccess(t);

		if (callback != null) {
			callback.onSuccess(t);
		}
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		if (callback != null) {
			callback.onError();
		}
	}

	public void configure(StateKey state, long taskId, TaskCallback<com.monits.agilefant.model.Task> callback) {
		this.state = state;
		this.taskId = taskId;
		this.callback = callback;
	}
}
