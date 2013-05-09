package com.monits.agilefant.task;

import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.service.DailyWorkService;

public class GetDailyWorkTask extends RoboAsyncTask<DailyWork> {

	@Inject
	private DailyWorkService dailyWorkService;

	private TaskCallback<DailyWork> callback;

	private ProgressDialog progressDialog;

	@Inject
	protected GetDailyWorkTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();

		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getContext().getString(R.string.loading));
		progressDialog.show();
	}

	@Override
	public DailyWork call() throws Exception {
		return dailyWorkService.getDailyWork();
	}

	@Override
	protected void onSuccess(DailyWork t) throws Exception {
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

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();

		progressDialog.dismiss();
	}

	public void configure(TaskCallback<DailyWork> callback) {
		this.callback = callback;
	}
}
