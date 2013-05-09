/**
 * 
 */
package com.monits.agilefant.service;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.DailyWorkStory;
import com.monits.agilefant.model.DailyWorkTask;
import com.monits.agilefant.parser.DailyWorkParser;
import com.monits.agilefant.util.StoryUtils;

/**
 * @author gmuniz
 *
 */
public class DailyWorkServiceImpl implements DailyWorkService {

	@Inject
	private UserService userService;

	@Inject
	private DailyWorkParser dailyWorkParser;

	@Inject
	private AgilefantService agilefantService;

	@Override
	public DailyWork getDailyWork() throws RequestException {
		DailyWork dailyWork =
				dailyWorkParser.parseDailyWork(agilefantService.getDailyWork(userService.getLoggedUser().getId()));

		populateContext(dailyWork);

		return dailyWork;
	}

	/**
	 * This method populates queued tasks which hasn't got it's iteration context setted, but they do have a story context,
	 * with the iteration coming from the story.
	 * 
	 * @param dailyWork
	 */
	private void populateContext(DailyWork dailyWork) {
		if (dailyWork != null) {
			for (DailyWorkTask queuedTask : dailyWork.getQueuedTasks()) {
				if (queuedTask.getIteration() == null && queuedTask.getStory() != null) {
					DailyWorkStory story =
							StoryUtils.findStoryById(dailyWork.getStories(), queuedTask.getStory().getId());

					if (story != null) {
						queuedTask.setIteration(story.getIteration());
					}
				}
			}
		}
	}
}
