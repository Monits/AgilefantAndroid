package com.monits.agilefant.parser;

import java.util.List;

import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;

public interface BacklogParser {

	List<Product> allBacklogsParser(String allBackLogsJson);

	List<Project> myBacklogsParser(String myBacklogsJson);
}
