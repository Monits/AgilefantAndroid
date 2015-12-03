package com.monits.agilefant.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.recyclerviewholders.DailyWorkTaskItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolder;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.TaskRankUpdaterService;

import java.util.List;

/**
 * Created by edipasquale on 07/10/15.
 */
public class TasksWithoutStoryRecyclerAdapter extends TasksRecyclerAdapter {

	/**
	 * @param context  Current context
	 * @param taskList List of task objects
	 * @param rankUpdaterService Service that manages task rank changes.
	 */
	public TasksWithoutStoryRecyclerAdapter(final FragmentActivity context, final List<Task> taskList,
			final TaskRankUpdaterService rankUpdaterService) {
		super(context, taskList, rankUpdaterService);
	}

	@Override
	public WorkItemViewHolder<Task> onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View view = inflater.inflate(R.layout.task_item, parent, false);

		return new DailyWorkTaskItemViewHolder(view, context, this);
	}
}
