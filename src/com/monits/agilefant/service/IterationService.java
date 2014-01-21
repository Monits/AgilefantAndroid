package com.monits.agilefant.service;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.Iteration;

public interface IterationService {

	/**
	 * Get Iteration details
	 *
	 * @param id Iteration id
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getIteration(long id, Listener<Iteration> listener, ErrorListener error);
}
