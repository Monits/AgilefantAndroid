package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;

public class IterationFragment extends RoboFragment implements OnPageChangeListener {

	private static final String ITERATION = "iteration";

	private Iteration mIteration;

	private ViewPager viewPager;

	private PagerTitleStrip pagerTabStrip;

	/**
	 * Creates a new IterationFragment with the given iteration
	 * @param iteration The iteration
	 * @return a new IterationFragment with the given iteration
	 */
	public static IterationFragment newInstance(final Iteration iteration) {
		final Bundle arguments = new Bundle();
		arguments.putSerializable(ITERATION, iteration);

		final IterationFragment fragment = new IterationFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		final Bundle arguments = getArguments();
		mIteration = (Iteration) arguments.getSerializable(ITERATION);

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_iteration, null);

		viewPager = (ViewPager) view.findViewById(R.id.pager);
		pagerTabStrip = (PagerTitleStrip) view.findViewById(R.id.pager_header);

		if (mIteration != null) {

			final List<Fragment> fragments = new ArrayList<>();

			final ArrayList<Story> storiesArray = new ArrayList<>();
			storiesArray.addAll(mIteration.getStories());

			final ArrayList<Task> tasksWithoutStory = new ArrayList<>();
			tasksWithoutStory.addAll(mIteration.getTasksWithoutStory());

			final IterationDetailsFragment iterationDetailsFragment = IterationDetailsFragment.newInstance(mIteration);
			fragments.add(iterationDetailsFragment);
			fragments.add(StoriesFragment.newInstance(storiesArray, mIteration));
			fragments.add(TaskWithoutStoryFragment.newInstance(tasksWithoutStory, mIteration));
			fragments.add(IterationBurndownFragment.newInstance(mIteration.getId()));

			this.viewPager.setAdapter(new ScreenSlidePagerAdapter(getActivity(), getChildFragmentManager(), fragments));
			this.viewPager.addOnPageChangeListener(this);

			pagerTabStrip.setBackgroundResource(iterationDetailsFragment.getTitleBackgroundResourceId());
		}

		return view;
	}

	@Override
	public void onPageScrollStateChanged(final int state) {
	}

	@Override
	public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
	}

	@Override
	public void onPageSelected(final int position) {
		final ScreenSlidePagerAdapter pagerAdapter = (ScreenSlidePagerAdapter) this.viewPager.getAdapter();
		final BaseDetailTabFragment fragment = (BaseDetailTabFragment) pagerAdapter.getItem(position);

		pagerTabStrip.setBackgroundResource(fragment.getTitleBackgroundResourceId());
		pagerTabStrip.setTextColor(getResources().getColor(fragment.getColorResourceId()));
	}
}
