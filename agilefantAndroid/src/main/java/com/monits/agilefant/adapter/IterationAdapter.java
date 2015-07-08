package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.monits.agilefant.R;
import com.monits.agilefant.model.Iteration;

public class IterationAdapter extends BaseAdapter {

	private final List<Iteration> iterations;
	private final Context context;
	private final int currentIterationIndex;

	/**
	 * Constructor
	 * @param context The contexts
	 * @param iterations The iterations.
	 * @param currentIterationIndex The current iteration
	 */
	public IterationAdapter(final Context context, final List<Iteration> iterations, final int currentIterationIndex) {
		this.iterations = iterations;
		this.context = context;
		this.currentIterationIndex = currentIterationIndex;
	}

	@Override
	public int getCount() {
		return iterations.size();
	}

	@Override
	public Iteration getItem(final int position) {
		return iterations.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return iterations.get(position).getId();
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {

		final View ret;
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			final LayoutInflater inflater = LayoutInflater.from(context);
			final View view = inflater.inflate(R.layout.iteration_item_change, parent, false);

			holder.check = (CheckBox) view.findViewById(R.id.check);

			view.setTag(holder);
			ret = view;
		} else {
			ret = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		final Iteration it = getItem(position);
		holder.check.setText(it.getName());
		holder.check.setChecked(position == currentIterationIndex);

		return ret;
	}

	private static class ViewHolder {
		CheckBox check;
	}
}
