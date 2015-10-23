/**
 *
 */
package com.monits.agilefant.service;

import android.support.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.StoryUtils;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.util.Locale;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * @author gmuniz
 *
 */
public class DailyWorkServiceImpl implements DailyWorkService {


	private static final int DAILYWORK_REATTEMPT = 1;
	private static final float DAILYWORK_TIMEOUT_SECOND_ATTEMPT = 1.5f;
	private static final int DAILYWORK_TIMEOUT = 30000;
	private static final String DAILY_WORK_ACTION = "%1$s/ajax/dailyWorkData.action?userId=%2$d";

	private final UserService userService;

	private final AgilefantService agilefantService;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	/**
	 * @param agilefantService Injected via constructor by Dagger
	 * @param userService Injected via constructor by Dagger
	 * @param gson Injected Gson Object
	 */

	@Inject
	public DailyWorkServiceImpl(final AgilefantService agilefantService,
								final UserService userService, final Gson gson) {
		this.agilefantService = agilefantService;
		this.userService = userService;
		this.gson = gson;
	}

	@Override
	public void getDailyWork(final Listener<DailyWork> listener, final ErrorListener error) {

		final String url = String.format(Locale.US, DAILY_WORK_ACTION, agilefantService.getHost(),
				userService.getLoggedUser().getId());

		final GsonRequest<DailyWork> request = new GsonRequest<>(
				Request.Method.GET, url, gson, DailyWork.class,
				new Listener<DailyWork>() {

					@Override
					public void onResponse(final DailyWork response) {
						populateContext(response);
						listener.onResponse(response);
					}
				}, error, null);

		/*
		 * Default Volley timeout is too low and the request to get the
		 * DailyWork of a user can maybe take a little more time.
		 * 30 secs timeout, 1 reattempt, 45 secs timeout for the second attempt
		 */
		request.setRetryPolicy(
				new DefaultRetryPolicy(DAILYWORK_TIMEOUT, DAILYWORK_REATTEMPT, DAILYWORK_TIMEOUT_SECOND_ATTEMPT));

		agilefantService.addRequest(request);

	}

	/**
	 * This method populates queued tasks which hasn't got it's iteration context setted,
	 * but they do have a story context, with the iteration coming from the story.
	 *
	 * @param dailyWork The daily work
	 */
	private void populateContext(@NonNull final DailyWork dailyWork) {

		for (final Task queuedTask : dailyWork.getQueuedTasks()) {
			if (queuedTask.getIteration() == null && queuedTask.getStory() != null) {
				final Story story =
						StoryUtils.findStoryById(dailyWork.getStories(), queuedTask.getStory().getId());

				if (story != null) {
					queuedTask.setIteration(story.getIteration());
				}
			}
		}
	}
}
