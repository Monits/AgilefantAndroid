package com.monits.agilefant.service;


import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;

import java.util.Date;

/**
 * Manages metrics in Agilefant
 * @author Ivan Corbalan
 *
 */
public interface MetricsService {

	/**
	 * Change spent Effort of task.
	 *
	 * <blockquote>
	 *     <b>NOTE:</b> This method will automatically update the current task as if the request was successfull.
	 *     In case request fails, this changes will be rollbacked.
	 * </blockquote>
	 *
	 * @param date Date
	 * @param minutesSpent Minutes to enter
	 * @param description Description or comment
	 * @param task the task to be modified
	 * @param userId User id
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void taskChangeSpentEffort(Date date, long minutesSpent, String description, Task task, long userId,
							Listener<String> listener, ErrorListener error);

	/**
	 * Change state of task
	 *
	 * <blockquote>
	 *     <b>NOTE:</b> This method will automatically update the current task as if the request was successfull.
	 *     In case request fails, this changes will be rollbacked.
	 * </blockquote>
	 *
	 * @param state State to enter
	 * @param task the task to be modified.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void taskChangeState(StateKey state, Task task, Listener<Task> listener, ErrorListener error);

	/**
	 * Change effort Left of task
	 *
	 * <blockquote>
	 *     <b>NOTE:</b> This method will automatically update the current task as if the request was successfull.
	 *     In case request fails, this changes will be rollbacked.
	 * </blockquote>
	 *
	 * @param effortLeft Hour to enter
	 * @param task the task to be modified.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void changeEffortLeft(double effortLeft, Task task, Listener<Task> listener, ErrorListener error);

	/**
	 * Changes the story state.
	 *
	 *<blockquote>
	 *      <b>NOTE:</b> This method will automatically update the current story as if the request was successfull.
	 *      In case request fails, this changes will be rollbacked.
	 *</blockquote>
	 *
	 * @param state State to set
	 * @param story the story to be modified.
	 * @param tasksToDone whether if all tasks
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void changeStoryState(StateKey state, Story story, boolean tasksToDone, Listener<Story> listener,
						ErrorListener error);
}