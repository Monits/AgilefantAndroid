package com.monits.agilefant.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.network.request.GsonRequest;
import com.monits.android_volley.network.request.RfcCompliantListenableRequest;

public class AgilefantServiceImpl implements AgilefantService {

	private static final String TASK_ORIGINAL_ESTIMATE = "task.originalEstimate";
	private static final String TASK_EFFORT_LEFT = "task.effortLeft";
	private static final String STORE_TASK_ACTION = "%1$s/ajax/storeTask.action";
	private static final String TASK_ID = "taskId";
	private static final String TASK_STATE = "task.state";

	private static final String LOG_TASK_EFFORT_ACTION = "%1$s/ajax/logTaskEffort.action";
	private static final String USER_IDS = "userIds";
	private static final String PARENT_OBJECT_ID = "parentObjectId";
	private static final String HOUR_ENTRY_DESCRIPTION = "hourEntry.description";
	private static final String HOUR_ENTRY_MINUTES_SPENT = "hourEntry.minutesSpent";
	private static final String HOUR_ENTRY_DATE = "hourEntry.date";

	private static final String HTTP_REXEG = "^https?://.*$";
	private static final String HTTP = "http://";

	private String host;

	private static final String GET_MY_BACKLOGS_URL = "%1$s/ajax/myAssignmentsMenuData.action";
	private static final String GET_ALL_BACKLOGS_URL = "%1$s/ajax/menuData.action";
	private static final String LOGIN_URL = "%1$s/j_spring_security_check";
	private static final String PASSWORD = "j_password";
	private static final String USERNAME = "j_username";

	private static final String LOGIN_OK = "/index.jsp";

	private static final String GET_ITERATION = "%1$s/ajax/iterationData.action?iterationId=%2$d";
	private static final String ITERATION_ID = "iterationId";

	private static final String RETRIEVE_USER_ACTION = "/ajax/retrieveUser.action";
	private static final String USER_ID = "userId";

	private static final String DAILY_WORK_ACTION = "%1$s/ajax/dailyWorkData.action?userId=%2$d";

	private static final String STORE_STORY_ACTION = "%1$s/ajax/storeStory.action";
	private static final String STORY_ID = "storyId";
	private static final String STORY_STATE = "story.state";
	private static final String TASKS_TO_DONE = "tasksToDone";
	private static final String BACKLOG_ID = "backlogId";

	private static final String PROJECT_DATA = "%1$s/ajax/projectData.action?projectId=%2$d";

	@Inject
	private Gson gson;

	@Inject
	private RequestQueue requestQueue;

	@Override
	public void login(final String userName, final String password, final Listener<String> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, LOGIN_URL, host);

