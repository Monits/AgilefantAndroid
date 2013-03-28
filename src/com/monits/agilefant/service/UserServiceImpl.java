package com.monits.agilefant.service;

import com.google.inject.Inject;
import com.monits.agilefant.exception.RequestException;

public class UserServiceImpl implements UserService {

	@Inject
	private AgilefantService agilefantService;

	@Override
	public String login(String userName, String password) throws RequestException {
		return agilefantService.login(userName, password);
	}

}
