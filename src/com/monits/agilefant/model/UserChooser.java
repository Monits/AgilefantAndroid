package com.monits.agilefant.model;

public class UserChooser {

	private long id;

	private boolean enabled;

	private String matchedString;

	private String name;

	public UserChooser() {
	}

	public UserChooser(final long id, final boolean enabled,
			final String matchedString, final String name) {
		this.id = id;
		this.enabled = enabled;
		this.matchedString = matchedString;
		this.name = name;
	}

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

}