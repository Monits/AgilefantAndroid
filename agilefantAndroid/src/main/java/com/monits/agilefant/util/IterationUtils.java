package com.monits.agilefant.util;

import android.support.annotation.StringRes;

import java.util.EnumMap;
import java.util.List;

import com.monits.agilefant.R;
import com.monits.agilefant.model.State;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.User;

public final class IterationUtils {

	private static final String NONE = "(none)";

	private static EnumMap<StateKey, State> states;

	static {
		states = new EnumMap<>(StateKey.class);
		states.put(StateKey.IMPLEMENTED,
			new State(StateKey.IMPLEMENTED.getState(), R.drawable.state_light_green, android.R.color.black));
		states.put(StateKey.BLOCKED,
			new State(StateKey.BLOCKED.getState(), R.drawable.state_red, android.R.color.white));
		states.put(StateKey.PENDING,
			new State(StateKey.PENDING.getState(), R.drawable.state_cyan, android.R.color.black));
		states.put(StateKey.DONE,
			new State(StateKey.DONE.getState(), R.drawable.state_green, android.R.color.white));
		states.put(StateKey.NOT_STARTED,
			new State(StateKey.NOT_STARTED.getState(), R.drawable.state_gray, android.R.color.black));
		states.put(StateKey.STARTED,
			new State(StateKey.STARTED.getState(), R.drawable.state_yellow, android.R.color.black));
		states.put(StateKey.DEFERRED,
			new State(StateKey.DEFERRED.getState(), R.drawable.state_black, android.R.color.white));
	}

	private IterationUtils() {
		throw new AssertionError("Utility classes should not been instantiated");
	}

	/**
	 * Return the name of the given state
	 * @param state The state
	 * @return the name of the given state
	 */
	@StringRes
	public static int getStateName(final StateKey state) {
		return states.get(state).getName();
	}

	/**
	 * Return the background id for the given state
	 * @param state the state
	 * @return the background id for the given state
	 */
	public static int getStateBackground(final StateKey state) {
		return states.get(state).getBackgroundId();
	}

	/**
	 * Return the text color id for the given state
	 * @param state The state
	 * @return the text color id for the given state
	 */
	public static int getStateTextColor(final StateKey state) {
		return states.get(state).getTextColorId();
	}

	/**
	 * Return the string representation for a list of users
	 * @param users The users
	 * @return the string representation for a list of users
	 */
	public static String getResposiblesDisplay(final List<User> users) {
		if (users == null || users.isEmpty()) {
			return NONE;
		} else {
			final StringBuilder sb = new StringBuilder();

			for (int i = 0; i < users.size(); i++) {
				sb.append(users.get(i).getInitials());
				if (i < users.size() - 1) {
					sb.append(", ");
				}
			}
			return sb.toString();
		}
	}

}
