package com.monits.agilefant.adapter;

import java.util.Collections;
import java.util.List;

import roboguice.RoboGuice;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.util.IterationUtils;
import com.monits.agilefant.util.RankComparator;

public class MyStoriesAdapter extends AbstractExpandableListAdapter<Story, Task> {

	private final OnClickListener onClickChildListener;
	private AdapterViewActionListener<Task> childActionListener;
	private final OnClickListener onClickGroupListener;
	private AdapterViewActionListener<Story> groupActionListener;

	/**
	 * Constructor
	 * @param context The context
	 * @param stories The stories
	 */
	public MyStoriesAdapter(final Context context, final List<Story> stories) {
		super(context);

		RoboGuice.injectMembers(context, this);

		Collections.sort(stories, RankComparator.INSTANCE);
		for (final Story story : stories) {
			super.addGroup(story);
			final List<Task> tasks = story.getTasks();
			Collections.sort(tasks, RankComparator.INSTANCE);
			for (final Task task : tasks) {
				super.addChildToGroup(story, task);
			}
		}

		onClickChildListener = new OnClickListener() {

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

	@SuppressWarnings("checkstyle:finalparameters")
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			final boolean isLastChild, View convertView, final ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.my_tasks_item_nocontext, null);

			holder.name = (TextView) convertView.findViewById(R.id.column_name);
			holder.state = (TextView) convertView.findViewById(R.id.column_state);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Task task = getChild(groupPosition, childPosition);

		holder.name.setText(task.getName());
		holder.name.setTag(R.id.tag_group_position, groupPosition);
		holder.name.setTag(R.id.tag_child_position, childPosition);
		holder.name.setOnClickListener(onClickChildListener);

		holder.state.setText(IterationUtils.getStateName(task.getState()));
		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));
		holder.state.setTag(R.id.tag_group_position, groupPosition);
		holder.state.setTag(R.id.tag_child_position, childPosition);
		holder.state.setOnClickListener(onClickChildListener);

		return convertView;
	}

	@SuppressWarnings("checkstyle:finalparameters")
	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			View convertView, final ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.my_story_item, null);

			holder.name = (TextView) convertView.findViewById(R.id.column_name);
			holder.context = (TextView) convertView.findViewById(R.id.column_context);
			holder.state = (TextView) convertView.findViewById(R.id.column_state);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Story story = getGroup(groupPosition);

		holder.name.setText(story.getName());

		holder.state.setText(IterationUtils.getStateName(story.getState()));
		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(story.getState())));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(story.getState()));
		holder.state.setTag(R.id.tag_group_position, groupPosition);
		holder.state.setOnClickListener(onClickGroupListener);

		if (story.getIteration() == null) {
			holder.context.setText(" - ");
			holder.context.setOnClickListener(null);
		} else {
			final Iteration iteration = story.getIteration();

			holder.context.setText(iteration.getName());
			holder.context.setTag(R.id.tag_group_position, groupPosition);
			holder.context.setOnClickListener(onClickGroupListener);
		}

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

	private static class ViewHolder {
		public TextView name;
		public TextView context;
		public TextView state;
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		final Task child = getChild(groupPosition, childPosition);
		return child == null ? -1 : child.getId();
	}

	@Override
	public long getGroupId(final int groupPosition) {
		final Story group = getGroup(groupPosition);
		return group == null ? -1 : group.getId();
	}
}
