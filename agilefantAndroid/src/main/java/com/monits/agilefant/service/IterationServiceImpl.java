package com.monits.agilefant.service;

import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.FilterableIteration;
import com.monits.agilefant.model.Iteration;

import javax.inject.Inject;


public class IterationServiceImpl implements IterationService {

	private final AgilefantService agilefantService;

	/**
	 * @param agilefantService Injected via constructor by Dagger
	 */

	@Inject
	public IterationServiceImpl(final AgilefantService agilefantService) {
		this.agilefantService = agilefantService;
	}

	@Override
	public void getIteration(final long id, final Listener<Iteration> listener, final ErrorListener error) {
		agilefantService.getIteration(id, listener, error);
	}

	@Override
	public void getCurrentFilterableIterations(
			final Listener<List<FilterableIteration>> listener, final ErrorListener error) {

		agilefantService.getCurrentFilterableIterations(listener, error);
	}

}
