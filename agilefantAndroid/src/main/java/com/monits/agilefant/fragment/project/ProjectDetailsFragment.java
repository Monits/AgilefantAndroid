package com.monits.agilefant.fragment.project;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.fragment.iteration.BaseDetailTabFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.ProjectService;
import com.monits.agilefant.util.DateUtils;
import com.monits.agilefant.util.IterationUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectDetailsFragment extends BaseDetailTabFragment {

	private static final String PARAMS_PROJECT = "project";

	@Inject
	ProjectService projectService;

	private Project project;

	@Bind(R.id.assignees)
	TextView assigneesLabel;

	/**
	 * Return a new instance of ProjectDetailsFragment
	 * @param project The project
	 * @return a new ProjectDetailsFragment object
	 */
	public static ProjectDetailsFragment newInstance(final Project project) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(PARAMS_PROJECT, project);

		final ProjectDetailsFragment fragment = new ProjectDetailsFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AgilefantApplication.getObjectGraph().inject(this);

		final Bundle arguments = getArguments();
		project = (Project) arguments.getSerializable(PARAMS_PROJECT);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_project_details, container, false);
		ButterKnife.bind(this, rootView);
		final TextView productLabel = (TextView) rootView.findViewById(R.id.product);
		final TextView projectLabel = (TextView) rootView.findViewById(R.id.project);
		final TextView startLabel = (TextView) rootView.findViewById(R.id.project_start_date);
		final TextView endLabel = (TextView) rootView.findViewById(R.id.project_end_date);

		productLabel.setText(project.getParent().getName());
		projectLabel.setText(project.getTitle());
		startLabel.setText(DateUtils.formatDate(project.getStartDate(), DateUtils.DATE_PATTERN));
		endLabel.setText(DateUtils.formatDate(project.getEndDate(), DateUtils.DATE_PATTERN));
		assigneesLabel.setText(IterationUtils.getResposiblesDisplay(project.getAssignees()));
		assigneesLabel.setOnClickListener(getClickListener());

		return rootView;
	}

	private View.OnClickListener getClickListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Fragment fragment = getUserChooserFragment();

				getActivity().getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, fragment)
					.addToBackStack(null)
					.commit();
			}
		};
	}

	private UserChooserFragment getUserChooserFragment() {
		return UserChooserFragment.newInstance(
			project.getAssignees(),
			new UserChooserFragment.OnUsersSubmittedListener() {

				@Override
				public void onSubmitUsers(final List<User> users) {
					project.setAssignees(users);
					assigneesLabel.setText(IterationUtils.getResposiblesDisplay(users));

					projectService.updateProject(
						project,
						new Response.Listener<Project>() {
							@Override
							public void onResponse(final Project project) {
								Toast.makeText(getActivity(),
									R.string.feedback_success_updated_project, Toast.LENGTH_SHORT).show();
							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(final VolleyError arg0) {
								Toast.makeText(
									getActivity(), R.string.feedback_failed_update_project, Toast.LENGTH_SHORT).show();
							}
						});
				}
			});
	}

	@Override
	public int getTitleResourceId() {
		return R.string.project_details;
	}

	@Override
	public String toString() {
		return "ProjectDetailsFragment [project: " + project + ", backlog: " + project.getParent() + ']';
	}
}
