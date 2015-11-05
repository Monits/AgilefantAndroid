package com.monits.agilefant.service;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;

import java.util.List;

public interface DailyWorkService {

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
	 * @param user the user to update the work queue.
	 * @param allTasks the complete list of tasks, to update it's ranks.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void rankDailyTaskUnder(Task task, Task targetTask, User user, List<Task> allTasks,
							Response.Listener<Task> listener, Response.ErrorListener error);
}
