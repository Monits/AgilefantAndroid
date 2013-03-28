package com.monits.agilefant.service;

import com.monits.agilefant.connector.HttpConnection;
import com.monits.agilefant.exception.RequestException;

public class AgilefantServiceImpl implements AgilefantService {

	private static final String LOGIN_URL = "http://agilefant.monits.com/j_spring_security_check";
	private static final String PASSWORD = "j_password";
	private static final String USERNAME = "j_username";

	@Override
	public String login(String userName, String password) throws RequestException {
		HttpConnection connection = new HttpConnection();
		connection.addParameter(USERNAME, userName);
		connection.addParameter(PASSWORD, password);
		return connection.executeGet(LOGIN_URL, true);
	}
}
