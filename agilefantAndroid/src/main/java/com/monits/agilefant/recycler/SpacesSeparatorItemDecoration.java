package com.monits.agilefant.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.monits.agilefant.R;

/**
 * Simple empty space to separate
 * Created by Raul Morales on 9/30/15.
 */
public class SpacesSeparatorItemDecoration extends RecyclerView.ItemDecoration {

	private final Context context;

	/**
	 * Constructor
	 * @param context The context
	 */
	public SpacesSeparatorItemDecoration(final Context context) {
		this.context = context;
	}

	@Override
	public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
		final RecyclerView.State state) {
		outRect.bottom = (int) context.getResources().getDimension(R.dimen.recycler_divider_task);
	}
}
