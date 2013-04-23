package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.StoriesAdapter;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;

public class StoriesFragment extends RoboFragment implements Observer {

	private static final String STORIES = "STORIES";

	private List<Story> stories;

	private ExpandableListView storiesListView;

	private StoriesAdapter storiesAdapter;

	public static StoriesFragment newInstance(ArrayList<Story> stories){
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(STORIES, stories);

		StoriesFragment storiesFragment = new StoriesFragment();
		storiesFragment.setArguments(bundle);

		return storiesFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		this.stories= arguments.getParcelableArrayList(STORIES);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stories, container, false);

		storiesListView = (ExpandableListView)rootView.findViewById(R.id.stories);
		storiesListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		storiesAdapter = new StoriesAdapter(rootView.getContext(), stories);
		storiesListView.setAdapter(storiesAdapter);

		storiesListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent,
					View v, int groupPosition, int childPosition, long id) {
				Task task = storiesAdapter.getChild(groupPosition, childPosition);
				task.addObserver(StoriesFragment.this);

				FragmentTransaction transaction = getParentFragment().getFragmentManager().beginTransaction();
				transaction.add(R.id.container, SpentEffortFragment.newInstance(task));
				transaction.addToBackStack(null);
				transaction.commit();

				return false;
			}
		});

		return rootView;
	}

	@Override
	public void update(Observable observable, Object data) {
		if (isVisible()) {
			storiesAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}
}
