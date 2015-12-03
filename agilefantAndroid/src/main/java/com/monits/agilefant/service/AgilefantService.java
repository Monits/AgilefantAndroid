package com.monits.agilefant.service;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public interface AgilefantService {

	/**
	 * Sets the domain you are working
	 * @param domain The domain
	 */
	void setDomain(String domain);

	/**
	 * Login in Agilefant with domain entered
	 * @param userName The username
	 * @param password the password
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void login(String userName, String password, Listener<String> listener, ErrorListener error);

	/**
	 * @return The host where it is working
	 */
	String getHost();

	/**
	 * Adds a request to the queue
	 * @param request The request to add
	 */
	void addRequest(Request<?> request);
}