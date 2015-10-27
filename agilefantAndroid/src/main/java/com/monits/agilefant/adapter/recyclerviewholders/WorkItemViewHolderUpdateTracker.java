package com.monits.agilefant.adapter.recyclerviewholders;

import com.monits.agilefant.model.WorkItem;

/**
 * Created by edipasquale on 01/10/15.
 */
public interface WorkItemViewHolderUpdateTracker {

	/**
	 * @param updatedTask Receives the updated Task object
	 */
	void onUpdate(WorkItem updatedTask);
}
