package com.monits.agilefant.service;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.Project;

public interface ProjectService {

	/**
	 * Retrieves the details of the project.
	 *
	 * @param projectId the id of the project.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getProjectData(long projectId, Listener<Project> listener, ErrorListener error);
}
