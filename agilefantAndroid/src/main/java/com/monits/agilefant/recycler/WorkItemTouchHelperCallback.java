package com.monits.agilefant.recycler;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Callback for RecyclerView tasks without Story
 * Created by Raul Morales on 9/30/15.
 */
public class WorkItemTouchHelperCallback extends ItemTouchHelper.Callback {

	private final DragAndDropListener dragAndDropListener;
	private int originalPosition;
	private int newPosition;

	/**
	 * Constructor
	 * @param listener The Adapter to implement DragAndDropListener
	 */
	public WorkItemTouchHelperCallback(final DragAndDropListener listener) {
		this.dragAndDropListener = listener;
	}

	@Override
	public int getMovementFlags(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
		final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
		return makeMovementFlags(dragFlags, 0);
	}

	@Override
	public boolean onMove(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
		final RecyclerView.ViewHolder target) {
		originalPosition = viewHolder.getAdapterPosition();
		newPosition = target.getAdapterPosition();
		dragAndDropListener.onMove(originalPosition, newPosition);
		return true;
	}

	@Override
	public void onSelectedChanged(final RecyclerView.ViewHolder viewHolder, final int actionState) {
		super.onSelectedChanged(viewHolder, actionState);
		if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && newPosition != originalPosition) {
			dragAndDropListener.onChangePosition(originalPosition, newPosition);
		}
	}

	@Override
	public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction) {
		//do nothing, in this case we are not interested Swipe
	}

	@Override
	public boolean isItemViewSwipeEnabled() {
		return false;
	}

	@Override
	public String toString() {
		return "WorkItemTouchHelperCallback{"
				+ ", originalPosition=" + originalPosition
				+ ", newPosition=" + newPosition
				+ '}';
	}
}
