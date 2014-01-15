package com.monits.agilefant.task;

import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.ProjectService;

public class GetProjectData extends RoboAsyncTask<Project> {

	@Inject
	private ProjectService projectService;

	private ProgressDialog progressDialog;

	private long mProjectId;

	@Inject
	protected GetProjectData(final Context context) {
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
	public Project call() throws Exception {
		return projectService.getProjectData(mProjectId);
	}

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public void configure(final long projectId) {
		this.mProjectId = projectId;
	}
}
