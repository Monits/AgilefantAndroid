package com.monits.agilefant.service;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.RequeueAfterRequestDecorator;
import com.android.volley.RequeuePolicy;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.network.request.UrlGsonRequest;
import com.monits.volleyrequests.network.request.GsonRequest;
import com.monits.volleyrequests.network.request.RfcCompliantListenableRequest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class AgilefantServiceImpl implements AgilefantService {

	private static final String STORY_STORY_POINTS = "story.storyPoints";
	private static final String STORY_STORY_VALUE = "story.storyValue";
	private static final String STORY_NAME = "story.name";
	private static final String STORY_DESCRIPTION = "story.description";
	private static final String RANK_TASK_UNDER_ACTION = "%1$s/ajax/rankTaskAndMoveUnder.action";
	private static final String RANK_UNDER_ID = "rankUnderId";
	private static final String RESPONSIBLES_CHANGED = "responsiblesChanged";
	private static final String NEW_RESPONSIBLES = "newResponsibles";
	private static final String USERS_CHANGED = "usersChanged";
	private static final String ASSIGNEE_IDS = "assigneeIds";
	private static final String ASSIGNEES_CHANGED = "assigneesChanged";
	private static final String PROJECT_ID = "projectId";
	private static final String PROJECT_LEAF_STORIES_ACTION = "%1$s/ajax/projectLeafStories.action";
	private static final String OBJECT_ID = "objectId";
	private static final String TASK_ORIGINAL_ESTIMATE = "task.originalEstimate";
	private static final String TASK_EFFORT_LEFT = "task.effortLeft";
	private static final String TASK_ID = "taskId";
	private static final String TASK_STATE = "task.state";

	private static final String USER_IDS = "userIds";

	private static final String HTTP_REXEG = "^https?://.*$";
	private static final String HTTP = "http://";

	private String host;

	private static final String LOGIN_URL = "%1$s/j_spring_security_check";
	private static final String PASSWORD = "j_password";
	private static final String USERNAME = "j_username";

	private static final String LOGIN_OK = "/index.jsp";

	private static final String ITERATION_ID = "iterationId";
	private static final String ITERATION = "iteration";

	private static final String STORE_STORY_ACTION = "%1$s/ajax/storeStory.action";
	private static final String STORY_ID = "storyId";
	private static final String STORY_STATE = "story.state";
	private static final String STORY_MOVE = "%1$s/ajax/moveStory.action";
	private static final String TASKS_TO_DONE = "tasksToDone";
	private static final String BACKLOG_ID = "backlogId";

	private static final String PROJECT_DATA = "%1$s/ajax/projectData.action?projectId=%2$d";
	private static final String STORY_CREATE = "%1$s/ajax/createStory.action";
	private static final String TASK_CREATE = "%1$s/ajax/createTask.action";

	private static final String RANK_STORY_UNDER_ACTION = "%1$s/ajax/rankStoryUnder.action";
	private static final String RANK_STORY_OVER_ACTION = "%1$s/ajax/rankStoryOver.action";
	private static final String TARGET_STORY_ID = "targetStoryId";

	protected static final String TASK_NAME = "task.name";

	private final ReloginRequeuePolicy reloginRequeuePolicy = new ReloginRequeuePolicy();

	private final SharedPreferences sharedPreferences;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	private final RequestQueue requestQueue;

	/**
	 * @param sharedPreferences Injected via constructor by Dagger
	 * @param gson Injected via constructor by Dagger
	 * @param requestQueue Injected via constructor by Dagger
	 */
	@Inject
	public AgilefantServiceImpl(final SharedPreferences sharedPreferences,
								final Gson gson, final RequestQueue requestQueue) {
		this.gson = gson;
		this.sharedPreferences = sharedPreferences;
		this.requestQueue = requestQueue;
	}

	@Override
	public void login(final String userName, final String password, final Listener<String> listener,
					final ErrorListener error) {
		final String url = String.format(Locale.US, LOGIN_URL, getHost());

		final ErrorListener reqError = new ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError volleyError) {
				final NetworkResponse response = volleyError.networkResponse;
				if (response != null && response.statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
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

		final RfcCompliantListenableRequest<String> request =
				new RfcCompliantListenableRequest<String>(Method.POST, url, listener, reqError) {

					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						final Map<String, String> params = new HashMap<>();

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
	public void setDomain(final String domain) {
		if (domain.matches(HTTP_REXEG)) {
			this.host = domain;
		} else {
			this.host = HTTP + domain;
		}
	}

	@Override
	public String getHost() {
		if (host == null) {
			setDomain(sharedPreferences.getString(UserService.DOMAIN_KEY, null));
		}
		return host;
	}

	@Override
	public void updateStory(final Story story, final Boolean tasksToDone, final Listener<Story> listener,
							final ErrorListener error) {

		final String url = String.format(Locale.US, STORE_STORY_ACTION, getHost());

		final UrlGsonRequest<Story> req = new UrlGsonRequest<Story>(Method.POST, url,
				gson, Story.class, listener, error, null) {


			@Override
			public byte[] getBody() throws AuthFailureError {
				final Long backlogId;
				final Long iterationId;
				final Iteration iteration = story.getIteration();
				final Backlog backlog = story.getBacklog();
				if (backlog != null && iteration != null) {
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
						appendURLEncodedParam(body, USER_IDS, String.valueOf(user.getId()), paramsEncoding);
					}

					appendURLEncodedParam(body, USERS_CHANGED, String.valueOf(true), paramsEncoding);
					appendURLEncodedParam(body, STORY_ID, String.valueOf(story.getId()), paramsEncoding);
					appendURLEncodedParam(body, STORY_STATE, story.getState().name(), paramsEncoding);
					appendURLEncodedParam(body, BACKLOG_ID, String.valueOf(backlogId), paramsEncoding);

					if (iterationId != null) {
						appendURLEncodedParam(body, ITERATION_ID, String.valueOf(iterationId), paramsEncoding);
					}

					if (tasksToDone != null) {
						appendURLEncodedParam(body, TASKS_TO_DONE, String.valueOf(tasksToDone), paramsEncoding);
					}

				} catch (final UnsupportedEncodingException e) {
					throw new AssertionError(e);
				}

				return body.toString().getBytes(Charset.forName(paramsEncoding));
			}
		};

		addRequest(req);
	}

	@Override
	public void getProjectDetails(final long projectId, final Listener<Project> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, PROJECT_DATA, getHost(), projectId);

		final GsonRequest<Project> request = new GsonRequest<>(Method.GET, url,
				gson, Project.class, listener, error, null);

		addRequest(request);
	}

	@Override
	public void getProjectLeafStories(final long projectId, final Listener<List<Story>> listener,
									final ErrorListener error) {
		final String url = String.format(Locale.US, PROJECT_LEAF_STORIES_ACTION, getHost());

		final Type listType = new TypeToken<ArrayList<Story>>() { }.getType();

		final UrlGsonRequest<List<Story>> request = new UrlGsonRequest<List<Story>>(
				Method.POST, url, gson, listType, listener, error, null) {

			@Override
			public byte[] getBody() throws AuthFailureError {

				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();
				try {
					appendURLEncodedParam(body, OBJECT_ID, String.valueOf(projectId), paramsEncoding);
				} catch (final UnsupportedEncodingException e) {
					throw new AssertionError(e);
				}

				return body.toString().getBytes(Charset.forName(paramsEncoding));
			}
		};

		addRequest(request);
	}

	@Override
	public void updateProject(final Project project, final Listener<Project> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, "%1$s/ajax/storeProject.action", getHost());

		final UrlGsonRequest<Project> request = new UrlGsonRequest<Project>(
				Method.POST, url, gson, Project.class, listener, error, null) {

			@Override
			public byte[] getBody() throws AuthFailureError {
				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();
				try {
					for (final User user : project.getAssignees()) {
						appendURLEncodedParam(body, ASSIGNEE_IDS, String.valueOf(user.getId()), paramsEncoding);
					}

					appendURLEncodedParam(body, ASSIGNEES_CHANGED, String.valueOf(true), paramsEncoding);
					appendURLEncodedParam(body, PROJECT_ID, String.valueOf(project.getId()), paramsEncoding);

				} catch (final UnsupportedEncodingException e) {
					throw new AssertionError(e);
				}

				return body.toString().getBytes(Charset.forName(paramsEncoding));
			}
		};

		addRequest(request);
	}

	@Override
	public void rankTaskUnder(final Task task, final Task targetTask, final Listener<Task> listener,
							final ErrorListener error) {

		final String url = String.format(Locale.US, RANK_TASK_UNDER_ACTION, getHost());

		final GsonRequest<Task> request = new GsonRequest<Task>(Method.POST, url,
				gson, Task.class, listener, error, null) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<>();

				params.put(ITERATION_ID, String.valueOf(task.getIteration().getId()));
				params.put(TASK_ID, String.valueOf(task.getId()));
				params.put(RANK_UNDER_ID, String.valueOf(targetTask == null ? -1 : targetTask.getId()));

				return params;
			}
		};

		addRequest(request);
	}

	@Override
	public void rankStoryUnder(final Story story, final Story targetStory, final Listener<Story> listener,
							final ErrorListener error) {
		rankStoryUnder(story, targetStory, null, listener, error);
	}

	@Override
	public void rankStoryUnder(final Story story, final Story targetStory, final Long backlogId,
							final Listener<Story> listener, final ErrorListener error) {

		final String url = String.format(Locale.US, RANK_STORY_UNDER_ACTION, getHost());

		final GsonRequest<Story> request = newGsonRequest(story, targetStory, backlogId, url, listener, error);

		addRequest(request);
	}

	@Override
	public void rankStoryOver(final Story story, final Story targetStory, final Listener<Story> listener,
							final ErrorListener error) {
		rankStoryOver(story, targetStory, null, listener, error);
	}

	@Override
	public void rankStoryOver(final Story story, final Story targetStory, final Long backlogId,
							final Listener<Story> listener, final ErrorListener error) {

		final String url = String.format(Locale.US, RANK_STORY_OVER_ACTION, getHost());

		final GsonRequest<Story> request = newGsonRequest(story, targetStory, backlogId, url, listener, error);

		addRequest(request);
	}

	private GsonRequest<Story> newGsonRequest(final Story story, final Story targetStory, final Long backlogId,
											final String url, final Listener<Story> listener,
											final ErrorListener error) {

		return new GsonRequest<Story>(Method.POST, url, gson, Story.class, listener, error, null) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<>();

				params.put(STORY_ID, String.valueOf(story.getId()));
				params.put(TARGET_STORY_ID, String.valueOf(targetStory.getId()));

				if (backlogId != null) {
					params.put(BACKLOG_ID, String.valueOf(backlogId));
				}

				return params;
			}
		};
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
	 * @throws UnsupportedEncodingException If the given encoding is not supported
	 */
	private StringBuilder appendURLEncodedParam(final StringBuilder sb, final String key, final String value,
												final String encoding) throws UnsupportedEncodingException {

		sb.append(URLEncoder.encode(key, encoding));
		sb.append('=');
		sb.append(URLEncoder.encode(value, encoding));
		sb.append('&');

		return sb;
	}

	@Override
	public void moveStory(final long backlogId, final Story story, final Listener<Story> listener,
						final ErrorListener error) {
		final String url = String.format(Locale.US, STORY_MOVE, getHost());

		final GsonRequest<Story> request = new GsonRequest<Story>(
				Method.POST, url, gson, Story.class, listener, error, null) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<>();

				params.put("backlogId", String.valueOf(backlogId));
				params.put("moveParents", "false"); //This parameter is always false
				params.put("storyId", String.valueOf(story.getId()));

				return params;
			}

		};
		addRequest(request);
	}

	@Override
	public void createStory(final BacklogElementParameters parameters,
							final Listener<Story> listener, final ErrorListener error) {

		final String url = String.format(Locale.US, STORY_CREATE, getHost());

		final UrlGsonRequest<Story> request = new UrlGsonRequest<Story>(
				Method.POST, url, gson, Story.class, listener, error, null) {

			@Override
			public byte[] getBody() throws AuthFailureError {
				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();
				try {
					final Long iterationId = parameters.getIterationId();
					Long backlogId = parameters.getBacklogId();
					if (backlogId == null && iterationId != null) {
						backlogId = iterationId;
					}

					appendURLEncodedParam(body, BACKLOG_ID, String.valueOf(backlogId), paramsEncoding);

					if (iterationId != null) {
						appendURLEncodedParam(body, ITERATION, String.valueOf(iterationId), paramsEncoding);
					}

					appendURLEncodedParam(body, STORY_DESCRIPTION, "", paramsEncoding);
					appendURLEncodedParam(body, STORY_NAME, parameters.getName(), paramsEncoding);
					appendURLEncodedParam(body, STORY_STATE, String.valueOf(parameters.getStateKey()), paramsEncoding);
					appendURLEncodedParam(body, STORY_STORY_POINTS, "", paramsEncoding);
					appendURLEncodedParam(body, STORY_STORY_VALUE, "", paramsEncoding);

					for (final User user : parameters.getSelectedUser()) {
						appendURLEncodedParam(body, USER_IDS, String.valueOf(user.getId()), paramsEncoding);
					}

					appendURLEncodedParam(body, USERS_CHANGED, String.valueOf(true), paramsEncoding);

				} catch (final UnsupportedEncodingException e) {
					throw new AssertionError(e);
				}

				return body.toString().getBytes(Charset.forName(paramsEncoding));
			}
		};

		addRequest(request);
	}

	@Override
	public void createTask(final BacklogElementParameters parameters, final Listener<Task> listener,
						final ErrorListener errorListener) {

		final String url = String.format(Locale.US, TASK_CREATE, getHost());

		final UrlGsonRequest<Task> request = new UrlGsonRequest<Task>(
				Method.POST, url, gson,
				Task.class, listener, errorListener, null) {

			@Override
			public byte[] getBody() throws AuthFailureError {

				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();
				try {

					appendURLEncodedParam(
							body, ITERATION_ID, String.valueOf(parameters.getIterationId()), paramsEncoding);

					for (final User user : parameters.getSelectedUser()) {
						appendURLEncodedParam(body, NEW_RESPONSIBLES, String.valueOf(user.getId()), paramsEncoding);
					}

					appendURLEncodedParam(body, RESPONSIBLES_CHANGED, String.valueOf(true), paramsEncoding);
					appendURLEncodedParam(body, TASK_EFFORT_LEFT, "", paramsEncoding);
					appendURLEncodedParam(body, TASK_NAME, parameters.getName(), paramsEncoding);
					appendURLEncodedParam(body, TASK_ORIGINAL_ESTIMATE, "", paramsEncoding);
					appendURLEncodedParam(body, TASK_STATE, String.valueOf(parameters.getStateKey()), paramsEncoding);

				} catch (final UnsupportedEncodingException e) {
					throw new AssertionError(e);
				}

				return body.toString().getBytes(Charset.forName(paramsEncoding));
			}
		};

		addRequest(request);
	}

	@Override
	public void addRequest(final Request<?> request) {
		requestQueue.add(RequeueAfterRequestDecorator.wrap(request, reloginRequeuePolicy));
	}

	@Override
	public String toString() {
		return "AgilefantServiceImpl{host=" + host + '}';
	}

	/**
	 * Implementation of {@link RequeuePolicy} which will attempt to re-login before requeueing.
	 *
	 * @author gmuniz
	 *
	 */
	private final class ReloginRequeuePolicy implements RequeuePolicy {

		private ReloginRequeuePolicy() {
		}

		@Override
		public boolean shouldRequeue(final NetworkResponse networkResponse) {
			return networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_MOVED_TEMP;
		}

		@Override
		public void executeBeforeRequeueing(final Listener<?> successCallback, final ErrorListener errorCallback) {

			login(
					sharedPreferences.getString(UserService.USER_NAME_KEY, null),
					sharedPreferences.getString(UserService.PASSWORD_KEY, null),
					new Listener<String>() {

						@Override
						public void onResponse(final String arg0) {
							// We don't really care about response, we only need to be notified.
							successCallback.onResponse(null);
						}
					},
					errorCallback
			);
		}
	}

}