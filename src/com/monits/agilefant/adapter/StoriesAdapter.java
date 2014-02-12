package com.monits.agilefant.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.HoursUtils;
import com.monits.agilefant.util.IterationUtils;
import com.monits.agilefant.util.RankComparator;

public class StoriesAdapter extends AbstractExpandableListAdapter<Story, Task> {

	private final LayoutInflater inflater;
	private AdapterViewActionListener<Task> childActionListener;
	private final OnClickListener onClickListener;
	private AdapterViewActionListener<Story> groupActionListener;
	private final OnClickListener onClickGroupListener;

	public StoriesAdapter(final Context context, final List<Story> stories) {
		super(context);
		Collections.sort(stories, RankComparator.INSTANCE);
		for (final Story story : stories) {
			super.addGroup(story);
			final List<Task> tasks = story.getTasks();
			Collections.sort(tasks, RankComparator.INSTANCE);
			for (final Task task : tasks) {
				super.addChildToGroup(story, task);
			}
		}

		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		onClickListener = new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Integer groupPosition = (Integer) v.getTag(R.id.tag_group_position);
				final Integer childPosition = (Integer) v.getTag(R.id.tag_child_position);

				if (childActionListener != null && groupPosition != null && childPosition != null) {
					childActionListener.onAction(v, getChild(groupPosition, childPosition));
				}
			}
		};

		onClickGroupListener = new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Integer groupPosition = (Integer) v.getTag(R.id.tag_group_position);

				if (groupActionListener != null && groupPosition != null) {
					groupActionListener.onAction(v, getGroup(groupPosition));
				}
			}
		};
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			final boolean isLastChild, View convertView, final ViewGroup parent) {
		HolderChild holder;
		if (null == convertView) {
			holder = new HolderChild();
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
			holder = (HolderChild) convertView.getTag();
		}

		final Task task = getChild(groupPosition, childPosition);

		holder.name.setText(task.getName());

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		holder.state.setText(IterationUtils.getStateName(task.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));


		holder.responsibles.setText(IterationUtils.getResposiblesDisplay(task.getResponsibles()));
		holder.effortLeft.setText(HoursUtils.convertMinutesToHours(task.getEffortLeft()));
		holder.originalEstimate.setText(HoursUtils.convertMinutesToHours(task.getOriginalEstimate()));
		holder.spendEffort.setText(HoursUtils.convertMinutesToHours(task.getEffortSpent()));

		holder.name.setTag(R.id.tag_child_position, childPosition);
		holder.name.setTag(R.id.tag_group_position, groupPosition);
		holder.name.setOnClickListener(onClickListener);
		holder.spendEffort.setTag(R.id.tag_child_position, childPosition);
		holder.spendEffort.setTag(R.id.tag_group_position, groupPosition);
		holder.spendEffort.setOnClickListener(onClickListener);
		holder.originalEstimate.setTag(R.id.tag_child_position, childPosition);
		holder.originalEstimate.setTag(R.id.tag_group_position, groupPosition);
		holder.originalEstimate.setOnClickListener(onClickListener);
		holder.effortLeft.setTag(R.id.tag_child_position, childPosition);
		holder.effortLeft.setTag(R.id.tag_group_position, groupPosition);
		holder.effortLeft.setOnClickListener(onClickListener);
		holder.state.setTag(R.id.tag_child_position, childPosition);
		holder.state.setTag(R.id.tag_group_position, groupPosition);
		holder.state.setOnClickListener(onClickListener);
		holder.responsibles.setTag(R.id.tag_child_position, childPosition);
		holder.responsibles.setTag(R.id.tag_group_position, groupPosition);
		holder.responsibles.setOnClickListener(onClickListener);

		return convertView;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			View convertView, final ViewGroup parent) {
		HolderGroup holder;
		if (null == convertView) {
			holder = new HolderGroup();
			final View inflate = inflater.inflate(R.layout.stories_item, null);
			holder.name = (TextView) inflate.findViewById(R.id.column_name);
			holder.state = (TextView) inflate.findViewById(R.id.column_state);
			holder.responsibles = (TextView) inflate.findViewById(R.id.column_responsibles);
			holder.effortLeft = (TextView) inflate.findViewById(R.id.column_effort_left);
			holder.originalEstimate = (TextView) inflate.findViewById(R.id.column_original_estimate);
			holder.spendEffort = (TextView) inflate.findViewById(R.id.column_spent_effort);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (HolderGroup) convertView.getTag();
		}

		final Story story = getGroup(groupPosition);
		// FIXME: Find a way to make this more automagic, and remove this onDemand call.
		story.attachTasksObservers();

		holder.name.setText(story.getName());

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(story.getState())));
		holder.state.setText(IterationUtils.getStateName(story.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(story.getState()));

		holder.responsibles.setText(IterationUtils.getResposiblesDisplay(story.getResponsibles()));

		holder.effortLeft.setText(HoursUtils.convertMinutesToHours(story.getMetrics().getEffortLeft()));
		holder.originalEstimate.setText(HoursUtils.convertMinutesToHours(story.getMetrics().getOriginalEstimate()));
		holder.spendEffort.setText(HoursUtils.convertMinutesToHours(story.getMetrics().getEffortSpent()));

		holder.state.setTag(R.id.tag_group_position, groupPosition);
		holder.state.setOnClickListener(onClickGroupListener);
		holder.responsibles.setTag(R.id.tag_group_position, groupPosition);
		holder.responsibles.setOnClickListener(onClickGroupListener);

		return convertView;
	}

	/**
	 * Add a listener to intercept click events on group's children row's, children views
	 *
	 * @param listener the listener to be set.
	 */
	public void setOnChildActionListener(final AdapterViewActionListener<Task> listener) {
		this.childActionListener = listener;
	}

	/**
	 * Add a listener to intercept click events on group's children views.
	 *
	 * @param listener the listener to be set.
	 */
	public void setOnGroupActionListener(final AdapterViewActionListener<Story> listener) {
		this.groupActionListener = listener;
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
