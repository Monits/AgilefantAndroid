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
import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolder;

import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolderUpdateTracker;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolder;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.recycler.DragAndDropListener;
import com.monits.agilefant.service.MetricsService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by edipasquale on 25/09/15.
 */
public class TasksRecyclerAdapter extends RecyclerView.Adapter<WorkItemViewHolder<Task>> implements
		TaskItemViewHolderUpdateTracker, DragAndDropListener {

	@Inject
	MetricsService metricsService;

	private List<Task> taskList;
	protected final LayoutInflater inflater;
	protected final FragmentActivity context;

	/**
	 * @param context Current context
	 * @param taskList List of task objects
	 */
	public TasksRecyclerAdapter(final FragmentActivity context, final List<Task> taskList) {
		this.context = context;
		this.taskList = taskList;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public WorkItemViewHolder<Task> onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View view = inflater.inflate(R.layout.my_tasks_item, parent, false);

		return new TaskItemViewHolder(view, context, this);
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
	public void onUpdate(final Task updatedTask) {
		for (int i = 0; i < taskList.size(); i++) {
			if (updatedTask.getId() == taskList.get(i).getId()) {
				taskList.set(i, updatedTask);
				notifyItemChanged(i);
				break;
			}
		}
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
		final Task currentTask = taskList.get(fromPosition);
		final Task targetTask = taskList.get(toPosition);

		metricsService.rankTaskUnder(currentTask, targetTask, taskList,
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
	public String toString() {
		return "TasksRecyclerAdapter{"
				+ "taskList=" + taskList
				+ '}';
	}
}
