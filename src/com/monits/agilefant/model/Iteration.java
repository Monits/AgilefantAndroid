package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Model of iteration
 * @author Ivan Corbalan
 *
 */
public class Iteration implements Serializable {

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

	@SerializedName("parent")
	private Backlog parent;

	/**
	 * Constructor
	 * @param id The iteration id
	 * @param title The iteration title
	 */
	public Iteration(final long id, final String title) {
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
	public void setId(final long id) {
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
	public void setTitle(final String title) {
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
	public void setName(final String name) {
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
	public void setStories(final List<Story> stories) {
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
	public void setStartDate(final long startDate) {
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
	public void setEndDate(final long endDate) {
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
	public void setTasksWithoutStory(final List<Task> tasksWithoutStory) {
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
	public void setRootIteration(final RootIteration rootIteration) {
		this.rootIteration = rootIteration;
	}

	public Backlog getParent() {
		return parent;
	}

	public void setParent(final Backlog parent) {
		this.parent = parent;
	}
}