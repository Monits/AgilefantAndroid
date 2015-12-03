package com.monits.agilefant.service;

import com.android.volley.Response;
import com.monits.agilefant.model.Task;

import java.util.List;

/**
 * Created by rpereyra on 11/9/15.
 */
public interface TaskRankUpdaterService {

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

}
