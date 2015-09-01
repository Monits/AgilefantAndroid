package com.monits.agilefant.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.parse.adapter.UserChooserDeserializer;

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
		return gsonBuilder.create();
	}
}
