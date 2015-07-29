package com.monits.agilefant.activity;

import android.content.res.TypedArray;
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

		final ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		final View activityView = content.getChildAt(0);
		final View toolbarContainer = getLayoutInflater().inflate(R.layout.toolbar, content);
		setSupportActionBar((Toolbar) toolbarContainer.findViewById(R.id.toolbar));

		activityView.setPadding(activityView.getPaddingLeft(), activityView.getPaddingTop() + getActionBarSize(),
			activityView.getPaddingRight(), activityView.getPaddingBottom());

		toolbarConfigured = true;
	}

	private int getActionBarSize() {
		final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] { R.attr.actionBarSize });
		final int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();

		return actionBarHeight;
	}

	@Override
	public String toString() {
		return "BaseToolbaredActivity [toolbarConfigured:" + toolbarConfigured + ']';
	}
}
