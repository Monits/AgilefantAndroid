package com.monits.agilefant.adapter;

import java.util.Collections;
import java.util.List;

import roboguice.RoboGuice;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.model.DailyWorkStory;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.task.GetIteration;
import com.monits.agilefant.util.IterationUtils;
import com.monits.agilefant.util.StoryRankComparator;
import com.monits.agilefant.util.TaskRankComparator;

public class MyStoriesAdapter extends AbstractExpandableListAdapter<DailyWorkStory, Task> {

	@Inject
	private GetIteration getIteration;

	public MyStoriesAdapter(Context context, List<DailyWorkStory> stories) {
		super(context);

		Collections.sort(stories, new StoryRankComparator());
		for (DailyWorkStory story : stories) {
			super.addGroup(story);
			List<Task> tasks = story.getTasks();
			Collections.sort(tasks, new TaskRankComparator());
			for (Task task : tasks) {
				super.addChildToGroup(story, task);
			}
		}

		RoboGuice.injectMembers(context, this);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.my_tasks_item_nocontext, null);

			holder.name = (TextView) convertView.findViewById(R.id.column_name);
			holder.state = (TextView) convertView.findViewById(R.id.column_state);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Task task = getChild(groupPosition, childPosition);

		holder.name.setText(task.getName());

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		holder.state.setText(IterationUtils.getStateName(task.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		ViewHolder holder = null;
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

		DailyWorkStory story = getGroup(groupPosition);

		holder.name.setText(story.getName());

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(story.getState())));
		holder.state.setText(IterationUtils.getStateName(story.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(story.getState()));

		if (story.getIteration() != null) {
			Iteration iteration = story.getIteration();

			holder.context.setText(iteration.getName());
			holder.context.setTag(story);
			holder.context.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DailyWorkStory task = (DailyWorkStory) v.getTag();

					getIteration.configure(
							task.getIteration().getParent().getName(),
							task.getIteration().getId());

					getIteration.execute();
				}
			});
		} else {
			holder.context.setText(" - ");
			holder.context.setOnClickListener(null);
		}

		return convertView;
	}

	class ViewHolder {
		public TextView name;
		public TextView context;
		public TextView state;
	}
}
