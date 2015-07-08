package com.monits.agilefant.activity;

import roboguice.inject.ContentView;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.backlog.task.CreateTaskWithoutStory;
import com.monits.agilefant.fragment.iteration.IterationFragment;
import com.monits.agilefant.model.Iteration;

@ContentView(R.layout.activity_iteration)
public class IterationActivity extends BaseActivity {

	public static final String ITERATION = "ITERATION";

	private Iteration iteration;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			return;
		}

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final Bundle bundle = getIntent().getExtras();
		iteration = (Iteration) bundle.getSerializable(ITERATION);

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, IterationFragment.newInstance(iteration));
		transaction.commit();
	}


	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_iteration_new_element, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_story:
			final CreateStoryFragment createStoryFragment = CreateStoryFragment.newInstance(iteration.getId());
			getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, createStoryFragment)
				.addToBackStack(null)
				.commit();

			return true;
		case R.id.action_new_task:
			final CreateTaskWithoutStory createTaskWithoutStory = CreateTaskWithoutStory.newInstance(iteration.getId());
			getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, createTaskWithoutStory)
				.addToBackStack(null)
				.commit();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public String toString() {
		return "IterationActivity [iteration_id:" + iteration.getId() + ']';
	}
}