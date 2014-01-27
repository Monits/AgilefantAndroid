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
import com.monits.agilefant.model.FilterableUser;
import com.monits.agilefant.model.User;

public class AutoCompleteUsersAdapter extends BaseAdapter implements Filterable {

	private final Context context;

	private List<FilterableUser> filterableUsers;
	private List<FilterableUser> filteredUsers;

	private final Filter filter;

	public AutoCompleteUsersAdapter(final Context context) {
		this.context = context;
		this.filter = new ItemFilter();
	}

	@Override
	public int getCount() {
		return filteredUsers != null ? filteredUsers.size() : 0;
	}

	@Override
	public User getItem(final int position) {
		final int count = getCount();
		return count > 0 && position < count ? filteredUsers.get(position).getOriginalUser() : null;
	}

	@Override
	public long getItemId(final int position) {
		final User user = getItem(position);
		return user != null ? user.getId() : 0;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final TextView ret;
		if (convertView == null) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			ret = (TextView) inflater.inflate(R.layout.item_autocomplete_user, parent, false);
		} else {
			ret = (TextView) convertView;
		}

		final User user = getItem(position);
		ret.setText(user.getFullName());

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
			final String filterString = constraint.toString();
			final FilterResults results = new FilterResults();

			final int count = filterableUsers.size();
			final ArrayList<FilterableUser> usersList = new ArrayList<FilterableUser>(count);

			for (int i = 0; i < count; i++) {
				final FilterableUser user = filterableUsers.get(i);
				final String matchedString = user.getMatchedString();
				if (filterString != null && user.isEnabled()
						&& (matchedString.toLowerCase().contains(filterString.toLowerCase())
								|| user.getOriginalUser().getInitials().equalsIgnoreCase(filterString))) {

					usersList.add(user);
				}
			}

			results.values = usersList;
			results.count = usersList.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(final CharSequence constraint, final FilterResults results) {
			filteredUsers = (List<FilterableUser>) results.values;
			notifyDataSetChanged();
		}
	}

	public void setFilterableUsers(final List<FilterableUser> filterableUsers) {
		this.filterableUsers = filterableUsers;

		notifyDataSetChanged();
	}
}
