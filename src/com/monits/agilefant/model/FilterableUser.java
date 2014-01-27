package com.monits.agilefant.model;

import com.google.gson.annotations.SerializedName;

public class FilterableUser {

	@SerializedName("id")
	private long id;

	@SerializedName("enabled")
	private boolean enabled;

	@SerializedName("matchedString")
	private String matchedString;

	@SerializedName("originalObject")
	private User originalUser;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the matchedString
	 */
	public String getMatchedString() {
		return matchedString;
	}

	/**
	 * @param matchedString the matchedString to set
	 */
	public void setMatchedString(final String matchedString) {
		this.matchedString = matchedString;
	}

	/**
	 * @return the originalUser
	 */
	public User getOriginalUser() {
		return originalUser;
	}

	/**
	 * @param originalUser the originalUser to set
	 */
	public void setOriginalUser(final User originalUser) {
		this.originalUser = originalUser;
	}

}
