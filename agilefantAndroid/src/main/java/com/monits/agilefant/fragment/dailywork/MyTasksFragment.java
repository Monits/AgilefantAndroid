package com.monits.agilefant.fragment.dailywork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ProjectAdapter;
import com.monits.agilefant.adapter.TasksRecyclerAdapter;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.BacklogService;

import javax.inject.Inject;

public class MyTasksFragment extends Fragment implements Observer {

	private static final String TASKS_KEY = "TASKS";
	private static final String PROJECTS_KEY = "PROJECTS";

	@Inject
	BacklogService backlogService;

	private List<Task> mTasks;

	private List<Project> mProjects;

	private TasksRecyclerAdapter tasksAdapter;

	private ProjectAdapter backlogsAdapter;




	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY.equals(intent.getAction())) {
				final Task task = (Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_NEW_TASK_WITHOUT_STORY);

				mTasks.add(task);
				tasksAdapter.setTasks(mTasks);
				tasksAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * Returns a new MyTasksFragment with the given task without stories
	 * @param taskWithoutStories The task to set
	 * @return a new MyTasksFragment with the given task without stories
	 */
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

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);

		if (savedInstanceState == null) {
			mTasks = (List<Task>) getArguments().getSerializable(TASKS_KEY);

		} else {
			mTasks = (List<Task>) savedInstanceState.getSerializable(TASKS_KEY);
			mProjects = (List<Project>) savedInstanceState.getSerializable(PROJECTS_KEY);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		AgilefantApplication.getObjectGraph().inject(this);
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_my_tasks, container, false);
	}

	@SuppressWarnings("checkstyle:anoninnerlength")
	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		backlogsAdapter = new ProjectAdapter(getActivity());
		final RecyclerView tasksListView = (RecyclerView) view.findViewById(R.id.tasks_list);

		if (mProjects == null) {
			final ProgressDialog progressDialog = new ProgressDialog(getActivity());
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(getActivity().getString(R.string.loading));
			progressDialog.show();

			backlogService.getMyBacklogs(
					new Listener<List<Project>>() {

						@Override
						public void onResponse(final List<Project> response) {
							if (response != null) {

								mProjects = response;
								setProjectsList(tasksListView);

								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
							}
						}
					},
					new ErrorListener() {

						@Override
						public void onErrorResponse(final VolleyError arg0) {
							if (progressDialog != null && progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
							Toast.makeText(getActivity(), R.string.error_retrieve_my_backlogs,
									Toast.LENGTH_SHORT).show();
						}
					});
		} else {
			setProjectsList(tasksListView);
		}
	}


	private void setProjectsList(final RecyclerView tasksListView) {

		backlogsAdapter.setProjects(mProjects);

		// If list is empty we show an empty message
		if (mTasks.isEmpty()) {

			final TextView emptyView = (TextView) getView().findViewById(R.id.tasks_empty_view);
			tasksListView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);

		} else {

			tasksAdapter = new TasksRecyclerAdapter(getActivity(), mTasks);
			tasksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
			tasksListView.setAdapter(tasksAdapter);
		}
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(broadcastReceiver);

		super.onDestroy();
	}

	@Override
	public void update(final Observable observable, final Object arg1) {

		if (isVisible()) {
			tasksAdapter.notifyDataSetChanged();
			observable.deleteObserver(MyTasksFragment.this);
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(TASKS_KEY, (Serializable) mTasks);
		outState.putSerializable(PROJECTS_KEY, (Serializable) mProjects);
	}

	@Override
	public String toString() {
		return "MyTasksFragment{" + "backlogsAdapter=" + backlogsAdapter + ", mTasks=" + mTasks + '}';
	}
}