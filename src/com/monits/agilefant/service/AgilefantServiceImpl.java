package com.monits.agilefant.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.monits.agilefant.connector.HttpConnection;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;

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

	private static final String GET_MY_BACKLOGS_URL = "/ajax/myAssignmentsMenuData.action";
	private static final String GET_ALL_BACKLOGS_URL = "/ajax/menuData.action";
	private static final String LOGIN_URL = "/j_spring_security_check";
	private static final String PASSWORD = "j_password";
	private static final String USERNAME = "j_username";

	private static final String LOGIN_OK = "/index.jsp";

	private static final String GET_ITERATION = "/ajax/iterationData.action";
	private static final String ITERATION_ID = "iterationId";

	private static final String RETRIEVE_USER_ACTION = "/ajax/retrieveUser.action";
	private static final String USER_ID = "userId";

	private static final String DAILY_WORK_ACTION = "/ajax/dailyWorkData.action";

	private static final String STORE_STORY_ACTION = "/ajax/storeStory.action";
	private static final String STORY_ID = "storyId";
	private static final String STORY_STATE = "story.state";
	private static final String TASKS_TO_DONE = "tasksToDone";
	private static final String BACKLOG_ID = "backlogId";

	private static final String PROJECT_DATA = "/ajax/projectData.action";
	private static final String PROJECT_ID = "projectId";

	@Inject
	private Gson gson;

	@Override
	public boolean login(final String userName, final String password) throws RequestException {
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(USERNAME, userName);
		connection.addParameter(PASSWORD, password);
		final String response = connection.executePost(host + LOGIN_URL);

		if (response.split(";")[0].equals(host + LOGIN_OK)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Product> getAllBacklogs() throws RequestException {
		final HttpConnection connection = new HttpConnection();

		final Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
		return gson.fromJson(connection.executeGet(host + GET_ALL_BACKLOGS_URL), listType);
	}

	@Override
	public Iteration getIteration(final long id) throws RequestException {
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(ITERATION_ID, String.valueOf(id));

		return gson.fromJson(
				connection.executeGet(host + GET_ITERATION), Iteration.class);
	}

	@Override
	public void setDomain(final String domain) {
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
	public void taskChangeSpentEffort(final long date, final long minutesSpent,
			final String description, final long taskId, final long userId) throws RequestException {
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(HOUR_ENTRY_DATE, String.valueOf(date));
		connection.addParameter(HOUR_ENTRY_MINUTES_SPENT, String.valueOf(minutesSpent));
		connection.addParameter(HOUR_ENTRY_DESCRIPTION, description);
		connection.addParameter(PARENT_OBJECT_ID, String.valueOf(taskId));
		connection.addParameter(USER_IDS, String.valueOf(userId));

		connection.executePost(host + LOG_TASK_EFFORT_ACTION);
	}

	@Override
	public Task taskChangeState(final StateKey state, final long taskId) throws RequestException {
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(TASK_STATE, state.name());
		connection.addParameter(TASK_ID, String.valueOf(taskId));

		return gson.fromJson(
				connection.executePost(host + STORE_TASK_ACTION), Task.class);
	}

	@Override
	public Task changeEffortLeft(final double effortLeft, final long taskId) throws RequestException{
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(TASK_EFFORT_LEFT, String.valueOf(effortLeft));
		connection.addParameter(TASK_ID, String.valueOf(taskId));

		return gson.fromJson(connection.executePost(host + STORE_TASK_ACTION), Task.class);
	}

	@Override
	public Task changeOriginalEstimate(final int origalEstimate, final long taskId) throws RequestException {
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(TASK_ORIGINAL_ESTIMATE, String.valueOf(origalEstimate));
		connection.addParameter(TASK_ID, String.valueOf(taskId));

		return gson.fromJson(connection.executePost(host + STORE_TASK_ACTION), Task.class);
	}

	@Override
	public User retrieveUser(final Long id) throws RequestException {
		final HttpConnection connection = new HttpConnection();

		if (id != null) {
			connection.addParameter(USER_ID, String.valueOf(id));
		}

		return gson.fromJson(connection.executeGet(host + RETRIEVE_USER_ACTION), User.class);
	}

	@Override
	public List<Project> getMyBacklogs() throws RequestException {
		final HttpConnection connection = new HttpConnection();

		final Type listType = new TypeToken<ArrayList<Project>>() {}.getType();
		return gson.fromJson(connection.executeGet(host + GET_MY_BACKLOGS_URL), listType);
	}

	@Override
	public DailyWork getDailyWork(final Long id) throws RequestException {
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(USER_ID, String.valueOf(id));

		return gson.fromJson(
				connection.executeGet(host + DAILY_WORK_ACTION), DailyWork.class);
	}

	@Override
	public Story changeStoryState(final StateKey state, final long storyId,
			final long backlogId, final long iterationId, final boolean tasksToDone) throws RequestException {
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(STORY_STATE, state.name());
		connection.addParameter(STORY_ID, String.valueOf(storyId));
		connection.addParameter(BACKLOG_ID, String.valueOf(backlogId));
		connection.addParameter(ITERATION_ID, String.valueOf(iterationId));
		connection.addParameter(TASKS_TO_DONE, String.valueOf(tasksToDone));

		return gson.fromJson(connection.executePost(host + STORE_STORY_ACTION), Story.class);
	}

	@Override
	public Project getProjectDetails(final long projectId) throws RequestException {
		final HttpConnection connection = new HttpConnection();
		connection.addParameter(PROJECT_ID, String.valueOf(projectId));
		return gson.fromJson(connection.executeGet(host + PROJECT_DATA), Project.class);
	}
}