package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.DateUtils;

public class IterationFragment extends RoboFragment implements OnPageChangeListener {

	private static final String ITERATION = "iteration";

	private Iteration mIteration;

	private TextView startDate;
	private TextView endDate;

	private ViewPager viewPager;

	private PagerTitleStrip pagerTabStrip;

	private TextView product;

	private TextView project;

	private TextView iterationNameTree;

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
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_iteration, null);

		startDate = (TextView) view.findViewById(R.id.iteration_start_date);
		endDate = (TextView) view.findViewById(R.id.iteration_end_date);
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		pagerTabStrip = (PagerTitleStrip) view.findViewById(R.id.pager_header);
		product = (TextView) view.findViewById(R.id.product);
		project = (TextView) view.findViewById(R.id.project);
		iterationNameTree = (TextView) view.findViewById(R.id.iteration_name_tree);

		if (mIteration != null) {

			final Backlog parent = mIteration.getParent();
			if (parent != null) {
				view.findViewById(R.id.path_layout).setVisibility(View.VISIBLE);

				product.setText(mIteration.getRootIteration().getName());
				project.setText(parent.getName());
				iterationNameTree.setText(mIteration.getName());

				project.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(final View v) {
						final Intent intent = new Intent(getActivity(), ProjectActivity.class);
						intent.putExtra(ProjectActivity.EXTRA_BACKLOG, parent);
						startActivity(intent);
					}
				});
			} else {
				view.findViewById(R.id.path_layout).setVisibility(View.GONE);
			}

			startDate.setText(
					DateUtils.formatDate(mIteration.getStartDate(), DateUtils.DATE_PATTERN));
			endDate.setText(
					DateUtils.formatDate(mIteration.getEndDate(), DateUtils.DATE_PATTERN));

			final List<Fragment> fragments = new ArrayList<Fragment>();

			final ArrayList<Story> storiesArray = new ArrayList<Story>();
			storiesArray.addAll(mIteration.getStories());

			final ArrayList<Task> tasksWithoutStory = new ArrayList<Task>();
			tasksWithoutStory.addAll(mIteration.getTasksWithoutStory());

			fragments.add(StoriesFragment.newInstance(storiesArray));
			fragments.add(TaskWithoutStoryFragment.newInstance(tasksWithoutStory));
			fragments.add(IterationBurndownFragment.newInstance(mIteration.getId()));

			this.viewPager.setAdapter(new ScreenSlidePagerAdapter(getActivity(), getChildFragmentManager(), fragments));
			this.viewPager.setOnPageChangeListener(this);

			pagerTabStrip.setBackgroundResource(R.drawable.gradient_stories_title);
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
		final ScreenSlidePagerAdapter pagerAdapter = (ScreenSlidePagerAdapter)this.viewPager.getAdapter();
		final Fragment fragment = pagerAdapter.getItem(position);

		if (fragment instanceof StoriesFragment) {
			pagerTabStrip.setBackgroundResource(R.drawable.gradient_stories_title);
			pagerTabStrip.setTextColor(getResources().getColor(android.R.color.white));
		} else if (fragment instanceof TaskWithoutStoryFragment) {
			pagerTabStrip.setBackgroundResource(R.drawable.gradient_task_without_story_title);
			pagerTabStrip.setTextColor(getResources().getColor(android.R.color.white));
		} else {
			pagerTabStrip.setBackgroundResource(R.drawable.gradient_burndown_title);
			pagerTabStrip.setTextColor(getResources().getColor(R.color.all_backlogs_child_text_color));
		}
	}
}
