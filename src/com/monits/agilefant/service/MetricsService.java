package com.monits.agilefant.service;

import java.sql.Date;

import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;

public interface MetricsService {

	/**
	 * Change spent Effort of task
	 * @param date Date
	 * @param minutesSpent Minutes to enter
	 * @param description Description or comment
	 * @param taskId Task id
	 * @param userId User id
	 * @return If changed spent effort
	 */
	boolean taskChangeSpentEffort(Date date, long minutesSpent, String description, long taskId, long userId);

	/**
	 * Change state of task
	 * @param state State to enter
	 * @param taskId Task id
	 * @return Updated task
	 */
	Task taskChangeState(StateKey state, long taskId);

	/**
	 * Change effort Left of task
	 * @param effortLeft Hour to enter
	 * @param taskId Task id
	 * @return Updated task
	 */
	Task changeEffortLeft(int effortLeft, long taskId);

	/**
	 * Change original estimate of task
	 * @param origalEstimate Hour to enter
	 * @param taskId Task id
	 * @return Updated Task
	 */
	Task changeOriginalEstimate(int origalEstimate, long taskId);
}