package com.monits.agilefant.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.dailywork.MyQueueWorkFragment;
import com.monits.agilefant.fragment.dailywork.MyStoriesFragment;
import com.monits.agilefant.fragment.dailywork.MyTasksFragment;
import com.monits.agilefant.model.DailyWork;

public class DailyWorkPagerAdapter extends FragmentStatePagerAdapter {

	private final Context context;
	private final DailyWork dailyWork;
	private final static int SIZE = 3;

	/**
	 * Constructor
	 * @param fragmentManager The fragment manager
	 * @param dailyWork The dailyWork model
	 * @param context The context
	 */
	public DailyWorkPagerAdapter(final FragmentManager fragmentManager, final DailyWork dailyWork,
		final Context context) {
		super(fragmentManager);
		this.context = context;
		this.dailyWork = dailyWork;
	}

	@Override
	public Fragment getItem(final int position) {
		switch (position) {
		case 0:
			return MyQueueWorkFragment.newInstance(dailyWork.getQueuedTasks());
		case 1:
			return MyStoriesFragment.newInstance(dailyWork.getStories());
		case 2:
			return MyTasksFragment.newInstance(dailyWork.getTaskWithoutStories());
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return SIZE;
	}

	@Override
	public CharSequence getPageTitle(final int position) {
		switch (position) {
		case 0:
			return context.getString(R.string.my_work_queue);
		case 1:
			return context.getString(R.string.my_stories);
		case 2:
			return context.getString(R.string.task_without_story);
		default:
			return context.getString(R.string.no_title);
		}
	}
}