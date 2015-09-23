package com.monits.agilefant.fragment.backlog;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.adapter.ProjectAdapter;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.IterationService;

import javax.inject.Inject;

public class MyBacklogsFragment extends Fragment {

	@Inject
	BacklogService backlogService;

	@Inject
	IterationService iterationService;

	private ProjectAdapter backlogsAdapter;

	/**
	 * @return a new MyBacklogsFragment
	 */
	public static MyBacklogsFragment newInstance() {
		return new MyBacklogsFragment();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		// Agilefant injection
		AgilefantApplication.getObjectGraph().inject(this);

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
		allbackLogs.setOnChildClickListener(getOnChildClickListener());
		allbackLogs.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> adapter, final View view, final int position,
					final long id) {
				final long packedPosition = allbackLogs.getExpandableListPosition(position);
				final int positionType = ExpandableListView.getPackedPositionType(packedPosition);

				if (positionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
					final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
					final Project project = backlogsAdapter.getGroup(groupPosition);

					final Intent intent = new Intent(getActivity(), ProjectActivity.class);
					intent.putExtra(ProjectActivity.EXTRA_BACKLOG, project);
					startActivity(intent);

					return true;
				}

				return false;
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

	private OnChildClickListener getOnChildClickListener() {
		return new OnChildClickListener() {

			@Override
			public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition,
					final int childPosition, final long id) {

				final Iteration iteration = backlogsAdapter.getChild(groupPosition, childPosition);
				final Project project = backlogsAdapter.getGroup(groupPosition);

				final FragmentActivity context = getActivity();
				final ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(false);
				progressDialog.setMessage(getResources().getString(R.string.loading));
				progressDialog.show();
				iterationService.getIteration(iteration.getId(), getSuccessListener(project, context, progressDialog),
						getErrorListener(context, progressDialog));

				return true;
			}
		};
	}

	private ErrorListener getErrorListener(final FragmentActivity context, final ProgressDialog progressDialog) {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError arg0) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
			}
		};
	}

	private Listener<Iteration> getSuccessListener(final Project project, final FragmentActivity context,
			final ProgressDialog progressDialog) {
		return new Listener<Iteration>() {

			@Override
			public void onResponse(final Iteration response) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				final Intent intent = new Intent(context, IterationActivity.class);

				// Workaround that may be patchy,
				// but it depends on the request whether it comes or not, and how to get it.
				response.setParent(project);

				intent.putExtra(IterationActivity.ITERATION, response);

				context.startActivity(intent);
			}
		};
	}

	@Override
	public String toString() {
		return "MyBacklogsFragment{"
				+ "backlogsAdapter=" + backlogsAdapter
				+ '}';
	}
}
