package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.util.HoursUltis;

public class TaskWithoutStoryAdaptar extends BaseAdapter{

	private Context context;
	private List<Task> tasks;
	private LayoutInflater inflater;

	public TaskWithoutStoryAdaptar(Context context, List<Task> tasks) {
		this.context = context;
		this.tasks = tasks;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return tasks.size();
	}

	@Override
	public Object getItem(int position) {
		return tasks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return tasks.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (null == convertView) {
			holder = new Holder();
			View inflate = inflater.inflate(R.layout.task_item, null);
			holder.name = (TextView) inflate.findViewById(R.id.task_name);
			holder.state = (TextView) inflate.findViewById(R.id.task_state);
			holder.responsibles = (TextView) inflate.findViewById(R.id.task_responsibles);
			holder.effortLeft = (TextView) inflate.findViewById(R.id.task_effort_left);
			holder.originalEstimate = (TextView) inflate.findViewById(R.id.task_original_estimate);
			holder.spendEffort = (TextView) inflate.findViewById(R.id.task_spend_effort);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		Task task = (Task) getItem(position);

		holder.name.setText(task.getName());
		holder.state.setText(task.getState());

		StringBuilder sb = new StringBuilder();

		for (User user : task.getResponsibles()) {
			sb.append(user.getInitials());
			sb.append(" ");
		}

		holder.responsibles.setText(sb.toString());
		holder.effortLeft.setText(HoursUltis.convertMinutesToHours(task.getEffortLeft()));
		holder.originalEstimate.setText(HoursUltis.convertMinutesToHours(task.getOriginalEstimate()));
		holder.spendEffort.setText(HoursUltis.convertMinutesToHours(task.getEffortSpent()));

		return convertView;
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
