package com.monits.agilefant.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Model of iteration
 * @author Ivan Corbalan
 *
 */
public class Iteration implements Serializable{

	private static final long serialVersionUID = -182238804828185878L;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	/**
	 * Constructor
	 * @param id The iteration id
	 * @param title The iteration title
	 */
	public Iteration(int id, String title) {
		this.id = id;
		this.title = title;
	}

	/**
	 * @return The iteration id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id The iteration id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return The iteration title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The iteration title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}