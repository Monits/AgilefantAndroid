package com.monits.agilefant.service;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.inject.Inject;
import com.monits.agilefant.model.Project;

public class ProjectServiceImpl implements ProjectService {

	@Inject
	private AgilefantService agilefantService;

	@Override
	public void getProjectData(final long projectId, final Listener<Project> listener, final ErrorListener error) {
		agilefantService.getProjectDetails(projectId, listener, error);
	}
}