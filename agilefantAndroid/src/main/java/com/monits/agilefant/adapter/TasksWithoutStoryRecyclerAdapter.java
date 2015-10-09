package com.monits.agilefant.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolder;
import com.monits.agilefant.model.Task;

import java.util.List;

/**
 * Created by edipasquale on 07/10/15.
 */
public class TasksWithoutStoryRecyclerAdapter extends TasksRecyclerAdapter {

	/**
	 * @param context  Current context
	 * @param taskList List of task objects
	 */
	public TasksWithoutStoryRecyclerAdapter(final FragmentActivity context, final List<Task> taskList) {
		super(context, taskList);
	}

	@Override
	public WorkItemViewHolder<Task> onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View view = inflater.inflate(R.layout.task_item, parent, false);

		return new TaskItemViewHolder(view, context, this);
	}
}
