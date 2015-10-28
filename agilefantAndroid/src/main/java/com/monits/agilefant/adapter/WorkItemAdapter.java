package com.monits.agilefant.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.helper.UpdateAdapterHelper;
import com.monits.agilefant.adapter.recyclerviewholders.StoryItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolderUpdateTracker;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolder;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.model.WorkItemType;
import com.monits.agilefant.recycler.DragAndDropListener;
import com.monits.agilefant.service.WorkItemService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by iloredom on 10/6/15.
 */
public class WorkItemAdapter extends RecyclerView.Adapter<WorkItemViewHolder<WorkItem>>
		implements WorkItemViewHolderUpdateTracker, DragAndDropListener {

	protected final FragmentActivity context;
	protected final LayoutInflater inflater;
	protected List<WorkItem> workItems;

	private final UpdateAdapterHelper updateAdapterHelper;

	@Inject
	/* default */ WorkItemService workItemService;

	/**
	 * Constructor
	 * @param context The context
	 */
	public WorkItemAdapter(final FragmentActivity context) {
		this.context = context;
		this.inflater = context.getLayoutInflater();
		updateAdapterHelper = new UpdateAdapterHelper(this);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public WorkItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

		final View view = inflater.inflate(viewType, parent, false);
		switch (viewType) {
		case R.layout.task_item:
			return new TaskItemViewHolder(view, context, this);
		case R.layout.stories_item:
			return new StoryItemViewHolder(view, context, this);
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

	/**
	 * @param workItems A work item list to set
	 */
	public void setWorkItems(final List<? extends WorkItem> workItems) {
		this.workItems = new ArrayList<>(workItems);
		notifyDataSetChanged();
	}

	@Override
	public void onUpdate(final WorkItem updatedWork) {
		updateAdapterHelper.updateItem(workItems, updatedWork);
	}

	private void addWorkItem(final Story workItem, final int position) {

		final List<Task> children = workItem.getTasks();
		int i = 0;
		for (final WorkItem item : children) {
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

	/**
	 * If it's present, update the given task
	 * @param updatedTask The updated task
	 */
	public void updateTask(final Task updatedTask) {
		final int storyIndex = workItems.indexOf(updatedTask.getStory());
		if (storyIndex != -1) {
			final Story story = (Story) workItems.get(storyIndex);

			final List<Task> tasks = story.getTasks();
			final int indexOf = tasks.indexOf(updatedTask);

			if (indexOf != -1) {
				tasks.get(indexOf).updateValues(updatedTask);
				notifyItemChanged(indexOf);
			}
		}
	}

	/**
	 * Add a story
	 * @param newStory The new story
	 */
	public void addStory(final Story newStory) {
		workItems.add(newStory);
		notifyItemInserted(workItems.size() - 1);
	}

	public boolean isEmpty() {
		return workItems.isEmpty();
	}

	@Override
	public void onMove(final int fromPosition, final int toPosition) {
		notifyItemMoved(fromPosition, toPosition);
		Collections.swap(workItems, fromPosition, toPosition);
	}

	@Override
	public void onChangePosition(final int fromPosition, final int toPosition) {
		final WorkItem fromItem = workItems.get(fromPosition);
		final WorkItem toItem = workItems.get(toPosition);

		if (fromItem.getType() == WorkItemType.STORY) {
			final Story story = (Story) fromItem;
			final Story storyTarget = (Story) toItem;

			final Response.Listener<Story> successListener =
					getSuccessListener(R.string.feedback_success_update_story_rank);
			final Response.ErrorListener errorListener = getErrorListener(R.string.feedback_failed_update_story_rank);

			if (fromPosition < toPosition) {
				workItemService.rankStoryOver(
						story, storyTarget, getStoryList(), successListener, errorListener);
			} else {
				workItemService.rankStoryUnder(
						story, storyTarget, getStoryList(), successListener, errorListener);
			}
		} else {

			final Task currentTask = (Task) fromItem;
			final Task targetTask = (Task) toItem;

			final Response.Listener<Task> successListener =
					getSuccessListener(R.string.feedback_success_updated_task_rank);
			final Response.ErrorListener errorListener = getErrorListener(R.string.feedback_failed_update_tasks_rank);

			workItemService.rankTaskUnder(currentTask, targetTask, currentTask.getStory().getTasks(),
					successListener, errorListener);
		}
	}

	@NonNull
	private Response.ErrorListener getErrorListener(@StringRes final int idMessage) {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(final VolleyError arg0) {
				Toast.makeText(context, idMessage, Toast.LENGTH_SHORT).show();
			}
		};
	}

	@NonNull
	private <T extends WorkItem> Response.Listener<T> getSuccessListener(@StringRes final int idMessage) {
		return new Response.Listener<T>() {
			@Override
			public void onResponse(final T arg0) {
				Toast.makeText(context, idMessage, Toast.LENGTH_SHORT).show();
			}
		};
	}

	private List<Story> getStoryList() {
		final List<Story> storyList = new ArrayList<>();
		for (final WorkItem item : workItems) {
			if (item.getType() == WorkItemType.STORY) {
				storyList.add((Story) item);
			}
		}
		return storyList;
	}

	@Override
	public WorkItem getItem(final int position) {
		return workItems.get(position);
	}
}
