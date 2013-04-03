package com.monits.agilefant.service;

import java.util.List;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Product;

public interface BacklogService {

	List<Product> getAllBacklogs() throws RequestException;
}
