package com.monits.agilefant.activity;

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
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.fragment.project.ProjectLeafStoriesFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment.OnUsersSubmittedListener;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.ProjectService;
import com.monits.agilefant.util.DateUtils;
import com.monits.agilefant.util.IterationUtils;

import java.util.LinkedList;
import java.util.List;

public class ProjectActivity extends BaseToolbaredActivity {

	private static final int ACTIVITY_VIEW = 1;

	public static final String EXTRA_BACKLOG = "com.monits.agilefant.intent.extra.PROJECT";

	@Inject
	private ProjectService projectService;

	private ViewFlipper viewFlipper;
	private TextView productLabel;
	private TextView projectLabel;
	private TextView startLabel;
	private TextView endLabel;
	private ViewPager viewPager;
	private TextView assigneesLabel;

	private Backlog backlog;
	private Project project;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_project);
		backlog = (Backlog) getIntent().getSerializableExtra(EXTRA_BACKLOG);

		final PagerTitleStrip pagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pager_header);
		pagerTitleStrip.setBackgroundResource(R.drawable.gradient_stories_title);

		projectService.getProjectData(
			backlog.getId(),
			new Listener<Project>() {

				@Override
				public void onResponse(final Project project) {
					ProjectActivity.this.project = project;

					final List<Fragment> fragments = new LinkedList<>();
					fragments.add(ProjectLeafStoriesFragment.newInstance(project));
					final ScreenSlidePagerAdapter pagerAdapter =
							new ScreenSlidePagerAdapter(ProjectActivity.this, getSupportFragmentManager(), fragments);
					viewPager = (ViewPager) findViewById(R.id.pager);
					viewPager.setAdapter(pagerAdapter);

					viewFlipper = (ViewFlipper) findViewById(R.id.root_flipper);
					viewFlipper.setDisplayedChild(ACTIVITY_VIEW);

					final Backlog projectParent = project.getParent();

					productLabel = (TextView) findViewById(R.id.product);
					productLabel.setText(projectParent.getName());
					projectLabel = (TextView) findViewById(R.id.project);
					projectLabel.setText(backlog.getName());
					startLabel = (TextView) findViewById(R.id.project_start_date);
					startLabel.setText(DateUtils.formatDate(project.getStartDate(), DateUtils.DATE_PATTERN));
					endLabel = (TextView) findViewById(R.id.project_end_date);
					endLabel.setText(DateUtils.formatDate(project.getEndDate(), DateUtils.DATE_PATTERN));
					assigneesLabel.setText(IterationUtils.getResposiblesDisplay(project.getAssignees()));
				}
			},
			new ErrorListener() {

				@Override
				public void onErrorResponse(final VolleyError arg0) {
					Toast.makeText(
						ProjectActivity.this, R.string.failed_to_retrieve_project_details, Toast.LENGTH_SHORT).show();
				}
			});

		assigneesLabel = (TextView) findViewById(R.id.assignees);
		assigneesLabel.setOnClickListener(getClickListener());
	}

	// Not much of an improvement on extract the listeners so we better suppress the warning
	@SuppressWarnings("checkstyle:anoninnerlength")
	private OnClickListener getClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Fragment fragment = UserChooserFragment.newInstance(
					project.getAssignees(),
					new OnUsersSubmittedListener() {

						@Override
						public void onSubmitUsers(final List<User> users) {
							project.setAssignees(users);
							assigneesLabel.setText(IterationUtils.getResposiblesDisplay(users));

							projectService.updateProject(
								project,
								new Listener<Project>() {
									@Override
									public void onResponse(final Project project) {
										Toast.makeText(ProjectActivity.this,
											R.string.feedback_success_updated_project, Toast.LENGTH_SHORT).show();
									}
								},
								new ErrorListener() {
									@Override
									public void onErrorResponse(final VolleyError arg0) {
										Toast.makeText(ProjectActivity.this,
											R.string.feedback_failed_update_project, Toast.LENGTH_SHORT).show();
									}
								});
						}
					});

				getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, fragment)
					.addToBackStack(null)
					.commit();
			}
		};
	}

	@Override
	public String toString() {
		return "ProjectFragment [backlog: " + backlog + "project" + project + ']';
	}
}
