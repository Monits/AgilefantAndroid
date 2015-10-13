package com.monits.agilefant.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.recyclerviewholders.StoryItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolderUpdateTracker;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolder;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.model.WorkItemType;

import java.util.List;

/**
 * Created by iloredom on 10/6/15.
 */
public class WorkItemAdapter extends RecyclerView.Adapter<WorkItemViewHolder<WorkItem>>
		implements TaskItemViewHolderUpdateTracker {

	protected final FragmentActivity context;
	protected final LayoutInflater inflater;
	protected List<WorkItem> workItems;

	/**
	 * Constructor
	 * @param context The context
	 * @param workItems The items
	 */
	public WorkItemAdapter(final FragmentActivity context, final List<WorkItem> workItems) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.workItems = workItems;
	}

	@Override
	public WorkItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

		final View view = inflater.inflate(viewType, parent, false);
		switch (viewType) {
		case R.layout.task_item:
			return new TaskItemViewHolder(view, context, this);
		case R.layout.stories_item:
			return new StoryItemViewHolder(view, context);
		default:
			throw new AssertionError("can not find view type");
		}
	}

	@Override
	public void onBindViewHolder(final WorkItemViewHolder holder, final int position) {
		final WorkItem workItem = workItems.get(position);
		holder.onBindView(workItem);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (workItem.getType() == WorkItemType.STORY) {
					final Story story = (Story) workItem;

					if (story.isExpanded()) {
						removeWorkItem(story, holder.getAdapterPosition() + 1);
					} else {
						addWorkItem(story, holder.getAdapterPosition() + 1);
					}
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return workItems == null ? 0 : workItems.size();
	}

	@Override
	public int getItemViewType(final int position) {

		final WorkItem workItem = workItems.get(position);
		switch (workItem.getType()) {
		case TASK:
			return R.layout.task_item;
		case STORY:
			return R.layout.stories_item;
		default:
			throw new AssertionError("can not find view type");
		}
	}

	public void setWorkItems(final List<WorkItem> workItems) {
		this.workItems = workItems;
	}

	@Override
	public void onUpdate(final Task updatedTask) {
		// We don't need to implement this yet...
	}

	private void addWorkItem(final Story workItem, final int position) {

		final List<Task> children = workItem.getTasks();
		int i = 0;
		for (final Task item : children) {
			workItems.add(position + i, item);
			i++;
		}
		workItem.setExpanded(true);
		notifyItemRangeInserted(position, children.size());
	}

	private void removeWorkItem(final Story workItem, final int position) {

		final List<Task> children = workItem.getTasks();
		workItems.removeAll(children);
		workItem.setExpanded(false);
		notifyItemRangeRemoved(position, children.size());

	}
}
