package com.monits.agilefant.adapter.helper;

import android.support.v7.widget.RecyclerView.Adapter;

import com.monits.agilefant.model.WorkItem;

import java.util.List;

/**
 * Created by lgnanni on 26/10/15.
 */
public class UpdateAdapterHelper {

	private final Adapter adapter;

	/**
	 * Update Adapter Helper
	 * @param adapter The adapter to refresh view
	 */

	public UpdateAdapterHelper(final Adapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Updates the selected item values and refresh the adapter view
	 * @param list Parent list of the item
	 * @param item The item to update
	 */

	public void updateItem(final List<? extends WorkItem> list, final WorkItem item) {
		int cont = 0;
		for (final WorkItem workItem : list) {
			if (item.getId() == workItem.getId()) {
				workItem.updateValues(item);
				adapter.notifyItemChanged(cont);
				break;
			}

			cont++;
		}
	}

}
