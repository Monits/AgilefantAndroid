package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;

public interface AgilefantService {
	boolean login(String userName, String password) throws RequestException;
	String getAllBacklogs() throws RequestException;
}
