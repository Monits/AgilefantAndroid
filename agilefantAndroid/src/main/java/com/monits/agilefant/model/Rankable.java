package com.monits.agilefant.model;

public interface Rankable<T> {

	/**
	 * Retrieves the rank.
	 *
	 * @return the rank.
	 */
	int getRank();

	/**
	 * Sets the rank.
	 * @param rank the rank to set
	 */
	void setRank(int rank);

	/**
	 * @return Return a copy of the this object.
	 */
	T getCopy();
}
