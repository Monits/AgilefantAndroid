package com.monits.agilefant.fragment.dailywork;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.MyTasksAdapter;
import com.monits.agilefant.model.DailyWorkTask;

public class MyTasksFragment extends RoboFragment {

	private static final String TASKS_KEY = "TASKS";
	private List<DailyWorkTask> mTasks;

	public static MyTasksFragment newInstance(List<DailyWorkTask> taskWithoutStories) {
		MyTasksFragment tasksFragment = new MyTasksFragment();
		Bundle arguments = new Bundle();

		ArrayList<DailyWorkTask> tasks = new ArrayList<DailyWorkTask>();
		tasks.addAll(taskWithoutStories);
		arguments.putParcelableArrayList(TASKS_KEY, tasks);

		tasksFragment.setArguments(arguments);

		return tasksFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTasks = getArguments().getParcelableArrayList(TASKS_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_tasks, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ListView tasksListView = (ListView) view.findViewById(R.id.tasks_list);
		View emptyView = view.findViewById(R.id.tasks_empty_view);

		MyTasksAdapter tasksAdapter = new MyTasksAdapter(getActivity(), mTasks);
		tasksListView.setAdapter(tasksAdapter);
		tasksListView.setEmptyView(emptyView);
	}

}
