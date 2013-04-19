package com.monits.agilefant.service;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.User;
import com.monits.agilefant.parser.UserParser;

public class UserServiceImpl implements UserService {

	@Inject
	private AgilefantService agilefantService;

	@Inject
	private SharedPreferences sharedPreferences;

	@Inject
	private UserParser userParser;

	private boolean isLoggedIn;

	@Override
	public boolean login(String domain, String userName, String password) throws RequestException {
		agilefantService.setDomain(domain);
		isLoggedIn = agilefantService.login(userName, password);

		Editor editor = sharedPreferences.edit();
		editor.putBoolean(ISLOGGEDIN_KEY, isLoggedIn);
		if (isLoggedIn) {
			editor.putString(USER_NAME_KEY, userName);
			editor.putString(PASSWORD_KEY, password);
			editor.putString(DOMAIN_KEY, domain);
		}
		editor.commit();

		return isLoggedIn;
	}

	@Override
	public boolean isLoggedIn() {
		return sharedPreferences.getBoolean(ISLOGGEDIN_KEY, false);
	}

	@Override
	public void logout() {
		this.isLoggedIn = false;
		sharedPreferences.edit().putBoolean(ISLOGGEDIN_KEY, isLoggedIn).commit();
	}

	@Override
	public User retrieveUser(Long id) throws RequestException {
		User user = userParser.parseUser(
				agilefantService.retrieveUser(id));

		return user;
	}

	@Override
	public User retrieveUser() throws RequestException {
		return retrieveUser(null);
	}

	@Override
	public void storeLoggedUser(User user) {
		if (user != null) {
			Editor editor = sharedPreferences.edit();
			editor.putLong(USER_ID_KEY, user.getId());
			editor.putString(USER_INITIALS_KEY, user.getInitials());
			editor.putString(FULLNAME_KEY, user.getFullName());
			editor.commit();
		}
	}

	@Override
	public User getLoggedUser() {
		if (isLoggedIn()) {
			User user = new User();

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
}