package com.monits.agilefant.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.parse.adapter.UserChooserDeserializer;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GsonModule {

	@Provides
	@Singleton
	Gson provideGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(UserChooser.class, new UserChooserDeserializer());
		gsonBuilder.registerTypeAdapter(Backlog.class, new JsonDeserializer<Backlog>() {

			@Override
			public Backlog deserialize(final JsonElement json, final Type typeOfT,
									final JsonDeserializationContext context) throws JsonParseException {
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
		});

		return gsonBuilder.create();
	}
}
