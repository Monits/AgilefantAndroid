package com.monits.agilefant.listeners.implementations;

import java.util.Observable;
import java.util.Observer;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.monits.agilefant.listeners.AdapterViewActionListener;

public abstract class AbstractObservableAdapterViewActionListener<T extends Observable>
		implements AdapterViewActionListener<T> {

	protected final FragmentActivity context;

	/**
	 * Constructor.
	 * @param context The context
	 */
	public AbstractObservableAdapterViewActionListener(final FragmentActivity context) {
		this.context = context;
	}

	@Override
	public void onAction(final View view, final T object) {
		object.addObserver(getObserver());
	}

	/**
	 * A implementation of {@link Observer} interface in which will be handling object updates.
	 *
	 * @return the given implementation.
	 */
	protected abstract Observer getObserver();
}
