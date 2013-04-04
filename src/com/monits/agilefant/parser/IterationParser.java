package com.monits.agilefant.parser;

import com.monits.agilefant.model.Iteration;

public interface IterationParser {

	Iteration iterationParser(String iterationJson);
}
