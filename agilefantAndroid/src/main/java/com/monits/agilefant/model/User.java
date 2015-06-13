package com.monits.agilefant.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class User implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -642609196033074436L;

	@SerializedName("id")
	private long id;

	@SerializedName("initials")
	private String initials;

	@SerializedName("fullName")
	private String fullName;

	public User() {
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}

	@Override
	public String toString() {
		// This value is shown when in landscape and using autocomplete.
		return fullName;
	}
}