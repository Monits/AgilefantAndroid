package com.monits.agilefant.fragment.dailywork;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.MyTasksAdapter;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;
import com.monits.agilefant.model.Task;

public class MyQueueWorkFragment extends Fragment implements Observer {

	private static final String TASKS_KEY = "TASKS";

	private MyTasksAdapter tasksAdapter;

	private List<Task> mTasks;

	/**
	 * Return a new MyQueueWorkFragment with the given queued tasks
	 * @param queuedTasks The queued tasks
	 * @return a new MyQueueWorkFragment with the given queued tasks
	 */
	public static MyQueueWorkFragment newInstance(final List<Task> queuedTasks) {
		final MyQueueWorkFragment queueWorkFragment = new MyQueueWorkFragment();
		final Bundle arguments = new Bundle();

		final ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.addAll(queuedTasks);
		arguments.putSerializable(TASKS_KEY, tasks);

		queueWorkFragment.setArguments(arguments);

		return queueWorkFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTasks = (List<Task>) getArguments().getSerializable(TASKS_KEY);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_queued_work, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ListView tasksListView = (ListView) view.findViewById(R.id.tasks_list);
		final View emptyView = view.findViewById(R.id.queue_empty_view);

		tasksAdapter = new MyTasksAdapter(getActivity(), mTasks);
		tasksAdapter.setOnActionListener(new TaskAdapterViewActionListener(getActivity(), MyQueueWorkFragment.this));
		tasksListView.setAdapter(tasksAdapter);
		tasksListView.setEmptyView(emptyView);
	}

	@Override
	public void update(final Observable observable, final Object arg1) {

		if (isVisible()) {
			tasksAdapter.notifyDataSetChanged();
			observable.deleteObserver(MyQueueWorkFragment.this);
		}
	}
}
