/**
 * 
 */
package com.monits.agilefant.parser;

import com.monits.agilefant.model.Story;

/**
 * @author gmuniz
 *
 */
public interface StoryParser {

	/**
	 * Parses a story from string in JSON format.
	 * 
	 * @param json the json to be parsed.
	 * @return the parsed story.
	 */
	Story parseStory(String json);
}
