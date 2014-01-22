package com.monits.agilefant.adapter;

import java.util.Collections;
import java.util.List;

import roboguice.RoboGuice;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.util.IterationUtils;
import com.monits.agilefant.util.StoryRankComparator;
import com.monits.agilefant.util.TaskRankComparator;

public class MyStoriesAdapter extends AbstractExpandableListAdapter<Story, Task> {

	@Inject
	private IterationService iterationService;

	public MyStoriesAdapter(final Context context, final List<Story> stories) {
		super(context);

		Collections.sort(stories, new StoryRankComparator());
		for (final Story story : stories) {
			super.addGroup(story);
			final List<Task> tasks = story.getTasks();
			Collections.sort(tasks, new TaskRankComparator());
			for (final Task task : tasks) {
				super.addChildToGroup(story, task);
			}
		}

		RoboGuice.injectMembers(context, this);
	}

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

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		holder.state.setText(IterationUtils.getStateName(task.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));

		return convertView;
	}

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

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(story.getState())));
		holder.state.setText(IterationUtils.getStateName(story.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(story.getState()));

		if (story.getIteration() != null) {
			final Iteration iteration = story.getIteration();

			holder.context.setText(iteration.getName());
			holder.context.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {

					final ProgressDialog progressDialog = new ProgressDialog(context);
					progressDialog.setIndeterminate(true);
					progressDialog.setCancelable(false);
					progressDialog.setMessage(context.getString(R.string.loading));
					progressDialog.show();
					iterationService.getIteration(
							iteration.getId(),
							new Listener<Iteration>() {

								@Override
								public void onResponse(final Iteration response) {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}

									final Intent intent = new Intent(context, IterationActivity.class);

									// Workaround that may be patchy, but it depends on the request whether it comes or not, and how to get it.
									response.setParent(iteration.getParent());

									intent.putExtra(IterationActivity.ITERATION, response);

									context.startActivity(intent);
								}
							},
							new ErrorListener() {

								@Override
								public void onErrorResponse(final VolleyError arg0) {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}

									Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
								}
							});
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
