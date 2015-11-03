package com.monits.agilefant.model;
import android.content.Context;
import android.support.annotation.StringRes;

import com.monits.agilefant.R;
public enum StateKey {

	NOT_STARTED(R.string.state_not_started, R.string.state_not_started_full),
	STARTED(R.string.state_started, R.string.state_started_full),
	PENDING(R.string.state_pending, R.string.state_pending_full),
	BLOCKED(R.string.state_blocked, R.string.state_blocked_full),
	IMPLEMENTED(R.string.state_implemented, R.string.state_implemented_full),
	DONE(R.string.state_done, R.string.state_done_full),
	DEFERRED(R.string.state_deferred, R.string.state_deferred_full);

	private int state;
	private int fullname;

	/**
	 * Constructor.
	 * @param state The state
	 */
	private StateKey(@StringRes final int state, @StringRes final int fullname) {
		this.state = state;
		this.fullname = fullname;
	}

	/**
	 * @return State
	 */
	@StringRes
	public int getState() {
		return state;
	}

	@StringRes
	public int getFullname() {
		return fullname;
	}

	/**
	 * @param context application context for getting resources
	 * @return returns a list with the {@link StateKey} values, in human readable form to display.
	 */
	public static String[] getDisplayStates(final Context context) {
		final StateKey[] stateKeys = StateKey.values();
		final String [] taskStates = new String[stateKeys.length];
		for (int i = 0 ; i < stateKeys.length ; i++) {
			taskStates[i] = context.getResources().getString(stateKeys[i].getFullname());
		}

		return taskStates;
	}

}
