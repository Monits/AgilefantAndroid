package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.TaskWithoutStoryAdapter;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;
import com.monits.agilefant.model.Task;

public class TaskWithoutStoryFragment extends RoboFragment implements Observer {

	private List<Task> taskWithoutStory;

	private ListView taskWithoutStoryListView;

	private TaskWithoutStoryAdapter taskWithoutStoryAdapter;


	public static TaskWithoutStoryFragment newInstance(final ArrayList<Task> taskWithoutStory) {
		final Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("TASK_WITHOUT_STORIES", taskWithoutStory);

		final TaskWithoutStoryFragment taskWithoutStoryFragment = new TaskWithoutStoryFragment();
		taskWithoutStoryFragment.setArguments(bundle);

		return taskWithoutStoryFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle arguments = getArguments();
		this.taskWithoutStory= arguments.getParcelableArrayList("TASK_WITHOUT_STORIES");
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_task_without_story, container, false);

		taskWithoutStoryListView = (ListView)rootView.findViewById(R.id.task_without_story);
		taskWithoutStoryListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		taskWithoutStoryAdapter = new TaskWithoutStoryAdapter(getActivity(), taskWithoutStory);
		taskWithoutStoryAdapter.setOnActionListener(new TaskAdapterViewActionListener(getActivity(), TaskWithoutStoryFragment.this));
		taskWithoutStoryListView.setAdapter(taskWithoutStoryAdapter);

		return rootView;
	}

	@Override
	public void update(final Observable observable, final Object data) {
		if (isVisible()) {
			taskWithoutStoryAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}
}
