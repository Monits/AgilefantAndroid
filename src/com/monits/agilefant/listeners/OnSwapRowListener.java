package com.monits.agilefant.listeners;

public interface OnSwapRowListener {

	/**
	 * This listener returns the position of the dragged item, and the new position of the affected row.
	 *
	 * @param itemPosition the position of the dragged item.
	 * @param targetPosition the position of the displaced row item.
	 * @param swapDirection the direction where the dragged item was swapped.
	 * @param aboveItemId the id of the row above the dragged.
	 * @param belowItemId the id of the row below the dragged one.
	 */
	void onSwapPositions(int itemPosition, int targetPosition, SwapDirection swapDirection, long aboveItemId, long belowItemId);

	public static enum SwapDirection {
		ABOVE_TARGET, BELOW_TARGET
	}
}
