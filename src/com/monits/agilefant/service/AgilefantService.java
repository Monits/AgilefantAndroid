package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;

public interface AgilefantService {
	String login(String userName, String password) throws RequestException;
}
