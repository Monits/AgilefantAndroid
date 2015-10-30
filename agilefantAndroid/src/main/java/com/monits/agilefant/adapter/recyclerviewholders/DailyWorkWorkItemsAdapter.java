package com.monits.agilefant.adapter.recyclerviewholders;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.WorkItemAdapter;
import com.monits.agilefant.model.WorkItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by rpereyra on 10/9/15.
 */
public class DailyWorkWorkItemsAdapter extends WorkItemAdapter {

	/**
	 * Constructor
	 *
	 * @param context   The context
	 * @param workItems The items
	 */
	public DailyWorkWorkItemsAdapter(final FragmentActivity context, final List<WorkItem> workItems) {
		super(context);
		setWorkItems(workItems);
	}

	@Override
	public WorkItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

		final View view = inflater.inflate(viewType, parent, false);
		switch (viewType) {
		case R.layout.my_tasks_item_nocontext:
			return new DailyWorkTaskItemViewHolder(view, context, this);
		case R.layout.my_story_item:
			return new StoryItemViewHolder(view, context, this);
		default:
			throw new AssertionError("can not find view type");
		}
	}

	@Override
	public int getItemViewType(final int position) {

		final WorkItem workItem = workItems.get(position);
		switch (workItem.getType()) {
		case TASK:
			return R.layout.my_tasks_item_nocontext;
		case STORY:
			return R.layout.my_story_item;
		default:
			throw new AssertionError("can not find view type");
		}
	}

	public List<WorkItem> getWorkItems() {
		return Collections.unmodifiableList(workItems);
	}
}
