package com.monits.agilefant.service;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.DailyWork;

public interface DailyWorkService {

	/**
	 * Retrieves the daily work.
	 *
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getDailyWork(Listener<DailyWork> listener, ErrorListener error);
}
