package com.monits.agilefant.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.helper.UpdateAdapterHelper;
import com.monits.agilefant.adapter.recyclerviewholders.DailyWorkTaskItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolderUpdateTracker;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.recycler.DragAndDropListener;
import com.monits.agilefant.service.TaskRankUpdaterService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by edipasquale on 25/09/15.
 */
public class TasksRecyclerAdapter extends RecyclerView.Adapter<WorkItemViewHolder<Task>> implements
		WorkItemViewHolderUpdateTracker, DragAndDropListener {

	protected List<Task> taskList;
	private final List<Task> originalTaskList;
	protected final LayoutInflater inflater;
	protected final FragmentActivity context;

	private final UpdateAdapterHelper updateAdapterHelper;

	private final TaskRankUpdaterService rankUpdaterService;

	/**
	 * @param context Current context
	 * @param taskList List of task objects
	 * @param rankUpdaterService Service that manages task rank changes.
	 */
	public TasksRecyclerAdapter(final FragmentActivity context, final List<Task> taskList,
			final TaskRankUpdaterService rankUpdaterService) {
		this.context = context;
		this.taskList = taskList;
		this.originalTaskList = this.taskList;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.rankUpdaterService = rankUpdaterService;

		updateAdapterHelper = new UpdateAdapterHelper(this);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public WorkItemViewHolder<Task> onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View view = inflater.inflate(R.layout.my_tasks_item, parent, false);

		return new DailyWorkTaskItemViewHolder(view, context, this);
	}

	@Override
	public void onBindViewHolder(final WorkItemViewHolder<Task> holder, final int position) {
		holder.onBindView(taskList.get(position));
	}

	@Override
	public int getItemCount() {
		return taskList.size();
	}

	@Override
	public void onUpdate(final WorkItem updatedWork) {
		updateAdapterHelper.updateItem(taskList, updatedWork);
	}

	public void setTasks(final List<Task> taskList) {
		this.taskList = taskList;
	}

	@Override
	public void onMove(final int fromPosition, final int toPosition) {
		notifyItemMoved(fromPosition, toPosition);
		Collections.swap(taskList, fromPosition, toPosition);

	}

	@Override
	public void onChangePosition(final int fromPosition, final int toPosition) {

		final Task currentTask;
		final Task targetTask;

		if (fromPosition < toPosition) {
			// Moves down
			currentTask = taskList.get(toPosition);
			targetTask = taskList.get(fromPosition);
		} else {
			// Moves up
			currentTask = taskList.get(toPosition);
			targetTask = toPosition == 0 ? null : taskList.get(fromPosition);
		}

		rankUpdaterService.rankTaskUnder(currentTask, targetTask, taskList,
				new Response.Listener<Task>() {

					@Override
					public void onResponse(final Task arg0) {
						Toast.makeText(
								context, R.string.feedback_success_updated_task_rank, Toast.LENGTH_SHORT).show();
					}
				},
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(
								context, R.string.feedback_failed_update_tasks_rank, Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public WorkItem getItem(final int position) {
		return taskList.get(position);
	}

	/**
	 * Filter the list leaving only those that match the text sent.
	 * @param query the text used to filter
	 */
	public void filter(final String query) {
		final String queryText = query.toLowerCase(Locale.getDefault());
		taskList = new ArrayList<>(); //Clean List
		for (final Task t : originalTaskList) {
			if (t.getName().toLowerCase(Locale.getDefault()).contains(queryText)) {
				taskList.add(t);
			}
			notifyDataSetChanged();
		}
	}

	@Override
	public String toString() {
		return "TasksRecyclerAdapter{"
				+ "taskList=" + taskList
				+ '}';
	}
}
