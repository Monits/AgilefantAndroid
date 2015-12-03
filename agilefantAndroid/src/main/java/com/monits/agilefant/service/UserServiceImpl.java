package com.monits.agilefant.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.model.User;
import com.monits.volleyrequests.network.request.GsonRequest;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class UserServiceImpl implements UserService {

	private final AgilefantService agilefantService;

	private static final String USER_ID = "userId";
	private static final String RETRIEVE_USER_ACTION = "/ajax/retrieveUser.action";
	private static final String USER_CHOOSER_DATA_ACTION = "%1$s/ajax/userChooserData.action";

	private final SharedPreferences sharedPreferences;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	/**
	 * @param agilefantService Injected via constructor by Dagger
	 * @param sharedPreferences Injected via constructor by Dagger
	 * @param gson Injected via constructor by Dagger
	 */

	@Inject
	public UserServiceImpl(final AgilefantService agilefantService,
						final SharedPreferences sharedPreferences, final Gson gson) {
		this.agilefantService = agilefantService;
		this.sharedPreferences = sharedPreferences;
		this.gson = gson;
	}

	@Override
	public void login(final String domain, final String userName, final String password, final Listener<User> listener,
			final ErrorListener error) {
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

					editor.apply();

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
		sharedPreferences.edit().putBoolean(ISLOGGEDIN_KEY, false).apply();
	}

	@Override
	public void retrieveUser(final Long id, final Listener<User> listener, final ErrorListener error) {
		final StringBuilder url = new StringBuilder().append(agilefantService.getHost()).append(RETRIEVE_USER_ACTION);
		if (id != null) {
			url.append('?').append(USER_ID).append('=').append(id);
		}

		final GsonRequest<User> request = new GsonRequest<>(
				Request.Method.GET, url.toString(), gson, User.class, listener, error, null);

		agilefantService.addRequest(request);
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
			editor.apply();
		}
	}

	@Override
	public User getLoggedUser() {
		if (isLoggedIn()) {
			final User user = new User();

			user.setId(sharedPreferences.getLong(USER_ID_KEY, -1));
			user.setInitials(sharedPreferences.getString(USER_INITIALS_KEY, null));
			user.setFullName(sharedPreferences.getString(FULLNAME_KEY, null));

			return user;
		}

		return null;
	}

	@Override
	public void getFilterableUsers(final Listener<List<UserChooser>> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, USER_CHOOSER_DATA_ACTION, agilefantService.getHost());

		final Type listType = new TypeToken<ArrayList<UserChooser>>() { }.getType();
		final GsonRequest<List<UserChooser>> request = new GsonRequest<>(
				Request.Method.POST, url, gson, listType, listener, error, null);

		agilefantService.addRequest(request);
	}
}