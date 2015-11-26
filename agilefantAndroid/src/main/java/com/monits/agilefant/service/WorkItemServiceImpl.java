package com.monits.agilefant.service;

import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.TaskTypeRank;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.network.request.UrlGsonRequest;
import com.monits.agilefant.service.rank.RankService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Created by edipasquale on 27/10/15.
 */
public class WorkItemServiceImpl extends RankService implements WorkItemService {

	private static final String STORE_TASK_ACTION = "%1$s/ajax/storeTask.action";
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
	private static final String RANK_STORY_UNDER_ACTION = "%1$s/ajax/rankStoryUnder.action";
	private static final String RANK_STORY_OVER_ACTION = "%1$s/ajax/rankStoryOver.action";
	private static final String TARGET_STORY_ID = "targetStoryId";

	private final AgilefantService agilefantService;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	/**
	 * @param agilefantService AgilefantService injected by Dagger
	 * @param gson Gson injected by Dagger
	 */
	@Inject
	public WorkItemServiceImpl(final AgilefantService agilefantService, final Gson gson) {
		super(agilefantService, gson);

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

	// TODO : Remove this unused method
	@Override
	public void rankTaskUnder(final Task task, final Task targetTask, final List<Task> allTasks,
							final Response.Listener<Task> listener, final Response.ErrorListener error) {
		final Map<String, String> params = new HashMap<>();

		params.put(ITERATION_ID, String.valueOf(task.getIteration().getId()));

		rankTaskUnder(TaskTypeRank.TASK_WITHOUT_STORY, params, allTasks, task, targetTask, listener, error);
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

		final String url = urlFormat(RANK_STORY_UNDER_ACTION);

		final UrlGsonRequest<Story> request = newGsonRequest(story, targetStory, backlogId, url, listener,
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

		final String url = urlFormat(RANK_STORY_OVER_ACTION);

		final UrlGsonRequest<Story> request = newGsonRequest(story, targetStory, backlogId, url, listener,
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
		final String url = urlFormat(STORY_CREATE);

		final UrlGsonRequest<Story> request = new UrlGsonRequest<Story>(
				Request.Method.POST, url, gson, Story.class, listener, error, null) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return getCreateStoryParams(parameters);
			}
		};

		agilefantService.addRequest(request);
	}

	private Map<String, String> getCreateStoryParams(final BacklogElementParameters parameters) {
		final Map<String, String> params = new HashMap<>();
		final Long iterationId = parameters.getIterationId();
		final Long backlogId;
		if (parameters.getBacklogId() == null && iterationId != null) {
			backlogId = iterationId;
		} else {
			backlogId = parameters.getBacklogId();
		}

		params.put(BACKLOG_ID, String.valueOf(backlogId));

		if (iterationId != null) {
			params.put(ITERATION, String.valueOf(iterationId));
		}

		params.put(STORY_DESCRIPTION, "");
		params.put(STORY_NAME, parameters.getName());
		params.put(STORY_STATE, String.valueOf(parameters.getStateKey()));
		params.put(STORY_STORY_POINTS, "");
		params.put(STORY_STORY_VALUE, "");

		for (final User user : parameters.getSelectedUser()) {
			params.put(USER_IDS, String.valueOf(user.getId()));
		}

		params.put(USERS_CHANGED, String.valueOf(true));

		return params;
	}

	@Override
	public void createTask(final BacklogElementParameters parameters, final Response.Listener<Task> listener,
						final Response.ErrorListener errorListener) {
		final String url = urlFormat(TASK_CREATE);

		final UrlGsonRequest<Task> request = new UrlGsonRequest<Task>(
				Request.Method.POST, url, gson,
				Task.class, listener, errorListener, null) {

			@Override
			public Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<>();
				params.put(ITERATION_ID, String.valueOf(parameters.getIterationId()));

				for (final User user : parameters.getSelectedUser()) {
					params.put(NEW_RESPONSIBLES, String.valueOf(user.getId()));
				}

				params.put(RESPONSIBLES_CHANGED, String.valueOf(true));
				params.put(TASK_EFFORT_LEFT, "");
				params.put(TASK_NAME, parameters.getName());
				params.put(TASK_ORIGINAL_ESTIMATE, "");
				params.put(TASK_STATE, String.valueOf(parameters.getStateKey()));

				return params;
			}
		};

		agilefantService.addRequest(request);
	}

	@Override
	public void updateTask(final Task task, final Response.Listener<Task> listener,
							final Response.ErrorListener error) {

		final String url = urlFormat(STORE_TASK_ACTION);

		final UrlGsonRequest<Task> request = new UrlGsonRequest<Task>(Request.Method.POST, url,
				gson, Task.class, listener, error, null) {

			@Override
			public Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<>();

				for (final User user : task.getResponsibles()) {
					params.put(NEW_RESPONSIBLES, String.valueOf(user.getId()));
				}

				params.put(RESPONSIBLES_CHANGED, String.valueOf(true));
				params.put(TASK_STATE, task.getState().name());

				/*
				 *  This 2 values, EL and OE, for sending requests it needs # of hours, not in minutes,
				 *  while in the rest of the application this works under minutes.
				 */
				params.put(TASK_EFFORT_LEFT, String.valueOf(task.getEffortLeft() / MINUTES));
				params.put(TASK_ORIGINAL_ESTIMATE, String.valueOf(task.getOriginalEstimate() / MINUTES));

				params.put(TASK_ID, String.valueOf(task.getId()));

				return params;
			}
		};

		agilefantService.addRequest(request);
	}

	@Override
	public void updateStory(final Story story, final Boolean tasksToDone, final Response.Listener<Story> listener,
							final Response.ErrorListener error) {
		final String url = urlFormat(STORE_STORY_ACTION);

		final UrlGsonRequest<Story> req = new UrlGsonRequest<Story>(Request.Method.POST, url,
				gson, Story.class, listener, error, null) {

			@Override
			public Map<String, String> getParams() throws AuthFailureError {
				return getUpdateStoryParams(story, tasksToDone);
			}
		};

		agilefantService.addRequest(req);

	}

	@NonNull
	private Map<String, String> getUpdateStoryParams(final Story story, final Boolean tasksToDone) {
		final Map<String, String> params = new HashMap<>();
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

		for (final User user : story.getResponsibles()) {
			params.put(USER_IDS, String.valueOf(user.getId()));
		}

		params.put(USERS_CHANGED, String.valueOf(true));
		params.put(STORY_ID, String.valueOf(story.getId()));
		params.put(STORY_STATE, story.getState().name());
		params.put(BACKLOG_ID, String.valueOf(backlogId));

		if (iterationId != null) {
			params.put(ITERATION_ID, String.valueOf(iterationId));
		}

		if (tasksToDone != null) {
			params.put(TASKS_TO_DONE, String.valueOf(tasksToDone));
		}

		return params;
	}

	private UrlGsonRequest<Story> newGsonRequest(final Story story, final Story targetStory, final Long backlogId,
												final String url, final Response.Listener<Story> listener,
												final Response.ErrorListener error) {

		return new UrlGsonRequest<Story>(Request.Method.POST, url, gson, Story.class, listener, error, null) {

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
