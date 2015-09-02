package com.monits.agilefant.model;
import android.support.annotation.StringRes;

import com.monits.agilefant.R;
public enum StateKey {

	NOT_STARTED(R.string.state_not_started),
	STARTED(R.string.state_started),
	PENDING(R.string.state_pending),
	BLOCKED(R.string.state_blocked),
	IMPLEMENTED(R.string.state_implemented),
	DONE(R.string.state_done),
	DEFERRED(R.string.state_deferred);

	private int state;

	/**
	 * Constructor.
	 * @param state The state
	 */
	private StateKey(@StringRes final int state) {
		this.state = state;
	}

	/**
	 * @return State
	 */
	@StringRes
	public int getState() {
		return state;
	}

	/**
	 * @return returns a list with the {@link StateKey} values, in human readable form to display.
	 */
	public static String[] getDisplayStates() {
		final StateKey[] stateKeys = StateKey.values();
		final String [] taskStates = new String[stateKeys.length];
		for (int i = 0 ; i < stateKeys.length ; i++) {
			taskStates[i] = stateKeys[i].name();
		}

		return taskStates;
	}

}
