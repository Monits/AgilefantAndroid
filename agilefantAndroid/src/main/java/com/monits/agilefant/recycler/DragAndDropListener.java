package com.monits.agilefant.recycler;

import com.monits.agilefant.model.WorkItem;

/**
 *
 * Created by Raul Morales on 10/1/15.
 */
public interface DragAndDropListener {
	/**
	 * Notifies that an item is being dragged
	 *
	 * @param fromPosition The original position of the item
	 * @param toPosition The new position of the item
	 */
	void onMove(int fromPosition, int toPosition);

	/**
	 * Notifies that an item has been dropped and it's new position should be committed
	 *
	 * @param fromPosition The original position of the item
	 * @param toPosition The new position of the item
	 */
	void onChangePosition(int fromPosition, int toPosition);

	/**
	 * Returns the object that is in the position that is passed
	 * @param position The position of the item
	 * @return the Workitem
	 */
	WorkItem getItem(int position);
}
