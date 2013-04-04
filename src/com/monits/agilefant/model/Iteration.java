package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Model of iteration
 * @author Ivan Corbalan
 *
 */
public class Iteration implements Serializable{

	private static final long serialVersionUID = -182238804828185878L;

	@SerializedName("id")
	private long id;

	@SerializedName("title")
	private String title;

	@SerializedName("name")
	private String name;

	@SerializedName("rankedStories")
	private List<Storie> stories;

	/**
	 * Constructor
	 * @param id The iteration id
	 * @param title The iteration title
	 */
	public Iteration(long id, String title) {
		this.id = id;
		this.title = title;
	}

	/**
	 * @return The iteration id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id The iteration id to set
	 */
	public void setId(long id) {
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the stories
	 */
	public List<Storie> getStories() {
		return stories;
	}

	/**
	 * @param stories the stories to set
	 */
	public void setStories(List<Storie> stories) {
		this.stories = stories;
	}
}