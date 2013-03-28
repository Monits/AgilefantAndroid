package com.monits.agilefant.parser;

import java.util.List;

import com.monits.agilefant.model.Product;

public interface BacklogParser {

	List<Product> allBacklogsParser(String allBackLogsJson);

}
