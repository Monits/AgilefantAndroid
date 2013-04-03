package com.monits.agilefant.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.monits.agilefant.parser.BacklogParser;
import com.monits.agilefant.parser.BacklogParserImpl;
import com.monits.agilefant.service.AgilefantService;
import com.monits.agilefant.service.AgilefantServiceImpl;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.BacklogServiceImpl;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.service.UserServiceImpl;

public class AgilefantModule extends AbstractModule{

	private final String HOST = "http://agilefant.monits.com";

	@Override
	protected void configure() {
		bind(AgilefantService.class).to(AgilefantServiceImpl.class).in(Singleton.class);
		bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
		bind(BacklogService.class).to(BacklogServiceImpl.class).in(Singleton.class);
		bind(BacklogParser.class).to(BacklogParserImpl.class).in(Singleton.class);
		bind(String.class).annotatedWith(Names.named("HOST")).toInstance(HOST);
	}
}
