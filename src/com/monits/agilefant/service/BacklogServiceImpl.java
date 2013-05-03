package com.monits.agilefant.service;

import java.util.List;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.parser.BacklogParser;

public class BacklogServiceImpl implements BacklogService{

	@Inject
	private AgilefantService agilefantService;

	@Inject
	private BacklogParser backlogParser;

	@Override
	public List<Product> getAllBacklogs() throws RequestException {
		return backlogParser.allBacklogsParser(agilefantService.getAllBacklogs());
	}

	@Override
	public List<Project> getMyBacklogs() throws RequestException {
		return backlogParser.myBacklogsParser(agilefantService.getMyBacklogs());
	}

}
