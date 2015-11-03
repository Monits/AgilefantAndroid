package com.monits.agilefant.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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

		final Intent upIntent = NavUtils.getParentActivityIntent(this);
		if (upIntent != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
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

	protected void setUpTabLayout(final ViewPager viewPager) {
		final TabLayout tabLayout = (TabLayout) findViewById(R.id.pager_header);
		tabLayout.setupWithViewPager(viewPager);

		tabLayout.post(new Runnable() {
			@Override
			public void run() {
				final int tabLayoutWidth = tabLayout.getWidth();
				final DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);

				final int deviceWidth = metrics.widthPixels;

				final ViewGroup.LayoutParams layoutParams = tabLayout.getLayoutParams();
				if (tabLayoutWidth < deviceWidth) {
					layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
					layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
					tabLayout.setTabMode(TabLayout.MODE_FIXED);
					tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
				} else {
					layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
					layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
					tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
				}
			}
		});

	}

	@Override
	public String toString() {
		return "BaseToolbaredActivity [toolbarConfigured:" + toolbarConfigured + ']';
	}
}
