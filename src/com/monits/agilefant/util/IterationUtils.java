package com.monits.agilefant.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.monits.agilefant.R;
import com.monits.agilefant.model.State;
import com.monits.agilefant.model.User;

public class IterationUtils {

	private static final String NONE = "(none)";
	private static final String READY_KEY = "IMPLEMENTED";
	private static final String READY_VALUE = "Ready";

	private static final String BLOCKED_KEY = "BLOCKED";
	private static final String BLOCKED_VALUE = "Blocked";

	private static final String PENDING_KEY = "PENDING";
	private static final String PENDING_VALUE = "Pending";

	private static final String DONE_KEY = "DONE";
	private static final String DONE_VALUE = "Done";

	private static final String NOT_STARTED_KEY = "NOT_STARTED";
	private static final String NOT_STARTED_VALUE = "Not Started";

	private static final String STARTED_KEY = "STARTED";
	private static final String STARTED_VALUE = "Started";

	private static final String DEFERRED_KEY = "DEFERRED";
	private static final String DEFERRED_VALUE = "Deferred";

	private static Map<String, State> states;

	static {
		states = new HashMap<String, State>();
		states.put(READY_KEY, new State(READY_VALUE, R.drawable.state_light_green, android.R.color.black));
		states.put(BLOCKED_KEY, new State(BLOCKED_VALUE, R.drawable.state_red, android.R.color.white));
		states.put(PENDING_KEY, new State(PENDING_VALUE, R.drawable.state_cyan, android.R.color.black));
		states.put(DONE_KEY, new State(DONE_VALUE, R.drawable.state_green, android.R.color.white));
		states.put(NOT_STARTED_KEY, new State(NOT_STARTED_VALUE, R.drawable.state_gray, android.R.color.black));
		states.put(STARTED_KEY, new State(STARTED_VALUE, R.drawable.state_yellow, android.R.color.black));
		states.put(DEFERRED_KEY, new State(DEFERRED_VALUE, R.drawable.state_black, android.R.color.white));
	}

	public static String getStateName (String state) {
		return states.get(state).getName();
	}

	public static int getStateBackground(String state) {
		return states.get(state).getBackgroundId();
	}

	public static int getStateTextColor(String state) {
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
