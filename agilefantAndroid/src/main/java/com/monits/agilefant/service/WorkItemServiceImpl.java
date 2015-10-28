package com.monits.agilefant.service;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Rankable;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.network.request.UrlGsonRequest;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Created by edipasquale on 27/10/15.
 */
public class WorkItemServiceImpl implements WorkItemService {

	private static final String STORE_TASK_ACTION = "%1$s/ajax/storeTask.action";
	private static final String TASK_ID = "taskId";
	private static final int MINUTES = 60;
	private static final String TASKS_TO_DONE = "tasksToDone";
	private static final String STORE_STORY_ACTION = "%1$s/ajax/storeStory.action";
	private static final String STORY_ID = "storyId";
	private static final String BACKLOG_ID = "backlogId";
	private static final String ITERATION_ID = "iterationId";
	protected static final String TASK_NAME = "task.name";
	private static final String TASK_CREATE = "%1$s/ajax/createTask.action";
	private static final String STORY_STATE = "story.state";
	private static final String STORY_CREATE = "%1$s/ajax/createStory.action";
	private static final String ITERATION = "iteration";
	private static final String TASK_STATE = "task.state";
	private static final String USER_IDS = "userIds";
	private static final String TASK_ORIGINAL_ESTIMATE = "task.originalEstimate";
	private static final String TASK_EFFORT_LEFT = "task.effortLeft";
	private static final String RESPONSIBLES_CHANGED = "responsiblesChanged";
	private static final String NEW_RESPONSIBLES = "newResponsibles";
	private static final String USERS_CHANGED = "usersChanged";
	private static final String STORY_STORY_POINTS = "story.storyPoints";
	private static final String STORY_STORY_VALUE = "story.storyValue";
	private static final String STORY_NAME = "story.name";
	private static final String STORY_DESCRIPTION = "story.description";
	private static final String STORY_MOVE = "%1$s/ajax/moveStory.action";
	private static final String RANK_STORY_UNDER_ACTION = "%1$s/ajax/rankStoryUnder.action";
	private static final String RANK_STORY_OVER_ACTION = "%1$s/ajax/rankStoryOver.action";
	private static final String TARGET_STORY_ID = "targetStoryId";
	private static final String RANK_TASK_UNDER_ACTION = "%1$s/ajax/rankTaskAndMoveUnder.action";
	private static final String RANK_UNDER_ID = "rankUnderId";

	private final AgilefantService agilefantService;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	/**
	 * @param agilefantService AgilefantService injected by Dagger
	 * @param gson Gson injected by Dagger
	 */
	@Inject
	public WorkItemServiceImpl(final AgilefantService agilefantService, final Gson gson) {
		this.gson = gson;
		this.agilefantService = agilefantService;
	}

