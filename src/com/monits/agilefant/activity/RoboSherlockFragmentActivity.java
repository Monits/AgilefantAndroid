package com.monits.agilefant.activity;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.ContentViewListener;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.inject.Inject;
import com.google.inject.Key;

public class RoboSherlockFragmentActivity extends SherlockFragmentActivity implements RoboContext {

	protected final Map<Key<?>, Object> ScopedObject = new HashMap<Key<?>, Object>();

	@Inject
	protected EventManager eventManager;

	@Inject
	protected ContentViewListener ignored; // BUG find a better place to put this

	@Override
	protected void onCreate(final Bundle bundle) {
		final RoboInjector injector = RoboGuice.getInjector(this);
		injector.injectMembersWithoutViews(this);
		super.onCreate(bundle);
		this.eventManager.fire(new roboguice.activity.event.OnCreateEvent(bundle));
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		this.eventManager.fire(new OnRestartEvent());
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.eventManager.fire(new OnStartEvent());
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.eventManager.fire(new OnResumeEvent());
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.eventManager.fire(new OnPauseEvent());
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		this.eventManager.fire(new OnNewIntentEvent());
	}

	@Override
	protected void onStop() {
		try {
			eventManager.fire(new OnStopEvent());
		} finally {
			super.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		try {
			eventManager.fire(new OnDestroyEvent());
		} finally {
			try {
				RoboGuice.destroyInjector(this);
			} finally {
				super.onDestroy();
			}
		}
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		final Configuration currentConfig = getResources().getConfiguration();
		super.onConfigurationChanged(newConfig);
		eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		RoboGuice.getInjector(this).injectViewMembers(this);
		eventManager.fire(new OnContentChangedEvent());
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
	}

	@Override
	public Map<Key<?>, Object> getScopedObjectMap() {
		return this.ScopedObject;
	}
}