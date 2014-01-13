package com.monits.agilefant.activity;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnCreateEvent;
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
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.google.inject.Key;

public class RoboActionBarActivity extends ActionBarActivity implements RoboContext {

	protected Map<Key<?>, Object> scopedObjects = new HashMap<Key<?>, Object>();

	@Inject
	protected EventManager eventManager;

	@Inject
	private ContentViewListener ignored; // RoboGuice's bug workaround, in order to use AppCompat without making the app crash.

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		final RoboInjector injector = RoboGuice.getInjector(this);
		injector.injectMembersWithoutViews(this);
		super.onCreate(savedInstanceState);
		eventManager.fire(new OnCreateEvent(savedInstanceState));
	}

	@Override
	public void setContentView(final int layoutResID) {
		super.setContentView(layoutResID);
		contentViewChanged();
	}

	@Override
	public void setContentView(final View view) {
		super.setContentView(view);
		contentViewChanged();
	}

	@Override
	public void setContentView(final View view, final ViewGroup.LayoutParams params) {
		super.setContentView(view, params);
		contentViewChanged();
	}

	@Override
	public void addContentView(final View view, final ViewGroup.LayoutParams params) {
		super.addContentView(view, params);
		contentViewChanged();
	}

	private void contentViewChanged() {
		RoboGuice.getInjector(this).injectViewMembers(this);
		eventManager.fire(new OnContentChangedEvent());
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		eventManager.fire(new OnRestartEvent());
	}

	@Override
	protected void onStart() {
		super.onStart();
		eventManager.fire(new OnStartEvent());
	}

	@Override
	protected void onResume() {
		super.onResume();
		eventManager.fire(new OnResumeEvent());
	}

	@Override
	protected void onPause() {
		super.onPause();
		eventManager.fire(new OnPauseEvent());
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		eventManager.fire(new OnNewIntentEvent());
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
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
	}

	@Override
	public Map<Key<?>, Object> getScopedObjectMap() {
		return scopedObjects;
	}
}