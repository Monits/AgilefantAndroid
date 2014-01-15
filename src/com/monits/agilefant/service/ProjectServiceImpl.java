package com.monits.agilefant.service;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Project;

public class ProjectServiceImpl implements ProjectService {

	@Inject
	private AgilefantService agilefantService;

	@Override
	public Project getProjectData(final long projectId) throws RequestException {
		return agilefantService.getProjectDetails(projectId);
	}

}
