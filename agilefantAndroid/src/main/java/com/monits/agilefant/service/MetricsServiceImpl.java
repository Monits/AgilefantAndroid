package com.monits.agilefant.service;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Rankable;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.network.request.UrlGsonRequest;
import com.monits.volleyrequests.network.request.RfcCompliantListenableRequest;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Manages metrics in Agilefant
 * @author Ivan Corbalan
 *
 */
public class MetricsServiceImpl implements MetricsService {

	private static final String STORE_TASK_ACTION = "%1$s/ajax/storeTask.action";
	private static final String NEW_RESPONSIBLES = "newResponsibles";
	private static final String RESPONSIBLES_CHANGED = "responsiblesChanged";
	private static final String TASK_ID = "taskId";
	private static final String TASK_STATE = "task.state";
	private static final String TASK_ORIGINAL_ESTIMATE = "task.originalEstimate";
	private static final String TASK_EFFORT_LEFT = "task.effortLeft";

	private static final String PARENT_OBJECT_ID = "parentObjectId";
	private static final String HOUR_ENTRY_DESCRIPTION = "hourEntry.description";
	private static final String HOUR_ENTRY_MINUTES_SPENT = "hourEntry.minutesSpent";
	private static final String HOUR_ENTRY_DATE = "hourEntry.date";
	private static final String LOG_TASK_EFFORT_ACTION = "%1$s/ajax/logTaskEffort.action";
	private static final String USER_IDS = "userIds";


	public static final int MINUTES = 60;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	private final AgilefantService agilefantService;

	/**
	 * @param agilefantService Injected via constructor by Dagger
	 * @param gson Injected via constructor by Dagger
	 */

	@Inject
	public MetricsServiceImpl(final AgilefantService agilefantService,
							final Gson gson) {
		this.agilefantService = agilefantService;
		this.gson = gson;
	}

