package com.monits.agilefant.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;

/**
 * Base activity with a Toolbar included.
 */
public class BaseToolbaredActivity extends BaseActivity {

	private boolean toolbarConfigured;

	@Override
	protected void onStart() {
		super.onStart();
		setUpActionBar();
	}

	private void setUpActionBar() {
		if (toolbarConfigured) {
			return;
		}

		final ViewGroup containerView = getToolbarContainer();
		final View toolbarContainer = getLayoutInflater().inflate(R.layout.toolbar, containerView, false);
		setSupportActionBar((Toolbar) toolbarContainer.findViewById(R.id.toolbar));

		// Make sure it's always the fist child
		containerView.addView(toolbarContainer, 0);

		toolbarConfigured = true;
	}

	private ViewGroup getToolbarContainer() {
		// If there is an explicit app bar container, use that
		final View appBarContainer = findViewById(R.id.app_bar_container);
		if (appBarContainer != null) {
			return (ViewGroup) appBarContainer;
		}

		// Otherwise just default to the activity layout root
		final ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		return (ViewGroup) content.getChildAt(0);
	}

	@Override
	public String toString() {
		return "BaseToolbaredActivity [toolbarConfigured:" + toolbarConfigured + ']';
	}
}
