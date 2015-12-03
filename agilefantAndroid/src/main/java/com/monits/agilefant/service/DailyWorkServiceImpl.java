/**
 *
 */
package com.monits.agilefant.service;

import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.TaskTypeRank;
import com.monits.agilefant.network.request.UrlGsonRequest;
import com.monits.agilefant.service.rank.RankService;
import com.monits.agilefant.util.DailyWorkRankComparator;
import com.monits.agilefant.util.StoryUtils;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * @author gmuniz
 *
 */
public class DailyWorkServiceImpl extends RankService implements DailyWorkService {


	private static final int DAILYWORK_REATTEMPT = 1;
	private static final float DAILYWORK_TIMEOUT_SECOND_ATTEMPT = 1.5f;
	private static final int DAILYWORK_TIMEOUT = 30000;
	private static final String DAILY_WORK_ACTION = "%1$s/ajax/dailyWorkData.action?userId=%2$d";
	private static final String RANK_MY_STORIES_UNDER = "%1$s/ajax/rankMyStoryAndMoveUnder.action";
	private static final String STORY_RANK_UNDER_ID = "storyRankUnderId";
	private static final String USER_ID = "userId";
	private static final String STORY_ID = "storyId";

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
	public DailyWorkServiceImpl(final AgilefantService agilefantService, final UserService userService,
								final Gson gson) {
		super(agilefantService, gson);

		this.agilefantService = agilefantService;
		this.userService = userService;
		this.gson = gson;
	}

	@Override
	public void getDailyWork(final Listener<DailyWork> listener, final ErrorListener error) {

		final String url = String.format(Locale.getDefault(), DAILY_WORK_ACTION,
				agilefantService.getHost(), userService.getLoggedUser().getId());

		final GsonRequest<DailyWork> request = new GsonRequest<>(
				Request.Method.GET, url, gson, DailyWork.class,
				new Listener<DailyWork>() {

					@Override
					public void onResponse(final DailyWork response) {
						populateNullData(response);

						// Ranks it's stories by workQueueRank property
						Collections.sort(response.getStories(), DailyWorkRankComparator.INSTANCE);

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

	@Override
	public void rankMyStoryUnder(final Story story, final Story rankStoryUnder, final Long userId, final Response
			.Listener<Story> listener, final Response.ErrorListener error) {

		final UrlGsonRequest<Story> request = new UrlGsonRequest<Story>(Request.Method.POST,
				urlFormat(RANK_MY_STORIES_UNDER), gson, Story.class,
				listener, error, null) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				final String rankStoryUnderId = rankStoryUnder == null ? "-1"
						: String.valueOf(rankStoryUnder.getId());

				final Map<String, String> params = new HashMap<>();
				params.put(STORY_ID, String.valueOf(story.getId()));
				params.put(STORY_RANK_UNDER_ID, rankStoryUnderId);
				params.put(USER_ID, String.valueOf(userId));

				return params;
			}
		};

		agilefantService.addRequest(request);
	}

	@Override
	public void rankTaskUnder(final Task task, final Task targetTask, final List<Task> allTasks,
								final Response.Listener<Task> listener, final Response.ErrorListener error) {

		final Map<String, String> params = new HashMap<>();

		params.put(USER_ID, String.valueOf(userService.getLoggedUser().getId()));

		rankTaskUnder(TaskTypeRank.DAILY_TASK, params, allTasks, task, targetTask, listener, error);
	}

	/**
	 * This method populates null data from received dailywork,
	 * @param dailyWork The daily work
	 */
	private void populateNullData(@NonNull final DailyWork dailyWork) {

		/**
		 * Populates queued tasks which hasn't got it's iteration context setted
		 * but they do have a story context, with it's  iteration coming from the story
		 */
		for (final Task queuedTask : dailyWork.getQueuedTasks()) {
			if (queuedTask.getIteration() == null && queuedTask.getStory() != null) {
				final Story story =
						StoryUtils.findStoryById(dailyWork.getStories(), queuedTask.getStory().getId());

				if (story != null) {
					queuedTask.setIteration(story.getIteration());
				}
			}
		}

		/**
		 * Populates parent story to all those tasks which had came with it's parent setted
		 * as null
		 */
		for (final Story story : dailyWork.getStories()) {
			for (final Task task : story.getTasks()) {
				if (task.getStory() == null) {
					task.setStory(story);
				}
			}
		}
	}
}
