package com.monits.agilefant.module;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;

import roboguice.inject.SharedPreferencesName;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.monits.agilefant.cache.BitmapLruCache;
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
import com.monits.volleyrequests.network.NullSafeImageLoader;

@SuppressLint("NewApi")
public class AgilefantModule extends AbstractModule {

	@Override
	protected void configure() {

		bindConstant().annotatedWith(SharedPreferencesName.class).to("default");

		// Services
		bind(AgilefantService.class).to(AgilefantServiceImpl.class).in(Singleton.class);
		bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
		bind(BacklogService.class).to(BacklogServiceImpl.class).in(Singleton.class);
		bind(IterationService.class).to(IterationServiceImpl.class).in(Singleton.class);
		bind(DailyWorkService.class).to(DailyWorkServiceImpl.class).in(Singleton.class);
		bind(MetricsService.class).to(MetricsServiceImpl.class).in(Singleton.class);
		bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
		bind(ProjectService.class).to(ProjectServiceImpl.class).in(Singleton.class);

		// Set a default cookie handler to accept and store cookies
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			HttpURLConnection.setFollowRedirects(false);
		}

		// network
		bind(RequestQueue.class).toProvider(RequestQueueProvider.class).in(Singleton.class);
		bind(ImageLoader.class).toProvider(ImageLoaderProvider.class).in(Singleton.class);
		bind(BitmapLruCache.class).in(Singleton.class);
	}

	private static class RequestQueueProvider implements Provider<RequestQueue> {

		private final Context context;

		@Inject
		public RequestQueueProvider(final Context context) {
			this.context = context;
		}

		@Override
		public RequestQueue get() {
			return Volley.newRequestQueue(context);
		}
	}

	private static class ImageLoaderProvider implements Provider<ImageLoader> {

		private final RequestQueue requestQueue;
		private final BitmapLruCache bitmapCache;

		@Inject
		public ImageLoaderProvider(final RequestQueue requestQueue, final BitmapLruCache bitmapCache) {
			this.requestQueue = requestQueue;
			this.bitmapCache = bitmapCache;
		}

		@Override
		public ImageLoader get() {
			return new NullSafeImageLoader(requestQueue, bitmapCache);
		}
	}
}
