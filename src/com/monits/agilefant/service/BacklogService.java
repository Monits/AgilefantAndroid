package com.monits.agilefant.service;

import java.util.List;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Product;

public interface BacklogService {

	/**
	 * Get All backlogs
	 * @return A list of products
	 * @throws RequestException
	 */
	List<Product> getAllBacklogs() throws RequestException;
}
