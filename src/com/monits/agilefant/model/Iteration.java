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
	private List<Story> stories;

	@SerializedName("startDate")
	private long startDate;

	@SerializedName("endDate")
	private long endDate;

	@SerializedName("tasks")
	private List<Task> tasksWithoutStory;

	@SerializedName("root")
	private RootIteration rootIteration;

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
	public List<Story> getStories() {
		return stories;
	}

	/**
	 * @param stories the stories to set
	 */
	public void setStories(List<Story> stories) {
		this.stories = stories;
	}

	/**
	 * @return the startDate
	 */
	public long getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public long getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the tasksWithoutStory
	 */
	public List<Task> getTasksWithoutStory() {
		return tasksWithoutStory;
	}

	/**
	 * @param tasksWithoutStory the tasksWithoutStory to set
	 */
	public void setTasksWithoutStory(List<Task> tasksWithoutStory) {
		this.tasksWithoutStory = tasksWithoutStory;
	}

	/**
	 * @return the rootIteration
	 */
	public RootIteration getRootIteration() {
		return rootIteration;
	}

	/**
	 * @param rootIteration the rootIteration to set
	 */
	public void setRootIteration(RootIteration rootIteration) {
		this.rootIteration = rootIteration;
	}
}