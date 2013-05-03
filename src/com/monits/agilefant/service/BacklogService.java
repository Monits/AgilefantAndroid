package com.monits.agilefant.service;

import java.util.List;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;

public interface BacklogService {

	/**
	 * Get All backlogs
	 * @return A list of products
	 * @throws RequestException
	 */
	List<Product> getAllBacklogs() throws RequestException;

	/**
	 * Get my backlogs
	 * @return A list of projects
	 * @throws RequestException
	 */
	List<Project> getMyBacklogs() throws RequestException;
}
