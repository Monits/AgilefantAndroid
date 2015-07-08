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
import com.monits.agilefant.model.UserChooser;

public class AutoCompleteUsersAdapter extends BaseAdapter implements Filterable {

	private final Context context;

	private List<UserChooser> filterableUsers;
	private List<UserChooser> filteredUsers;

	private final Filter filter;

	/**
	 * Constructor
	 * @param context The context
	 */
	public AutoCompleteUsersAdapter(final Context context) {
		this.context = context;
		this.filter = new ItemFilter();
	}

	@Override
	public int getCount() {
		return filteredUsers != null ? filteredUsers.size() : 0;
	}

	@Override
	public UserChooser getItem(final int position) {
		final int count = getCount();
		return count > 0 && position < count ? filteredUsers.get(position) : null;
	}

	@Override
	public long getItemId(final int position) {
		final UserChooser user = getItem(position);
		return user != null ? user.getId() : 0;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final TextView ret;
		if (convertView == null) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			ret = (TextView) inflater.inflate(R.layout.item_autocomplete_text, parent, false);
		} else {
			ret = (TextView) convertView;
		}

		final UserChooser user = getItem(position);
		ret.setText(user.getName());

		return ret;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	@SuppressLint("DefaultLocale")
	private class ItemFilter extends Filter {
		@Override
		protected FilterResults performFiltering(final CharSequence constraint) {
			final FilterResults results = new FilterResults();

			final ArrayList<UserChooser> usersList = new ArrayList<>(filterableUsers.size());

			if (constraint != null) {
				final String filterString = constraint.toString();
				for (final UserChooser userChooser : filterableUsers) {
					if (userChooser.isEnabled() && userChooser.match(filterString)) {
						usersList.add(userChooser);
					}
				}
			}
			results.values = usersList;
			results.count = usersList.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(final CharSequence constraint, final FilterResults results) {
			filteredUsers = (List<UserChooser>) results.values;
			notifyDataSetChanged();
		}
	}

	/**
	 * Set the filterable users
	 * @param filterableUsers The filterables users to set
	 */
	public void setFilterableUsers(final List<UserChooser> filterableUsers) {
		this.filterableUsers = filterableUsers;

		notifyDataSetChanged();
	}

	/**
	 * @return The filterables users
	 */
	public List<UserChooser> getFilterableUsers() {
		return filterableUsers;
	}
}
