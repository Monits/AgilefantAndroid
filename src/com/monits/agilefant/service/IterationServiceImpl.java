package com.monits.agilefant.service;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.parser.IterationParser;

public class IterationServiceImpl implements IterationService{

	@Inject
	private AgilefantService agilefantService;

	@Inject
	private IterationParser iterationParser;

	@Override
	public Iteration getIteration(long id) throws RequestException {
		return iterationParser.iterationParser(agilefantService.getIteration(id));
	}

}