		final ErrorListener reqError = new ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError volleyError) {
				final NetworkResponse response = volleyError.networkResponse;
				if (response != null && response.statusCode == 302) {
					final String location = response.headers.get("Location");
					if (location != null && location.split(";")[0].equals(getHost() + LOGIN_OK)) {
						listener.onResponse(location);
					}
				} else {
					error.onErrorResponse(volleyError);
				}
			}
		};

		final RfcCompliantListenableRequest<String> request =  new RfcCompliantListenableRequest<String>(Method.POST, url, listener, reqError) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put(USERNAME, userName);
				params.put(PASSWORD, password);

				return params;
			}

			@Override
			protected Response<String> parseNetworkResponse(final NetworkResponse response) {
				// We're looking for a redirect and a header, we don't care about a 2xx response.
				return null;
			}
		};

		requestQueue.add(request);
	}

	@Override
	public List<Product> getAllBacklogs() throws RequestException {
		final String url = String.format(Locale.US, GET_ALL_BACKLOGS_URL, host);

		final RequestFuture<List<Product>> future = RequestFuture.newFuture();
		final Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
		final GsonRequest<List<Product>> request = new GsonRequest<List<Product>>(
				Method.GET, url, gson, listType, future, future);

		return submitBlockingRequest(future, request);
	}

	@Override
	public Iteration getIteration(final long id) throws RequestException {
		final String url = String.format(Locale.US, GET_ITERATION, host, id);

		final RequestFuture<Iteration> future = RequestFuture.newFuture();
		final GsonRequest<Iteration> request = new GsonRequest<Iteration>(
				Method.GET, url, gson, Iteration.class, future, future);

		return submitBlockingRequest(future, request);
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
		final String url = String.format(Locale.US, LOG_TASK_EFFORT_ACTION, host);

		final RequestFuture<String> future = RequestFuture.newFuture();
		final RfcCompliantListenableRequest<String> request = new RfcCompliantListenableRequest<String>(Method.POST, url, future, future) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put(HOUR_ENTRY_DATE, String.valueOf(date));
				params.put(HOUR_ENTRY_MINUTES_SPENT, String.valueOf(minutesSpent));
				params.put(HOUR_ENTRY_DESCRIPTION, description);
				params.put(PARENT_OBJECT_ID, String.valueOf(taskId));
				params.put(USER_IDS, String.valueOf(userId));

				return params;
			}

			@Override
			protected Response<String> parseNetworkResponse(final NetworkResponse response) {
				try {
					final String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
					return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
				} catch (final UnsupportedEncodingException e) {
					return Response.error(new ParseError(e));
				}
			}
		};

		submitBlockingRequest(future, request);
	}

	@Override
	public Task taskChangeState(final StateKey state, final long taskId) throws RequestException {
		final String url = String.format(Locale.US, STORE_TASK_ACTION, host);

		final RequestFuture<Task> future = RequestFuture.newFuture();
		final GsonRequest<Task> request = new GsonRequest<Task>(
				Method.POST, url, gson, Task.class, future, future) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put(TASK_STATE, state.name());
				params.put(TASK_ID, String.valueOf(taskId));

				return params;
			}
		};

		return submitBlockingRequest(future, request);
	}

	@Override
	public Task changeEffortLeft(final double effortLeft, final long taskId) throws RequestException{
		final String url = String.format(Locale.US, STORE_TASK_ACTION, host);

		final RequestFuture<Task> future = RequestFuture.newFuture();
		final GsonRequest<Task> request = new GsonRequest<Task>(
				Method.POST, url, gson, Task.class, future, future) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put(TASK_EFFORT_LEFT, String.valueOf(effortLeft));
				params.put(TASK_ID, String.valueOf(taskId));

				return params;
			}
		};

		return submitBlockingRequest(future, request);
	}

	@Override
	public Task changeOriginalEstimate(final int origalEstimate, final long taskId) throws RequestException {
		final String url = String.format(Locale.US, STORE_TASK_ACTION, host);

		final RequestFuture<Task> future = RequestFuture.newFuture();
		final GsonRequest<Task> request = new GsonRequest<Task>(
				Method.POST, url, gson, Task.class, future, future) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put(TASK_ORIGINAL_ESTIMATE, String.valueOf(origalEstimate));
				params.put(TASK_ID, String.valueOf(taskId));

				return params;
			}
		};

		return submitBlockingRequest(future, request);
	}

	@Override
	public void retrieveUser(final Long id, final Listener<User> listener, final ErrorListener error) {
		final StringBuilder url = new StringBuilder()
			.append(host)
			.append(RETRIEVE_USER_ACTION);
		if (id != null) {
			url.append("?")
				.append(USER_ID)
				.append("=")
				.append(id);
		}

		final GsonRequest<User> request = new GsonRequest<User>(
				Method.GET, url.toString(), gson, User.class, listener, error);

		requestQueue.add(request);
	}

	@Override
	public List<Project> getMyBacklogs() throws RequestException {
		final String url = String.format(Locale.US, GET_MY_BACKLOGS_URL, host);

		final RequestFuture<List<Project>> future = RequestFuture.newFuture();
		final Type listType = new TypeToken<ArrayList<Project>>() {}.getType();
		final GsonRequest<List<Project>> request = new GsonRequest<List<Project>>(
				Method.GET, url, gson, listType, future, future);

		return submitBlockingRequest(future, request);
	}

	@Override
	public DailyWork getDailyWork(final Long id) throws RequestException {
		final String url = String.format(Locale.US, DAILY_WORK_ACTION, host, id);

		final RequestFuture<DailyWork> future = RequestFuture.newFuture();
		final GsonRequest<DailyWork> request = new GsonRequest<DailyWork>(
				Method.GET, url, gson, DailyWork.class, future, future);

		return submitBlockingRequest(future, request);
	}

	@Override
	public Story changeStoryState(final StateKey state, final long storyId,
			final long backlogId, final long iterationId, final boolean tasksToDone) throws RequestException {
		final String url = String.format(Locale.US, STORE_STORY_ACTION, host);

		final RequestFuture<Story> future = RequestFuture.newFuture();
		final GsonRequest<Story> request = new GsonRequest<Story>(
				Method.POST, url, gson, Story.class, future, future) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put(STORY_STATE, state.name());
				params.put(STORY_ID, String.valueOf(storyId));
				params.put(BACKLOG_ID, String.valueOf(backlogId));
				params.put(ITERATION_ID, String.valueOf(iterationId));
				params.put(TASKS_TO_DONE, String.valueOf(tasksToDone));

				return params;
			}
		};

		return submitBlockingRequest(future, request);
	}

	@Override
	public Project getProjectDetails(final long projectId) throws RequestException {
		final String url = String.format(Locale.US, PROJECT_DATA, host, projectId);

		final RequestFuture<Project> future = RequestFuture.newFuture();
		final GsonRequest<Project> request = new GsonRequest<Project>(
				Method.GET, url, gson, Project.class, future, future);

		return submitBlockingRequest(future, request);
	}

	/**
	 * Send a request as blocking.
	 *
	 * The submitted future MUST be already set to listen for success and error responses on the given request.
	 *
	 * @param future The future to be used when syncing.
	 * @param request The request to be performed.
	 * @return The result of the request.
	 * @throws RequestException If the request failed (didn't get a 2XX)
	 */
	private <T> T submitBlockingRequest(final RequestFuture<T> future, final Request<T> request) throws RequestException {
		/*
		 * FIXME: This method is ugly, it's API is ugly, and the null error parsing horrible.
		 * This is for compatibility with old HttpConnection, methods should be refactored to make
		 * everything async and this method obsolete.
		 */
		requestQueue.add(request);

		try {
			return future.get();
		} catch (final ExecutionException e) {
			// TODO : Actually parse the response / show appropriate messages!
			throw new RequestException(e);
		} catch (final Exception e) {
			Log.e(getClass().getName(), "Havoc broke while sending a blocking request to the server.", e);
			throw new RequestException(e);
		}
	}
}