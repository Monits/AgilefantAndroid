package com.monits.agilefant.fragment.dailywork;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.MyStoriesAdapter;
import com.monits.agilefant.model.Story;

public class MyStoriesFragment extends RoboFragment {

	private static final String STORIES_KEY = "STORIES";
	private List<Story> mStories;

	public static MyStoriesFragment newInstance(List<Story> stories) {
		MyStoriesFragment storiesFragment = new MyStoriesFragment();
		Bundle arguments = new Bundle();

		ArrayList<Story> myStories = new ArrayList<Story>();
		myStories.addAll(stories);
		arguments.putParcelableArrayList(STORIES_KEY, myStories);

		storiesFragment.setArguments(arguments);

		return storiesFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mStories = getArguments().getParcelableArrayList(STORIES_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_stories, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ExpandableListView expandableStories = (ExpandableListView) view.findViewById(R.id.my_stories_expandable);
		View emptyView = view.findViewById(R.id.my_stories_empty_view);

		MyStoriesAdapter storiesAdapter = new MyStoriesAdapter(getActivity(), mStories);
		expandableStories.setAdapter(storiesAdapter);
		expandableStories.setEmptyView(emptyView);
	}
}
