package com.monits.agilefant.ui.component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;

import com.monits.agilefant.adapter.AutoCompleteUsersAdapter;
import com.monits.agilefant.model.FilterableTeam;
import com.monits.agilefant.model.FilterableUser;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.UserChooser;

public class AutoCompleteUserChooserTextView extends AutoCompleteTextView {

	private final List<User> selectedUsers = new LinkedList<User>();
	private OnUserChooserActionListener onUserChooserActionListener;

	public AutoCompleteUserChooserTextView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
				setText(null);
				final UserChooser userChooser = (UserChooser) getAdapter().getItem(position);
				if (userChooser instanceof FilterableUser) {
					final User filterableUser = ((FilterableUser) userChooser).getUser();
					if (!selectedUsers.contains(filterableUser)) {
						selectedUsers.add(filterableUser);
					}
				} else {
					final List<Long> usersId = ((FilterableTeam) userChooser).getUsersId();
					final List<UserChooser> filterableUsers =
							((AutoCompleteUsersAdapter) getAdapter()).getFilterableUsers();
					addUsersFromTeam(usersId, filterableUsers);
				}
				sendUserChooserAction();
			}
		});
	}

	private void sendUserChooserAction() {
		if (onUserChooserActionListener != null) {
			onUserChooserActionListener.onUserChooserAction(selectedUsers);
		}
	}

	private void addUsersFromTeam(final List<Long> usersId, final List<UserChooser> filterableUsers) {
		for (int i = 0; i < filterableUsers.size(); i++) {
			final UserChooser user = filterableUsers.get(i);
			if (user instanceof FilterableUser && user.isEnabled() && usersId.contains(user.getId())
					&& !selectedUsers.contains(((FilterableUser) user).getUser())) {
				selectedUsers.add(((FilterableUser) user).getUser());
			}
		}
	}

	public void setUsers(final List<User> users) {
		selectedUsers.addAll(users);
	}

	public static interface OnUserChooserActionListener {
		public void onUserChooserAction(List<User> selectedUsers);
	}

	public void setOnUserChooserActionListener(final OnUserChooserActionListener listener) {
		this.onUserChooserActionListener = listener;
	}

	/**
	 * Project's assignees only contain's the initials and the id of the user,
	 * this method is to replace those incomplete current users with the complete objects
	 *
	 * @param filterableUsers
	 */
	public void populateCurrentSelectedUsers(final List<UserChooser> filterableUsers) {
		for (int i = 0; i < selectedUsers.size(); i++) {
			final User user = selectedUsers.get(i);
			for (final UserChooser userChooser : filterableUsers) {
				if (userChooser instanceof FilterableUser && userChooser.getId() == user.getId()) {
					selectedUsers.set(i, ((FilterableUser) userChooser).getUser());
				}
			}
		}
		sendUserChooserAction();
	}

	public void removeUser(final User user) {
		selectedUsers.remove(user);
		sendUserChooserAction();
	}

	public List<User> getSelectedUsers() {
		return Collections.unmodifiableList(selectedUsers);
	}
}