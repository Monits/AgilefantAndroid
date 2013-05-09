package com.monits.agilefant.util;

import java.util.List;

import com.monits.agilefant.model.DailyWorkStory;

public class StoryUtils {

	/**
	 * Searches the story in the given list that matches the given id.
	 * 
	 * @param stories the stories to look after.
	 * @param id the id from the story.
	 * 
	 * @return the story, null otherwise.
	 */
	public static DailyWorkStory findStoryById(List<DailyWorkStory> stories, long id) {
		for (DailyWorkStory story : stories) {
			if (story.getId() == id) {
				return story;
			}
		}

		return null;
	}
}
