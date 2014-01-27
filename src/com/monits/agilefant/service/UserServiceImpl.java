package com.monits.agilefant.service;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.model.FilterableUser;
import com.monits.agilefant.model.User;

public class UserServiceImpl implements UserService {

	@Inject
	private AgilefantService agilefantService;

	@Inject
	private SharedPreferences sharedPreferences;

	@Override
	public void login(final String domain, final String userName, final String password, final Listener<User> listener, final ErrorListener error) {
		agilefantService.setDomain(domain);
		agilefantService.login(
				userName,
				password,
				new Listener<String>() {

					@Override
					public void onResponse(final String arg0) {
						final Editor editor = sharedPreferences.edit();

						editor.putBoolean(ISLOGGEDIN_KEY, true);
						editor.putString(USER_NAME_KEY, userName);
						editor.putString(PASSWORD_KEY, password);
						editor.putString(DOMAIN_KEY, domain);

						editor.commit();

						retrieveUser(new Listener<User>() {

							@Override
							public void onResponse(final User arg0) {
								storeLoggedUser(arg0);

								listener.onResponse(arg0);
							}

						}, error);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						logout();

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public boolean isLoggedIn() {
		return sharedPreferences.getBoolean(ISLOGGEDIN_KEY, false);
	}

	@Override
	public void logout() {
		sharedPreferences.edit().putBoolean(ISLOGGEDIN_KEY, false).commit();
	}

	@Override
	public void retrieveUser(final Long id, final Listener<User> listener, final ErrorListener error) {
		agilefantService.retrieveUser(id, listener, error);
	}

	@Override
	public void retrieveUser(final Listener<User> listener, final ErrorListener error) {
		retrieveUser(null, listener, error);
	}

	@Override
	public void storeLoggedUser(final User user) {
		if (user != null) {
			final Editor editor = sharedPreferences.edit();
			editor.putLong(USER_ID_KEY, user.getId());
			editor.putString(USER_INITIALS_KEY, user.getInitials());
			editor.putString(FULLNAME_KEY, user.getFullName());
			editor.commit();
		}
	}

	@Override
	public User getLoggedUser() {
		if (isLoggedIn()) {
			final User user = new User();

			user.setId(
					sharedPreferences.getLong(USER_ID_KEY, -1));
			user.setInitials(
					sharedPreferences.getString(USER_INITIALS_KEY, null));
			user.setFullName(
					sharedPreferences.getString(FULLNAME_KEY, null));

			return user;
		}

		return null;
	}

	@Override
	public void getFilterableUsers(final Listener<List<FilterableUser>> listener, final ErrorListener error) {
		agilefantService.getFilterableUsers(listener, error);
	}
}