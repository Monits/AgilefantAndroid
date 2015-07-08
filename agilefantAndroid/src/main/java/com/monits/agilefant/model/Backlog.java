package com.monits.agilefant.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Backlog implements Serializable {

	private static final long serialVersionUID = -2908515098376303451L;

	@SerializedName("id")
	private long id;

	@SerializedName("name")
	private String name;

	/**
	 * Default constructor.
	 */
	public Backlog() {
		// Default constructor.
	}

	/**
	 * Constructor.
	 * @param project To be used to get the project data
	 */
	public Backlog(final Project project) {
		this.id = project.getId();
		this.name = project.getTitle();
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

	@Override
	public String toString() {
		return new StringBuilder("Backlog [id: ").append(id).append(", name: ").append(name).append(']').toString();
	}
}
