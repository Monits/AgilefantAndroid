package com.monits.agilefant.listeners;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public abstract class OnDrawableClickListener implements OnTouchListener {

	private final Drawable drawable;
	private final int fuzz = 40;

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
			if (x >= (v.getRight() - bounds.width() - fuzz) && x <= (v.getRight() - v.getPaddingRight() + fuzz)
					&& y >= (v.getPaddingTop() - fuzz) && y <= (v.getHeight() - v.getPaddingBottom()) + fuzz) {

				return onDrawableClick(event);
			}
		}

		return false;
	}

	public abstract boolean onDrawableClick(MotionEvent event);

	public static enum DrawablePosition {
		LEFT, TOP, RIGHT, BOTTOM
	}
}