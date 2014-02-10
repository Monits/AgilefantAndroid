package com.monits.agilefant.listeners;

import android.view.View;

public interface AdapterViewOnLongActionListener<T> {

	/**
	 * TODO: Adapter to send a object on long press
	 *
	 * @param view the view
	 * @param object the object to be send
	 */
	public boolean onLongAction(View view, T object);

}
