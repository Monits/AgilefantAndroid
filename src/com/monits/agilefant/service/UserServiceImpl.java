package com.monits.agilefant.service;

import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;

public class UserServiceImpl implements UserService {

	@Inject
	private AgilefantService agilefantService;

	@Inject
	private SharedPreferences sharedPreferences;

	@Override
	public boolean login(String domain, String userName, String password) throws RequestException {
		agilefantService.setDomain(domain);
		boolean isLoggedIn = agilefantService.login(userName, password);
		if (isLoggedIn) {
			sharedPreferences.edit().putString(USER_NAME_KEY, userName).commit();
			sharedPreferences.edit().putString(DOMAIN_KEY, domain).commit();
		}
		return isLoggedIn;

	}

}
