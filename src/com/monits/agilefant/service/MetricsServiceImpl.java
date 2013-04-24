package com.monits.agilefant.service;


import java.util.Date;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.parser.TaskParser;

/**
 * Manages metrics in Agilefant
 * @author Ivan Corbalan
 *
 */
public class MetricsServiceImpl implements MetricsService {

	@Inject
	private AgilefantService agilefantService;

	@Inject
	private TaskParser taskParser;

	@Override
	public void taskChangeSpentEffort(Date date, long minutesSpent,
			String description, long taskId, long userId) throws RequestException {
		agilefantService.taskChangeSpentEffort(date.getTime(), minutesSpent, description, taskId, userId);
	}

	@Override
	public Task taskChangeState(StateKey state, long taskId) throws RequestException {
		Task task = taskParser.taskParser(agilefantService.taskChangeState(state, taskId));
		return task;
	}

	@Override
	public Task changeEffortLeft(double effortLeft, long taskId) throws RequestException {
		Task task = taskParser.taskParser(agilefantService.changeEffortLeft(effortLeft, taskId));
		return task;
	}

	@Override
	public Task changeOriginalEstimate(int origalEstimate, long taskId) throws RequestException {
		Task task = taskParser.taskParser(agilefantService.changeOriginalEstimate(origalEstimate, taskId));
		return task;
	}
}