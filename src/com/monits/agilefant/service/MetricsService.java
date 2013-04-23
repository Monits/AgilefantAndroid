package com.monits.agilefant.service;


import java.util.Date;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;

/**
 * Manages metrics in Agilefant
 * @author Ivan Corbalan
 *
 */
public interface MetricsService {

	/**
	 * Change spent Effort of task
	 * @param date Date
	 * @param minutesSpent Minutes to enter
	 * @param description Description or comment
	 * @param taskId Task id
	 * @param userId User id
	 * @throws RequestException
	 */
	void taskChangeSpentEffort(Date date, long minutesSpent, String description, long taskId, long userId) throws RequestException;

	/**
	 * Change state of task
	 * @param state State to enter
	 * @param taskId Task id
	 * @return Updated task
	 * @throws RequestException
	 */
	Task taskChangeState(StateKey state, long taskId) throws RequestException;

	/**
	 * Change effort Left of task
	 * @param effortLeft Hour to enter
	 * @param taskId Task id
	 * @return Updated task
	 * @throws RequestException
	 */
	Task changeEffortLeft(int effortLeft, long taskId) throws RequestException;

	/**
	 * Change original estimate of task
	 * @param origalEstimate Hour to enter
	 * @param taskId Task id
	 * @return Updated Task
	 * @throws RequestException
	 */
	Task changeOriginalEstimate(int origalEstimate, long taskId) throws RequestException;
}