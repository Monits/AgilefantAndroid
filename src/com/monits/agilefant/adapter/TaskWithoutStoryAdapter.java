package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.HoursUtils;
import com.monits.agilefant.util.IterationUtils;

public class TaskWithoutStoryAdapter extends BaseAdapter {

	private final Context context;
	private final List<Task> tasks;
	private final LayoutInflater inflater;

	private AdapterViewActionListener<Task> actionListener;
	private final OnClickListener onClickListener;

	public TaskWithoutStoryAdapter(final Context context, final List<Task> tasks) {
		this.context = context;
		this.tasks = tasks;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
		return tasks.size();
	}

	@Override
	public Task getItem(final int position) {
		return tasks.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return tasks.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		Holder holder;
		if (null == convertView) {
			holder = new Holder();
			final View inflate = inflater.inflate(R.layout.task_item, null);
			holder.name = (TextView) inflate.findViewById(R.id.column_name);
			holder.state = (TextView) inflate.findViewById(R.id.column_state);
			holder.responsibles = (TextView) inflate.findViewById(R.id.column_responsibles);
			holder.effortLeft = (TextView) inflate.findViewById(R.id.column_effort_left);
			holder.originalEstimate = (TextView) inflate.findViewById(R.id.column_original_estimate);
			holder.spendEffort = (TextView) inflate.findViewById(R.id.column_spent_effort);

			convertView = inflate;
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}

		final Task task = getItem(position);

		holder.name.setText(task.getName());

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		holder.state.setText(IterationUtils.getStateName(task.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));

		holder.responsibles.setText(IterationUtils.getResposiblesDisplay(task.getResponsibles()));
		holder.effortLeft.setText(HoursUtils.convertMinutesToHours(task.getEffortLeft()));
		holder.originalEstimate.setText(HoursUtils.convertMinutesToHours(task.getOriginalEstimate()));
		holder.spendEffort.setText(HoursUtils.convertMinutesToHours(task.getEffortSpent()));

		holder.spendEffort.setTag(position);
		holder.spendEffort.setOnClickListener(onClickListener);
		holder.originalEstimate.setTag(position);
		holder.originalEstimate.setOnClickListener(onClickListener);
		holder.effortLeft.setTag(position);
		holder.effortLeft.setOnClickListener(onClickListener);
		holder.state.setTag(position);
		holder.state.setOnClickListener(onClickListener);
		holder.responsibles.setTag(position);
		holder.responsibles.setOnClickListener(onClickListener);

		return convertView;
	}

	/**
	 * Add a listener to intercept click events within row views separately.
	 *
	 * @param listener the listener to be set.
	 */
	public void setOnActionListener(final AdapterViewActionListener<Task> listener) {
		this.actionListener = listener;
	}

	class Holder {
		public TextView name;
		public TextView state;
		public TextView responsibles;
		public TextView effortLeft;
		public TextView originalEstimate;
		public TextView spendEffort;
	}

}
