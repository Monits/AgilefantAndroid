package com.monits.agilefant.fragment.dailywork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.TasksRecyclerAdapter;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.recycler.SpacesSeparatorItemDecoration;

public class MyQueueWorkFragment extends Fragment {

	private static final String TASKS_KEY = "TASKS";

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

		if (savedInstanceState == null) {
			mTasks = (List<Task>) getArguments().getSerializable(TASKS_KEY);
		} else {
			mTasks = (List<Task>) savedInstanceState.getSerializable(TASKS_KEY);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_queued_work, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final RecyclerView tasksListView = (RecyclerView) view.findViewById(R.id.tasks_list);
		final View emptyView = view.findViewById(R.id.queue_empty_view);

		if (mTasks.isEmpty()) {
			emptyView.setVisibility(View.VISIBLE);
			tasksListView.setVisibility(View.GONE);
		} else {
			tasksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
			final TasksRecyclerAdapter tasksAdapter = new TasksRecyclerAdapter(getActivity(), mTasks);
			tasksListView.addItemDecoration(new SpacesSeparatorItemDecoration(getActivity()));
			tasksListView.setAdapter(tasksAdapter);
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(TASKS_KEY, (Serializable) mTasks);
	}
}
