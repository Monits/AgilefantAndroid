package com.monits.agilefant.fragment.dailywork;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.MyStoriesAdapter;
import com.monits.agilefant.listeners.implementations.StoryAdapterViewActionListener;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;
import com.monits.agilefant.model.Story;

public class MyStoriesFragment extends Fragment implements Observer {

	private static final String STORIES_KEY = "STORIES";

	private MyStoriesAdapter storiesAdapter;

	private List<Story> mStories;

	/**
	 * Return a new MyQueueWorkFragment with the given stories
	 * @param stories the stories
	 * @return a new MyQueueWorkFragment with the given stories
	 */
	public static MyStoriesFragment newInstance(final List<Story> stories) {
		final MyStoriesFragment storiesFragment = new MyStoriesFragment();
		final Bundle arguments = new Bundle();

		final ArrayList<Story> myStories = new ArrayList<Story>();
		myStories.addAll(stories);
		arguments.putSerializable(STORIES_KEY, myStories);

		storiesFragment.setArguments(arguments);

		return storiesFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mStories = (List<Story>) getArguments().getSerializable(STORIES_KEY);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_stories, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ExpandableListView expandableStories = (ExpandableListView) view.findViewById(R.id.my_stories_expandable);
		final View emptyView = view.findViewById(R.id.my_stories_empty_view);

		final FragmentActivity activity = getActivity();
		storiesAdapter = new MyStoriesAdapter(activity, mStories);
		storiesAdapter.setOnGroupActionListener(new StoryAdapterViewActionListener(activity, MyStoriesFragment.this));
		storiesAdapter.setOnChildActionListener(new TaskAdapterViewActionListener(activity, MyStoriesFragment.this));
		expandableStories.setAdapter(storiesAdapter);
		expandableStories.setEmptyView(emptyView);
	}

	@Override
	public void update(final Observable observable, final Object data) {

		if (isVisible()) {
			storiesAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}
}