package com.monits.agilefant.listeners;

import android.view.View;

public interface AdapterViewOnLongActionListener<T> {

	/**
	 * TODO: Adapter to send a object on long press
	 *
	 * @param view the view
	 * @param object the object to be send
	 * @return true if the callback consumed the long click, false otherwise.
	 */
	boolean onLongAction(View view, T object);

}
