package com.monits.agilefant.service;

import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.inject.Inject;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;

public class BacklogServiceImpl implements BacklogService {

	@Inject
	private AgilefantService agilefantService;

	@Override
	public void getAllBacklogs(final Listener<List<Product>> listener, final ErrorListener error) {
		agilefantService.getAllBacklogs(listener, error);
	}

	@Override
	public void getMyBacklogs(final Listener<List<Project>> listener, final ErrorListener error) {
		agilefantService.getMyBacklogs(listener, error);
	}

}
