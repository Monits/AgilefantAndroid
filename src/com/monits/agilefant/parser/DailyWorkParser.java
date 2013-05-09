package com.monits.agilefant.parser;

import com.monits.agilefant.model.DailyWork;

public interface DailyWorkParser {

	/**
	 * Parses {@link DailyWork} in JSON format into object.
	 * 
	 * @param string {@link DailyWork} in JSON format
	 * @return
	 */
	DailyWork parseDailyWork(String string);
}
