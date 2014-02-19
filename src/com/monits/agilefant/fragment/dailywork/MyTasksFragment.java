package com.monits.agilefant.fragment.dailywork;

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
import com.monits.agilefant.adapter.MyTasksAdapter;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;
import com.monits.agilefant.model.Task;

public class MyTasksFragment extends RoboFragment implements Observer {

	private static final String TASKS_KEY = "TASKS";

	private List<Task> mTasks;

	private MyTasksAdapter tasksAdapter;

	public static MyTasksFragment newInstance(final List<Task> taskWithoutStories) {
		final MyTasksFragment tasksFragment = new MyTasksFragment();
		final Bundle arguments = new Bundle();

		final ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.addAll(taskWithoutStories);
		arguments.putSerializable(TASKS_KEY, tasks);

		tasksFragment.setArguments(arguments);

		return tasksFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTasks = (List<Task>) getArguments().getSerializable(TASKS_KEY);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_tasks, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ListView tasksListView = (ListView) view.findViewById(R.id.tasks_list);
		final View emptyView = view.findViewById(R.id.tasks_empty_view);

		tasksAdapter = new MyTasksAdapter(getActivity(), mTasks);
		tasksAdapter.setOnActionListener(new TaskAdapterViewActionListener(getActivity(), MyTasksFragment.this));
		tasksListView.setAdapter(tasksAdapter);
		tasksListView.setEmptyView(emptyView);
	}

	@Override
	public void update(final Observable observable, final Object arg1) {

		if (isVisible()) {
			tasksAdapter.notifyDataSetChanged();
			observable.deleteObserver(MyTasksFragment.this);
		}
	}
}