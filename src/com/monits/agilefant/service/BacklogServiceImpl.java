package com.monits.agilefant.service;

import java.util.List;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;

public class BacklogServiceImpl implements BacklogService{

	@Inject
	private AgilefantService agilefantService;

	@Override
	public List<Product> getAllBacklogs() throws RequestException {
		return agilefantService.getAllBacklogs();
	}

	@Override
	public List<Project> getMyBacklogs() throws RequestException {
		return agilefantService.getMyBacklogs();
	}

}
