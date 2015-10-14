package com.monits.agilefant.module;


import android.app.Application;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.monits.agilefant.cache.BitmapLruCache;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by edipasquale on 27/08/15.
 */
@Module
public class VolleyModule {

	@Provides
	@Singleton
	RequestQueue provideRequestQueue(final Application application) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			HttpURLConnection.setFollowRedirects(false);
		}

		return Volley.newRequestQueue(application);
	}

	@Provides
	@Singleton
	ImageLoader provideImageLoader(final RequestQueue requestQueue,
								final ImageLoader.ImageCache imageCache) {
		return new ImageLoader(requestQueue, imageCache);
	}

	@Provides
	@Singleton
	ImageLoader.ImageCache provideImageCache() {
		return new BitmapLruCache();
	}

}
