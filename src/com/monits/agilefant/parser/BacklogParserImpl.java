package com.monits.agilefant.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.monits.agilefant.model.Product;

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

}
