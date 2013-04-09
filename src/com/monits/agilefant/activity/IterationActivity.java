package com.monits.agilefant.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.fragment.iteration.StoriesFragment;
import com.monits.agilefant.fragment.iteration.TaskWithoutStoryFragment;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.task.GetIteration;
import com.monits.agilefant.util.DateUtils;

@ContentView(R.layout.activity_iteration)
public class IterationActivity extends RoboFragmentActivity{

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";
	@InjectView(R.id.iteration_name)
	private TextView name;
	@InjectView(R.id.iteration_start_date)
	private TextView startDate;
	@InjectView(R.id.iteration_end_date)
	private TextView endDate;

	@Inject
	private FragmentManager fragmentManager;

	@InjectView(R.id.pager)
	private ViewPager viewPager;

	private Iteration iteration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		iteration = (Iteration)bundle.getSerializable(GetIteration.ITERATION);

		if (iteration != null) {
			name.setText(iteration.getName());
			startDate.setText(DateUtils.formatDate(iteration.getStartDate(), DATE_PATTERN));
			endDate.setText(DateUtils.formatDate(iteration.getEndDate(), DATE_PATTERN));

			List<Fragment> fragments = new ArrayList<Fragment>();
			fragments.add(new StoriesFragment(iteration.getStories()));
			fragments.add(new TaskWithoutStoryFragment(iteration.getTasksWithoutStory()));

			viewPager.setAdapter(new ScreenSlidePagerAdapter(fragmentManager, fragments));
		}

	}
}