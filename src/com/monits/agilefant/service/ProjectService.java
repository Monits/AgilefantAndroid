package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Project;

public interface ProjectService {

	/**
	 * Retrieves the details of the project.
	 *
	 * @param projectId the id of the project.
	 * @return a {@link Project} object.
	 * @throws RequestException
	 */
	Project getProjectData(long projectId) throws RequestException;
}
