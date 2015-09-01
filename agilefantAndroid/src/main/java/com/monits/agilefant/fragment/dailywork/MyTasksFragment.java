package com.monits.agilefant.fragment.dailywork;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.MyTasksAdapter;
import com.monits.agilefant.adapter.ProjectAdapter;
import com.monits.agilefant.fragment.backlog.task.CreateDailyWorkTaskFragment;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.BacklogService;

import javax.inject.Inject;

public class MyTasksFragment extends Fragment implements Observer {

	private static final String TASKS_KEY = "TASKS";

	@Inject
	BacklogService backlogService;

	private List<Task> mTasks;

	private MyTasksAdapter tasksAdapter;

	private ProjectAdapter backlogsAdapter;

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY.equals(intent.getAction())) {
				final Task task = (Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_NEW_TASK_WITHOUT_STORY);

				mTasks.add(task);
				tasksAdapter.setItems(mTasks);
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

		mTasks = (List<Task>) getArguments().getSerializable(TASKS_KEY);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		AgilefantApplication.getObjectGraph().inject(this);
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_my_tasks, container, false);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.menu_dailywork_new_element, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_task:
			MyTasksFragment.this.getActivity().getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, CreateDailyWorkTaskFragment.newInstance())
				.addToBackStack(null)
				.commit();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("checkstyle:anoninnerlength")
	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		backlogsAdapter = new ProjectAdapter(getActivity());

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
						backlogsAdapter.setProjects(response);

						final ListView tasksListView = (ListView) view.findViewById(R.id.tasks_list);
						final View emptyView = view.findViewById(R.id.tasks_empty_view);

						final List<Project> projectList = new ArrayList<>();

						for (int i = 0; i < backlogsAdapter.getGroupCount(); i++) {
							projectList.add(backlogsAdapter.getGroup(i));
						}

						tasksAdapter = new MyTasksAdapter(getActivity(), mTasks);
						tasksAdapter.setOnActionListener(
								new TaskAdapterViewActionListener(getActivity(), MyTasksFragment.this, projectList));
						tasksListView.setAdapter(tasksAdapter);
						tasksListView.setEmptyView(emptyView);

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
					Toast.makeText(getActivity(), R.string.error_retrieve_my_backlogs, Toast.LENGTH_SHORT).show();
				}
			});



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
}