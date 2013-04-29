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
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.DateUtils;

public class IterationFragment extends RoboFragment implements OnPageChangeListener {

	private static final String ITERATION = "iteration";
	private static final String PROJECT_NAME = "project";

	private Iteration mIteration;
	private String mProjectName;

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

	private TextView startDate;
	private TextView endDate;

	private ViewPager viewPager;

	private PagerTitleStrip pagerTabStrip;

	private TextView product;

	private TextView project;

	private TextView iterationNameTree;

	public static IterationFragment newInstance(String projectName, Iteration iteration) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(ITERATION, iteration);
		arguments.putString(PROJECT_NAME, projectName);

		IterationFragment fragment = new IterationFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		mIteration = (Iteration) arguments.getSerializable(ITERATION);
		mProjectName = arguments.getString(PROJECT_NAME);

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_iteration, null);

		startDate = (TextView) view.findViewById(R.id.iteration_start_date);
		endDate = (TextView) view.findViewById(R.id.iteration_end_date);
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		pagerTabStrip = (PagerTitleStrip) view.findViewById(R.id.pager_header);
		product = (TextView) view.findViewById(R.id.product);
		project = (TextView) view.findViewById(R.id.project);
		iterationNameTree = (TextView) view.findViewById(R.id.iteration_name_tree);

		if (mIteration != null && mProjectName != null) {

			product.setText(mIteration.getRootIteration().getName());
			project.setText(mProjectName);
			iterationNameTree.setText(mIteration.getName());
			startDate.setText(DateUtils.formatDate(mIteration.getStartDate(), DATE_PATTERN));
			endDate.setText(DateUtils.formatDate(mIteration.getEndDate(), DATE_PATTERN));

			List<Fragment> fragments = new ArrayList<Fragment>();

			ArrayList<Story> storiesArray = new ArrayList<Story>();
			storiesArray.addAll(mIteration.getStories());

			ArrayList<Task> tasksWithoutStory = new ArrayList<Task>();
			tasksWithoutStory.addAll(mIteration.getTasksWithoutStory());

			fragments.add(StoriesFragment.newInstance(storiesArray));
			fragments.add(TaskWithoutStoryFragment.newInstance(tasksWithoutStory));
			fragments.add(IterationBurndownFragment.newInstance(mIteration.getId()));

			this.viewPager.setAdapter(new ScreenSlidePagerAdapter(getActivity(), getChildFragmentManager(), fragments));
			this.viewPager.setOnPageChangeListener(this);

			pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_stories_title));
		}

		return view;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		ScreenSlidePagerAdapter pagerAdapter = (ScreenSlidePagerAdapter)this.viewPager.getAdapter();
		Fragment fragment = pagerAdapter.getItem(position);

		if (fragment instanceof StoriesFragment) {
			pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_stories_title));
			pagerTabStrip.setTextColor(getResources().getColor(android.R.color.white));
		} else if (fragment instanceof TaskWithoutStoryFragment) {
			pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_task_without_story_title));
			pagerTabStrip.setTextColor(getResources().getColor(android.R.color.white));
		} else {
			pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_burndown_title));
			pagerTabStrip.setTextColor(getResources().getColor(R.color.all_backlogs_child_text_color));
		}
	}
}
