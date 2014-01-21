package com.monits.agilefant;

import roboguice.RoboGuice;
import android.app.Application;

import com.google.inject.Inject;
import com.monits.agilefant.cache.BitmapLruCache;

public class AgilefantApplication extends Application {

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
