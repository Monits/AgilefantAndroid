package com.monits.agilefant.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.project.ProjectFragment;
import com.monits.agilefant.model.Backlog;

public class ProjectActivity extends BaseToolbaredActivity {

	public static final String EXTRA_BACKLOG = "com.monits.agilefant.intent.extra.PROJECT";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iteration);


		final Backlog backlog = (Backlog) getIntent().getSerializableExtra(EXTRA_BACKLOG);

		final ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		final View fabContainer = getLayoutInflater().inflate(R.layout.fab_iteration_menu_layout, content);
		initFABs(fabContainer, backlog.getId());

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, ProjectFragment.newInstance(backlog));
		transaction.commit();
	}

	private void initFABs(final View fabContainer, final Long projectId) {
		final FloatingActionButton addFAB = (FloatingActionButton) fabContainer.findViewById(R.id.iteration_add_fab);
		addFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				final CreateStoryFragment createStoryFragment = CreateStoryFragment.newInstance(projectId);
				getSupportFragmentManager().beginTransaction()
						.replace(android.R.id.content, createStoryFragment)
						.addToBackStack(null)
						.commit();
			}
		});
	}
}
