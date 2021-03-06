package com.monits.agilefant.fragment.iteration;

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

import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.WorkItemAdapter;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.recycler.SpacesSeparatorItemDecoration;
import com.monits.agilefant.recycler.WorkItemTouchHelperCallback;
import com.monits.agilefant.service.MetricsService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoriesFragment extends BaseDetailTabFragment implements SearchView.OnQueryTextListener {

	@Inject
	/* default */ MetricsService metricsService;

	@Bind(R.id.stories)
	/* default */ RecyclerView recyclerView;

	@Bind(R.id.stories_empty_view)
	/* default */ TextView emptyView;

	private static final String STORIES = "STORIES";
	private static final String ITERATION = "ITERATION";
	private static final String STORIES_ID = "STORIES_ID";


	private WorkItemAdapter storiesAdapter;

	private int storyPosition;

	@SuppressWarnings("checkstyle:anoninnerlength")
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (AgilefantApplication.ACTION_TASK_UPDATED.equals(intent.getAction())
					&& !StoriesFragment.this.isDetached()) {

				final Task updatedTask = (Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_TASK_UPDATED);
				storiesAdapter.onUpdate(updatedTask);
			}

			if (AgilefantApplication.ACTION_NEW_STORY.equals(intent.getAction())) {
				final Story newStory = (Story) intent.getSerializableExtra(AgilefantApplication.EXTRA_NEW_STORY);
				storiesAdapter.addStory(newStory);

				emptyView.setVisibility(View.GONE);
				recyclerView.setVisibility(View.VISIBLE);
			}
		}
	};

	/**
	 * Return a new StoriesFragment with the given stories and iteration
	 * @param stories The stories
	 * @param mIteration The iteration
	 * @return a new StoriesFragment with the given stories and iteration
	 */
	public static StoriesFragment newInstance(final ArrayList<Story> stories, final Iteration mIteration) {
		return newInstance(stories, mIteration, 0);
	}

	/**
	 * Return a new StoriesFragment with the given stories and iteration
	 * @param stories The stories
	 * @param mIteration The iteration
	 * @param storyFocus The specific story id for focus
	 * @return a new StoriesFragment with the given stories and iteration
	 */
	public static StoriesFragment newInstance(final ArrayList<Story> stories, final Iteration mIteration,
											final int storyFocus) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(STORIES, stories);
		bundle.putSerializable(ITERATION, mIteration);
		bundle.putSerializable(STORIES_ID, storyFocus);

		final StoriesFragment storiesFragment = new StoriesFragment();
		storiesFragment.setArguments(bundle);
		return storiesFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AgilefantApplication.getObjectGraph().inject(this);
		getActivity().registerReceiver(broadcastReceiver,
				AgilefantApplication.registerReceiverIntentFilter(AgilefantApplication.ACTION_NEW_STORY));

		setHasOptionsMenu(true);

		final Bundle arguments = getArguments();

		final List<Story> stories;

		final int storyFocusID;
		if (savedInstanceState == null) {
			stories = (List<Story>) arguments.getSerializable(STORIES);
			storyFocusID = arguments.getInt(STORIES_ID, 0);
		} else {
			stories = (List<Story>) savedInstanceState.getSerializable(STORIES);
			storyFocusID = 0;
		}

		if (storyFocusID != 0) {
			int position = 0; //Obtain Story position
			for (final Story story : stories) {
				if (story.getId() == storyFocusID) {
					storyPosition = position;
					break;
				}
				position++;
			}
		}



		storiesAdapter = new WorkItemAdapter(getActivity());
		storiesAdapter.setWorkItems(stories);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stories, container, false);

		ButterKnife.bind(this, rootView);
		final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.stories);

		recyclerView.setAdapter(storiesAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.addItemDecoration(new SpacesSeparatorItemDecoration(getContext()));


		final WorkItemTouchHelperCallback workItemTouchHelperCallback =
				new WorkItemTouchHelperCallback(storiesAdapter);
		final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(workItemTouchHelperCallback);
		itemTouchHelper.attachToRecyclerView(recyclerView);

		if (storiesAdapter.isEmpty()) {
			emptyView.setVisibility(View.VISIBLE);
			recyclerView.setVisibility(View.GONE);
		}

		recyclerView.scrollToPosition(storyPosition);

		return rootView;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public int getTitleResourceId() {
		return R.string.stories;
	}

	@Override
	public boolean onQueryTextSubmit(final String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(final String query) {
		storiesAdapter.filter(query);
		return true;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STORIES, new ArrayList<>(storiesAdapter.getWorkItems()));
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
	public String toString() {
		return "StoriesFragment{"
				+ "broadcastReceiver=" + broadcastReceiver
				+ ", storiesAdapter=" + storiesAdapter
				+ ", storyPosition=" + storyPosition
				+ '}';
	}
}
