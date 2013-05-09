package com.monits.agilefant.parser;

import com.google.gson.Gson;
import com.monits.agilefant.model.DailyWork;

public class DailyWorkParserImpl implements DailyWorkParser {

	@Override
	public DailyWork parseDailyWork(String string) {
		Gson gson = new Gson();
		return gson.fromJson(string, DailyWork.class);
	}

}
