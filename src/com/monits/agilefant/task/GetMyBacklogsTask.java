/**
 * 
 */
package com.monits.agilefant.task;

import java.util.List;

import roboguice.util.RoboAsyncTask;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.inject.Inject;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.BacklogService;

/**
 * @author gmuniz
 *
 */
public class GetMyBacklogsTask extends RoboAsyncTask<List<Project>> {

	@Inject
	private BacklogService backlogService;

	private TaskCallback<List<Project>> callback;

	private View loadingView;

	@Inject
	protected GetMyBacklogsTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();

		loadingView.setVisibility(View.VISIBLE);
	}

	@Override
	public List<Project> call() throws Exception {
		return backlogService.getMyBacklogs();
	}

	@Override
	protected void onSuccess(List<Project> backlogs) throws Exception {
		super.onSuccess(backlogs);

		if (callback != null) {
			callback.onSuccess(backlogs);
		}
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		Log.e("GetMyBacklogsTask", "Failed to retrieve user backlogs", e);

		if (callback != null) {
			callback.onError();
		}
	}

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();

		loadingView.setVisibility(View.GONE);
	}

	/**
	 * Initializes task parameters.
	 * 
	 * @param callback the listener to be called as callback.
	 */
	public void configure(View loadingView, TaskCallback<List<Project>> callback) {
		this.loadingView = loadingView;
		this.callback = callback;
	}
}