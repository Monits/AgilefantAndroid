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
import com.monits.agilefant.model.FilterableTeam;
import com.monits.agilefant.model.FilterableUser;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.model.User;

public class AutoCompleteUsersAdapter extends BaseAdapter implements Filterable {

	private final Context context;

	private List<UserChooser> filterableUsers;
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
		return count > 0 && position < count ? filteredUsers.get(position).getUser() : null;
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
			ret = (TextView) inflater.inflate(R.layout.item_autocomplete_text, parent, false);
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
			final FilterResults results = new FilterResults();

			final int count = filterableUsers.size();
			final ArrayList<FilterableUser> usersList = new ArrayList<FilterableUser>(count);

			if (constraint != null) {
				final String filterString = constraint.toString();
				for (int i = 0; i < count; i++) {
					final UserChooser userChooser = filterableUsers.get(i);
					final String matchedString = userChooser.getMatchedString();
					if (userChooser.isEnabled()) {
						final boolean matchSomeChar = matchedString.toLowerCase().contains(filterString.toLowerCase());
						if (userChooser instanceof FilterableUser
								&& (matchSomeChar || ((FilterableUser) userChooser)
										.getUser().getInitials().equalsIgnoreCase(filterString))) {
							usersList.add((FilterableUser) userChooser);

						} else if (userChooser instanceof FilterableTeam && matchSomeChar) {
							final List<Long> usersId = ((FilterableTeam) userChooser).getUsersId();
							for (final UserChooser user : filterableUsers) {
								if (usersId.contains(user.getId()) && !usersList.contains((FilterableUser) user)) {
									usersList.add((FilterableUser) user);
								}
							}
						}
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
			filteredUsers = (List<FilterableUser>) results.values;
			notifyDataSetChanged();
		}
	}

	public void setFilterableUsers(final List<UserChooser> filterableUsers) {
		this.filterableUsers = filterableUsers;

		notifyDataSetChanged();
	}
}
