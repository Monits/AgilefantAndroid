package com.monits.agilefant.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.backlog.task.CreateTaskWithoutStory;
import com.monits.agilefant.fragment.iteration.IterationFragment;
import com.monits.agilefant.model.Iteration;

public class IterationActivity extends BaseToolbaredActivity {

	public static final String ITERATION = "ITERATION";

	private Iteration iteration;
	private LinearLayout optionsContainer;
	private boolean fabInited;



	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iteration);

		if (savedInstanceState != null) {
			return;
		}

		final Bundle bundle = getIntent().getExtras();
		iteration = (Iteration) bundle.getSerializable(ITERATION);

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, IterationFragment.newInstance(iteration));
		transaction.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();	// Let the toolbar be configured...

		// And do our magic on top
		if (!fabInited) {
			final ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
			final View fabContainer = getLayoutInflater().inflate(R.layout.fab_iteration_menu_layout, content);
			initFABs(fabContainer);
			fabInited = true;
		}
	}

	private void initFABs(final View fabContainer) {
		final FloatingActionButton addFAB = (FloatingActionButton) fabContainer.findViewById(R.id.iteration_add_fab);
		final FloatingActionButton addStoryFAB =
				(FloatingActionButton) fabContainer.findViewById(R.id.iteration_fab_new_story);
		final FloatingActionButton addTaskFAB =
				(FloatingActionButton) fabContainer.findViewById(R.id.iteration_fab_new_task);
		optionsContainer = (LinearLayout) fabContainer.findViewById(R.id.fab_buttons_container);

		View.OnClickListener animationClick = new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				animationFABMenu();
			}
		};

		optionsContainer.setOnClickListener(animationClick);

		addFAB.setOnClickListener(animationClick);

		addStoryFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final CreateStoryFragment createStoryFragment = CreateStoryFragment.newInstance(iteration.getId());
				animationFABMenu();
				replaceFragment(createStoryFragment);
			}
		});

		addTaskFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final CreateTaskWithoutStory createTaskWithoutStory = CreateTaskWithoutStory
						.newInstance(iteration.getId());
				animationFABMenu();
				replaceFragment(createTaskWithoutStory);
			}
		});
	}

	private void animationFABMenu() {
		if (optionsContainer.getVisibility() == View.INVISIBLE) {
			optionsContainer.setVisibility(View.VISIBLE);
		} else {
			optionsContainer.setVisibility(View.INVISIBLE);
		}
	}

	private void replaceFragment(final Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment)
				.addToBackStack(null)
				.commit();
	}

	@Override
	public String toString() {
		return "IterationActivity [iteration_id:" + iteration.getId() + ", fabInited:" + fabInited + ']';
	}
}