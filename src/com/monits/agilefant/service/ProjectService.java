package com.monits.agilefant.service;

import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;

public interface ProjectService {

	/**
	 * Retrieves the details of the project.
	 *
	 * @param projectId the id of the project.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void getProjectData(long projectId, Listener<Project> listener, ErrorListener error);

	/**
	 * Retrieves the stories backlog of the project.
	 *
	 * @param projectId the id of the project.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void getProjectLeafStories(long projectId, Listener<List<Story>> listener, ErrorListener error);
}
