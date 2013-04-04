package com.monits.agilefant.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.StoriesAdapter;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.task.GetIteration;

@ContentView(R.layout.activity_iteration)
public class IterationActivity extends RoboActivity{

	@InjectView(R.id.iteration_name)
	private TextView name;
	@InjectView(R.id.iteration_start_date)
	private TextView startDate;
	@InjectView(R.id.iteration_end_date)
	private TextView endDate;

	@InjectView(R.id.stories)
	private ExpandableListView stories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		Iteration iteration = (Iteration)bundle.getSerializable(GetIteration.ITERATION);

		if (iteration != null) {
			name.setText(iteration.getName());
			stories.setAdapter(new StoriesAdapter(this, iteration.getStories()));
		}

	}

}
