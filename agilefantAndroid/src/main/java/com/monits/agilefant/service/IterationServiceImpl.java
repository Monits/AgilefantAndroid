package com.monits.agilefant.service;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monits.agilefant.model.FilterableIteration;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.RankComparator;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


public class IterationServiceImpl implements IterationService {

	private static final String GET_ITERATION = "%1$s/ajax/iterationData.action?iterationId=%2$d";

	private static final String CURRENT_ITERATION_CHOOSER_ACTION = "%1$s/ajax/currentIterationChooserData.action";

	private final AgilefantService agilefantService;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	/**
	 * @param agilefantService Injected via constructor by Dagger
	 * @param gson Injected Gson
	 */
	@Inject
	public IterationServiceImpl(final AgilefantService agilefantService,
								final Gson gson) {
		this.agilefantService = agilefantService;
		this.gson = gson;
	}

	@Override
	public void getIteration(final long id, final Listener<Iteration> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, GET_ITERATION, agilefantService.getHost(), id);

		final GsonRequest<Iteration> request = new GsonRequest<>(
				Request.Method.GET, url, gson, Iteration.class,
				new Listener<Iteration>() {
					@Override
					public void onResponse(final Iteration iteration) {
						// Sort tasks by rank before returning
						Collections.sort(iteration.getTasksWithoutStory(), RankComparator.INSTANCE);

						// Add iteration to all tasks before returning
						for (final Task task : iteration.getTasksWithoutStory()) {
							task.setIteration(iteration);
						}
						//Add story to all tasks before returning
						for (final Story story : iteration.getStories()) {
							for (final Task task : story.getTasks()) {
								task.setStory(story);
							}
						}

						listener.onResponse(iteration);
					}
				}, error, null);

		agilefantService.addRequest(request);
	}

	@Override
	public void getCurrentFilterableIterations(
			final Listener<List<FilterableIteration>> listener, final ErrorListener error) {

		final String url = String.format(Locale.US, CURRENT_ITERATION_CHOOSER_ACTION,
				agilefantService.getHost());

		final Type listType = new TypeToken<ArrayList<FilterableIteration>>() { }.getType();
		final GsonRequest<List<FilterableIteration>> request = new GsonRequest<>(
				Request.Method.POST, url, gson, listType, listener, error, null);

		agilefantService.addRequest(request);
	}
}
