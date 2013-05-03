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
import com.monits.agilefant.model.Product;
import com.monits.agilefant.service.BacklogService;

/**
 * @author gmuniz
 *
 */
public class GetBacklogsTask extends RoboAsyncTask<List<Product>> {

	@Inject
	private BacklogService backlogService;

	private TaskCallback<List<Product>> callback;

	private View loadingView;

	@Inject
	protected GetBacklogsTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();

		loadingView.setVisibility(View.VISIBLE);
	}

	@Override
	public List<Product> call() throws Exception {
		return backlogService.getAllBacklogs();
	}

	@Override
	protected void onSuccess(List<Product> backlogs) throws Exception {
		super.onSuccess(backlogs);

		if (callback != null) {
			callback.onSuccess(backlogs);
		}
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		Log.e("GetBacklogsTask", "Failed to retrieve backlogs", e);

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
	public void configure(View loadingView, TaskCallback<List<Product>> callback) {
		this.loadingView = loadingView;
		this.callback = callback;
	}
}