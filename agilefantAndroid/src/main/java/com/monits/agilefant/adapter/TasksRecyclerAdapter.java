package com.monits.agilefant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolder;
import com.monits.agilefant.model.Task;

import java.util.List;

/**
 * Created by edipasquale on 25/09/15.
 */
public class TasksRecyclerAdapter extends RecyclerView.Adapter<TaskItemViewHolder> {

	private List<Task> taskList;
	private final LayoutInflater inflater;
	private final Context context;

	/**
	 * @param context Current context
	 * @param taskList List of task objects
	 */
	public TasksRecyclerAdapter(final Context context, final List<Task> taskList) {
		this.taskList = taskList;
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public TaskItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View view = inflater.inflate(R.layout.my_tasks_item, parent, false);

		return new TaskItemViewHolder(view, context);
	}

	@Override
	public void onBindViewHolder(final TaskItemViewHolder holder, final int position) {
		holder.onBindView(taskList.get(position));
	}

	@Override
	public int getItemCount() {
		return taskList.size();
	}

	public void setTasks(final List<Task> taskList) {
		this.taskList = taskList;
	}
}
