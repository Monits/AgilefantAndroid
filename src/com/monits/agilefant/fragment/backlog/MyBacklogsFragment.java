package com.monits.agilefant.fragment.backlog;

import java.util.List;

import roboguice.fragment.RoboFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.adapter.ProjectAdapter;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.IterationService;

public class MyBacklogsFragment extends RoboFragment {

	@Inject
	private BacklogService backlogService;

	@Inject
	private IterationService iterationService;

	private ProjectAdapter backlogsAdapter;

	public static MyBacklogsFragment newInstance() {
		return new MyBacklogsFragment();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_backlogs, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		final ExpandableListView allbackLogs = (ExpandableListView) view.findViewById(R.id.all_backlogs);

		final TextView emptyView = (TextView) view.findViewById(R.id.backlogs_empty);
		emptyView.setText(R.string.empty_my_backlogs);

		backlogsAdapter = new ProjectAdapter(getActivity());
		allbackLogs.setAdapter(backlogsAdapter);
		allbackLogs.setIndicatorBounds(View.INVISIBLE, View.INVISIBLE);
		allbackLogs.setDivider(null);
		allbackLogs.setChildDivider(null);
		allbackLogs.setEmptyView(emptyView);
		allbackLogs.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(final ExpandableListView parent, final View v,
					final int groupPosition, final int childPosition, final long id) {

				final Iteration iteration = backlogsAdapter.getChild(groupPosition, childPosition);
				final String projectName = backlogsAdapter.getGroup(groupPosition).getTitle();

				final FragmentActivity context = getActivity();
				final ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(false);
				progressDialog.setMessage(getResources().getString(R.string.loading));
				progressDialog.show();
				iterationService.getIteration(
						iteration.getId(),
						new Listener<Iteration>() {

							@Override
							public void onResponse(final Iteration response) {
								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}

								final Intent intent = new Intent(context, IterationActivity.class);

								intent.putExtra(IterationActivity.ITERATION, response);
								intent.putExtra(IterationActivity.PROJECTNAME, projectName);

								context.startActivity(intent);
							}
						},
						new ErrorListener() {

							@Override
							public void onErrorResponse(final VolleyError arg0) {
								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}

								Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
							}
						});

				return true;
			}
		});

		final View loadingView = view.findViewById(R.id.loading_view);
		loadingView.setVisibility(View.VISIBLE);
		backlogService.getMyBacklogs(
				new Listener<List<Project>>() {

					@Override
					public void onResponse(final List<Project> response) {
						if (response != null) {
							backlogsAdapter.setProjects(response);
						}

						loadingView.setVisibility(View.GONE);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						loadingView.setVisibility(View.GONE);

						Toast.makeText(getActivity(), R.string.error_retrieve_my_backlogs, Toast.LENGTH_SHORT).show();
					}
				});
	}
}
