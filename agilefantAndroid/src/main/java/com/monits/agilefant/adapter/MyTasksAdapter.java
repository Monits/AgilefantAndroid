package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.IterationUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyTasksAdapter extends BaseAdapter {

	private final Context context;
	private List<Task> tasks;

	private AdapterViewActionListener<Task> actionListener;
	private final OnClickListener onClickListener;

	/**
	 * Constructor
	 * @param context The context
	 * @param tasks The tasks
	 */
	public MyTasksAdapter(final Context context, final List<Task> tasks) {
		this.context = context;
		this.tasks = tasks;

		onClickListener = new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Integer position = (Integer) v.getTag();

				if (actionListener != null && position != null) {
					actionListener.onAction(v, getItem(position));
				}
			}
		};
	}

	@Override
	public int getCount() {
		return tasks == null ? 0 : tasks.size();
	}

	@Override
	public Task getItem(final int position) {
		return tasks == null ? null : tasks.get(position);
	}

	@Override
	public long getItemId(final int position) {
		final Task item = getItem(position);
		return item == null ? 0 : item.getId();
	}

	@SuppressWarnings("checkstyle:finalparameters")
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.my_tasks_item, null);
			holder = new ViewHolder(convertView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Task task = getItem(position);

		holder.name.setText(task.getName());
		holder.name.setTag(position);
		holder.name.setOnClickListener(onClickListener);

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		holder.state.setText(IterationUtils.getStateName(task.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));
		holder.state.setTag(position);
		holder.state.setOnClickListener(onClickListener);

		if (task.getIteration() == null) {
			holder.context.setText(" - ");
			holder.context.setOnClickListener(null);
		} else {
			final Iteration iteration = task.getIteration();

			holder.context.setText(iteration.getName());
			holder.context.setTag(position);
			holder.context.setOnClickListener(onClickListener);
		}

		return convertView;
	}

	/**
	 * Set the items
	 * @param items The items to set
	 */
	public void setItems(final List<Task> items) {
		this.tasks = items;

		notifyDataSetChanged();
	}

	/**
	 * Add a listener to intercept click events within row views separately.
	 *
	 * @param listener the listener to be set.
	 */
	public void setOnActionListener(final AdapterViewActionListener<Task> listener) {
		this.actionListener = listener;
	}

	static class ViewHolder {
		@Bind(R.id.column_name)
		TextView name;
		@Bind(R.id.column_context)
		TextView context;
		@Bind(R.id.column_state)
		TextView state;

		public ViewHolder(final View view) {
			ButterKnife.bind(this, view);
		}
	}
}