package com.monits.agilefant.model;

import com.google.gson.annotations.SerializedName;

public class DailyWorkTask extends Task {

	private static final long serialVersionUID = 1776185057890916752L;

	@SerializedName("iteration")
	private Iteration iteration;

	@SerializedName("story")
	private Context story;

	/**
	 * @return the story
	 */
	public Context getStory() {
		return story;
	}

	/**
	 * @param story the story to set
	 */
	public void setStory(Context story) {
		this.story = story;
	}

	/**
	 * @return the iteration
	 */
	public Iteration getIteration() {
		return iteration;
	}

	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(Iteration iteration) {
		this.iteration = iteration;
	}
}
