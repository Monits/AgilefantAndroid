package com.monits.agilefant.fragment.iteration;

import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.StoriesAdapter;
import com.monits.agilefant.model.Story;

public class StoriesFragment extends RoboFragment{

	private List<Story> stories;

	private ExpandableListView storiesListView;

	public StoriesFragment(List<Story> stories) {
		this.stories = stories;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stories, container, false);

		storiesListView = (ExpandableListView)rootView.findViewById(R.id.stories);
		storiesListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		storiesListView.setAdapter(new StoriesAdapter(rootView.getContext(), stories));
		return rootView;
	}

}
