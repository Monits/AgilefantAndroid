package com.monits.agilefant.model;

public interface Rankable<T> extends Cloneable {

	/**
	 * Retrieves the rank.
	 *
	 * @return the rank.
	 */
	public int getRank();

	/**
	 * Sets the rank.
	 */
	public void setRank(int rank);

	public T clone();
}
