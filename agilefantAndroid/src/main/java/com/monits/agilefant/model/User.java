package com.monits.agilefant.model;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class User implements Serializable {

	private static final long serialVersionUID = -642609196033074436L;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "We need to ignore this field due to native method calling")
	@SerializedName("id")
	private long id;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING",
			justification = "We need to ignore this field due to native method calling")
	@SerializedName("initials")
	private String initials;

	@SerializedName("fullName")
	private String fullName;

	/**
	 * Default constructor.
	 */
	public User() {
		// Default constructor.
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
	 * @return the initials
	 */
	public String getInitials() {
		return initials;
	}

	/**
	 * @param initials the initials to set
	 */
	public void setInitials(final String initials) {
		this.initials = initials;
	}

	/**
	 * @return the users fullname
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Set the users fullname
	 * @param fullName The fullname to set
	 */
	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}


	@Override
	public String toString() {
		// This value is shown when in landscape and using autocomplete.

		return fullName;
	}
}