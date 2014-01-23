package com.monits.agilefant.adapter;

import java.util.List;

import roboguice.RoboGuice;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
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
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.util.IterationUtils;

public class ProjectLeafStoriesAdapter extends BaseAdapter {

	@Inject
	private IterationService iterationService;

	private final Context context;
	private List<Story> stories;

	public ProjectLeafStoriesAdapter(final Context context) {
		this.context = context;

		RoboGuice.injectMembers(context, this);
	}

	@Override
	public int getCount() {
		return stories != null ? stories.size() : 0;
	}

	@Override
	public Story getItem(final int position) {
		final int count = getCount();
		return count > 0 && position < count ? stories.get(position) : null;
	}

	@Override
	public long getItemId(final int position) {
		final Story story = getItem(position);
		return story != null ? story.getId() : 0;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final ViewHolder holder;
		final View ret;
		if (convertView == null) {
			holder = new ViewHolder();
			ret = LayoutInflater.from(context)
					.inflate(R.layout.item_project_leaf_story, parent, false);

			holder.name = (TextView) ret.findViewById(R.id.column_name);
			holder.iteration = (TextView) ret.findViewById(R.id.column_context);
			holder.state = (TextView) ret.findViewById(R.id.column_state);

			ret.setTag(holder);
		} else {
			ret = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		final Story story = getItem(position);

		holder.name.setText(story.getName());

		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(story.getState())));
		holder.state.setText(IterationUtils.getStateName(story.getState()));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(story.getState()));

		if (story.getIteration() != null) {
			final Iteration iteration = story.getIteration();

			holder.iteration.setText(iteration.getName());
			holder.iteration.setOnClickListener(new OnClickListener() {

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
			holder.iteration.setText(" - ");
			holder.iteration.setOnClickListener(null);
		}

		return ret;
	}

	public void setStories(final List<Story> stories) {
		this.stories = stories;

		notifyDataSetChanged();
	}

	private static class ViewHolder {
		TextView name;
		TextView iteration;
		TextView state;
	}
}
