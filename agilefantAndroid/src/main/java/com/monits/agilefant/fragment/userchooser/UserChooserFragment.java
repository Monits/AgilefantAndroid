package com.monits.agilefant.fragment.userchooser;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.ui.component.AutoCompleteUserChooserTextView;

public class UserChooserFragment extends RoboFragment {

	private static final String CURRENT_RESPONSIBLES = "CURRENT_RESPONSIBLES";

	@Inject
	private UserService userService;

	private ViewSwitcher viewSwitcher;

	private ListView selectedUsersList;
	private SelectedUsersAdapter selectedUsersAdapter;

	private AutoCompleteUserChooserTextView userInput;
	private AutoCompleteUsersAdapter autoCompleteUsersAdapter;

	private OnUsersSubmittedListener onUsersSubmittedListener;

	/**
	 * Creates a new UserChooserFragment with th given responsibles.
	 * @param currentResponsibles The responsibles.
	 * @param listener The users submitted listener
	 * @return the new fragment
	 */
	public static UserChooserFragment newInstance(final List<User> currentResponsibles,
			final OnUsersSubmittedListener listener) {
		final UserChooserFragment fragment = new UserChooserFragment();

		final Bundle arguments = new Bundle();
		arguments.putSerializable(CURRENT_RESPONSIBLES, new ArrayList<User>(currentResponsibles));
		fragment.setArguments(arguments);
		fragment.setOnUsersSubmittedListener(listener);

		return fragment;
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
			public void onRemoveUser(final View view, final int position, final User user) {
				userInput.removeUser(user);
			}
		});

		selectedUsersList.setAdapter(selectedUsersAdapter);

		userInput = (AutoCompleteUserChooserTextView) view.findViewById(R.id.user_input);

		@SuppressWarnings("unchecked")
		final List<User> currentUsers = (List<User>) getArguments().getSerializable(CURRENT_RESPONSIBLES);
		if (currentUsers != null) {
			userInput.setUsers(currentUsers);
		}

		autoCompleteUsersAdapter = new AutoCompleteUsersAdapter(getActivity());
		userInput.setAdapter(autoCompleteUsersAdapter);

		userService.getFilterableUsers(
				new Listener<List<UserChooser>>() {

					@Override
					public void onResponse(final List<UserChooser> response) {
						viewSwitcher.setDisplayedChild(1);

						userInput.populateCurrentSelectedUsers(response);

						autoCompleteUsersAdapter.setFilterableUsers(response);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(
							getActivity(), R.string.feedback_failed_retrieve_users, Toast.LENGTH_SHORT).show();

						getFragmentManager().popBackStackImmediate();
					}
				});

		userInput.setOnUserChooserActionListener(
			new AutoCompleteUserChooserTextView.OnUserChooserActionListener() {
				@Override
				public void onUserChooserAction(final List<User> selected) {
					selectedUsersAdapter.setUsers(selected);
				}
			}
		);

		view.findViewById(R.id.submit_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (onUsersSubmittedListener != null) {
					onUsersSubmittedListener.onSubmitUsers(userInput.getSelectedUsers());
				}

				getFragmentManager().popBackStackImmediate();
			}
		});

		super.onViewCreated(view, savedInstanceState);
	}

	/**
	 * Set the listener for selected users submission
	 * @param listener The listener to set
	 */
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