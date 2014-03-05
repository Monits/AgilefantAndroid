package com.monits.agilefant.service;

import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.inject.Inject;
import com.monits.agilefant.model.FilterableIteration;
import com.monits.agilefant.model.Iteration;

public class IterationServiceImpl implements IterationService {

	@Inject
	private AgilefantService agilefantService;

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
