package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.TasksRecyclerAdapter;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.recycler.SpacesSeparatorItemDecoration;

import javax.inject.Inject;

public class TaskWithoutStoryFragment extends BaseDetailTabFragment implements Observer {

	private static final String EXTRA_TASKS = "com.monits.agilefant.extra.TASK_WITHOUT_STORIES";

	private static final String EXTRA_ITERATION = "com.monits.agilefant.extra.ITERATION";

	@Inject
	MetricsService metricsService;

	private List<Task> taskWithoutStory;

	@Bind(R.id.task_without_story)
	RecyclerView taskWithoutStoryListView;

	private TasksRecyclerAdapter taskWithoutStoryAdapter;

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
	 * @param iteration The iteration
	 * @return a new TaskWithoutStoryFragment with the given tasks without story and iteration
	 */
	public static TaskWithoutStoryFragment newInstance(final ArrayList<Task> taskWithoutStory,
			final Iteration iteration) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRA_TASKS, taskWithoutStory);
		bundle.putSerializable(EXTRA_ITERATION, iteration);

		final TaskWithoutStoryFragment taskWithoutStoryFragment = new TaskWithoutStoryFragment();
		taskWithoutStoryFragment.setArguments(bundle);

		return taskWithoutStoryFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgilefantApplication.getObjectGraph().inject(this);
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AgilefantApplication.ACTION_TASK_UPDATED);
		intentFilter.addAction(AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);

		final Bundle arguments = getArguments();
		this.taskWithoutStory = (List<Task>) arguments.getSerializable(EXTRA_TASKS);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
		final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_task_without_story, container, false);
		ButterKnife.bind(this, rootView);
		if (taskWithoutStory.isEmpty()) {
			rootView.findViewById(R.id.stories_empty_view).setVisibility(View.VISIBLE);
		} else {
			taskWithoutStoryListView.setLayoutManager(new LinearLayoutManager(getContext()));

			taskWithoutStoryAdapter = new TasksRecyclerAdapter(getActivity(), taskWithoutStory);

			taskWithoutStoryListView.setAdapter(taskWithoutStoryAdapter);
			taskWithoutStoryListView.addItemDecoration(new SpacesSeparatorItemDecoration(getContext()));
		}

		return rootView;
	}

	@Override
	public void update(final Observable observable, final Object data) {
		if (isVisible()) {
			taskWithoutStoryAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);

		}
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public int getTitleBackgroundResourceId() {
		return R.drawable.gradient_task_without_story_title;
	}

	@Override
	public int getColorResourceId() {
		return android.R.color.white;
	}

	@Override
	public int getTitleResourceId() {
		return R.string.task_without_story;
	}
}