	@Override
	public void changeStoryResponsibles(final List<User> responsibles, final Story story,
										final Response.Listener<Story> listener, final Response.ErrorListener error) {
		final Story fallbackStory = story.getCopy();
		story.setResponsibles(responsibles);

		updateStory(
				story,
				null,
				listener,
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						story.updateValues(fallbackStory);

						error.onErrorResponse(arg0);
					}
				});

	}

	@Override
	public void changeTaskResponsibles(final List<User> responsibles, final Task task, final Response.Listener<Task>
			listener, final Response.ErrorListener error) {
		final Task fallbackTask = task.getCopy();
		task.setResponsibles(responsibles);

		updateTask(task,
				new Response.Listener<Task>() {

					@Override
					public void onResponse(final Task response) {
						task.updateValues(response);

						listener.onResponse(response);
					}
				},
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void moveStory(final Story story, final Iteration iteration, final Response.Listener<Story> listener,
						final Response.ErrorListener error) {
		final Story fallbackStory = story.getCopy();
		story.setIteration(iteration);

		final long backlogId;
		if (iteration == null) {
			backlogId = story.getBacklog().getId();
		} else {
			backlogId = iteration.getId();
		}

		final String url = String.format(Locale.US, STORY_MOVE, agilefantService.getHost());

		final GsonRequest<Story> request = new GsonRequest<Story>(
				Request.Method.POST, url, gson, Story.class,
				new Response.Listener<Story>() {

					@Override
					public void onResponse(final Story storyOk) {
						// The story from the response is incomplete, sending the one we have updated.
						listener.onResponse(story);
					}
				},
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						story.setIteration(fallbackStory.getIteration());

						error.onErrorResponse(arg0);
					}
				}, null) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<>();

				params.put("backlogId", String.valueOf(backlogId));
				params.put("moveParents", "false"); //This parameter is always false
				params.put("storyId", String.valueOf(story.getId()));

				return params;
			}

		};
		agilefantService.addRequest(request);
	}

	@Override
	public void rankTaskUnder(final Task task, final Task targetTask, final List<Task> allTasks,
							final Response.Listener<Task> listener, final Response.ErrorListener error) {
		final List<Task> fallbackTasksList = new LinkedList<>();
		copyAndSetRank(allTasks, fallbackTasksList);

		final String url = String.format(Locale.US, RANK_TASK_UNDER_ACTION, agilefantService.getHost());

		final GsonRequest<Task> request = new GsonRequest<Task>(Request.Method.POST, url,
				gson, Task.class, listener,
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allTasks, fallbackTasksList);

						error.onErrorResponse(arg0);
					}
				}, null) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<>();

				params.put(ITERATION_ID, String.valueOf(task.getIteration().getId()));
				params.put(TASK_ID, String.valueOf(task.getId()));
				params.put(RANK_UNDER_ID, String.valueOf(targetTask == null ? -1 : targetTask.getId()));

				return params;
			}
		};

		agilefantService.addRequest(request);
	}

	@Override
	public void rankStoryUnder(final Story story, final Story targetStory, final List<Story> allStories,
							final Response.Listener<Story> listener, final Response.ErrorListener error) {
		rankStoryUnder(story, targetStory, null, allStories, listener, error);
	}

	@Override
	public void rankStoryUnder(final Story story, final Story targetStory, final Long backlogId,
							final List<Story> allStories, final Response.Listener<Story> listener,
							final Response.ErrorListener error) {
		final List<Story> fallbackStoryList = new LinkedList<>();
		copyAndSetRank(allStories, fallbackStoryList);

		final String url = String.format(Locale.US, RANK_STORY_UNDER_ACTION, agilefantService.getHost());

		final GsonRequest<Story> request = newGsonRequest(story, targetStory, backlogId, url, listener,
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allStories, fallbackStoryList);

						error.onErrorResponse(arg0);
					}
				});

		agilefantService.addRequest(request);
	}

	@Override
	public void rankStoryOver(final Story story, final Story targetStory, final List<Story> allStories,
							final Response.Listener<Story> listener, final Response.ErrorListener error) {
		rankStoryOver(story, targetStory, null, allStories, listener, error);
	}

	@Override
	public void rankStoryOver(final Story story, final Story targetStory, final Long backlogId, final List<Story>
			allStories, final Response.Listener<Story> listener, final Response.ErrorListener error) {
		final List<Story> fallbackStoryList = new LinkedList<>();
		copyAndSetRank(allStories, fallbackStoryList);

		final String url = String.format(Locale.US, RANK_STORY_OVER_ACTION, agilefantService.getHost());

		final GsonRequest<Story> request = newGsonRequest(story, targetStory, backlogId, url, listener,
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allStories, fallbackStoryList);

						error.onErrorResponse(arg0);
					}
				});

		agilefantService.addRequest(request);
	}

	@Override
	public void createStory(final BacklogElementParameters parameters, final Response.Listener<Story> listener,
							final Response.ErrorListener error) {
		final String url = String.format(Locale.US, STORY_CREATE, agilefantService.getHost());

		final UrlGsonRequest<Story> request = new UrlGsonRequest<Story>(
				Request.Method.POST, url, gson, Story.class, listener, error, null) {

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

		agilefantService.addRequest(request);
	}

	@Override
	public void createTask(final BacklogElementParameters parameters, final Response.Listener<Task> listener,
						final Response.ErrorListener errorListener) {
		final String url = String.format(Locale.US, TASK_CREATE, agilefantService.getHost());

		final UrlGsonRequest<Task> request = new UrlGsonRequest<Task>(
				Request.Method.POST, url, gson,
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

		agilefantService.addRequest(request);
	}

	@Override
	public void updateTask(final Task task, final Response.Listener<Task> listener,
							final Response.ErrorListener error) {

		final String url = String.format(Locale.US, STORE_TASK_ACTION, agilefantService.getHost());

		final UrlGsonRequest<Task> request = new UrlGsonRequest<Task>(Request.Method.POST, url,
				gson, Task.class, listener, error, null) {

			@Override
			public byte[] getBody() throws AuthFailureError {

				return taskUpdateAppendUrl(task, getParamsEncoding());
			}
		};

		agilefantService.addRequest(request);
	}

	@Override
	public void updateStory(final Story story, final Boolean tasksToDone, final Response.Listener<Story> listener,
							final Response.ErrorListener error) {
		final String url = String.format(Locale.US, STORE_STORY_ACTION, agilefantService.getHost());

		final UrlGsonRequest<Story> req = new UrlGsonRequest<Story>(Request.Method.POST, url,
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

		agilefantService.addRequest(req);
	}

	private byte[] taskUpdateAppendUrl(final WorkItem task, final String paramsEncoding) {

		final StringBuilder body = new StringBuilder();
		try {
			for (final User user : task.getResponsibles()) {
				appendURLEncodedParam(body, NEW_RESPONSIBLES, String.valueOf(user.getId()), paramsEncoding);
			}

			appendURLEncodedParam(body, RESPONSIBLES_CHANGED, String.valueOf(true), paramsEncoding);
			appendURLEncodedParam(body, TASK_STATE, task.getState().name(), paramsEncoding);

					/*
					 *  This 2 values, EL and OE, for sending requests it needs # of hours, not in minutes,
					 *  while in the rest of the application this works under minutes.
					 */
			appendURLEncodedParam(body,
					TASK_EFFORT_LEFT, String.valueOf(task.getEffortLeft() / MINUTES), paramsEncoding);
			appendURLEncodedParam(body, TASK_ORIGINAL_ESTIMATE,
					String.valueOf(task.getOriginalEstimate() / MINUTES), paramsEncoding);

			appendURLEncodedParam(body, TASK_ID, String.valueOf(task.getId()), paramsEncoding);

		} catch (final UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}

		return body.toString().getBytes(Charset.forName(paramsEncoding));
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

	/**
	 * Updates the ranks of the items in the source list with the ones in the fallbacklist.
	 *
	 * @param <T> the class to rank
	 * @param source the list to be updated.
	 * @param fallbackList the list containing the values to be updated with.
	 */
	public <T extends Rankable<T>> void rollbackRanks(final List<T> source, final List<T> fallbackList) {

		for (final T currentTaskAt : source) {

			final int indexOfFallbackTask = fallbackList.indexOf(currentTaskAt);
			final T fallbackTask = fallbackList.get(indexOfFallbackTask);

			currentTaskAt.setRank(fallbackTask.getRank());
		}
	}

	/**
	 * This method clones the source items into the fallback list, and updates the ranks of the source list.
	 *
	 * @param <T> the class to rank
	 * @param source the original list.
	 * @param copy the list where cloned items will be added.
	 */
	public <T extends Rankable<T>> void copyAndSetRank(final List<T> source, final List<T> copy) {

		for (int i = 0; i < source.size(); i++) {
			final T itemAt = source.get(i);

			copy.add(itemAt.getCopy());

			// Rank is equal to the index in the list.
			itemAt.setRank(i);
		}
	}

	private GsonRequest<Story> newGsonRequest(final Story story, final Story targetStory, final Long backlogId,
											final String url, final Response.Listener<Story> listener,
											final Response.ErrorListener error) {

		return new GsonRequest<Story>(Request.Method.POST, url, gson, Story.class, listener, error, null) {

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
}
