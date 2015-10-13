package com.monits.agilefant.fragment.iteration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
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
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoriesFragment extends BaseDetailTabFragment implements Observer {

	@Inject
	/* default */ MetricsService metricsService;

	@Bind(R.id.stories)
	/* default */ RecyclerView recyclerView;

	@Bind(R.id.stories_empty_view)
	/* default */ TextView emptyView;

	private static final String STORIES = "STORIES";
	private static final String ITERATION = "ITERATION";

	private WorkItemAdapter storiesAdapter;

	@SuppressWarnings("checkstyle:anoninnerlength")
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (AgilefantApplication.ACTION_TASK_UPDATED.equals(intent.getAction())
					&& !StoriesFragment.this.isDetached()) {

				final Task updatedTask = (Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_TASK_UPDATED);
				storiesAdapter.updateTask(updatedTask);
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
		final Bundle bundle = new Bundle();
		bundle.putSerializable(STORIES, stories);
		bundle.putSerializable(ITERATION, mIteration);

		final StoriesFragment storiesFragment = new StoriesFragment();
		storiesFragment.setArguments(bundle);

		return storiesFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AgilefantApplication.getObjectGraph().inject(this);
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AgilefantApplication.ACTION_TASK_UPDATED);
		intentFilter.addAction(AgilefantApplication.ACTION_NEW_STORY);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);

		final Bundle arguments = getArguments();
		storiesAdapter = new WorkItemAdapter(getActivity());
		storiesAdapter.setWorkItems((List<Story>) arguments.getSerializable(STORIES));
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

		return rootView;
	}

	@Override
	public void update(final Observable observable, final Object data) {

		if (isVisible()) {
			storiesAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
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
}
