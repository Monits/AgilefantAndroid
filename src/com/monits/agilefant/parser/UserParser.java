/**
 * 
 */
package com.monits.agilefant.parser;

import com.monits.agilefant.model.User;

/**
 * @author gmuniz
 *
 */
public interface UserParser {

	/**
	 * Parses user json string into {@link User}.
	 * 
	 * @param json String in JSON format
	 * 
	 * @return the parsed user
	 */
	User parseUser(String json);
}
