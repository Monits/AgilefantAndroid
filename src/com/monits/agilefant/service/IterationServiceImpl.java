package com.monits.agilefant.service;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Iteration;

public class IterationServiceImpl implements IterationService {

	@Inject
	private AgilefantService agilefantService;

	@Override
	public Iteration getIteration(final long id) throws RequestException {
		return agilefantService.getIteration(id);
	}

}
