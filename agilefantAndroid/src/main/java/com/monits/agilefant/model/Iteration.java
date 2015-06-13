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

	/**
	 *
	 */
	private static final long serialVersionUID = 4014161739613202291L;

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
	 * Default constructor
	 */
	public Iteration() {
	}

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
	 * @return The iteration name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name The iteration name to set
	 */
	public void setName(final String name) {
		this.name = name;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Iteration other = (Iteration) obj;
		if (id != other.id)
			return false;
		return true;
	}

}