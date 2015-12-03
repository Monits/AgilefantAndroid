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
import com.monits.agilefant.model.Product;
import com.monits.agilefant.service.BacklogService;

import javax.inject.Inject;

public class AllBacklogsFragment extends Fragment {

	@Inject
	/* default */BacklogService backlogService;

	private AllBacklogsAdapter allBacklogsAdapter;

	/**
	 * @return a new AllBacklogsFragment
	 */
	public static AllBacklogsFragment newInstance() {
		return new AllBacklogsFragment();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		AgilefantApplication.getObjectGraph().inject(this);
		return inflater.inflate(R.layout.fragment_backlogs, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		final RecyclerView allBacklogs = (RecyclerView) view.findViewById(R.id.all_backlogs);
		final TextView emptyView = (TextView) view.findViewById(R.id.backlogs_empty);

		allBacklogsAdapter = new AllBacklogsAdapter(getActivity());
		allBacklogs.setLayoutManager(new LinearLayoutManager(getActivity()));
		allBacklogs.setAdapter(allBacklogsAdapter);

		final View loadingView = view.findViewById(R.id.loading_view);
		loadingView.setVisibility(View.VISIBLE);
		backlogService.getAllBacklogs(
			new Listener<List<Product>>() {

				@Override
				public void onResponse(final List<Product> response) {
					if (response != null) {
						// If list is empty we show an empty message
						if (response.isEmpty()) {
							allBacklogs.setVisibility(View.GONE);
							emptyView.setVisibility(View.VISIBLE);
						} else {
							allBacklogsAdapter.setBacklogs(response);
						}
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
