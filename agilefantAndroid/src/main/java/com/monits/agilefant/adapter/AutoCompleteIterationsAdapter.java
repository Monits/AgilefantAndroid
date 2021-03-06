package com.monits.agilefant.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.model.FilterableIteration;

public class AutoCompleteIterationsAdapter extends BaseAdapter implements Filterable {

	private final Context context;

	private List<FilterableIteration> iterations;
	private List<FilterableIteration> filteredIterations;

	private final IterationFilter filter;

	/**
	 * Constructor
	 * @param context The context
	 */
	public AutoCompleteIterationsAdapter(final Context context) {
		this.context = context;
		this.filter = new IterationFilter();
	}

	@Override
	public int getCount() {
		return filteredIterations == null ? 0 : filteredIterations.size();
	}

	@Override
	public FilterableIteration getItem(final int position) {
		final int count = getCount();
		if (count > 0
				&& position >= 0
				&& position < count) {

			return filteredIterations.get(position);
		}

		return null;
	}

	@Override
	public long getItemId(final int position) {
		final FilterableIteration iteration = getItem(position);

		if (iteration != null) {
			return iteration.getId();
		}

		return 0;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final TextView ret;
		if (convertView instanceof TextView) {
			ret = (TextView) convertView;
		} else {
			final LayoutInflater inflater = LayoutInflater.from(context);
			ret = (TextView) inflater.inflate(R.layout.item_autocomplete_text, parent, false);
		}

		final FilterableIteration iteration = getItem(position);

		ret.setText(iteration.getName());

		return ret;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Set the items and notify the changes.
	 * @param items The items to set
	 */
	public void setItems(final List<FilterableIteration> items) {
		this.iterations = items;

		notifyDataSetChanged();
	}

	@SuppressLint("DefaultLocale")
	private class IterationFilter extends Filter {

		@Override
		protected FilterResults performFiltering(final CharSequence constraint) {
			final String filterString = constraint.toString().toLowerCase();
			final FilterResults results = new FilterResults();

			final List<FilterableIteration> filteredList = new ArrayList<>(iterations.size());

			for (final FilterableIteration iteration : iterations) {
				final String matchedString = iteration.getMatchedString().toLowerCase();
				if (filterString != null && iteration.isEnabled()
						&& (matchedString.contains(filterString)
								|| iteration.getName().toLowerCase().contains(filterString))) {

					filteredList.add(iteration);
				}
			}

			results.values = filteredList;
			results.count = filteredList.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(final CharSequence constraint, final FilterResults results) {
			filteredIterations = (List<FilterableIteration>) results.values;

			notifyDataSetChanged();
		}
	}
}