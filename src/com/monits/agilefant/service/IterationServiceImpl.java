package com.monits.agilefant.service;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.inject.Inject;
import com.monits.agilefant.model.Iteration;

public class IterationServiceImpl implements IterationService {

	@Inject
	private AgilefantService agilefantService;

	@Override
	public void getIteration(final long id, final Listener<Iteration> listener, final ErrorListener error) {
		agilefantService.getIteration(id, listener, error);
	}

}
