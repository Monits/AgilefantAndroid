package com.monits.agilefant.fragment.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ProjectLeafStoriesRecyclerAdapter;
import com.monits.agilefant.fragment.iteration.BaseDetailTabFragment;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.recycler.SpacesSeparatorItemDecoration;
import com.monits.agilefant.recycler.WorkItemTouchHelperCallback;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.ProjectService;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ProjectLeafStoriesFragment extends BaseDetailTabFragment implements SearchView.OnQueryTextListener {

	private static final String BACKLOG = "PROJECT_BACKLOG";

	private Project project;

	@Inject
	/* default */ ProjectService projectService;

	@Inject
	/* default */ MetricsService metricsService;

	@Bind(R.id.root_flipper)
	/* default */ ViewFlipper viewFlipper;

	@Bind(R.id.stories_empty_view)
	/* default */ TextView emptyView;

	private ItemTouchHelper itemTouchHelper;

	@SuppressFBWarnings(
		value = "MISSING_FIELD_IN_TO_STRING", justification = "It's a view, we don't need this in toString")
	private RecyclerView storiesListView;

	private ProjectLeafStoriesRecyclerAdapter storiesAdapter;
	private List<Story> stories;

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (AgilefantApplication.ACTION_NEW_STORY.equals(intent.getAction())) {
				final Story newStory = (Story) intent.getSerializableExtra(AgilefantApplication.EXTRA_NEW_STORY);
				stories.add(newStory);

				storiesAdapter.setStories(stories);
			}
		}
	};

	/**
	 * Create a new ProjectLeafStoriesFragment with the given project
	 * @param projectBacklog The project
	 * @return a new ProjectLeafStoriesFragment with the given project
	 */
	public static ProjectLeafStoriesFragment newInstance(final Project projectBacklog) {
		final ProjectLeafStoriesFragment fragment = new ProjectLeafStoriesFragment();

		final Bundle args = new Bundle();
		args.putSerializable(BACKLOG, projectBacklog);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		getActivity().registerReceiver(broadcastReceiver, AgilefantApplication.registerReceiverIntentFilter(
				AgilefantApplication.ACTION_NEW_STORY));

		super.onResume();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		final Bundle arguments = getArguments();
		project = (Project) arguments.getSerializable(BACKLOG);
		AgilefantApplication.getObjectGraph().inject(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view =
				LayoutInflater.from(getActivity()).inflate(R.layout.fragment_project_leaf_stories, container, false);

		ButterKnife.bind(this, view);

		setHasOptionsMenu(true);

		storiesListView = (RecyclerView) view.findViewById(R.id.stories_list);

		if (stories == null || stories.isEmpty()) {
			emptyView.setVisibility(View.VISIBLE);
			storiesListView.setVisibility(View.GONE);
		}
		storiesAdapter = new ProjectLeafStoriesRecyclerAdapter(getActivity(), project);

		storiesListView.setLayoutManager(new LinearLayoutManager(getActivity()));
		storiesListView.setAdapter(storiesAdapter);
		storiesListView.addItemDecoration(new SpacesSeparatorItemDecoration(getContext()));

		final WorkItemTouchHelperCallback workItemTouchHelperCallback =
				new WorkItemTouchHelperCallback(storiesAdapter);
		itemTouchHelper = new ItemTouchHelper(workItemTouchHelperCallback);
		itemTouchHelper.attachToRecyclerView(storiesListView);

		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
		projectService.getProjectLeafStories(
			project.getId(),
			new Listener<List<Story>>() {

				@Override
				public void onResponse(final List<Story> stories) {
					ProjectLeafStoriesFragment.this.stories = stories;
					if (!stories.isEmpty()) {
						storiesAdapter.setStories(stories);
						emptyView.setVisibility(View.GONE);
						storiesListView.setVisibility(View.VISIBLE);
					}
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

	@Override
	public void onPrepareOptionsMenu(final Menu menu) {
		super.onPrepareOptionsMenu(menu);
		final MenuItem item = menu.findItem(R.id.action_search);
		item.setVisible(true);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
		searchView.setOnQueryTextListener(this);
	}

	@Override
	public int getTitleResourceId() {
		return R.string.backlog;
	}

	@Override
	public String toString() {
		return "ProjectLeafStoriesFragment [project: " + project
				+ ", adapter: " + storiesAdapter + ']';
	}

	@Override
	public boolean onQueryTextSubmit(final String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(final String newText) {
		if (newText.isEmpty()) {
			// Attach callback
			itemTouchHelper.attachToRecyclerView(storiesListView);

		} else {
			// dettach callback
			itemTouchHelper.attachToRecyclerView(null);
		}
		// Apply filter
		storiesAdapter.filter(newText);

		return true;
	}
}
