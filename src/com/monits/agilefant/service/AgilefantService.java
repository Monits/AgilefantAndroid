package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;

public interface AgilefantService {

	/**
	 * Sets the domain you are working
	 * @param domain
	 */
	void setDomain(String domain);

	/**
	 * Login in Agilefant with domain entered
	 * @param userName
	 * @param password
	 * @return If the user is logged
	 * @throws RequestException
	 */
	boolean login(String userName, String password) throws RequestException;

	/**
	 * @return All backlogs in JSON format
	 * @throws RequestException
	 */
	String getAllBacklogs() throws RequestException;

	/**
	 * @param Iteration id
	 * @return Details of iteration in JSON format
	 * @throws RequestException
	 */
	String getIteration(long id) throws RequestException;

	/**
	 * @return The host where it is working
	 */
	String getHost();
}
