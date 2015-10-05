package com.monits.agilefant.service;

import java.util.Collections;
import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.FilterableIteration;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.util.RankComparator;

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
		agilefantService.getIteration(id, new Listener<Iteration>() {
			@Override
			public void onResponse(final Iteration iteration) {
				// Sort taks by rank before returning
				Collections.sort(iteration.getTasksWithoutStory(), RankComparator.INSTANCE);

				listener.onResponse(iteration);
			}
		}, error);
	}

	@Override
	public void getCurrentFilterableIterations(
			final Listener<List<FilterableIteration>> listener, final ErrorListener error) {

		agilefantService.getCurrentFilterableIterations(listener, error);
	}

}
