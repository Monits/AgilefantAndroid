package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.StoriesAdapter;
import com.monits.agilefant.listeners.implementations.StoryAdapterViewActionListener;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;

public class StoriesFragment extends RoboFragment implements Observer {

	private static final String STORIES = "STORIES";
	private List<Story> stories;

	private ExpandableListView storiesListView;
	private StoriesAdapter storiesAdapter;
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (intent.getAction().equals(AgilefantApplication.ACTION_TASK_UPDATED)
					&& !StoriesFragment.this.isDetached()) {

				final Task updatedTask = intent.getParcelableExtra(AgilefantApplication.EXTRA_TASK_UPDATED);

				for (final Story story : stories) {
					final List<Task> tasks = story.getTasks();
					final int indexOf = tasks.indexOf(updatedTask);
					if (indexOf != -1) {
						tasks.get(indexOf).updateValues(updatedTask);
						storiesAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	};

	public static StoriesFragment newInstance(final ArrayList<Story> stories) {
		final Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(STORIES, stories);

		final StoriesFragment storiesFragment = new StoriesFragment();
		storiesFragment.setArguments(bundle);

		return storiesFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AgilefantApplication.ACTION_TASK_UPDATED);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);

		final Bundle arguments = getArguments();
		this.stories= arguments.getParcelableArrayList(STORIES);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stories, container, false);

		storiesListView = (ExpandableListView)rootView.findViewById(R.id.stories);
		storiesListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		storiesAdapter = new StoriesAdapter(rootView.getContext(), stories);

		storiesAdapter.setOnChildActionListener(new TaskAdapterViewActionListener(getActivity(), StoriesFragment.this));
		storiesAdapter.setOnGroupActionListener(new StoryAdapterViewActionListener(getActivity(), StoriesFragment.this));
		storiesListView.setAdapter(storiesAdapter);

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
}
