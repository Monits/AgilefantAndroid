package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.iteration.IterationBurndownFragment;
import com.monits.agilefant.fragment.iteration.StoriesFragment;
import com.monits.agilefant.fragment.iteration.TaskWithoutStoryFragment;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments;
	private Context context;

	/**
	 * Constructor
	 * @param context The context
	 * @param fragmentManager The fragment manager
	 * @param fragments The fragments
	 * */
	public ScreenSlidePagerAdapter(final Context context, final FragmentManager fragmentManager,
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
		if (getItem(position) instanceof StoriesFragment) {
			return context.getResources().getString(R.string.stories);
		} else if (getItem(position) instanceof IterationBurndownFragment) {
			return context.getResources().getString(R.string.burndown);
		} else if (getItem(position) instanceof TaskWithoutStoryFragment) {
			return context.getResources().getString(R.string.task_without_story);
		} else {
			return context.getResources().getString(R.string.no_title);
		}
	}
}