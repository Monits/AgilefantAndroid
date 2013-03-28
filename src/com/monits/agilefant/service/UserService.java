package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;

public interface UserService {
	String login(String userName, String password) throws RequestException;
}
