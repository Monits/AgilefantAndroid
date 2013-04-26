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
	public ProductExpandableListView(Context context) {
		super(context);
	}

	public boolean isExpanded()
	{
		return expanded;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// HACK! TAKE THAT ANDROID!
		if (isExpanded()) {
			// Calculate entire height by providing a very large height hint.
			// But do not use the highest 2 bits of this integer; those are
			// reserved for the MeasureSpec mode.
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

			super.onMeasure(widthMeasureSpec, expandSpec);

			ViewGroup.LayoutParams params = getLayoutParams();
			params.height = getMeasuredHeight();
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void setExpanded(boolean expanded)
	{
		this.expanded = expanded;
	}
}