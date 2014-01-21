package com.monits.agilefant.fragment.backlog;

import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.BacklogsAdapter;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.service.BacklogService;

public class AllBacklogsFragment extends RoboFragment {

	@Inject
	private BacklogService backlogService;

	private BacklogsAdapter backlogsAdapter;

	public static AllBacklogsFragment newInstance() {
		return new AllBacklogsFragment();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_backlogs, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		final ExpandableListView allbackLogs = (ExpandableListView) view.findViewById(R.id.all_backlogs);

		backlogsAdapter = new BacklogsAdapter(getActivity());
		allbackLogs.setAdapter(backlogsAdapter);

		final View loadingView = view.findViewById(R.id.loading_view);
		loadingView.setVisibility(View.VISIBLE);
		backlogService.getAllBacklogs(
				new Listener<List<Product>>() {

					@Override
					public void onResponse(final List<Product> response) {
						if (response != null) {
							backlogsAdapter.setBacklogs(response);
						}

						loadingView.setVisibility(View.GONE);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						loadingView.setVisibility(View.GONE);

						Toast.makeText(getActivity(), R.string.error_retrieve_backlogs, Toast.LENGTH_SHORT).show();
					}
				});
	}
}
