package com.monits.agilefant.service;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;

import java.util.List;

public interface BacklogService {

	/**
	 * Get All backlogs
	 *
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getAllBacklogs(Listener<List<Product>> listener, ErrorListener error);

	/**
	 * Get my backlogs
	 *
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getMyBacklogs(Listener<List<Project>> listener, ErrorListener error);
}
