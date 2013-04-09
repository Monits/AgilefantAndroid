package com.monits.agilefant.fragment.iteration;

import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.TaskWithoutStoryAdaptar;
import com.monits.agilefant.model.Task;

public class TaskWithoutStoryFragment extends RoboFragment{

	private List<Task> taskWithoutStory;

	private ListView taskWithoutStoryListView;

	public TaskWithoutStoryFragment(List<Task> taskWithoutStory) {
		this.taskWithoutStory = taskWithoutStory;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_task_without_story, container, false);

		taskWithoutStoryListView = (ListView)rootView.findViewById(R.id.task_without_story);
		taskWithoutStoryListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		taskWithoutStoryListView.setAdapter(new TaskWithoutStoryAdaptar(getActivity(), taskWithoutStory));
		return rootView;
	}

}
