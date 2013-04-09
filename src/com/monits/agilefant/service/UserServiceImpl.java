package com.monits.agilefant.service;

import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;

public class UserServiceImpl implements UserService {

	@Inject
	private AgilefantService agilefantService;

	@Inject
	private SharedPreferences sharedPreferences;

	private boolean isLoggedIn;

	@Override
	public boolean login(String domain, String userName, String password) throws RequestException {
		agilefantService.setDomain(domain);
		isLoggedIn = agilefantService.login(userName, password);
		sharedPreferences.edit().putBoolean(ISLOGGEDIN_KEY, isLoggedIn).commit();
		if (isLoggedIn) {
			sharedPreferences.edit().putString(USER_NAME_KEY, userName).commit();
			sharedPreferences.edit().putString(PASSWORD_KEY, password).commit();
			sharedPreferences.edit().putString(DOMAIN_KEY, domain).commit();
		}
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
}