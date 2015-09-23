package com.monits.agilefant.adapter.recyclerviewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.monits.agilefant.model.Backlog;


/**
 * Created by sdeira on 15/09/15.
 */
public abstract class BacklogViewHolder extends RecyclerView.ViewHolder {

	/**
	 * Abstract Recycler View Holder
	 * @param itemView Inflate View
	 */
	public BacklogViewHolder(final View itemView) {
		super(itemView);
	}

	/**
	 * OnBindViewHolder
	 * @param backlog Backlog with data to bind
	 */
	public abstract void onBindViewHolder(final Backlog backlog);

	/**
	 * onItemClick
	 */
	public abstract void onItemClick();
}
