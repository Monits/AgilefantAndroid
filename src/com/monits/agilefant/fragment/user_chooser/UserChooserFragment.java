package com.monits.agilefant.fragment.user_chooser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.AutoCompleteUsersAdapter;
import com.monits.agilefant.adapter.SelectedUsersAdapter;
import com.monits.agilefant.adapter.SelectedUsersAdapter.OnRemoveUserListener;
import com.monits.agilefant.model.FilterableUser;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.UserService;

public class UserChooserFragment extends RoboFragment {

	private static final String CURRENT_RESPONSIBLES = "CURRENT_RESPONSIBLES";

	@Inject
	private UserService userService;

	private ViewSwitcher viewSwitcher;

	private final List<User> selectedUsers = new LinkedList<User>();
	private ListView selectedUsersList;
	private SelectedUsersAdapter selectedUsersAdapter;

	private AutoCompleteTextView userInput;
	private AutoCompleteUsersAdapter autoCompleteUsersAdapter;

	private OnUsersSubmittedListener onUsersSubmittedListener;

	public static UserChooserFragment newInstance(final List<User> currentResponsibles, final OnUsersSubmittedListener listener) {
		final UserChooserFragment fragment = new UserChooserFragment();

		final Bundle arguments = new Bundle();
		arguments.putSerializable(CURRENT_RESPONSIBLES, new ArrayList<User>(currentResponsibles));
		fragment.setArguments(arguments);
		fragment.setOnUsersSubmittedListener(listener);

		return fragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {

		final Bundle arguments = getArguments();
		final List<User> currentUsers = (List<User>) arguments.getSerializable(CURRENT_RESPONSIBLES);
		if (currentUsers != null) {
			selectedUsers.addAll(currentUsers);
		}

		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_chooser, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);

		selectedUsersList = (ListView) view.findViewById(R.id.users_list);
		selectedUsersAdapter = new SelectedUsersAdapter(getActivity());
		selectedUsersAdapter.setOnRemoveUserListener(new OnRemoveUserListener() {

			@Override
			public void OnRemoveUser(final View view, final int position, final User user) {
				selectedUsers.remove(position);
				selectedUsersAdapter.setUsers(selectedUsers);
			}
		});

		selectedUsersList.setAdapter(selectedUsersAdapter);

		userInput = (AutoCompleteTextView) view.findViewById(R.id.user_input);
		autoCompleteUsersAdapter = new AutoCompleteUsersAdapter(getActivity());
		userInput.setAdapter(autoCompleteUsersAdapter);
		userInput.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> adapter, final View view, final int position,
					final long id) {
				userInput.setText(null);

				selectedUsers.add(
						autoCompleteUsersAdapter.getItem(position));
				selectedUsersAdapter.setUsers(selectedUsers);
			}
		});

		userService.getFilterableUsers(
				new Listener<List<FilterableUser>>() {

					@Override
					public void onResponse(final List<FilterableUser> response) {
						viewSwitcher.setDisplayedChild(1);

						populateCurrentSelectedUsers(response);

						autoCompleteUsersAdapter.setFilterableUsers(response);
						selectedUsersAdapter.setUsers(selectedUsers);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(getActivity(), R.string.feedback_failed_retrieve_users, Toast.LENGTH_SHORT).show();

						getFragmentManager().popBackStackImmediate();
					}
				});

		view.findViewById(R.id.submit_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (onUsersSubmittedListener != null) {
					onUsersSubmittedListener.onSubmitUsers(selectedUsers);
				}

				getFragmentManager().popBackStackImmediate();
			}
		});

		super.onViewCreated(view, savedInstanceState);
	}

	/**
	 * Project's assignees only contain's the initials and the id of the user, this method is to replace those incomplete current users with
	 * the complete objects
	 *
	 * @param filterableUsers
	 */
	private void populateCurrentSelectedUsers(final List<FilterableUser> filterableUsers) {
		for (int i = 0; i < selectedUsers.size(); i++) {
			final User user = selectedUsers.get(i);
			for (final FilterableUser filterableUser : filterableUsers) {
				if (filterableUser.getId() == user.getId()) {
					selectedUsers.remove(i);
					selectedUsers.add(i, filterableUser.getOriginalUser());
				}
			}
		}
	}

	public void setOnUsersSubmittedListener(final OnUsersSubmittedListener listener) {
		this.onUsersSubmittedListener = listener;
	}

	public static interface OnUsersSubmittedListener {

		/**
		 * This method is called when user's selection was done and confirmed, and returns the list of selected users.
		 *
		 * @param users the selected users.
		 */
		void onSubmitUsers(List<User> users);
	}
}