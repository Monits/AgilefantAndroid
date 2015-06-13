package com.monits.agilefant.service;


import java.util.Date;
import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.backlog.BacklogElementParameters;

/**
 * Manages metrics in Agilefant
 * @author Ivan Corbalan
 *
 */
public interface MetricsService {

	/**
	 * Change spent Effort of task.
	 *
	 * <blockquote> <b>NOTE:</b> This method will automatically update the current task as if the request was successfull. In case request fails, this changes will be rollbacked.</blockquote>
	 *
	 * @param date Date
	 * @param minutesSpent Minutes to enter
	 * @param description Description or comment
	 * @param task the task to be modified
	 * @param userId User id
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void taskChangeSpentEffort(Date date, long minutesSpent, String description, Task task, long userId, Listener<String> listener, ErrorListener error);

	/**
	 * Change state of task
	 *
	 * <blockquote> <b>NOTE:</b> This method will automatically update the current task as if the request was successfull. In case request fails, this changes will be rollbacked.</blockquote>
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
	 * <blockquote> <b>NOTE:</b> This method will automatically update the current task as if the request was successfull. In case request fails, this changes will be rollbacked.</blockquote>
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
	 *<blockquote> <b>NOTE:</b> This method will automatically update the current story as if the request was successfull. In case request fails, this changes will be rollbacked.</blockquote>
	 *
	 * @param state State to set
	 * @param story the story to be modified.
	 * @param tasksToDone whether if all tasks
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void changeStoryState(StateKey state, Story story, boolean tasksToDone, Listener<Story> listener, ErrorListener error);

	/**
	 * Changes the story's responsibles.
	 *
	 *<blockquote> <b>NOTE:</b> This method will automatically update the current story as if the request was successfull. In case request fails, this changes will be rollbacked.</blockquote>
	 *
	 * @param responsibles the responsibles to set.
	 * @param state State to set
	 * @param story the story to be modified.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void changeStoryResponsibles(List<User> responsibles, Story story, Listener<Story> listener, ErrorListener error);

	/**
	 * Changes the task's responsibles.
	 *
	 *<blockquote> <b>NOTE:</b> This method will automatically update the current task as if the request was successfull. In case request fails, this changes will be rollbacked.</blockquote>
	 *
	 * @param responsibles the responsibles to set.
	 * @param state State to set
	 * @param task the task to be modified.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void changeTaskResponsibles(List<User> responsibles, Task task, Listener<Task> listener, ErrorListener error);

	/**
	 * Change story's iteration
	 *
	 * <blockquote> <b>NOTE:</b> This method will automatically update the current story as if the request was successfull. In case request fails, this changes will be rollbacked.</blockquote>
	 *
	 * @param story the Story to be modified
	 * @param iteration the Iteration to modify the Story
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void moveStory(Story story, Iteration iteration, Listener<Story> listener, ErrorListener error);

	/**
	 * Updates Task's rank under the given target task.
	 *
	 * <blockquote> <b>NOTE:</b> This method will automatically update all task's rank as if the request was successfull. In case request fails, this changes will be rollbacked.</blockquote>
	 *
	 * @param task the task to be updated in rank.
	 * @param targetTask the task to be ranked under.
	 * @param allTasks the complete list of tasks, to update it's ranks.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void rankTaskUnder(Task task, Task targetTask, List<Task> allTasks, Listener<Task> listener, ErrorListener error);

	/**
	 * Updates Story's rank lower than the given target story.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain above the given story in rank.
	 * @param allStories the complete list of stories, to update it's ranks.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryUnder(Story story, Story targetStory, List<Story> allStories, Listener<Story> listener, ErrorListener error);

	/**
	 * Updates Story's rank lower than the given target story. Useable when sorting project leaf stories.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain above the given story in rank.
	 * @param allStories the complete list of stories, to update it's ranks.
	 * @param backlogId the id of the backlog where this sorting is taking place.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryUnder(Story story, Story targetStory, Long backlogId, List<Story> allStories, Listener<Story> listener, ErrorListener error);

	/**
	 * Updates Story's rank higher than the given target story.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain below the given story in rank.
	 * @param allStories the complete list of stories, to update it's ranks.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryOver(Story story, Story targetStory, List<Story> allStories, Listener<Story> listener, ErrorListener error);

	/**
	 * Updates Story's rank higher than the given target story. Useable when sorting project leaf stories.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain below the given story in rank.
	 * @param allStories the complete list of stories, to update it's ranks.
	 * @param backlogId the id of the backlog where this sorting is taking place.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryOver(Story story, Story targetStory, Long backlogId, List<Story> allStories, Listener<Story> listener, ErrorListener error);

	/**
	 * Create Story
	 *
	 * @param parameters values of story
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void createStory(BacklogElementParameters parameters, Listener<Story> listener, ErrorListener error);

	/**
	 * Create TaskWithoutStory
	 *
	 * @param parameters the values to create task without story
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void createTask(BacklogElementParameters parameters, Listener<Task> listener, ErrorListener errorListener);
}