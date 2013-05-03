package com.monits.agilefant.fragment.backlog;

import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.BacklogsAdapter;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.task.GetBacklogsTask;

public class AllBacklogsFragment extends RoboFragment {

	@Inject
	private GetBacklogsTask getBacklogsTask;

	private BacklogsAdapter backlogsAdapter;

	public static AllBacklogsFragment newInstance() {
		return new AllBacklogsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_backlogs, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ExpandableListView allbackLogs = (ExpandableListView) view.findViewById(R.id.all_backlogs);

		backlogsAdapter = new BacklogsAdapter(getActivity());
		allbackLogs.setAdapter(backlogsAdapter);

		getBacklogsTask.configure(
				view.findViewById(R.id.loading_view),
				new TaskCallback<List<Product>>() {

					@Override
					public void onSuccess(List<Product> response) {
						if (response != null) {
							backlogsAdapter.setBacklogs(response);
						}
					}

					@Override
					public void onError() {
						Toast.makeText(getActivity(), R.string.error_retrieve_backlogs, Toast.LENGTH_SHORT).show();
					}
				});

		getBacklogsTask.execute();
	}
}
