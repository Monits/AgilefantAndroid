/**
 * 
 */
package com.monits.agilefant.listeners;

/**
 * @author gmuniz
 *
 */
public interface TaskCallback<T> {

	/**
	 * Triggered if an error occurred.
	 */
	void onError();

	/**
	 * Triggered if task result was successful.
	 * @param response The response
	 */
	void onSuccess(T response);
}
