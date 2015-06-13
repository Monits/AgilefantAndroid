package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DailyWork implements Serializable {

	private static final long serialVersionUID = 2241576318943952740L;

	@SerializedName("queuedTasks")
	private List<Task> queuedTasks;

	@SerializedName("stories")
	private List<Story> stories;

	@SerializedName("tasksWithoutStory")
	private List<Task> taskWithoutStories;

	/**
	 * @return the queuedTasks
	 */
	public List<Task> getQueuedTasks() {
		return queuedTasks;
	}

	/**
	 * @param queuedTasks the queuedTasks to set
	 */
	public void setQueuedTasks(final List<Task> queuedTasks) {
		this.queuedTasks = queuedTasks;
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
	 * @return the taskWithoutStories
	 */
	public List<Task> getTaskWithoutStories() {
		return taskWithoutStories;
	}

	/**
	 * @param taskWithoutStories the taskWithoutStories to set
	 */
	public void setTaskWithoutStories(final List<Task> taskWithoutStories) {
		this.taskWithoutStories = taskWithoutStories;
	}
}
