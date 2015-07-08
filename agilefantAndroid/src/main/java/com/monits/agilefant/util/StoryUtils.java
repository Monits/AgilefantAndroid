package com.monits.agilefant.util;

import java.util.List;

import com.monits.agilefant.model.Story;

public final class StoryUtils {

	private StoryUtils() {
		throw new AssertionError("Utility classes should not been instantiated");
	}

	/**
	 * Searches the story in the given list that matches the given id.
	 * 
	 * @param stories the stories to look after.
	 * @param id the id from the story.
	 * 
	 * @return the story, null otherwise.
	 */
	public static Story findStoryById(final List<Story> stories, final long id) {
		for (final Story story : stories) {
			if (story.getId() == id) {
				return story;
			}
		}

		return null;
	}
}
