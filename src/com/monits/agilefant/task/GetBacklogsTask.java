/**
 * 
 */
package com.monits.agilefant.task;

import java.util.List;

import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;
import com.monits.agilefant.R;
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

	private ProgressDialog progressDialog;

	private TaskCallback<List<Product>> callback;

	@Inject
	protected GetBacklogsTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();

		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getContext().getString(R.string.loading));
		progressDialog.show();
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

		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	/**
	 * Initializes task parameters.
	 * 
	 * @param callback the listener to be called as callback.
	 */
	public void configure(TaskCallback<List<Product>> callback) {
		this.callback = callback;
	}
}