/**
 * 
 */
package com.monits.agilefant.parser;

import com.google.gson.Gson;
import com.monits.agilefant.model.User;

/**
 * @author gmuniz
 *
 */
public class UserParserImpl implements UserParser {

	@Override
	public User parseUser(String json) {
		Gson gson = new Gson();
		User user = gson.fromJson(json, User.class);

		return user;
	}

}
