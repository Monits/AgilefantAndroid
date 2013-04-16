package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Iteration;

public interface IterationService {

	/**
	 * Get Iteration details
	 * @param id Iteration id
	 * @return A Iteration
	 * @throws RequestException
	 */
	Iteration getIteration(long id) throws RequestException;
}
