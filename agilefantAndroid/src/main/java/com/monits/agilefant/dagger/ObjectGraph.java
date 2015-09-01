package com.monits.agilefant.dagger;


import android.app.Application;

import com.monits.agilefant.module.AgilefantModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by edipasquale on 27/08/15.
 */

@Singleton
@Component(modules = {AgilefantModule.class})
public interface ObjectGraph extends Graph {

	final class Initializer {
		private Initializer() {
		}

		public static ObjectGraph init(final Application application) {
			return DaggerObjectGraph.builder()
					.agilefantModule(new AgilefantModule(application))
					.build();
		}
	}
}
