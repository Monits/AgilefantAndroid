package com.monits.agilefant.service;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.parser.ProjectParser;

public class ProjectServiceImpl implements ProjectService {

	@Inject
	private ProjectParser projectParser;

	@Inject
	private AgilefantService agilefantService;

	@Override
	public Project getProjectData(final long projectId) throws RequestException {
		return projectParser.parseProject(
				agilefantService.getProjectDetails(projectId));
	}

}
