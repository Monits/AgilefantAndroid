package com.monits.agilefant.module;

import roboguice.inject.SharedPreferencesName;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.monits.agilefant.parser.BacklogParser;
import com.monits.agilefant.parser.BacklogParserImpl;
import com.monits.agilefant.parser.IterationParser;
import com.monits.agilefant.parser.IterationParserImpl;
import com.monits.agilefant.service.AgilefantService;
import com.monits.agilefant.service.AgilefantServiceImpl;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.BacklogServiceImpl;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.IterationServiceImpl;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.service.UserServiceImpl;

public class AgilefantModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(AgilefantService.class).to(AgilefantServiceImpl.class).in(Singleton.class);
		bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
		bind(BacklogService.class).to(BacklogServiceImpl.class).in(Singleton.class);
		bind(BacklogParser.class).to(BacklogParserImpl.class).in(Singleton.class);
		bind(IterationParser.class).to(IterationParserImpl.class).in(Singleton.class);
		bind(IterationService.class).to(IterationServiceImpl.class).in(Singleton.class);
		bindConstant().annotatedWith(SharedPreferencesName.class).to("default");
	}
}
