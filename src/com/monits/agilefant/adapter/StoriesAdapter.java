package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.model.Storie;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;

public class StoriesAdapter extends AbstractExpandableListAdapter<Storie, Task>{

	private static final int MINUTES = 60;


	public StoriesAdapter(Context context, List<Storie> storieList) {
		super(context);
		for (Storie storie : storieList) {
			super.addGroup(storie);
			for (Task task : storie.getTasks()) {
				super.addChildToGroup(storie, task);
			}
		}
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private LayoutInflater inflater;


	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		holderChild holder;
		if (null == convertView) {
			holder = new holderChild();
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
			holder = (holderChild) convertView.getTag();
		}

		Task task = (Task) getChild(groupPosition, childPosition);

		holder.name.setText(task.getName());
		holder.state.setText(task.getState());

		StringBuilder sb = new StringBuilder();

		for (User user : task.getResponsibles()) {
			sb.append(user.getInitials());
			sb.append(" ");
		}

		holder.responsibles.setText(sb.toString());
		holder.effortLeft.setText(String.valueOf(task.getEffortLeft() / MINUTES) + "h");
		holder.originalEstimate.setText(String.valueOf(task.getOriginalEstimate() / MINUTES) + "h");
		holder.spendEffort.setText(String.valueOf(task.getEffortSpent() / MINUTES) + "h");

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		holderGroup holder;
		if (null == convertView) {
			holder = new holderGroup();
			View inflate = inflater.inflate(R.layout.storie_item, null);
			holder.name = (TextView) inflate.findViewById(R.id.storie_name);
			holder.state = (TextView) inflate.findViewById(R.id.storie_state);
			holder.responsibles = (TextView) inflate.findViewById(R.id.storie_responsibles);
			holder.effortLeft = (TextView) inflate.findViewById(R.id.storie_effort_left);
			holder.originalEstimate = (TextView) inflate.findViewById(R.id.storie_original_estimate);
			holder.spendEffort = (TextView) inflate.findViewById(R.id.storie_spend_effort);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (holderGroup) convertView.getTag();
		}

		Storie storie = (Storie) getGroup(groupPosition);

		holder.name.setText(storie.getName());
		holder.state.setText(storie.getState());

		StringBuilder sb = new StringBuilder();

		for (User user : storie.getResponsibles()) {
			sb.append(user.getInitials());
			sb.append(" ");
		}

		holder.responsibles.setText(sb.toString());
		holder.effortLeft.setText(String.valueOf(storie.getMetrics().getEffortLeft() / MINUTES) + "h");
		holder.originalEstimate.setText(String.valueOf(storie.getMetrics().getOriginalEstimate() / MINUTES) + "h");
		holder.spendEffort.setText(String.valueOf(storie.getMetrics().getEffortSpent() / MINUTES) + "h");

		return convertView;
	}

	class holderGroup {
		public TextView name;
		public TextView state;
		public TextView responsibles;
		public TextView effortLeft;
		public TextView originalEstimate;
		public TextView spendEffort;
	}

	class holderChild {
		public TextView name;
		public TextView state;
		public TextView responsibles;
		public TextView effortLeft;
		public TextView originalEstimate;
		public TextView spendEffort;
	}
}
