package com.monits.agilefant.parser;

import com.google.gson.Gson;
import com.monits.agilefant.model.Task;

/**
 * Parser for Agilefant tasks
 * @author Ivan Corbalan
 *
 */
public class TaskParserImpl implements TaskParser {

	@Override
	public Task taskParser(String json) {
		Task task = null;
		Gson gson = new Gson();

		task = gson.fromJson(json, Task.class);
		return task;
	}

}
