package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DailyWork implements Serializable {

	private static final long serialVersionUID = 2241576318943952740L;

	@SerializedName("queuedTasks")
	private List<DailyWorkTask> queuedTasks;

	@SerializedName("stories")
	private List<DailyWorkStory> stories;

	@SerializedName("tasksWithoutStory")
	private List<DailyWorkTask> taskWithoutStories;

	/**
	 * @return the queuedTasks
	 */
	public List<DailyWorkTask> getQueuedTasks() {
		return queuedTasks;
	}

	/**
	 * @param queuedTasks the queuedTasks to set
	 */
	public void setQueuedTasks(List<DailyWorkTask> queuedTasks) {
		this.queuedTasks = queuedTasks;
	}

	/**
	 * @return the stories
	 */
	public List<DailyWorkStory> getStories() {
		return stories;
	}

	/**
	 * @param stories the stories to set
	 */
	public void setStories(List<DailyWorkStory> stories) {
		this.stories = stories;
	}

	/**
	 * @return the taskWithoutStories
	 */
	public List<DailyWorkTask> getTaskWithoutStories() {
		return taskWithoutStories;
	}

	/**
	 * @param taskWithoutStories the taskWithoutStories to set
	 */
	public void setTaskWithoutStories(List<DailyWorkTask> taskWithoutStories) {
		this.taskWithoutStories = taskWithoutStories;
	}
}
