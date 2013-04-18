package com.monits.agilefant.parser;

import com.monits.agilefant.model.Task;

/**
 * Parser for Agilefant tasks
 * @author Ivan Corbalan
 *
 */
public interface TaskParser {

	/**
	 * Task Parser
	 * @param json String in JSON format
	 * @return The task
	 */
	Task taskParser(String json);
}
