package com.monits.agilefant.fragment.iteration;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IterationFragment extends Fragment {

	private static final String ITERATION = "iteration";

	private Iteration mIteration;

	@Bind(R.id.pager)
	ViewPager viewPager;

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
		final View view = inflater.inflate(R.layout.fragment_iteration, container, false);
		ButterKnife.bind(this, view);
		if (mIteration != null) {

			final List<Fragment> fragments = new ArrayList<>();

			final ArrayList<Story> storiesArray = new ArrayList<>();
			storiesArray.addAll(mIteration.getStories());

			final ArrayList<Task> tasksWithoutStory = new ArrayList<>();
			tasksWithoutStory.addAll(mIteration.getTasksWithoutStory());

			final IterationDetailsFragment iterationDetailsFragment = IterationDetailsFragment.newInstance(mIteration);
			fragments.add(iterationDetailsFragment);
			fragments.add(StoriesFragment.newInstance(storiesArray, mIteration));
			fragments.add(TaskWithoutStoryFragment.newInstance(tasksWithoutStory));
			fragments.add(IterationBurndownFragment.newInstance(mIteration.getId()));

			viewPager.setAdapter(new ScreenSlidePagerAdapter(getActivity(), getChildFragmentManager(), fragments));

			final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.pager_header);
			tabLayout.setupWithViewPager(viewPager);
			tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		}

		return view;
	}

	@Override
	public String toString() {
		return "IterationFragment{ mIteration= " + mIteration + '}';
	}
}
