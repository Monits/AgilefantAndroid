/**
 *
 */
package com.monits.agilefant.service;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.inject.Inject;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.StoryUtils;

/**
 * @author gmuniz
 *
 */
public class DailyWorkServiceImpl implements DailyWorkService {

	@Inject
	private UserService userService;

	@Inject
	private AgilefantService agilefantService;

	@Override
	public void getDailyWork(final Listener<DailyWork> listener, final ErrorListener error) {

		agilefantService.getDailyWork(
			userService.getLoggedUser().getId(),
			new Listener<DailyWork>() {

				@Override
				public void onResponse(final DailyWork response) {

					populateContext(response);

					listener.onResponse(response);
				}
			}, error);

	}

	/**
	 * This method populates queued tasks which hasn't got it's iteration context setted,
	 * but they do have a story context, with the iteration coming from the story.
	 *
	 * @param dailyWork The daily work
	 */
	private void populateContext(final DailyWork dailyWork) {
		if (dailyWork != null) {
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
}
