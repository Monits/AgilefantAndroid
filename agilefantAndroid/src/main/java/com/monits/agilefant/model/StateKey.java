package com.monits.agilefant.model;

public enum StateKey {

	NOT_STARTED("Not Started"),
	STARTED("In Progress"),
	PENDING("Pending"),
	BLOCKED("Blocked"),
	IMPLEMENTED("Ready"),
	DONE("Done"),
	DEFERRED("Deferred");

	private String state;

	/**
	 * Constructor.
	 * @param state The state
	 */
	private StateKey(final String state) {
		this.state = state;
	}

	/**
	 * @return State
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return returns a list with the {@link StateKey} values, in human readable form to display.
	 */
	public static String[] getDisplayStates() {
		final StateKey[] stateKeys = StateKey.values();
		final String [] taskStates = new String[stateKeys.length];
		for (int i = 0 ; i < stateKeys.length ; i++) {
			taskStates[i] = stateKeys[i].getState();
		}

		return taskStates;
	}

}
