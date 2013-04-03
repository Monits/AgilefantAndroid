package com.monits.agilefant.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

/**
 * @author Ivan Corbalan
 *
 */
public class ProductExpandableListView extends ExpandableListView {
	private int rowHeight;
	private static final String LOG_TAG = "DebugExpandableListView";
	private int rows;

	/**
	 * Constructor
	 * @param context Context
	 */
	public ProductExpandableListView(Context context, int rowHeight) {
		super(context);
		this.rowHeight = rowHeight;
	}

	/**
	 * Set rows
	 * @param rows Rows
	 */
	public void setRows(int rows) {
		this.rows = rows;
		Log.d(LOG_TAG, "rows set: " + rows);
	}

	public int getRows() {
		return this.rows;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		setMeasuredDimension(getMeasuredWidth(), rows * rowHeight);
		Log.d(LOG_TAG, "onMeasure " + this +
			": width: " + decodeMeasureSpec(widthMeasureSpec) +
			"; height: " + decodeMeasureSpec(heightMeasureSpec)+
			"; measuredHeight: " + getMeasuredHeight() +
			"; measuredWidth: " + getMeasuredWidth());
	}

	private String decodeMeasureSpec(int measureSpec) {
		int mode = View.MeasureSpec.getMode(measureSpec);
		String modeString = "<> ";
		switch (mode) {
			case View.MeasureSpec.UNSPECIFIED:
				modeString = "UNSPECIFIED ";
				break;

			case View.MeasureSpec.EXACTLY:
				modeString = "EXACTLY ";
				break;

			case View.MeasureSpec.AT_MOST:
				modeString = "AT_MOST ";
				break;
		}

		return modeString + Integer.toString(View.MeasureSpec.getSize(measureSpec));
	}
}