package com.monits.agilefant.parse.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;

import java.lang.reflect.Type;
/**
 * Created by lgnanni on 14/10/15.
 */
public class BacklogDeserializer implements JsonDeserializer<Backlog> {

	@Override
	public Backlog deserialize(final JsonElement json, final Type typeOfT,
		final JsonDeserializationContext context) throws
		JsonParseException {
		final String classType = json.getAsJsonObject().get("class").getAsString();
		if (classType.endsWith("Product")) {
			return context.deserialize(json, Product.class);
		} else if (classType.endsWith("Iteration")) {
			return context.deserialize(json, Iteration.class);
		} else if (classType.endsWith("Project")) {
			return context.deserialize(json, Project.class);
		}

		throw new AssertionError("trying to decode a backlog from " + json.getAsString());
	}
}
