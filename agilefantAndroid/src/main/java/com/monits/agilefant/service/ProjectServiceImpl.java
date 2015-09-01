package com.monits.agilefant.service;

import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;

import javax.inject.Inject;


public class ProjectServiceImpl implements ProjectService {

	private final AgilefantService agilefantService;

	/**
	 * @param agilefantService Injected via constructor by Dagger
	 */

	@Inject
	public ProjectServiceImpl(final AgilefantService agilefantService) {
		this.agilefantService = agilefantService;
	}

	@Override
	public void getProjectData(final long projectId, final Listener<Project> listener, final ErrorListener error) {
		agilefantService.getProjectDetails(projectId, listener, error);
	}

	@Override
	public void getProjectLeafStories(final long projectId,
			final Listener<List<Story>> listener, final ErrorListener error) {
		agilefantService.getProjectLeafStories(projectId, listener, error);
	}

	@Override
	public void updateProject(final Project project, final Listener<Project> listener, final ErrorListener error) {
		agilefantService.updateProject(project, listener, error);
	}
}