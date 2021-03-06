package com.monits.agilefant.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.SearchResult;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.parse.adapter.BacklogDeserializer;
import com.monits.agilefant.parse.adapter.SearchResultDeserializer;
import com.monits.agilefant.parse.adapter.UserChooserDeserializer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GsonModule {

	@Provides
	@Singleton
	/* default */Gson provideGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(UserChooser.class, new UserChooserDeserializer());
		gsonBuilder.registerTypeAdapter(Backlog.class, new BacklogDeserializer());
		gsonBuilder.registerTypeAdapter(SearchResult.class, new SearchResultDeserializer());

		return gsonBuilder.create();
	}
}
