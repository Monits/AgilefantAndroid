package com.monits.agilefant.parser;

import com.monits.agilefant.model.Project;

public interface ProjectParser {

	/**
	 * Parses a Project from a JSON in string form, containing a single item.
	 *
	 * @param json JSON in string format.
	 * @return a {@link Project} object.
	 */
	Project parseProject(String json);
}
