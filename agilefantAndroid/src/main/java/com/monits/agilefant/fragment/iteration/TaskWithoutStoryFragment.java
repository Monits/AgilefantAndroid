package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.TaskWithoutStoryAdapter;
import com.monits.agilefant.listeners.OnSwapRowListener;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.view.DynamicListView;

public class TaskWithoutStoryFragment extends RoboFragment implements Observer {

	private static final String EXTRA_TASKS = "com.monits.agilefant.extra.TASK_WITHOUT_STORIES";

	private static final String EXTRA_ITERATION = "com.monits.agilefant.extra.ITERATION";

	@Inject
	private MetricsService metricsService;

	private List<Task> taskWithoutStory;

	private DynamicListView taskWithoutStoryListView;

	private TaskWithoutStoryAdapter taskWithoutStoryAdapter;
	private Iteration iteration;

	@SuppressWarnings("checkstyle:anoninnerlength")
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (intent.getAction().equals(AgilefantApplication.ACTION_TASK_UPDATED)
					&& !TaskWithoutStoryFragment.this.isDetached()) {

				final Task updatedTask = (Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_TASK_UPDATED);

				final int indexOf = taskWithoutStory.indexOf(updatedTask);
				if (indexOf != -1) {
					taskWithoutStory.get(indexOf).updateValues(updatedTask);
					taskWithoutStoryAdapter.notifyDataSetChanged();
				}
			}

			if (intent.getAction().equals(AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY)) {
				final Task newTaskWithoutStory =
						(Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_NEW_TASK_WITHOUT_STORY);

				taskWithoutStory.add(newTaskWithoutStory);
				taskWithoutStoryAdapter.setItems(taskWithoutStory);
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

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AgilefantApplication.ACTION_TASK_UPDATED);
		intentFilter.addAction(AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);

		final Bundle arguments = getArguments();
		this.taskWithoutStory = (List<Task>) arguments.getSerializable(EXTRA_TASKS);
		this.iteration = (Iteration) arguments.getSerializable(EXTRA_ITERATION);
	}

	@SuppressWarnings("checkstyle:anoninnerlength")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_task_without_story, container, false);

		taskWithoutStoryListView = (DynamicListView) rootView.findViewById(R.id.task_without_story);
		taskWithoutStoryListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		taskWithoutStoryListView.setItems(taskWithoutStory);

		taskWithoutStoryAdapter = new TaskWithoutStoryAdapter(getActivity(), taskWithoutStory);
		taskWithoutStoryAdapter.setOnActionListener(
				new TaskAdapterViewActionListener(getActivity(), TaskWithoutStoryFragment.this));
		taskWithoutStoryListView.setAdapter(taskWithoutStoryAdapter);
		taskWithoutStoryListView.setOnSwapRowListener(new OnSwapRowListener() {

			@Override
			public void onSwapPositions(final int itemPosition, final int swappedItemPosition,
					final SwapDirection direction, final long aboveItemId, final long belowItemId) {

				final Task task = taskWithoutStory.get(itemPosition);
				final Task targetTask = aboveItemId != -1
						? taskWithoutStory.get(taskWithoutStoryListView.getPositionForID(aboveItemId)) : null;

				task.setIteration(iteration);

				metricsService.rankTaskUnder(task, targetTask, taskWithoutStory,
					new Listener<Task>() {

						@Override
						public void onResponse(final Task arg0) {
							Toast.makeText(
								getActivity(), R.string.feedback_success_updated_task_rank, Toast.LENGTH_SHORT).show();
						}
					},
					new ErrorListener() {

						@Override
						public void onErrorResponse(final VolleyError arg0) {
							Toast.makeText(
								getActivity(), R.string.feedback_failed_update_tasks_rank, Toast.LENGTH_SHORT).show();

							// Re-Sorting is made on service layer, notifying the adapter.
							if (isVisible()) {
								taskWithoutStoryAdapter.sortAndNotify();
							}
						}
					});
			}
		});

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

}
