package com.monits.agilefant.model;

import com.google.gson.annotations.SerializedName;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class FilterableIteration {

	@SerializedName("id")
	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "The toString is used to display the Auto completions")
	private long id;

	@SerializedName("enabled")
	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "The toString is used to display the Auto completions")
	private boolean enabled;

	@SerializedName("matchedString")
	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "The toString is used to display the Auto completions")
	private String matchedString;

	@SerializedName("name")
	private String name;

	@SerializedName("originalObject")
	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "The toString is used to display the Auto completions")
	private Iteration iteration;

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

	/**
	 * @return the iteration
	 */
	public Iteration getIteration() {
		return iteration;
	}

	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(final Iteration iteration) {
		this.iteration = iteration;
	}

	@Override
	public String toString() {
		// This value is shown when in landscape and using autocomplete.
		return name;
	}
}