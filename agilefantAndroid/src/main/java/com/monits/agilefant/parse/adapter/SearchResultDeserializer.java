package com.monits.agilefant.parse.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.monits.agilefant.model.SearchResult;
import com.monits.agilefant.model.search.SearchResultType;

import java.lang.reflect.Type;

/**
 * Created by edipasquale on 11/11/15.
 */
public class SearchResultDeserializer implements JsonDeserializer<SearchResult> {

	@Override
	public SearchResult deserialize(final JsonElement json, final Type typeOfT,
									final JsonDeserializationContext context) throws JsonParseException {

		final JsonObject object = json.getAsJsonObject();
		final String unparsedType = object.get("originalObject").getAsJsonObject().get("class").getAsString();
		final String label = object.get("label").getAsString();
		final int id = object.get("originalObject").getAsJsonObject().get("id").getAsInt();
		SearchResultType type;

		// Parsing type
		if (unparsedType.endsWith("Project")) {
			type = SearchResultType.PROJECT;
		} else if (unparsedType.endsWith("Product")) {
			type = SearchResultType.PRODUCT;
		} else if (unparsedType.endsWith("Story")) {
			type = SearchResultType.STORY;
		} else if (unparsedType.endsWith("Iteration")) {
			type = SearchResultType.ITERATION;
		} else {
			type = SearchResultType.TASK;
		}

		return new SearchResult(label, type, id);
	}
}
