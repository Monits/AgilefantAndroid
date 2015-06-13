package com.monits.agilefant.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.monits.agilefant.R;
import com.monits.agilefant.model.State;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.User;

public class IterationUtils {

	private static final String NONE = "(none)";

	private static Map<StateKey, State> states;

	static {
		states = new HashMap<StateKey, State>();
		states.put(StateKey.IMPLEMENTED, new State(StateKey.IMPLEMENTED.getState(), R.drawable.state_light_green, android.R.color.black));
		states.put(StateKey.BLOCKED, new State(StateKey.BLOCKED.getState(), R.drawable.state_red, android.R.color.white));
		states.put(StateKey.PENDING, new State(StateKey.PENDING.getState(), R.drawable.state_cyan, android.R.color.black));
		states.put(StateKey.DONE, new State(StateKey.DONE.getState(), R.drawable.state_green, android.R.color.white));
		states.put(StateKey.NOT_STARTED, new State(StateKey.NOT_STARTED.getState(), R.drawable.state_gray, android.R.color.black));
		states.put(StateKey.STARTED, new State(StateKey.STARTED.getState(), R.drawable.state_yellow, android.R.color.black));
		states.put(StateKey.DEFERRED, new State(StateKey.DEFERRED.getState(), R.drawable.state_black, android.R.color.white));
	}

	public static String getStateName (StateKey state) {
		return states.get(state).getName();
	}

	public static int getStateBackground(StateKey state) {
		return states.get(state).getBackgroundId();
	}

	public static int getStateTextColor(StateKey state) {
		return states.get(state).getTextColorId();
	}

	public static String getResposiblesDisplay (List<User> users) {
		if (users == null || users.isEmpty()) {
			return NONE;
		} else {
			StringBuilder sb = new StringBuilder();

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
