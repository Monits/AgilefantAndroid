package com.monits.agilefant.service;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


public class BacklogServiceImpl implements BacklogService {

	private static final String GET_ALL_BACKLOGS_URL = "%1$s/ajax/menuData.action";
	private static final String GET_MY_BACKLOGS_URL = "%1$s/ajax/myAssignmentsMenuData.action";

	private final AgilefantService agilefantService;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	/**
	 * @param agilefantService Injected via Constructor by Dagger
	 * @param gson Injected via Constructor by Dagger
	 */
	@Inject
	public BacklogServiceImpl(final AgilefantService agilefantService,
							final Gson gson) {
		this.agilefantService = agilefantService;
		this.gson = gson;
	}

	@Override
	public void getAllBacklogs(final Listener<List<Product>> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, GET_ALL_BACKLOGS_URL, agilefantService.getHost());

		final Type listType = new TypeToken<ArrayList<Product>>() { }.getType();
		final GsonRequest<List<Product>> request = new GsonRequest<>(Request.Method.GET, url,
				gson, listType, listener, error, null);

		agilefantService.addRequest(request);
	}

	@Override
	public void getMyBacklogs(final Listener<List<Project>> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, GET_MY_BACKLOGS_URL, agilefantService.getHost());

		final Type listType = new TypeToken<ArrayList<Project>>() { }.getType();
		final GsonRequest<List<Project>> request = new GsonRequest<>(Request.Method.GET, url,
				gson, listType, listener, error, null);

		agilefantService.addRequest(request);
	}

}
