package com.monits.agilefant.adapter;

import java.util.List;

import roboguice.RoboGuice;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.model.DailyWorkTask;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.util.IterationUtils;

public class MyTasksAdapter extends BaseAdapter {

	@Inject
	private IterationService iterationService;

	private final Context context;
	private final List<DailyWorkTask> tasks;


	public MyTasksAdapter(final Context context, final List<DailyWorkTask> tasks) {
		this.context = context;
		this.tasks = tasks;

		RoboGuice.injectMembers(context, this);
	}

	@Override
	public int getCount() {
		return tasks != null ? tasks.size() : 0;
	}

	@Override
	public DailyWorkTask getItem(final int position) {
		return tasks != null ? tasks.get(position) : null;
	}

	@Override
	public long getItemId(final int position) {
		final DailyWorkTask item = getItem(position);
		return item != null ? item.getId() : 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.my_tasks_item, null);

			holder.name = (TextView) convertView.findViewById(R.id.column_name);
			holder.context = (TextView) convertView.findViewById(R.id.column_context);
			holder.state = (TextView) convertView.findViewById(R.id.column_state);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DailyWorkTask task = getItem(position);

		holder.name.setText(task.getName());

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		holder.state.setText(IterationUtils.getStateName(task.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));

		if (task.getIteration() != null) {
			final Iteration iteration = task.getIteration();

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
