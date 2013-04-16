package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;

public interface UserService {

	/**
	 * SharedPreferences domain key
	 */
	public static final String DOMAIN_KEY = "DOMAIN_KEY";

	/**
	 * SharedPreferences user name key
	 */
	public static final String USER_NAME_KEY = "USER_NAME_KEY";

	/**
	 * SharedPreferences password key
	 */
	public static final String PASSWORD_KEY = "PASSWORD_KEY";

	/**
	 * SharedPreferences isLoggedin key
	 */
	public static final String ISLOGGEDIN_KEY = "ISLOGGEDIN_KEY";

	/**
	 * Login in Agilefant
	 * @param domain Agilefant domain
	 * @param userName User name
	 * @param password The password
	 * @return If the user is logged
	 * @throws RequestException
	 */
	boolean login(String domain, String userName, String password) throws RequestException;

	/**
	 * @return If the user is logged
	 */
	boolean isLoggedIn();

	/**
	 * Logout in Agilefant
	 */
	void logout();
}
