package com.monits.agilefant.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.FilterableUser;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.network.request.GsonRequest;
import com.monits.android_volley.network.request.RfcCompliantListenableRequest;

public class AgilefantServiceImpl implements AgilefantService {

	private static final String RANK_TASK_UNDER_ACTION = "%1$s/ajax/rankTaskAndMoveUnder.action";
	private static final String RANK_UNDER_ID = "rankUnderId";
	private static final String RESPONSIBLES_CHANGED = "responsiblesChanged";
	private static final String NEW_RESPONSIBLES = "newResponsibles";
	private static final String USERS_CHANGED = "usersChanged";
	private static final String ASSIGNEE_IDS = "assigneeIds";
	private static final String ASSIGNEES_CHANGED = "assigneesChanged";
	private static final String PROJECT_ID = "projectId";
	private static final String USER_CHOOSER_DATA_ACTION = "%1$s/ajax/userChooserData.action";
	private static final String PROJECT_LEAF_STORIES_ACTION = "%1$s/ajax/projectLeafStories.action";
	private static final String OBJECT_ID = "objectId";
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
	private static final String STORY_MOVE = "%1$s/ajax/moveStory.action";
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
					} else {
						error.onErrorResponse(volleyError);
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
	public void getAllBacklogs(final Listener<List<Product>> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, GET_ALL_BACKLOGS_URL, host);

		final Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
		final GsonRequest<List<Product>> request = new GsonRequest<List<Product>>(
				Method.GET, url, gson, listType, listener, error);

		requestQueue.add(request);
	}

	@Override
	public void getIteration(final long id, final Listener<Iteration> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, GET_ITERATION, host, id);

		final GsonRequest<Iteration> request = new GsonRequest<Iteration>(
				Method.GET, url, gson, Iteration.class, listener, error);

		requestQueue.add(request);
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
	public void taskChangeSpentEffort(final long date, final long minutesSpent, final String description, final long taskId, final long userId,
			final Listener<String> listener, final ErrorListener error) {

		final String url = String.format(Locale.US, LOG_TASK_EFFORT_ACTION, host);

		final RfcCompliantListenableRequest<String> request = new RfcCompliantListenableRequest<String>(Method.POST, url, listener, error) {

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

		requestQueue.add(request);
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
	public void getMyBacklogs(final Listener<List<Project>> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, GET_MY_BACKLOGS_URL, host);

		final Type listType = new TypeToken<ArrayList<Project>>() {}.getType();
		final GsonRequest<List<Project>> request = new GsonRequest<List<Project>>(
				Method.GET, url, gson, listType, listener, error);

		requestQueue.add(request);
	}

	@Override
	public void getDailyWork(final Long id, final Listener<DailyWork> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, DAILY_WORK_ACTION, host, id);

		final GsonRequest<DailyWork> request = new GsonRequest<DailyWork>(
				Method.GET, url, gson, DailyWork.class, listener, error);

		requestQueue.add(request);
	}

	@Override
	public void updateStory(final Story story, final Boolean tasksToDone, final Listener<Story> listener, final ErrorListener error) {

		final String url = String.format(Locale.US, STORE_STORY_ACTION, host);

		final GsonRequest<Story> request = new GsonRequest<Story>(
				Method.POST, url, gson, Story.class, listener, error) {

			@Override
			public byte[] getBody() throws AuthFailureError {
				final Long backlogId;
				final Long iterationId;
				final Iteration iteration = story.getIteration();
				final Backlog backlog = story.getBacklog();
				if (backlog != null
						&& iteration != null) {
					backlogId = backlog.getId();
					iterationId = iteration.getId();
				} else if (iteration != null) {
					backlogId = iteration.getId();
					iterationId = iteration.getId();
				} else {
					backlogId = backlog.getId();
					iterationId = null;
				}

				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();
				try {
					for (final User user : story.getResponsibles()) {
						appendURLEncodedParam(body,
								USER_IDS, String.valueOf(user.getId()), paramsEncoding);
					}

					appendURLEncodedParam(body,
							USERS_CHANGED, String.valueOf(true), paramsEncoding);

					appendURLEncodedParam(body,
							STORY_ID, String.valueOf(story.getId()), paramsEncoding);

					appendURLEncodedParam(body,
							STORY_STATE, story.getState().name(), paramsEncoding);

					appendURLEncodedParam(body,
							BACKLOG_ID, String.valueOf(backlogId), paramsEncoding);

					if (iterationId != null) {
						appendURLEncodedParam(body,
								ITERATION_ID, String.valueOf(iterationId), paramsEncoding);
					}

					if (tasksToDone != null) {
						appendURLEncodedParam(body,
								TASKS_TO_DONE, String.valueOf(tasksToDone), paramsEncoding);
					}

				} catch (final UnsupportedEncodingException e) {
					throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
				}

				return body.toString().getBytes();
			}
		};

		requestQueue.add(request);
	}

	@Override
	public void getProjectDetails(final long projectId, final Listener<Project> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, PROJECT_DATA, host, projectId);

		final GsonRequest<Project> request = new GsonRequest<Project>(
				Method.GET, url, gson, Project.class, listener, error);

		requestQueue.add(request);
	}

	@Override
	public void getProjectLeafStories(final long projectId, final Listener<List<Story>> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, PROJECT_LEAF_STORIES_ACTION, host);

		final Type listType = new TypeToken<ArrayList<Story>>() {}.getType();
		final GsonRequest<List<Story>> request = new GsonRequest<List<Story>>(
				Method.POST, url, gson, listType, listener, error) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put(OBJECT_ID, String.valueOf(projectId));

				return params;
			}
		};

		requestQueue.add(request);
	}

	@Override
	public void getFilterableUsers(final Listener<List<FilterableUser>> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, USER_CHOOSER_DATA_ACTION, host);

		final Type listType = new TypeToken<ArrayList<FilterableUser>>() {}.getType();
		final GsonRequest<List<FilterableUser>> request = new GsonRequest<List<FilterableUser>>(
				Method.POST, url, gson, listType, listener, error);

		requestQueue.add(request);
	}

	@Override
	public void updateProject(final Project project, final Listener<Project> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, "%1$s/ajax/storeProject.action", host);

		final GsonRequest<Project> request = new GsonRequest<Project>(
				Method.POST, url, gson, Project.class, listener, error) {

			@Override
			public byte[] getBody() throws AuthFailureError {
				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();
				try {
					for (final User user : project.getAssignees()) {
						appendURLEncodedParam(body,
								ASSIGNEE_IDS, String.valueOf(user.getId()), paramsEncoding);
					}

					appendURLEncodedParam(body,
							ASSIGNEES_CHANGED, String.valueOf(true), paramsEncoding);

					appendURLEncodedParam(body,
							PROJECT_ID, String.valueOf(project.getId()), paramsEncoding);

				} catch (final UnsupportedEncodingException e) {
					throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
				}

				return body.toString().getBytes();
			}
		};

		requestQueue.add(request);
	}

	@Override
	public void updateTask(final Task task, final Listener<Task> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, STORE_TASK_ACTION, host);

		final GsonRequest<Task> request = new GsonRequest<Task>(
				Method.POST, url, gson, Task.class, listener, error) {

			@Override
			public byte[] getBody() throws AuthFailureError {
				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();

				try {
					for (final User user : task.getResponsibles()) {
						appendURLEncodedParam(body,
								NEW_RESPONSIBLES, String.valueOf(user.getId()), paramsEncoding);
					}
					appendURLEncodedParam(body,
							RESPONSIBLES_CHANGED, String.valueOf(true), paramsEncoding);


					appendURLEncodedParam(body,
							TASK_STATE, String.valueOf(task.getState().name()), paramsEncoding);

					/*
					 *  This 2 values, EL and OE, for sending requests it needs # of hours, not in minutes,
					 *  while in the rest of the application this works under minutes.
					 */
					appendURLEncodedParam(body,
							TASK_EFFORT_LEFT, String.valueOf(task.getEffortLeft() / 60), paramsEncoding);
					appendURLEncodedParam(body,
							TASK_ORIGINAL_ESTIMATE, String.valueOf(task.getOriginalEstimate() / 60), paramsEncoding);

					appendURLEncodedParam(body,
							TASK_ID, String.valueOf(task.getId()), paramsEncoding);

				} catch (final UnsupportedEncodingException e) {
					throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
				}

				return body.toString().getBytes();
			}
		};

		requestQueue.add(request);
	}

	@Override
	public void rankTaskUnder(final Task task, final Task targetTask,
			final Listener<Task> listener, final ErrorListener error) {

		final String url = String.format(Locale.US, RANK_TASK_UNDER_ACTION, host);

		final GsonRequest<Task> request = new GsonRequest<Task>(
				Method.POST, url, gson, Task.class, listener, error) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put(ITERATION_ID, String.valueOf(task.getIteration().getId()));
				params.put(TASK_ID, String.valueOf(task.getId()));
				params.put(RANK_UNDER_ID, String.valueOf(targetTask != null ? targetTask.getId() : -1));

				return params;
			}
		};

		requestQueue.add(request);
	}

	/**
	 * An auxiliary method in order to append parameters to the given post body's StringBuilder,
	 * for some special requests.
	 *
	 * @param sb the StringBuilder to build post body.
	 * @param key the key of the param.
	 * @param value the value for that key.
	 * @param encoding the encoding.
	 * @return the same StringBuilder that was given, with the given params appended.
	 *
	 * @throws UnsupportedEncodingException
	 */
	private StringBuilder appendURLEncodedParam(final StringBuilder sb,
			final String key, final String value, final String encoding) throws UnsupportedEncodingException {

		sb.append(URLEncoder.encode(key, encoding));
		sb.append('=');
		sb.append(URLEncoder.encode(value, encoding));
		sb.append('&');

		return sb;
	}

	@Override
	public void moveStory(final long backlogId, final Story story,
			final Listener<Story> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, STORY_MOVE, host);
		
		final GsonRequest<Story> request = new GsonRequest<Story>(
				Method.POST, url, gson, Story.class, listener, error) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<String, String>();

				params.put("backlogId", String.valueOf(backlogId));
				params.put("moveParents", "false"); //This parameter is always false
				params.put("storyId", String.valueOf(story.getId()));
				
				return params;
			}
			
		};
		requestQueue.add(request);
	}


}