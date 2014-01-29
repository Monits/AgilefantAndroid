package com.monits.agilefant.fragment.project;

import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ProjectLeafStoriesAdapter;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.service.ProjectService;

public class ProjectLeafStoriesFragment extends RoboFragment {

	private static final String BACKLOG = "PROJECT_BACKLOG";

	private Backlog backlog;

	@Inject
	private ProjectService projectService;

	private ViewFlipper viewFlipper;
	private ListView storiesListView;
	private View storiesEmptyView;

	private ProjectLeafStoriesAdapter storiesAdapter;

	public static ProjectLeafStoriesFragment newInstance(final Backlog projectBacklog) {
		final ProjectLeafStoriesFragment fragment = new ProjectLeafStoriesFragment();

		final Bundle args = new Bundle();
		args.putSerializable(BACKLOG, projectBacklog);

		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		final Bundle arguments = getArguments();
		backlog = (Backlog) arguments.getSerializable(BACKLOG);

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = LayoutInflater.from(getActivity())
			.inflate(R.layout.fragment_project_leaf_stories, container, false);

		viewFlipper = (ViewFlipper) view.findViewById(R.id.root_flipper);
		storiesListView = (ListView) view.findViewById(R.id.stories_list);
		storiesEmptyView = view.findViewById(R.id.stories_empty_view);

		storiesAdapter = new ProjectLeafStoriesAdapter(getActivity(), getFragmentManager());
		storiesListView.setEmptyView(storiesEmptyView);
		storiesListView.setAdapter(storiesAdapter);

		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		projectService.getProjectLeafStories(
				backlog.getId(),
				new Listener<List<Story>>() {

					@Override
					public void onResponse(final List<Story> stories) {
						storiesAdapter.setStories(stories);

						viewFlipper.setDisplayedChild(1);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(getActivity(), "Failed to retrieve leaf stories.", Toast.LENGTH_SHORT).show();
					}
				});

		super.onViewCreated(view, savedInstanceState);
	}
}
