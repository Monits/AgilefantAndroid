package com.monits.agilefant.model;

public enum StateKey {

	IMPLEMENTED("IMPLEMENTED"),
	BLOCKED("BLOCKED"),
	PENDING("PENDING"),
	DONE("DONE"),
	NOT_STARTED("NOT_STARTED"),
	STARTED("STARTED"),
	DEFERRED("DEFERRED");

	private String state;

	/**
	 * Constructor.
	 * @param state
	 */
	private StateKey (String state){
		this.state = state;
	}

	/**
	 * @return State
	 */
	public String getState() {
		return state;
	}
}
