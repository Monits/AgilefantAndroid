package com.monits.agilefant;

import android.app.Application;
import android.os.StrictMode;

import com.monits.agilefant.dagger.DaggerObjectGraph;
import com.monits.agilefant.dagger.ObjectGraph;
import com.monits.agilefant.cache.BitmapLruCache;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

public class AgilefantApplication extends Application {

	public static final String FLURRY_API_KEY = "24CMC96FHSZN74WWKBKV";

	public static final String ACTION_TASK_UPDATED = "com.monits.agilefant.intent.action.TASK_UPDATED";
	public static final String EXTRA_TASK_UPDATED = "com.monits.agilefant.intent.extra.UPDATED_TASK";
	public static final String EXTRA_NEW_STORY = "com.monits.agilefant.intent.extra.NEW_STORY";
	public static final String ACTION_NEW_STORY = "com.monits.agilefant.intent.action.NEW_STORY";
	public static final String ACTION_NEW_TASK_WITHOUT_STORY =
			"com.monits.agilefant.intent.action.NEW_TASK_WITHOUT_STORY";
	public static final String EXTRA_NEW_TASK_WITHOUT_STORY =
			"com.monits.agilefant.intent.extra.NEW_TASK_WITHOUT_STORY";

	private static ObjectGraph objectGraph;

	@Inject
	BitmapLruCache bitmapCache;

	@Override
	public void onCreate() {
		super.onCreate();
		objectGraphSetter(this);
		strictModeVerification();
		LeakCanary.install(this);
	}

	private static void objectGraphSetter(final Application app) {
		objectGraph = DaggerObjectGraph.Initializer.init(app);
	}

	@Override
	public void onLowMemory() {
		bitmapCache.evictAll();

		super.onLowMemory();
	}

	public static ObjectGraph getObjectGraph() {
		return objectGraph;
	}

	/**
    * Verifies the code for bad implementations and leaks
    */
	private static void strictModeVerification() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build());
		}
	}
}

