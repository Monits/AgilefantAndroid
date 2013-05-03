package com.monits.agilefant.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;

public class BacklogParserImpl implements BacklogParser{

	@Override
	public List<Product> allBacklogsParser(String allBackLogsJson) {
		List<Product> products = new ArrayList<Product>();
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonElement tradeElement = parser.parse(allBackLogsJson);
		JsonArray trade = tradeElement.getAsJsonArray();

		for (JsonElement jsonElement : trade) {
			products.add(gson.fromJson(jsonElement, Product.class));
		}

		return products;
	}

	@Override
	public List<Project> myBacklogsParser(String myBacklogsJson) {
		List<Project> projects = new LinkedList<Project>();

		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(myBacklogsJson);
		JsonArray jsonArray = json.getAsJsonArray();

		for (JsonElement jsonElement : jsonArray) {
			projects.add(gson.fromJson(jsonElement, Project.class));
		}

		return projects;
	}

}
