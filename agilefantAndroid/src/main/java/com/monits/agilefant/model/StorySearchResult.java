package com.monits.agilefant.model;

public class StorySearchResult {
	private final int storyId;
	private final int iterationId;

	/**
	 * Default Constructor
	 *
	 * @param storyId Result story id
	 * @param iterationId Result iteration id
	 */
	public StorySearchResult(final int storyId, final int iterationId) {
		this.iterationId = iterationId;
		this.storyId = storyId;
	}

	public int getStoryId() {
		return storyId;
	}

	public int getIterationId() {
		return iterationId;
	}

	@Override
	public String toString() {
		return "StorySearchResult{"
				+ "storyId=" + storyId
				+ ", iterationId=" + iterationId
				+ '}';
	}
}
