package com.monits.agilefant.fragment.backlog;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.AllBacklogsAdapter;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.IterationService;

import javax.inject.Inject;

public class MyBacklogsFragment extends Fragment {

	@Inject
	/* default */ BacklogService backlogService;

	@Inject
	/* default */ IterationService iterationService;

	private AllBacklogsAdapter backlogsAdapter;

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
		final RecyclerView allbackLogs = (RecyclerView) view.findViewById(R.id.all_backlogs);

		final TextView emptyView = (TextView) view.findViewById(R.id.backlogs_empty);
		emptyView.setText(R.string.empty_my_backlogs);

		backlogsAdapter = new AllBacklogsAdapter(getActivity());
		allbackLogs.setLayoutManager(new LinearLayoutManager(getActivity()));
		allbackLogs.setAdapter(backlogsAdapter);

		final View loadingView = view.findViewById(R.id.loading_view);
		loadingView.setVisibility(View.VISIBLE);
		backlogService.getMyBacklogs(
			new Listener<List<Project>>() {

				@Override
				public void onResponse(final List<Project> response) {
					if (response != null) {
						// If list is empty we show an empty message
						if (response.isEmpty()) {
							emptyView.setVisibility(View.VISIBLE);
							allbackLogs.setVisibility(View.GONE);
						} else {
							backlogsAdapter.setBacklogs(response);
						}
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

	@Override
	public String toString() {
		return "MyBacklogsFragment{"
				+ "backlogsAdapter=" + backlogsAdapter
				+ '}';
	}
}
