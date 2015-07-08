package com.monits.agilefant.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * @author Ivan Corbalan
 *
 */
public class ProductExpandableListView extends ExpandableListView {

	private boolean expanded = true;

	/**
	 * Constructor
	 * @param context Context
	 */
	public ProductExpandableListView(final Context context) {
		super(context);
	}

	/**
	 * @return True if is expanded, otherwise false
	 */
	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		// HACK! TAKE THAT ANDROID!
		if (isExpanded()) {
			// Calculate entire height by providing a very large height hint.
			// But do not use the highest 2 bits of this integer; those are
			// reserved for the MeasureSpec mode.
			final int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

			super.onMeasure(widthMeasureSpec, expandSpec);

			final ViewGroup.LayoutParams params = getLayoutParams();
			params.height = getMeasuredHeight();
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 * Set the list view as expandent
	 * @param expanded The new value to set
	 */
	public void setExpanded(final boolean expanded) {
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return new StringBuilder("ProductExpandableListView [expanded: ").append(expanded).append(']').toString();
	}
}