package com.monits.agilefant.recycler;

/**
 *
 * Created by Raul Morales on 10/1/15.
 */
public interface DragAndDropListener {
	/**
	 * Notifies that an item is being dragged
	 *
	 * @param fromPosition The original position of the task
	 * @param toPosition The new position of the task
	 */
	void onMove(int fromPosition, int toPosition);

	/**
	 * Notifies that an item has been dropped and it's new position should be committed
	 *
	 * @param fromPosition The original position of the task
	 * @param toPosition The new position of the task
	 */
	void onChangePosition(int fromPosition, int toPosition);
}
