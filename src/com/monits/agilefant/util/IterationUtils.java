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

	private static final String READY_VALUE = "Ready";

	private static final String BLOCKED_VALUE = "Blocked";

	private static final String PENDING_VALUE = "Pending";

	private static final String DONE_VALUE = "Done";

	private static final String NOT_STARTED_VALUE = "Not Started";

	private static final String STARTED_VALUE = "Started";

	private static final String DEFERRED_VALUE = "Deferred";

	private static Map<StateKey, State> states;

	static {
		states = new HashMap<StateKey, State>();
		states.put(StateKey.IMPLEMENTED, new State(READY_VALUE, R.drawable.state_light_green, android.R.color.black));
		states.put(StateKey.BLOCKED, new State(BLOCKED_VALUE, R.drawable.state_red, android.R.color.white));
		states.put(StateKey.PENDING, new State(PENDING_VALUE, R.drawable.state_cyan, android.R.color.black));
		states.put(StateKey.DONE, new State(DONE_VALUE, R.drawable.state_green, android.R.color.white));
		states.put(StateKey.NOT_STARTED, new State(NOT_STARTED_VALUE, R.drawable.state_gray, android.R.color.black));
		states.put(StateKey.STARTED, new State(STARTED_VALUE, R.drawable.state_yellow, android.R.color.black));
		states.put(StateKey.DEFERRED, new State(DEFERRED_VALUE, R.drawable.state_black, android.R.color.white));
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
		if (users.isEmpty()) {
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
