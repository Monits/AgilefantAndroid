package com.monits.agilefant.service;


import java.util.Date;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;

/**
 * Manages metrics in Agilefant
 * @author Ivan Corbalan
 *
 */
public class MetricsServiceImpl implements MetricsService {

	@Inject
	private AgilefantService agilefantService;

	@Override
	public void taskChangeSpentEffort(final Date date, final long minutesSpent,
			final String description, final long taskId, final long userId) throws RequestException {
		agilefantService.taskChangeSpentEffort(date.getTime(), minutesSpent, description, taskId, userId);
	}

	@Override
	public Task taskChangeState(final StateKey state, final long taskId) throws RequestException {
		final Task task = agilefantService.taskChangeState(state, taskId);
		return task;
	}

	@Override
	public Task changeEffortLeft(final double effortLeft, final long taskId) throws RequestException {
		final Task task = agilefantService.changeEffortLeft(effortLeft, taskId);
		return task;
	}

	@Override
	public Task changeOriginalEstimate(final int origalEstimate, final long taskId) throws RequestException {
		final Task task = agilefantService.changeOriginalEstimate(origalEstimate, taskId);
		return task;
	}

	@Override
	public Story changeStoryState(final StateKey state, final long storyId, final long backlogId, final long iterationId, final boolean tasksToDone)
			throws RequestException {

		return agilefantService.changeStoryState(state, storyId, backlogId, iterationId, tasksToDone);
	}

}