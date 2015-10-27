package com.monits.agilefant.model;

import java.util.List;

/**
 * Interface for items that models an item in a particular backlog.
 */
public interface WorkItem {

	/**
	 * @return long The id of the item.
	 */
	long getId();

	/**
	 * @return String The name of the item.
	 */
	String getName();

	/**
	 * @return StateKey The state of the item.
	 */
	StateKey getState();

	/**
	 * @return int The rank of the item.
	 */
	int getRank();

	/**
	 * @return List of User List of the responsible users.
	 */
	List<User> getResponsibles();

	/**
	 * @return long The effort left.
	 */
	long getEffortLeft();

	/**
	 * @return long The effort spent.
	 */
	long getEffortSpent();

	/**
	 * @return long The original estimate.
	 */
	long getOriginalEstimate();

	/**
	 * Work item type
	 * @return return the type of the work item
	 */
	WorkItemType getType();

	/**
	 * This is a convenience to update multiple values at once and to notify changes only once, to avoid
	 * views to render multiple times.
	 *
	 * @param workItem the updated item.
	 */
	void updateValues(final WorkItem workItem);
}
