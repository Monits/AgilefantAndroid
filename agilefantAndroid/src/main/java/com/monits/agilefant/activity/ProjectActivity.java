package com.monits.agilefant.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.project.ProjectDetailsFragment;
import com.monits.agilefant.fragment.project.ProjectLeafStoriesFragment;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Project;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectActivity extends BaseToolbaredActivity {
	public static final String EXTRA_BACKLOG = "com.monits.agilefant.intent.extra.PROJECT";
	public static final String PROJECT = "project";

	@Bind(R.id.pager)
	/* default */ ViewPager viewPager;

	@Bind(R.id.pager_header)
	/* default */ TabLayout tabLayout;

	private Project project;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iteration);
		ButterKnife.bind(this);

		final Backlog backlog = (Backlog) getIntent().getSerializableExtra(EXTRA_BACKLOG);
		if (savedInstanceState == null) {
			project = (Project) getIntent().getSerializableExtra(PROJECT);
		} else {
			project = (Project) savedInstanceState.get(PROJECT);
		}

		final ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		final View fabContainer = getLayoutInflater().inflate(R.layout.fab_iteration_menu_layout, content);
		initFABs(fabContainer, backlog.getId());
		populatePager(project);
	}

	private void populatePager(final Project project) {
		final List<Fragment> fragments = new ArrayList<>();
		fragments.add(ProjectDetailsFragment.newInstance(project));
		fragments.add(ProjectLeafStoriesFragment.newInstance(project));
		viewPager.setAdapter(new ScreenSlidePagerAdapter(this, getSupportFragmentManager(), fragments));
		tabLayout.setupWithViewPager(viewPager);
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

	@Override
	public String toString() {
		return "ProjectActivity{"
				+ ", project=" + project
				+ '}';
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(PROJECT, project);
	}
}
