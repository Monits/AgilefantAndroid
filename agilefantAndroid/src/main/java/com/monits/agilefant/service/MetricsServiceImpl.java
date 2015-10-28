package com.monits.agilefant.service;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.volleyrequests.network.request.RfcCompliantListenableRequest;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

/**
 * Manages metrics in Agilefant
 * @author Ivan Corbalan
 *
 */
public class MetricsServiceImpl implements MetricsService {

	private static final String PARENT_OBJECT_ID = "parentObjectId";
	private static final String HOUR_ENTRY_DESCRIPTION = "hourEntry.description";
	private static final String HOUR_ENTRY_MINUTES_SPENT = "hourEntry.minutesSpent";
	private static final String HOUR_ENTRY_DATE = "hourEntry.date";
	private static final String LOG_TASK_EFFORT_ACTION = "%1$s/ajax/logTaskEffort.action";
	private static final String USER_IDS = "userIds";
	private static final int MINUTES = 60;

	private final AgilefantService agilefantService;
	private final WorkItemService workItemService;

	/**
	 * @param agilefantService Injected via constructor by Dagger
	 * @param workItemService Injected via constructor by Dagger
	 */
	@Inject
	public MetricsServiceImpl(final AgilefantService agilefantService, final WorkItemService workItemService) {
		this.agilefantService = agilefantService;
		this.workItemService = workItemService;
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

		workItemService.updateTask(
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

		workItemService.updateTask(
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

		workItemService.updateStory(
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
}