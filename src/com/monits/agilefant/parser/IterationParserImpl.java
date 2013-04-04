package com.monits.agilefant.parser;

import com.google.gson.Gson;
import com.monits.agilefant.model.Iteration;

public class IterationParserImpl implements IterationParser{

	@Override
	public Iteration iterationParser(String iterationJson) {
		Iteration iteration = null;
		Gson gson = new Gson();

		iteration = gson.fromJson(iterationJson, Iteration.class);
		return iteration;
	}

}
