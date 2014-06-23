package com.monits.agilefant.model;

public class FilterableUser extends UserChooser {

	private User user;

	public FilterableUser() {
	}

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

}