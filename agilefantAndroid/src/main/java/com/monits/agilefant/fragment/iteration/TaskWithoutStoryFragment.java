package com.monits.agilefant.fragment.iteration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.TasksWithoutStoryRecyclerAdapter;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.recycler.SpacesSeparatorItemDecoration;
import com.monits.agilefant.recycler.WorkItemTouchHelperCallback;
import com.monits.agilefant.service.WorkItemService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskWithoutStoryFragment extends BaseDetailTabFragment implements SearchView.OnQueryTextListener {

	private static final String EXTRA_TASKS = "com.monits.agilefant.extra.TASK_WITHOUT_STORIES";

	@Bind(R.id.task_without_story)
	/* default */ RecyclerView taskWithoutStoryListView;

	private TasksWithoutStoryRecyclerAdapter taskWithoutStoryAdapter;

	private List<Task> taskWithoutStory;

	@Inject
	/* default */ WorkItemService workItemService;

	@SuppressWarnings("checkstyle:anoninnerlength")
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (AgilefantApplication.ACTION_TASK_UPDATED.equals(intent.getAction())
					&& !TaskWithoutStoryFragment.this.isDetached()) {

				final Task updatedTask = (Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_TASK_UPDATED);

				final int indexOf = taskWithoutStory.indexOf(updatedTask);
				if (indexOf != -1) {
					taskWithoutStory.get(indexOf).updateValues(updatedTask);
					taskWithoutStoryAdapter.notifyDataSetChanged();
				}
			}

			if (AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY.equals(intent.getAction())) {
				final Task newTaskWithoutStory =
						(Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_NEW_TASK_WITHOUT_STORY);

				taskWithoutStory.add(newTaskWithoutStory);
				taskWithoutStoryAdapter.setTasks(taskWithoutStory);
				taskWithoutStoryAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * Return a new TaskWithoutStoryFragment with the given tasks without story and iteration
	 * @param taskWithoutStory The tasks
	 * @return a new TaskWithoutStoryFragment with the given tasks without story and iteration
	 */
	public static TaskWithoutStoryFragment newInstance(final ArrayList<Task> taskWithoutStory) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRA_TASKS, taskWithoutStory);

		final TaskWithoutStoryFragment taskWithoutStoryFragment = new TaskWithoutStoryFragment();
		taskWithoutStoryFragment.setArguments(bundle);

		return taskWithoutStoryFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AgilefantApplication.getObjectGraph().inject(this);
		getActivity().registerReceiver(broadcastReceiver,
				AgilefantApplication.registerReceiverIntentFilter(AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY));

		setHasOptionsMenu(true);

		final Bundle arguments = getArguments();
		this.taskWithoutStory = (List<Task>) arguments.getSerializable(EXTRA_TASKS);

	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
		final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_task_without_story, container, false);
		ButterKnife.bind(this, rootView);

		taskWithoutStoryAdapter = new TasksWithoutStoryRecyclerAdapter(
				getActivity(), taskWithoutStory, workItemService);

		if (taskWithoutStory.isEmpty()) {
			rootView.findViewById(R.id.stories_empty_view).setVisibility(View.VISIBLE);
		} else {
			taskWithoutStoryListView.setLayoutManager(new LinearLayoutManager(getContext()));

			taskWithoutStoryListView.setAdapter(taskWithoutStoryAdapter);
			taskWithoutStoryListView.addItemDecoration(new SpacesSeparatorItemDecoration(getContext()));

			final WorkItemTouchHelperCallback workItemTouchHelperCallback =
					new WorkItemTouchHelperCallback(taskWithoutStoryAdapter);
			final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(workItemTouchHelperCallback);
			itemTouchHelper.attachToRecyclerView(taskWithoutStoryListView);
		}

		return rootView;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public int getTitleResourceId() {
		return R.string.task_without_story;
	}

	@Override
	public String toString() {
		return "TaskWithoutStoryFragment{"
				+ "taskWithoutStory=" + taskWithoutStory
				+ "taskWithoutStoryAdapter=" + taskWithoutStoryAdapter
				+ '}';
	}

	@Override
	public boolean onQueryTextSubmit(final String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(final String query) {
		taskWithoutStoryAdapter.filter(query);
		return true;
	}

	@Override
	public void onPrepareOptionsMenu(final Menu menu) {
		super.onPrepareOptionsMenu(menu);
		final MenuItem item = menu.findItem(R.id.action_search);
		item.setVisible(true);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
		searchView.setOnQueryTextListener(this);
	}


}
