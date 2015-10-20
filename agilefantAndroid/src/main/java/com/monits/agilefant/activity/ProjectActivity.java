package com.monits.agilefant.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.project.ProjectDetailsFragment;
import com.monits.agilefant.fragment.project.ProjectLeafStoriesFragment;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.ProjectService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectActivity extends BaseToolbaredActivity {

	public static final String EXTRA_BACKLOG = "com.monits.agilefant.intent.extra.PROJECT";
	public static final String PROJECT = "project";

	@Inject
	/* default */ ProjectService projectService;

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
		AgilefantApplication.getObjectGraph().inject(this);

		final Backlog backlog = (Backlog) getIntent().getSerializableExtra(EXTRA_BACKLOG);

		final ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		final View fabContainer = getLayoutInflater().inflate(R.layout.fab_iteration_menu_layout, content);
		initFABs(fabContainer, backlog.getId());

		if (savedInstanceState == null) {
			final ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage(getString(R.string.loading));
			progressDialog.show();

			// FIXME: Get the project data before launch the activity.
			projectService.getProjectData(
				backlog.getId(),
				new Response.Listener<Project>() {

					@Override
					public void onResponse(final Project project) {

						ProjectActivity.this.project = project;
						populatePager(project);

						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
					}
				},
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(ProjectActivity.this, R.string.failed_to_retrieve_project_details,
								Toast.LENGTH_SHORT).show();
					}
				});
		} else {
			project = (Project) savedInstanceState.get(PROJECT);

			populatePager(project);
		}
	}

	private void populatePager(final Project project) {
		final List<Fragment> fragments = new ArrayList<>();
		fragments.add(ProjectDetailsFragment.newInstance(project));
		fragments.add(ProjectLeafStoriesFragment.newInstance(project));

		viewPager.setAdapter(new ScreenSlidePagerAdapter(this, getSupportFragmentManager(), fragments));

		setUpTabLayout(viewPager);
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
				+ "project=" + project
				+ '}';
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(PROJECT, project);
	}
}
