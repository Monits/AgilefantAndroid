package com.monits.agilefant.service;

import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;

import javax.inject.Inject;


public class BacklogServiceImpl implements BacklogService {


	private final AgilefantService agilefantService;

	/**
	 * @param agilefantService Injected via Constructor by Dagger
	 */

	@Inject
	public BacklogServiceImpl(final AgilefantService agilefantService) {
		this.agilefantService = agilefantService;
	}

	@Override
	public void getAllBacklogs(final Listener<List<Product>> listener, final ErrorListener error) {
		agilefantService.getAllBacklogs(listener, error);
	}

	@Override
	public void getMyBacklogs(final Listener<List<Project>> listener, final ErrorListener error) {
		agilefantService.getMyBacklogs(listener, error);
	}

}
