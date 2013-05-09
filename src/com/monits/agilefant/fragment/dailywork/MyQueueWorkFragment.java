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

public class MyQueueWorkFragment extends RoboFragment {

	private static final String TASKS_KEY = "TASKS";
	private List<DailyWorkTask> mTasks;

	public static MyQueueWorkFragment newInstance(List<DailyWorkTask> queuedTasks) {
		MyQueueWorkFragment queueWorkFragment = new MyQueueWorkFragment();
		Bundle arguments = new Bundle();

		ArrayList<DailyWorkTask> tasks = new ArrayList<DailyWorkTask>();
		tasks.addAll(queuedTasks);
		arguments.putParcelableArrayList(TASKS_KEY, tasks);

		queueWorkFragment.setArguments(arguments);

		return queueWorkFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTasks = getArguments().getParcelableArrayList(TASKS_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_queued_work, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ListView tasksListView = (ListView) view.findViewById(R.id.tasks_list);
		View emptyView = view.findViewById(R.id.queue_empty_view);

		MyTasksAdapter tasksAdapter = new MyTasksAdapter(getActivity(), mTasks);
		tasksListView.setAdapter(tasksAdapter);
		tasksListView.setEmptyView(emptyView);
	}
}
