package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;

public interface UserService {

	public static final String DOMAIN_KEY = "DOMAIN_KEY";
	public static final String USER_NAME_KEY = "USER_NAME_KEY";

	boolean login(String domain, String userName, String password) throws RequestException;
}
