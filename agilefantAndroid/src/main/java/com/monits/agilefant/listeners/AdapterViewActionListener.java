/**
 * 
 */
package com.monits.agilefant.listeners;

import android.view.View;

/**
 * @author gmuniz
 *
 */
public interface AdapterViewActionListener<T> {

	/**
	 * Triggered when an event bound to this listener occurs,
	 * and returns the view involved, and the object represented in that row.
	 * 
	 * @param view the view where event was triggered.
	 * @param object the object represented in that row.
	 */
	void onAction(View view, T object);
}
