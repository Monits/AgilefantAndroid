package com.monits.agilefant.service;

import com.android.volley.Response;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.backlog.BacklogElementParameters;

import java.util.List;

/**
 * Created by edipasquale on 27/10/15.
 */
public interface WorkItemService {

	/**
	 * Changes the story's responsibles.
	 *
	 *<blockquote>
	 *      <b>NOTE:</b> This method will automatically update the current story as if the request was successfull.
	 *      In case request fails, this changes will be rollbacked.
	 *</blockquote>
	 *
	 * @param responsibles the responsibles to set.
	 * @param story the story to be modified.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void changeStoryResponsibles(List<User> responsibles, Story story, Response.Listener<Story> listener,
								Response.ErrorListener error);

	/**
	 * Changes the task's responsibles.
	 *
	 *<blockquote>
	 *      <b>NOTE:</b> This method will automatically update the current task as if the request was successfull.
	 *      In case request fails, this changes will be rollbacked.
	 *</blockquote>
	 *
	 * @param responsibles the responsibles to set.
	 * @param task the task to be modified.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void changeTaskResponsibles(List<User> responsibles, Task task, Response.Listener<Task> listener,
								Response.ErrorListener error);

	/**
	 * Change story's iteration
	 *
	 * <blockquote>
	 *     <b>NOTE:</b> This method will automatically update the current story as if the request was successfull.
	 *     In case request fails, this changes will be rollbacked.
	 * </blockquote>
	 *
	 * @param story the Story to be modified
	 * @param iteration the Iteration to modify the Story
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void moveStory(Story story, Iteration iteration, Response.Listener<Story> listener, Response.ErrorListener error);

	/**
	 * Updates Task's rank under the given target task.
	 *
	 * <blockquote>
	 *     <b>NOTE:</b> This method will automatically update all task's rank as if the request was successfull.
	 *     In case request fails, this changes will be rollbacked.
	 * </blockquote>
	 *
	 * @param task the task to be updated in rank.
	 * @param targetTask the task to be ranked under.
	 * @param allTasks the complete list of tasks, to update it's ranks.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void rankTaskUnder(Task task, Task targetTask, List<Task> allTasks, Response.Listener<Task> listener,
					Response.ErrorListener error);

	/**
	 * Updates Story's rank lower than the given target story.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain above the given story in rank.
	 * @param allStories the complete list of stories, to update it's ranks.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryUnder(Story story, Story targetStory, List<Story> allStories, Response.Listener<Story> listener,
						Response.ErrorListener error);

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
	void rankStoryUnder(Story story, Story targetStory, Long backlogId, List<Story> allStories,
						Response.Listener<Story> listener, Response.ErrorListener error);

	/**
	 * Updates Story's rank higher than the given target story.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain below the given story in rank.
	 * @param allStories the complete list of stories, to update it's ranks.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryOver(Story story, Story targetStory, List<Story> allStories, Response.Listener<Story> listener,
					Response.ErrorListener error);

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
	void rankStoryOver(Story story, Story targetStory, Long backlogId, List<Story> allStories,
					Response.Listener<Story> listener, Response.ErrorListener error);

	/**
	 * Create Story
	 *
	 * @param parameters values of story
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void createStory(BacklogElementParameters parameters, Response.Listener<Story> listener,
					Response.ErrorListener error);

	/**
	 * Create TaskWithoutStory
	 *
	 * @param parameters the values to create task without story
	 * @param listener callback if the request was successful
	 * @param errorListener callback if the request failed
	 */
	void createTask(BacklogElementParameters parameters, Response.Listener<Task> listener,
					Response.ErrorListener errorListener);

	/**
	 * Updates task data
	 * @param task the updated task
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	// TODO : Exists the possibility of bypassing its functionality to Metrics Service and get the same results
	void updateTask(Task task, Response.Listener<Task> listener, Response.ErrorListener error);

	/**
	 * Changes the story state.
	 *
	 * @param story the story to update.
	 * @param tasksToDone whether if all tasks should be also set as done.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	// TODO : Exists the possibility of bypassing its functionality to Metrics Service and get the same results
	void updateStory(Story story, Boolean tasksToDone, Response.Listener<Story> listener,
					Response.ErrorListener error);
}
