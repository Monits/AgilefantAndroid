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
	private com.monits.agilefant.model.Task task;
	private TaskCallback<com.monits.agilefant.model.Task> callback;

	private boolean isSuccess;
	private com.monits.agilefant.model.Task fallbackTask;


	@Inject
	protected UpdateStateTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();

		fallbackTask = task.clone();
		task.setState(state, true);
	}

	@Override
	public com.monits.agilefant.model.Task call() throws Exception {
		return metricsService.taskChangeState(state, task.getId());
	}

	@Override
	protected void onSuccess(com.monits.agilefant.model.Task t)
			throws Exception {
		super.onSuccess(t);

		isSuccess = true;
		task.updateValues(t);
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
				callback.onSuccess(task);
			}
		} else {
			task.updateValues(fallbackTask);

			if (callback != null) {
				callback.onError();
			}
		}

	}

	public void configure(StateKey state, com.monits.agilefant.model.Task task, TaskCallback<com.monits.agilefant.model.Task> callback) {
		this.state = state;
		this.task = task;
		this.callback = callback;
	}
}
