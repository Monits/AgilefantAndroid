package com.monits.agilefant.adapter.recyclerviewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.monits.agilefant.model.WorkItem;

public abstract class WorkItemViewHolder<T extends WorkItem> extends RecyclerView.ViewHolder {

	/**
	 * Constructor
	 * @param itemView view's Item
	 */
	public WorkItemViewHolder(final View itemView) {
		super(itemView);
	}

	/**
	 * Method to be called when the view is bind
	 * @param item The item
	 */
	public abstract void onBindView(final T item);
}
