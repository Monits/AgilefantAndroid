package com.monits.agilefant.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.monits.agilefant.fragment.iteration.StoriesFragment;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{

	private static final String TASK_WITHOUT_STORIES = "Task Without stories";
	private static final String STORIES = "Stories";

	private List<Fragment> fragments;

	public ScreenSlidePagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
		super(fragmentManager);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if (getItem(position) instanceof StoriesFragment) {
			return STORIES;
		} else {
			return TASK_WITHOUT_STORIES;
		}
	}
}