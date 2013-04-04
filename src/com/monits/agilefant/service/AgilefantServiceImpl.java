package com.monits.agilefant.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.monits.agilefant.connector.HttpConnection;
import com.monits.agilefant.exception.RequestException;

public class AgilefantServiceImpl implements AgilefantService {

	@Inject @Named("HOST")
	private String HOST;

	private static final String GET_ALL_BACKLOGS_URL = "/ajax/menuData.action";
	private static final String LOGIN_URL = "/j_spring_security_check";
	private static final String PASSWORD = "j_password";
	private static final String USERNAME = "j_username";

	private static final String LOGIN_OK = "/index.jsp";

	private static final String GET_ITERATION = "/ajax/iterationData.action";
	private static final String ITERATION_ID = "iterationId";

	@Override
	public boolean login(String userName, String password) throws RequestException {
		HttpConnection connection = new HttpConnection();
		connection.addParameter(USERNAME, userName);
		connection.addParameter(PASSWORD, password);
		String response = connection.executePost(HOST + LOGIN_URL);

		if (response.split(";")[0].equals(HOST + LOGIN_OK)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getAllBacklogs() throws RequestException {
		HttpConnection connection = new HttpConnection();
		return connection.executeGet(HOST + GET_ALL_BACKLOGS_URL);
	}

	@Override
	public String getIteration(long id) throws RequestException {
		HttpConnection connection = new HttpConnection();
		connection.addParameter(ITERATION_ID, String.valueOf(id));
		return connection.executeGet(HOST + GET_ITERATION);
	}
}
