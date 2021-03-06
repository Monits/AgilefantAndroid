package com.monits.agilefant.model;

import android.annotation.SuppressLint;

public class FilterableUser extends UserChooser {

	private User user;

	/**
	 * Default constructor.
	 */
	public FilterableUser() {
		// Default constructor.
	}

	/**
	 * Constructor.
	 * @param id The id.
	 * @param enabled Flag to see if it's enabled
	 * @param matchedString The string that matched the user
	 * @param name The name
	 * @param user The user
	 */
	public FilterableUser(final long id, final boolean enabled,
			final String matchedString, final String name, final User user) {
		super(id, enabled, matchedString, name);
		this.user = user ;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(final User user) {
		this.user = user;
	}

	/**
	 * Checks if there a partial match or with your initials.
	 * @param filterString The partial string
	 * @return True if there a partial match or with your initials, false otherwise.
	 */
	@Override
	@SuppressLint("DefaultLocale")
	public boolean match(final String filterString) {
		final String initials = getUser().getInitials();
		return super.match(filterString) || initials != null && initials.equalsIgnoreCase(filterString);
	}

	@Override
	public String toString() {
		return new StringBuilder("FilterableUser [user: ").append(user).append(']').toString();
	}
}
