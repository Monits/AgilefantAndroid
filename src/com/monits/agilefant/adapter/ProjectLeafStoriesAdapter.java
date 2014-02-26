package com.monits.agilefant.adapter;

import java.util.List;

import roboguice.RoboGuice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.listeners.AdapterViewOnLongActionListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.util.IterationUtils;

public class ProjectLeafStoriesAdapter extends BaseAdapter {

	private final Context context;

	private List<Story> stories;

	private AdapterViewActionListener<Story> actionListener;
	private final OnClickListener onClickListener;

	private AdapterViewOnLongActionListener<Story> onLongActionListener;
	private final OnLongClickListener onLongClickListener;

	public ProjectLeafStoriesAdapter(final Context context) {
		RoboGuice.injectMembers(context, this);

		this.context = context;

		onClickListener = new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Integer position = (Integer) v.getTag();

				if (actionListener != null && position != null) {
					actionListener.onAction(v, getItem(position));
				}
			}
		};

		onLongClickListener = new OnLongClickListener() {

			@Override
			public boolean onLongClick(final View v) {
				final Integer position = (Integer) v.getTag();

				if (onLongActionListener != null && position != null) {
					return onLongActionListener.onLongAction(v, getItem(position));
				}

				return true;
			}
		};
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
			holder.responsibles = (TextView) ret.findViewById(R.id.column_responsibles);

			ret.setTag(holder);
		} else {
			ret = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		final Story story = getItem(position);

		holder.name.setText(story.getName());

		holder.responsibles.setText(
				IterationUtils.getResposiblesDisplay(story.getResponsibles()));
		holder.responsibles.setTag(position);
		holder.responsibles.setOnClickListener(onClickListener);

		holder.state.setText(IterationUtils.getStateName(story.getState()));
		holder.state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(story.getState())));
		holder.state.setBackgroundResource(IterationUtils.getStateBackground(story.getState()));
		holder.state.setTag(position);
		holder.state.setOnClickListener(onClickListener);

		if (story.getIteration() != null) {
			final Iteration iteration = story.getIteration();

			holder.iteration.setText(iteration.getName());
			holder.iteration.setOnClickListener(onClickListener);
		} else {
			holder.iteration.setText(" - ");
			holder.iteration.setOnClickListener(null);
		}

		holder.iteration.setTag(position);
		holder.iteration.setOnLongClickListener(onLongClickListener);

		return ret;
	}

	public void setStories(final List<Story> stories) {
		this.stories = stories;

		notifyDataSetChanged();
	}

	/**
	 * Add a listener to intercept long press events on stories iterations.
	 *
	 * @param onLongActionListener the listener to be set
	 */
	public void setOnLongActionListener(final AdapterViewOnLongActionListener<Story> onLongActionListener) {
		this.onLongActionListener = onLongActionListener;
	}

	/**
	 * Add a listener to intercept click events within row views separately.
	 *
	 * @param listener the listener to be set.
	 */
	public void setOnActionListener(final AdapterViewActionListener<Story> listener) {
		this.actionListener = listener;
	}

	private static class ViewHolder {
		TextView name;
		TextView iteration;
		TextView responsibles;
		TextView state;
	}
}
