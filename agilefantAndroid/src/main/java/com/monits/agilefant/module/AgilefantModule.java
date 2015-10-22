package com.monits.agilefant.module;

import dagger.Module;
import dagger.Provides;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

import javax.inject.Singleton;

@Module(includes = {GsonModule.class, VolleyModule.class})
public class AgilefantModule {

	private final Application application;

	/**
	 * @param application used as a context
	 */
	public AgilefantModule(final Application application) {
		this.application = application;
	}

	@Provides
	@Singleton
	/* default */ SharedPreferences provideSharedPreferences(final Application application) {
		return PreferenceManager.getDefaultSharedPreferences(application);
	}

	@Provides
	@Singleton
	/* default */ Application provideApplication() {
		return this.application;
	}

	@Provides
	@Singleton
	/* default */ AgilefantService provideAgilefantService(final AgilefantServiceImpl agilefantService) {
		return agilefantService;
	}

	@Provides
	@Singleton
	/* default */ BacklogService provideBacklogService(final BacklogServiceImpl backlogService) {
		return backlogService;
	}

	@Provides
	@Singleton
	/* default */ DailyWorkService provideDailyWorkService(final DailyWorkServiceImpl dailyWorkService) {
		return dailyWorkService;
	}

	@Provides
	@Singleton
	/* default */ IterationService provideIterationService(final IterationServiceImpl iterationService) {
		return iterationService;
	}

	@Provides
	@Singleton
	/* default */ MetricsService provideMetricsService(final MetricsServiceImpl metricsService) {
		return metricsService;
	}

	@Provides
	@Singleton
	/* default */ ProjectService provideProjectService(final ProjectServiceImpl projectService) {
		return projectService;
	}

	@Provides
	@Singleton
	/* default */ UserService provideUserService(final UserServiceImpl userService) {
		return userService;
	}

}
