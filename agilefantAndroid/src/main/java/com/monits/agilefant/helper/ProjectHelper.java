package com.monits.agilefant.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.ProjectService;

import javax.inject.Inject;

/**
 * Created by lgnanni on 22/10/15.
 */
public class ProjectHelper {

	private final Context context;
	private final Backlog backlog;


	@Inject
   /* default */ ProjectService projectService;

	/**
	 * Project Helper
	 * @param context Context
	 * @param backlog Backlog
	 */
	public ProjectHelper(final Context context, final Backlog backlog) {
		this.context = context;
		this.backlog = backlog;
		AgilefantApplication.getObjectGraph().inject(this);
	}

	/**
	 * Makes service call and open ProjectActivity on response
	 */
	public void openProject() {
		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(context.getString(R.string.loading));
		progressDialog.show();

		projectService.getProjectData(
				backlog.getId(),
				new Response.Listener<Project>() {
					@Override
					public void onResponse(final Project project) {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						context.startActivity(ProjectActivity.getIntent(context, backlog, project));
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError arg0) {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						Toast.makeText(context, R.string.failed_to_retrieve_project_details,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public String toString() {
		return "ProjectHelper: [context: " + context + ", backlog: " + backlog + ']';
	}
}
