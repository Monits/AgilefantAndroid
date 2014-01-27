package com.monits.agilefant.service;

import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.FilterableUser;
import com.monits.agilefant.model.User;

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
	 * SharedPreferences user fullName key
	 */
	public static final String FULLNAME_KEY = "USER_FULLNAME_KEY";

	/**
	 * SharedPreferences user id key
	 */
	public static final String USER_ID_KEY = "USER_ID_KEY";

	/**
	 * SharedPreferences user initials key
	 */
	public static final String USER_INITIALS_KEY = "USER_INITIALS_KEY";

	/**
	 * Login in Agilefant
	 *
	 * @param domain Agilefant domain
	 * @param userName User name
	 * @param password The password
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void login(String domain, String userName, String password, Listener<User> listener, ErrorListener error);

	/**
	 * @return If the user is logged
	 */
	boolean isLoggedIn();

	/**
	 * Logout in Agilefant
	 */
	void logout();

	/**
	 * Retrieves the user identified by the given id. If id is null, It'll retrieve the user bound
	 * to the current agilefant session.
	 *
	 * @param id the id of the user
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void retrieveUser(Long id, Listener<User> listener, ErrorListener error);

	/**
	 * Retrieves the current session user.
	 * <blockquote>
	 * This is a convenience for .retrieveUser(null)
	 * </blockquote>
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void retrieveUser(Listener<User> listener, ErrorListener error);

	/**
	 * saves the user in the shared preferences.
	 *
	 * @param user the user to be stored.
	 */
	void storeLoggedUser(User user);

	/**
	 * Retrieves the logged user.
	 *
	 * @return the logged user.
	 */
	User getLoggedUser();

	/**
	 * Retrieves the whole list of users in agilefant.
	 *
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getFilterableUsers(Listener<List<FilterableUser>> listener, ErrorListener error);
}