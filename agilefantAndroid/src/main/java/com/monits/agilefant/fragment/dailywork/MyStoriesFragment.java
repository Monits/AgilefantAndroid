package com.monits.agilefant.fragment.dailywork;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.recyclerviewholders.DailyWorkWorkItemsAdapter;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.recycler.SpacesSeparatorItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyStoriesFragment extends Fragment implements Observer {

	private static final String STORIES_KEY = "STORIES";

	private DailyWorkWorkItemsAdapter adapter;

	@Bind(R.id.my_stories_empty_view)
	/* default */ TextView emptyView;

	/**
	 * Return a new MyQueueWorkFragment with the given stories
	 * @param stories the stories
	 * @return a new MyQueueWorkFragment with the given stories
	 */
	public static MyStoriesFragment newInstance(final List<Story> stories) {
		final MyStoriesFragment storiesFragment = new MyStoriesFragment();
		final Bundle arguments = new Bundle();

		final ArrayList<Story> myStories = new ArrayList<>();
		myStories.addAll(stories);
		arguments.putSerializable(STORIES_KEY, myStories);

		storiesFragment.setArguments(arguments);

		return storiesFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final List<Story> stories;
		if (savedInstanceState == null) {
			stories = (List<Story>) getArguments().getSerializable(STORIES_KEY);
		} else {
			stories = (List<Story>) savedInstanceState.getSerializable(STORIES_KEY);
		}

		adapter = new DailyWorkWorkItemsAdapter(getActivity(), new ArrayList<WorkItem>(stories));
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_my_stories, container, false);
		ButterKnife.bind(this, view);

		final RecyclerView recyclerViewStories = (RecyclerView) view.findViewById(R.id.my_stories_expandable);
		recyclerViewStories.addItemDecoration(new SpacesSeparatorItemDecoration(getContext()));

		if (adapter.isEmpty()) {
			emptyView.setVisibility(View.VISIBLE);

			recyclerViewStories.setVisibility(View.GONE);
		}

		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final RecyclerView recyclerViewStories = (RecyclerView) view.findViewById(R.id.my_stories_expandable);
		recyclerViewStories.setAdapter(adapter);
		recyclerViewStories.setLayoutManager(new LinearLayoutManager(getActivity()));
	}

	@Override
	public void update(final Observable observable, final Object data) {
		if (isVisible()) {
			adapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STORIES_KEY, (Serializable) adapter.getWorkItems());
	}
}