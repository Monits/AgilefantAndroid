package com.monits.agilefant.adapter;

import java.util.List;

import roboguice.RoboGuice;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.model.DailyWorkTask;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.task.GetIteration;
import com.monits.agilefant.util.IterationUtils;

public class MyTasksAdapter extends BaseAdapter {

	@Inject
	private GetIteration getIteration;

	private Context context;
	private List<DailyWorkTask> tasks;


	public MyTasksAdapter(Context context, List<DailyWorkTask> tasks) {
		this.context = context;
		this.tasks = tasks;

		RoboGuice.injectMembers(context, this);
	}

	@Override
	public int getCount() {
		return tasks != null ? tasks.size() : 0;
	}

	@Override
	public DailyWorkTask getItem(int position) {
		return tasks != null ? tasks.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		DailyWorkTask item = getItem(position);
		return item != null ? item.getId() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
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

		DailyWorkTask task = getItem(position);

		holder.name.setText(task.getName());

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		holder.state.setText(IterationUtils.getStateName(task.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));

		if (task.getIteration() != null) {
			Iteration iteration = task.getIteration();

			holder.context.setText(iteration.getName());
			holder.context.setTag(task);
			holder.context.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DailyWorkTask task = (DailyWorkTask) v.getTag();

					String projectName = null;
					com.monits.agilefant.model.Context iterationParent = task.getIteration().getParent();
					if (iterationParent != null) {
						projectName = iterationParent.getName();
					}

					getIteration.configure(
							projectName,
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
