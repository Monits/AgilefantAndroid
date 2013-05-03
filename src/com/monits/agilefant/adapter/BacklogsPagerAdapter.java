package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.backlog.AllBacklogsFragment;
import com.monits.agilefant.fragment.backlog.MyBacklogsFragment;

public class BacklogsPagerAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments;
	private Context context;

	public BacklogsPagerAdapter(Context context, FragmentManager fragmentManager, List<Fragment> fragments) {
		super(fragmentManager);
		this.context  = context;
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
		if (getItem(position) instanceof AllBacklogsFragment) {
			return context.getResources().getString(R.string.all_backlogs);
		} else if (getItem(position) instanceof MyBacklogsFragment) {
			return context.getResources().getString(R.string.my_stories);
		} else {
			return context.getResources().getString(R.string.no_title);
		}
	}
}
