package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.project.ProjectLeafStoriesFragment;

public class ProjectPagerAdapter extends FragmentStatePagerAdapter {

	private final List<Fragment> fragments;
	private final Context context;

	/**
	 * Constructor
	 * @param context The context
	 * @param fragmentManager The fragment manager
	 * @param fragments The fragments
	 */
	public ProjectPagerAdapter(final Context context, final FragmentManager fragmentManager,
			final List<Fragment> fragments) {
		super(fragmentManager);
		this.context  = context;
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(final int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(final int position) {
		final Fragment fragment = getItem(position);
		if (fragment instanceof ProjectLeafStoriesFragment) {
			return context.getResources().getString(R.string.backlog);
		}

		return context.getResources().getString(R.string.no_title);
	}
}
