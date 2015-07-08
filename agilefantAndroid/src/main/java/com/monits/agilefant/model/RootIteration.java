package com.monits.agilefant.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class RootIteration implements Serializable {

	private static final long serialVersionUID = 6685530221513814790L;

	@SerializedName("name")
	private String name;

	/**
	 * Default constuctor.
	 */
	public RootIteration() {
		// Default constructor.
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return new StringBuilder("RootIteration [name: ").append(name).append(']').toString();
	}
}
