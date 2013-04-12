package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;

public interface AgilefantService {
	void setDomain(String domain);
	boolean login(String userName, String password) throws RequestException;
	String getAllBacklogs() throws RequestException;
	String getIteration(long id) throws RequestException;
	String getHost();
}
