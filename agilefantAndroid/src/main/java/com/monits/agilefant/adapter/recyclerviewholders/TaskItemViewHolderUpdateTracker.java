package com.monits.agilefant.adapter.recyclerviewholders;

import com.monits.agilefant.model.Task;

/**
 * Created by edipasquale on 01/10/15.
 */
public interface TaskItemViewHolderUpdateTracker {

	/**
	 * @param updatedTask Receives the updated Task object
	 */
	void onUpdate(Task updatedTask);
}
