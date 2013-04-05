package com.monits.agilefant.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.util.HoursUltis;
import com.monits.agilefant.util.StoryRankComparator;
import com.monits.agilefant.util.TaskRankComparator;

public class StoriesAdapter extends AbstractExpandableListAdapter<Story, Task>{

	public StoriesAdapter(Context context, List<Story> stories) {
		super(context);
		Collections.sort(stories, new StoryRankComparator());
		for (Story storie : stories) {
			super.addGroup(storie);
			List<Task> tasks = storie.getTasks();
			Collections.sort(tasks, new TaskRankComparator());
			for (Task task : tasks) {
				super.addChildToGroup(storie, task);
			}
		}
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private LayoutInflater inflater;


	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		HolderChild holder;
		if (null == convertView) {
			holder = new HolderChild();
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
			holder = (HolderChild) convertView.getTag();
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
		holder.effortLeft.setText(HoursUltis.convertMinutesToHours(task.getEffortLeft()));
		holder.originalEstimate.setText(HoursUltis.convertMinutesToHours(task.getOriginalEstimate()));
		holder.spendEffort.setText(HoursUltis.convertMinutesToHours(task.getEffortSpent()));

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		HolderGroup holder;
		if (null == convertView) {
			holder = new HolderGroup();
			View inflate = inflater.inflate(R.layout.stories_item, null);
			holder.name = (TextView) inflate.findViewById(R.id.storie_name);
			holder.state = (TextView) inflate.findViewById(R.id.storie_state);
			holder.responsibles = (TextView) inflate.findViewById(R.id.storie_responsibles);
			holder.effortLeft = (TextView) inflate.findViewById(R.id.storie_effort_left);
			holder.originalEstimate = (TextView) inflate.findViewById(R.id.storie_original_estimate);
			holder.spendEffort = (TextView) inflate.findViewById(R.id.storie_spend_effort);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (HolderGroup) convertView.getTag();
		}

		Story storie = (Story) getGroup(groupPosition);

		holder.name.setText(storie.getName());
		holder.state.setText(storie.getState());

		StringBuilder sb = new StringBuilder();

		for (User user : storie.getResponsibles()) {
			sb.append(user.getInitials());
			sb.append(" ");
		}

		holder.responsibles.setText(sb.toString());
		holder.effortLeft.setText(HoursUltis.convertMinutesToHours(storie.getMetrics().getEffortLeft()));
		holder.originalEstimate.setText(HoursUltis.convertMinutesToHours(storie.getMetrics().getOriginalEstimate()));
		holder.spendEffort.setText(HoursUltis.convertMinutesToHours(storie.getMetrics().getEffortSpent()));

		return convertView;
	}

	class HolderGroup {
		public TextView name;
		public TextView state;
		public TextView responsibles;
		public TextView effortLeft;
		public TextView originalEstimate;
		public TextView spendEffort;
	}

	class HolderChild {
		public TextView name;
		public TextView state;
		public TextView responsibles;
		public TextView effortLeft;
		public TextView originalEstimate;
		public TextView spendEffort;
	}
}
