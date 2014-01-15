package com.monits.agilefant.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;

public class GsonModule extends AbstractModule {

	@Override
	protected void configure() {
		final GsonBuilder gsonBuilder = new GsonBuilder();

		// Register type adapters

		bind(Gson.class).toInstance(gsonBuilder.create());
	}
}
