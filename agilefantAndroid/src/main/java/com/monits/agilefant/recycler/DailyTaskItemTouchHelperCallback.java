package com.monits.agilefant.recycler;

import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.model.WorkItemType;

public class DailyTaskItemTouchHelperCallback extends WorkItemTouchHelperCallback {

	/**
	 * Constructor
	 *
	 * @param listener The Adapter to implement DragAndDropListener
	 */
	public DailyTaskItemTouchHelperCallback(final DragAndDropListener listener) {
		super(listener);
	}

	protected boolean canMoveOver(final WorkItem originalWorkItem, final WorkItem targetWorkItem) {
		return originalWorkItem.getType() == WorkItemType.TASK && targetWorkItem.getType() == WorkItemType.TASK;
	}
}
