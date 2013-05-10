/**
 * 
 */
package com.monits.agilefant.parser;

import com.google.gson.Gson;
import com.monits.agilefant.model.Story;


/**
 * @author gmuniz
 *
 */
public class StoryParserImpl implements StoryParser {

	@Override
	public Story parseStory(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Story.class);
	}
}
