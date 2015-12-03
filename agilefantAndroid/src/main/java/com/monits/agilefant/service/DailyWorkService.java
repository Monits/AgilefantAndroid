package com.monits.agilefant.service;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.Story;

public interface DailyWorkService extends TaskRankUpdaterService {

	/**
	 * Retrieves the daily work.
	 *
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getDailyWork(Listener<DailyWork> listener, ErrorListener error);

	/**
	 * Updates target Story's rank under the dragged story
	 *
	 * @param story Dragged story
	 * @param storyRankUnderId Target Story
	 * @param userId Logged user id
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void rankMyStoryUnder(final Story story, final Story storyRankUnderId, final Long userId,
						final Response.Listener<Story> listener, final Response.ErrorListener error);
}
