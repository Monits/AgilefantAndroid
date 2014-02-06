package com.monits.agilefant.activity;

import java.util.LinkedList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ProjectPagerAdapter;
import com.monits.agilefant.fragment.project.ProjectLeafStoriesFragment;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment.OnUsersSubmittedListener;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.ProjectService;
import com.monits.agilefant.util.DateUtils;
import com.monits.agilefant.util.IterationUtils;

@ContentView(R.layout.activity_project)
public class ProjectActivity extends BaseActivity {

	private static final int ACTIVITY_VIEW = 1;

	public static final String EXTRA_BACKLOG = "com.monits.agilefant.intent.extra.PROJECT";

	@Inject
	private ProjectService projectService;

	@InjectView(value = R.id.root_flipper)
	private ViewFlipper viewFlipper;

	@InjectView(value = R.id.product)
	private TextView productLabel;

	@InjectView(value = R.id.project)
	private TextView projectLabel;

	@InjectView(value = R.id.project_start_date)
	private TextView startLabel;

	@InjectView(value = R.id.project_end_date)
	private TextView endLabel;

	@InjectView(value = R.id.pager)
	private ViewPager viewPager;

	@InjectView(value = R.id.pager_header)
	private PagerTitleStrip pagerTitleStrip;

	@InjectView(value = R.id.assignees)
	private TextView assigneesLabel;

	private ProjectPagerAdapter pagerAdapter;

	private Backlog backlog;

	private Project project;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		backlog = getIntent().getParcelableExtra(EXTRA_BACKLOG);

		pagerTitleStrip.setBackgroundResource(R.drawable.gradient_stories_title);

		projectService.getProjectData(
				backlog.getId(),
				new Listener<Project>() {

					@Override
					public void onResponse(final Project project) {
						ProjectActivity.this.project = project;

						final List<Fragment> fragments = new LinkedList<Fragment>();
						fragments.add(ProjectLeafStoriesFragment.newInstance(project));
						pagerAdapter = new ProjectPagerAdapter(ProjectActivity.this, getSupportFragmentManager(), fragments);
						viewPager.setAdapter(pagerAdapter);

						viewFlipper.setDisplayedChild(ACTIVITY_VIEW);

						final Backlog projectParent = project.getParent();
						productLabel.setText(projectParent.getName());
						projectLabel.setText(backlog.getName());
						startLabel.setText(
								DateUtils.formatDate(project.getStartDate(), DateUtils.DATE_PATTERN));
						endLabel.setText(
								DateUtils.formatDate(project.getEndDate(), DateUtils.DATE_PATTERN));
						assigneesLabel.setText(
								IterationUtils.getResposiblesDisplay(project.getAssignees()));
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(ProjectActivity.this, R.string.failed_to_retrieve_project_details, Toast.LENGTH_SHORT).show();
					}
				});

		assigneesLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Fragment fragment = UserChooserFragment.newInstance(
						project.getAssignees(),
						new OnUsersSubmittedListener() {

							@Override
							public void onSubmitUsers(final List<User> users) {
								project.setAssignees(users);
								assigneesLabel.setText(
										IterationUtils.getResposiblesDisplay(users));

								projectService.updateProject(
										project,
										new Listener<Project>() {

											@Override
											public void onResponse(final Project project) {
												Toast.makeText(ProjectActivity.this, R.string.feedback_success_updated_project, Toast.LENGTH_SHORT).show();
											}
										},
										new ErrorListener() {

											@Override
											public void onErrorResponse(final VolleyError arg0) {
												Toast.makeText(ProjectActivity.this, R.string.feedback_failed_update_project, Toast.LENGTH_SHORT).show();
											}
										});
							}
						});

				getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, fragment)
					.addToBackStack(null)
					.commit();
			}
		});
	}
}
