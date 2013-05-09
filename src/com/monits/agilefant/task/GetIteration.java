package com.monits.agilefant.task;

import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.service.IterationService;

public class GetIteration extends RoboAsyncTask<String>{

	public static final String PROJECTNAME = "PROJECTNAME";

	public static final String ITERATION = "ITERATION";

	@Inject
	private IterationService iterationService;

	private long id;

	private ProgressDialog progressDialog;

	private String projectName;

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getContext().getString(R.string.loading));
		progressDialog.show();
		super.onPreExecute();
	}

	@Inject
	public GetIteration(Context context) {
		super(context);
	}

	@Override
	public String call() {
		try {
			Iteration iteration = iterationService.getIteration(id);

			Intent intent = new Intent(this.context, IterationActivity.class);
			intent.putExtra(ITERATION, iteration);
			intent.putExtra(PROJECTNAME, projectName);
			this.context.startActivity(intent);
		} catch (RequestException e) {
			onException(e);
		}
		return null;
	}

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();
		progressDialog.dismiss();
	}

	public void configure(String projectName, long id) {
		this.projectName = projectName;
		this.id = id;
	}
}