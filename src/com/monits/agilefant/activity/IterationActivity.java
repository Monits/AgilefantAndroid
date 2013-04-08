package com.monits.agilefant.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.StoriesAdapter;
import com.monits.agilefant.adapter.TaskWithoutStoryAdaptar;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.task.GetIteration;
import com.monits.agilefant.util.DateUtils;

@ContentView(R.layout.activity_iteration)
public class IterationActivity extends RoboActivity{

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";
	@InjectView(R.id.iteration_name)
	private TextView name;
	@InjectView(R.id.iteration_start_date)
	private TextView startDate;
	@InjectView(R.id.iteration_end_date)
	private TextView endDate;
	@InjectView(R.id.switcher)
	private ViewSwitcher switcher;

	@InjectView(R.id.next_view)
	private Button nextView;

	@InjectView(R.id.stories)
	private ExpandableListView stories;

	@InjectView(R.id.task_without_story)
	private ListView taskWithoutStory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		Iteration iteration = (Iteration)bundle.getSerializable(GetIteration.ITERATION);

		if (iteration != null) {
			name.setText(iteration.getName());
			startDate.setText(DateUtils.formatDate(iteration.getStartDate(), DATE_PATTERN));
			endDate.setText(DateUtils.formatDate(iteration.getEndDate(), DATE_PATTERN));

			stories.setAdapter(new StoriesAdapter(this, iteration.getStories()));

			if (!iteration.getTasksWithoutStory().isEmpty()) {
				nextView.setVisibility(View.VISIBLE);
				nextView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						switcher.showNext();
					}
				});
				taskWithoutStory.setAdapter(new TaskWithoutStoryAdaptar(this, iteration.getTasksWithoutStory()));
			}
		}

	}

}
