package com.monits.agilefant.service;

import com.monits.agilefant.connector.HttpConnection;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.StateKey;

public class AgilefantServiceImpl implements AgilefantService {

	private static final String TASK_ORIGINAL_ESTIMATE = "task.originalEstimate";
	private static final String TASK_EFFORT_LEFT = "task.effortLeft";
	private static final String STORE_TASK_ACTION = "/ajax/storeTask.action";
	private static final String TASK_ID = "taskId";
	private static final String TASK_STATE = "task.state";

	private static final String LOG_TASK_EFFORT_ACTION = "/ajax/logTaskEffort.action";
	private static final String USER_IDS = "userIds";
	private static final String PARENT_OBJECT_ID = "parentObjectId";
	private static final String HOUR_ENTRY_DESCRIPTION = "hourEntry.description";
	private static final String HOUR_ENTRY_MINUTES_SPENT = "hourEntry.minutesSpent";
	private static final String HOUR_ENTRY_DATE = "hourEntry.date";

	private static final String HTTP_REXEG = "^https?://.*$";
	private static final String HTTP = "http://";

	private String host;

	private static final String GET_ALL_BACKLOGS_URL = "/ajax/menuData.action";
	private static final String LOGIN_URL = "/j_spring_security_check";
	private static final String PASSWORD = "j_password";
	private static final String USERNAME = "j_username";

	private static final String LOGIN_OK = "/index.jsp";

	private static final String GET_ITERATION = "/ajax/iterationData.action";
	private static final String ITERATION_ID = "iterationId";

	private static final String RETRIEVE_USER_ACTION = "/ajax/retrieveUser.action";
	private static final String USER_ID = "userId";

	@Override
	public boolean login(String userName, String password) throws RequestException {
		HttpConnection connection = new HttpConnection();
		connection.addParameter(USERNAME, userName);
		connection.addParameter(PASSWORD, password);
		String response = connection.executePost(host + LOGIN_URL);

		if (response.split(";")[0].equals(host + LOGIN_OK)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getAllBacklogs() throws RequestException {
		HttpConnection connection = new HttpConnection();
		return connection.executeGet(host + GET_ALL_BACKLOGS_URL);
	}

	@Override
	public String getIteration(long id) throws RequestException {
		HttpConnection connection = new HttpConnection();
		connection.addParameter(ITERATION_ID, String.valueOf(id));
		return connection.executeGet(host + GET_ITERATION);
	}

	@Override
	public void setDomain(String domain) {
		if (domain.matches(HTTP_REXEG)) {
			this.host = domain;
		} else {
			this.host = HTTP + domain;
		}
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void taskChangeSpentEffort(long date, long minutesSpent,
			String description, long taskId, long userId) throws RequestException {
		HttpConnection connection = new HttpConnection();
		connection.addParameter(HOUR_ENTRY_DATE, String.valueOf(date));
		connection.addParameter(HOUR_ENTRY_MINUTES_SPENT, String.valueOf(minutesSpent));
		connection.addParameter(HOUR_ENTRY_DESCRIPTION, description);
		connection.addParameter(PARENT_OBJECT_ID, String.valueOf(taskId));
		connection.addParameter(USER_IDS, String.valueOf(userId));

		connection.executePost(host + LOG_TASK_EFFORT_ACTION);
	}

	@Override
	public String taskChangeState(StateKey state, long taskId) throws RequestException {
		HttpConnection connection = new HttpConnection();
		connection.addParameter(TASK_STATE, state.getState());
		connection.addParameter(TASK_ID, String.valueOf(taskId));

		return connection.executePost(host + STORE_TASK_ACTION);
	}

	@Override
	public String changeEffortLeft(double effortLeft, long taskId) throws RequestException{
		HttpConnection connection = new HttpConnection();
		connection.addParameter(TASK_EFFORT_LEFT, String.valueOf(effortLeft));
		connection.addParameter(TASK_ID, String.valueOf(taskId));

		return connection.executePost(host + STORE_TASK_ACTION);
	}

	@Override
	public String changeOriginalEstimate(int origalEstimate, long taskId) throws RequestException {
		HttpConnection connection = new HttpConnection();
		connection.addParameter(TASK_ORIGINAL_ESTIMATE, String.valueOf(origalEstimate));
		connection.addParameter(TASK_ID, String.valueOf(taskId));

		return connection.executePost(host + STORE_TASK_ACTION);
	}

	@Override
	public String retrieveUser(Long id) throws RequestException {
		HttpConnection connection = new HttpConnection();

		if (id != null) {
			connection.addParameter(USER_ID, String.valueOf(id));
		}

		return connection.executeGet(host + RETRIEVE_USER_ACTION);
	}
}