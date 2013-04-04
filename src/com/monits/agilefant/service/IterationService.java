package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Iteration;

public interface IterationService {

	Iteration getIteration(long id) throws RequestException;
}
