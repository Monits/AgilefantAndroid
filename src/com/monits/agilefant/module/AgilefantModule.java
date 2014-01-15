package com.monits.agilefant.module;

import roboguice.inject.SharedPreferencesName;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.monits.agilefant.parser.BacklogParser;
import com.monits.agilefant.parser.BacklogParserImpl;
import com.monits.agilefant.parser.DailyWorkParser;
import com.monits.agilefant.parser.DailyWorkParserImpl;
import com.monits.agilefant.parser.IterationParser;
import com.monits.agilefant.parser.IterationParserImpl;
import com.monits.agilefant.parser.ProjectParser;
import com.monits.agilefant.parser.ProjectParserImpl;
import com.monits.agilefant.parser.StoryParser;
import com.monits.agilefant.parser.StoryParserImpl;
import com.monits.agilefant.parser.TaskParser;
import com.monits.agilefant.parser.TaskParserImpl;
import com.monits.agilefant.parser.UserParser;
import com.monits.agilefant.parser.UserParserImpl;
import com.monits.agilefant.service.AgilefantService;
import com.monits.agilefant.service.AgilefantServiceImpl;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.BacklogServiceImpl;
import com.monits.agilefant.service.DailyWorkService;
import com.monits.agilefant.service.DailyWorkServiceImpl;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.IterationServiceImpl;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.MetricsServiceImpl;
import com.monits.agilefant.service.ProjectService;
import com.monits.agilefant.service.ProjectServiceImpl;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.service.UserServiceImpl;

public class AgilefantModule extends AbstractModule{

	@Override
	protected void configure() {

		bindConstant().annotatedWith(SharedPreferencesName.class).to("default");

		bind(AgilefantService.class).to(AgilefantServiceImpl.class).in(Singleton.class);
		bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
		bind(BacklogService.class).to(BacklogServiceImpl.class).in(Singleton.class);
		bind(IterationService.class).to(IterationServiceImpl.class).in(Singleton.class);
		bind(DailyWorkService.class).to(DailyWorkServiceImpl.class).in(Singleton.class);
		bind(MetricsService.class).to(MetricsServiceImpl.class).in(Singleton.class);
		bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
		bind(ProjectService.class).to(ProjectServiceImpl.class).in(Singleton.class);

		bind(StoryParser.class).to(StoryParserImpl.class).in(Singleton.class);
		bind(TaskParser.class).to(TaskParserImpl.class).in(Singleton.class);
		bind(UserParser.class).to(UserParserImpl.class).in(Singleton.class);
		bind(DailyWorkParser.class).to(DailyWorkParserImpl.class).in(Singleton.class);
		bind(BacklogParser.class).to(BacklogParserImpl.class).in(Singleton.class);
		bind(IterationParser.class).to(IterationParserImpl.class).in(Singleton.class);
		bind(ProjectParser.class).to(ProjectParserImpl.class).in(Singleton.class);
	}
}
