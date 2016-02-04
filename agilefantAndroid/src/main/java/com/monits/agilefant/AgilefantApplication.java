package com.monits.agilefant;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.monits.agilefant.cache.BitmapLruCache;
import com.monits.agilefant.dagger.DaggerObjectGraph;
import com.monits.agilefant.dagger.ObjectGraph;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.squareup.leakcanary.LeakCanary;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

public class AgilefantApplication extends Application {

	private static ObjectGraph objectGraph;

	@Inject
	/* default */ BitmapLruCache bitmapCache;

	public static final String FLURRY_API_KEY = "24CMC96FHSZN74WWKBKV";

	public static final String EXTRA_TASK_UPDATED =
			"com.monits.agilefant.intent.extra.UPDATED_TASK";
	public static final String EXTRA_NEW_STORY =
			"com.monits.agilefant.intent.extra.NEW_STORY";
	public static final String EXTRA_NEW_TASK_WITHOUT_STORY =
			"com.monits.agilefant.intent.extra.NEW_TASK_WITHOUT_STORY";

	public static final String ACTION_NEW_STORY =
			"com.monits.agilefant.intent.action.NEW_STORY";
	public static final String ACTION_NEW_TASK_WITHOUT_STORY =
			"com.monits.agilefant.intent.action.NEW_TASK_WITHOUT_STORY";
	public static final String ACTION_TASK_UPDATED =
			"com.monits.agilefant.intent.action.TASK_UPDATED";

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({ACTION_NEW_TASK_WITHOUT_STORY, ACTION_TASK_UPDATED, ACTION_NEW_STORY})
	public @interface Actions { }

	/**
	 * This factory method returns an intent that will be used for a broadcast
	 *
	 * @param task A Task object
	 * @param action A @StringDef representing the action
	 * @return An intent that contains extra values
	 */
	public static Intent updateTaskTimeBroadcastIntent(@NonNull final Task task,
			@Actions final String action) {
		final Intent intent = new Intent(action);
		switch (action) {
		case ACTION_NEW_TASK_WITHOUT_STORY:
			intent.putExtra(EXTRA_NEW_TASK_WITHOUT_STORY, task);
			break;
		case ACTION_TASK_UPDATED:
			intent.putExtra(EXTRA_TASK_UPDATED, task);
			break;
		default:
			return intent;
		}
		return intent;
	}

	/**
	 * This factory method returns an intent that will be used for a broadcast
	 *
	 * @param newStory A Story object
	 * @return An intent with the Story Object as extra
	 */
	public static Intent createNewStoryBroadcastIntent(@NonNull final Story newStory) {
		final Intent intent = new Intent(ACTION_NEW_STORY);
		intent.putExtra(EXTRA_NEW_STORY, newStory);
		return intent;
	}

	/**
	 * This factory method returns an intentFilter that will be used to register a receiver
	 *
	 * @param action A @StringDef representing the action of the intentFilter
	 * @return An intentFilter
	 */
	public static IntentFilter registerReceiverIntentFilter(@Actions final String action) {
		return new IntentFilter(action);
	}

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

