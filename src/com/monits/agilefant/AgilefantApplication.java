package com.monits.agilefant;

import roboguice.RoboGuice;
import android.app.Application;

import com.google.inject.Inject;
import com.monits.agilefant.cache.BitmapLruCache;

public class AgilefantApplication extends Application {

	public static final String ACTION_TASK_UPDATED = "com.monits.agilefant.intent.action.TASK_UPDATED";
	public static final String EXTRA_TASK_UPDATED = "com.monits.agilefant.intent.extra.UPDATED_TASK";
	public static final String EXTRA_NEW_STORY = "com.monits.agilefant.intent.extra.NEW_STORY";
	public static final String ACTION_NEW_STORY = "com.monits.agilefant.intent.action.NEW_STORY";

	@Inject
	private BitmapLruCache bitmapCache;

	@Override
	public void onCreate() {
		RoboGuice.getInjector(this).injectMembers(this);

		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		bitmapCache.evictAll();

		super.onLowMemory();
	}
}