	@Override
	public void taskChangeSpentEffort(final Date date, final long minutesSpent, final String description,
									final Task task, final long userId,
									final Listener<String> listener, final ErrorListener error) {

		final Task fallbackTask = task.getCopy();
		task.setEffortSpent(task.getEffortSpent() + minutesSpent);

		final String url = String.format(Locale.US, LOG_TASK_EFFORT_ACTION, agilefantService.getHost());

		final RfcCompliantListenableRequest<String> request = new RfcCompliantListenableRequest<String>(
				Request.Method.POST, url, listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				final Map<String, String> params = new HashMap<>();

				params.put(HOUR_ENTRY_DATE, String.valueOf(date.getTime()));
				params.put(HOUR_ENTRY_MINUTES_SPENT, String.valueOf(minutesSpent));
				params.put(HOUR_ENTRY_DESCRIPTION, description);
				params.put(PARENT_OBJECT_ID, String.valueOf(task.getId()));
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

		agilefantService.addRequest(request);
	}

	@Override
	public void taskChangeState(final StateKey state, final Task task, final Listener<Task> listener,
								final ErrorListener error) {
		final Task fallbackTask = task.getCopy();
		task.setState(state, true);

		updateTask(
				task,
				new Listener<Task>() {

					@Override
					public void onResponse(final Task response) {
						task.updateValues(response);

						listener.onResponse(response);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void changeEffortLeft(final double effortLeft, final Task task, final Listener<Task> listener,
								final ErrorListener error) {

		// Updating current task prior to sending request to the API
		final Task fallbackTask = task.getCopy();
		task.setEffortLeft((long) (effortLeft * MINUTES));

		updateTask(
				task,
				new Listener<Task>() {

					@Override
					public void onResponse(final Task response) {
						task.updateValues(response);

						listener.onResponse(response);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				});
	}

	private void updateTask(final Task task, final Listener<Task> listener,
							final ErrorListener error) {

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

	@Override
	public void changeStoryState(final StateKey state, final Story story, final boolean tasksToDone,
								final Listener<Story> listener, final ErrorListener error) {
		final Story fallbackStory = story.getCopy();
		final List<Task> currentTasks = story.getTasks();

		story.setState(state);
		if (tasksToDone) {
			for (final Task task : currentTasks) {
				task.setState(StateKey.DONE, true);
			}
		}

		agilefantService.updateStory(
				story,
				tasksToDone,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						story.setState(fallbackStory.getState());
						if (tasksToDone) {
							final List<Task> fallbackTasks = fallbackStory.getTasks();
							for (int i = 0; i < currentTasks.size(); i++) {
								currentTasks.get(i).setState(fallbackTasks.get(i).getState());
							}
						}

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void changeStoryResponsibles(final List<User> responsibles, final Story story,
										final Listener<Story> listener, final ErrorListener error) {

		final Story fallbackStory = story.getCopy();
		story.setResponsibles(responsibles);

		agilefantService.updateStory(
				story,
				null,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						story.updateValues(fallbackStory);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void changeTaskResponsibles(final List<User> responsibles, final Task task, final Listener<Task> listener,
									final ErrorListener error) {

		final Task fallbackTask = task.getCopy();
		task.setResponsibles(responsibles);

		updateTask(task,
				new Listener<Task>() {

					@Override
					public void onResponse(final Task response) {
						task.updateValues(response);

						listener.onResponse(response);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void moveStory(final Story story, final Iteration iteration, final Listener<Story> listener,
						final ErrorListener error) {

		final Story fallbackStory = story.getCopy();
		story.setIteration(iteration);

		final long backlogId;
		if (iteration == null) {
			backlogId = story.getBacklog().getId();
		} else {
			backlogId = iteration.getId();
		}

		agilefantService.moveStory(
				backlogId,
				story,
				new Listener<Story>() {

					@Override
					public void onResponse(final Story storyOk) {
						// The story from the response is incomplete, sending the one we have updated.
						listener.onResponse(story);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						story.setIteration(fallbackStory.getIteration());

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void rankTaskUnder(final Task task, final Task targetTask, final List<Task> allTasks,
							final Listener<Task> listener, final ErrorListener error) {

		final List<Task> fallbackTasksList = new LinkedList<>();
		copyAndSetRank(allTasks, fallbackTasksList);

		agilefantService.rankTaskUnder(
				task,
				targetTask,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allTasks, fallbackTasksList);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void rankStoryOver(final Story story, final Story targetStory, final List<Story> allStories,
							final Listener<Story> listener, final ErrorListener error) {

		rankStoryOver(story, targetStory, null, allStories, listener, error);
	}

	@Override
	public void rankStoryUnder(final Story story, final Story targetStory, final List<Story> allStories,
							final Listener<Story> listener, final ErrorListener error) {
		rankStoryUnder(story, targetStory, null, allStories, listener, error);
	}

	@Override
	public void rankStoryUnder(final Story story, final Story targetStory, final Long backlogId,
							final List<Story> allStories, final Listener<Story> listener, final ErrorListener error) {

		final List<Story> fallbackStoryList = new LinkedList<>();
		copyAndSetRank(allStories, fallbackStoryList);

		agilefantService.rankStoryUnder(
				story,
				targetStory,
				backlogId,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allStories, fallbackStoryList);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void rankStoryOver(final Story story, final Story targetStory, final Long backlogId,
							final List<Story> allStories, final Listener<Story> listener, final ErrorListener error) {

		final List<Story> fallbackStoryList = new LinkedList<>();
		copyAndSetRank(allStories, fallbackStoryList);

		agilefantService.rankStoryOver(
				story,
				targetStory,
				backlogId,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allStories, fallbackStoryList);

						error.onErrorResponse(arg0);
					}
				});
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
	public void createStory(final BacklogElementParameters parameters, final Listener<Story> listener,
							final ErrorListener error) {
		agilefantService.createStory(parameters, listener, error);
	}

	@Override
	public void createTask(final BacklogElementParameters parameters, final Listener<Task> listener,
						final ErrorListener errorListener) {
		agilefantService.createTask(parameters, listener, errorListener);
	}
}