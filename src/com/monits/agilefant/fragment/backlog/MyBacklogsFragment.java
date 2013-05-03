package com.monits.agilefant.fragment.backlog;

import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ProjectAdapter;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.task.GetIteration;
import com.monits.agilefant.task.GetMyBacklogsTask;

public class MyBacklogsFragment extends RoboFragment {

	@Inject
	private GetMyBacklogsTask getMyBacklogsTask;

	@Inject
	private GetIteration getIteration;

	private ProjectAdapter backlogsAdapter;

	public static MyBacklogsFragment newInstance() {
		return new MyBacklogsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_backlogs, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ExpandableListView allbackLogs = (ExpandableListView) view.findViewById(R.id.all_backlogs);

		backlogsAdapter = new ProjectAdapter(getActivity());
		allbackLogs.setAdapter(backlogsAdapter);
		allbackLogs.setIndicatorBounds(View.INVISIBLE, View.INVISIBLE);

		allbackLogs.setDivider(null);
		allbackLogs.setChildDivider(null);

		allbackLogs.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				Iteration iteration = backlogsAdapter.getChild(groupPosition, childPosition);
				String projectName = backlogsAdapter.getGroup(groupPosition).getTitle();

				getIteration.configure(projectName, iteration.getId());
				getIteration.execute();

				return true;
			}
		});

		getMyBacklogsTask.configure(
				view.findViewById(R.id.loading_view),
				new TaskCallback<List<Project>>() {

					@Override
					public void onSuccess(List<Project> response) {
						if (response != null) {
							backlogsAdapter.setProjects(response);
						}
					}

					@Override
					public void onError() {
						Toast.makeText(getActivity(), R.string.error_retrieve_my_backlogs, Toast.LENGTH_SHORT).show();
					}
				});

		getMyBacklogsTask.execute();
	}
}
