package com.monits.agilefant.parser;

import com.google.gson.Gson;
import com.monits.agilefant.model.Project;

public class ProjectParserImpl implements ProjectParser {

	@Override
	public Project parseProject(final String json) {
		final Gson gson = new Gson();
		return gson.fromJson(json, Project.class);
	}

}
