package com.monits.agilefant.model;

import android.annotation.SuppressLint;

import java.util.Locale;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class UserChooser {

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "We need to ignore this field due to native method calling")
	private long id;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "We need to ignore this field due to native method calling")
	private boolean enabled;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "We need to ignore this field due to native method calling")
	private String matchedString;

	private String name;

	/**
	 * Default constructor.
	 */
	public UserChooser() {
		// Default constructor.
	}

	/**
	 * Constructor
	 * @param id The id.
	 * @param enabled True if it's enabled.
	 * @param matchedString The string that matched the user.
	 * @param name The name
	 */
	public UserChooser(final long id, final boolean enabled, final String matchedString, final String name) {
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
	 * Checks if there a partial match.
	 * @param filterString The partial string
	 * @return True if there a partial match, false otherwise.
	 */
	@SuppressLint("DefaultLocale")
	public boolean match(final String filterString) {
		return matchedString.toLowerCase(Locale.getDefault()).contains(filterString.toLowerCase(Locale.getDefault()));
	}

	@Override
	public String toString() {
		// This value is shown when in landscape and using autocomplete.
		return name;
	}
}
