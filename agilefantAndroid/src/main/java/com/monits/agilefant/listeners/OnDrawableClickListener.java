package com.monits.agilefant.listeners;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public abstract class OnDrawableClickListener implements OnTouchListener {

	private static final int FUZZ = 40;
	private final Drawable drawable;

	/**
	 * Constructor
	 * @param view The view
	 * @param position The item position
	 */
	public OnDrawableClickListener(final TextView view, final DrawablePosition position) {
		super();
		final Drawable[] drawables = view.getCompoundDrawables();
		this.drawable = drawables[position.ordinal()];
	}

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			final Rect bounds = drawable.getBounds();
			if (x >= (v.getRight() - bounds.width() - FUZZ) && x <= (v.getRight() - v.getPaddingRight() + FUZZ)
					&& y >= (v.getPaddingTop() - FUZZ) && y <= (v.getHeight() - v.getPaddingBottom()) + FUZZ) {

				return onDrawableClick(event);
			}
		}

		return false;
	}

	/**
	 * DrawableClick Event
	 * @param event The event triggered
	 * @return true if the callback consumed the event, false otherwise.
	 */
	public abstract boolean onDrawableClick(MotionEvent event);

	public enum DrawablePosition {
		LEFT, TOP, RIGHT, BOTTOM
	}
}