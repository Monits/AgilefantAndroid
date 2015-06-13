package com.monits.agilefant.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.parse.adapter.UserChooserDeserializer;

public class GsonModule extends AbstractModule {

	@Override
	protected void configure() {
		final GsonBuilder gsonBuilder = new GsonBuilder();

		// Register type adapters
		gsonBuilder.registerTypeAdapter(UserChooser.class, new UserChooserDeserializer());

		bind(Gson.class).toInstance(gsonBuilder.create());
	}
}
