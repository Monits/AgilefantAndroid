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
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolderUpdateTracker;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.model.WorkItemType;
import com.monits.agilefant.recycler.DragAndDropListener;
import com.monits.agilefant.service.WorkItemService;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by iloredom on 10/6/15.
 */
public class WorkItemAdapter extends RecyclerView.Adapter<WorkItemViewHolder<WorkItem>>
		implements WorkItemViewHolderUpdateTracker, DragAndDropListener {

	protected final FragmentActivity context;
	protected final LayoutInflater inflater;
	protected List<WorkItem> workItems;
	protected List<WorkItem> originalWorkItems;

	private final UpdateAdapterHelper updateAdapterHelper;

	private String queryText = "";

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
		final int originalPosition = originalWorkItems.indexOf(workItem);
		final WorkItem originalWorkItem = originalWorkItems.get(originalPosition);
		holder.onBindView(workItem);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (workItem.getType() == WorkItemType.STORY) {
					final Story story = (Story) workItem;
					if (story.isExpanded()) {
						removeWorkItem(story, holder.getAdapterPosition() + 1);
						removeWorkItemFromOriginalList((Story) originalWorkItem);
					} else {
						addWorkItem(story, holder.getAdapterPosition() + 1);
						addWorkItemFromOriginalList((Story) originalWorkItem, originalPosition + 1);
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
		this.originalWorkItems = new ArrayList<>(workItems);
		this.workItems = new ArrayList<>(originalWorkItems);
		notifyDataSetChanged();
	}

	/**
	 * Returns the WorkItem list attached to this adapter
	 * @return The workItem list
	 */
	public List<WorkItem> getWorkItems() {
		return this.workItems;
	}

	@Override
	public void onUpdate(final WorkItem updatedWork) {
		updateAdapterHelper.updateItem(workItems, updatedWork);
	}

	private void addWorkItem(final Story workItem, final int position) {
		final List<Task> children = workItem.getTasks();
		int i = 0;
		for (final WorkItem item : children) {
			if (matchesFilterQuery(item.getName()) || matchesFilterQuery(workItem.getName())) {
				workItems.add(position + i, item);
				i++;
			}
		}
		workItem.setExpanded(true);
		notifyItemRangeInserted(position, i);
	}

	private void removeWorkItem(final Story workItem, final int position) {
		final List<Task> children = workItem.getTasks();
		int i = 0;
		for (final WorkItem item : children) {
			if (matchesFilterQuery(item.getName()) || matchesFilterQuery(workItem.getName())) {
				workItems.remove(item);
				i++;
			}
		}
		workItem.setExpanded(false);
		notifyItemRangeRemoved(position, i);
	}

	private void addWorkItemFromOriginalList(final Story workItem, final int position) {
		final List<Task> children = workItem.getTasks();
		int i = 0;
		for (final WorkItem item : children) {
			originalWorkItems.add(position + i, item);
			i++;
		}
	}

	private void removeWorkItemFromOriginalList(final Story workItem) {
		final List<Task> children = workItem.getTasks();
		originalWorkItems.removeAll(children);
	}

	/**
	 * Add a story
	 * @param newStory The new story
	 */
	public void addStory(final Story newStory) {
		workItems.add(newStory);
		originalWorkItems.add(newStory);
		notifyItemInserted(workItems.size() - 1);
	}

	public boolean isEmpty() {
		return workItems.isEmpty();
	}

	@Override
	public void onMove(final int fromPosition, final int toPosition) {
		final WorkItem fromItem = workItems.get(fromPosition);
		final WorkItem toItem = workItems.get(toPosition);

		// Moving a Story
		if (fromItem.getType() == WorkItemType.STORY) {
			final Story fromStory = (Story) fromItem;
			final Story toStory = (Story) toItem;

			// Move stories along tasks
			moveItemRange(fromPosition, visibleElementCount(fromStory), toPosition, visibleElementCount(toStory));
		} else {
			// Moving a Task
			final Task fromTask = (Task) fromItem;
			final Task toTask = (Task) toItem;

			if (fromTask.getStory() == toTask.getStory()) {
				moveItemRange(fromPosition, 1, toPosition, 1);
			}
		}
	}

	/**
	 * @param story The story to analyze
	 * @return Count of visible elements
	 */
	private int visibleElementCount(final Story story) {
		return story.isExpanded() ? story.getTasks().size() + 1 : 1;
	}

	private void moveItemRange(final int from, final int length, final int to, final int toElementLength) {
		// Take them out of the list...
		final List<WorkItem> movedElements = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			movedElements.add(workItems.remove(from));
		}

		// ... and back in their new positions
		// Account for the range we are moving
		final int fixedTo = from < to ? to - length + toElementLength : to;
		for (int i = 0; i < length; i++) {
			workItems.add(fixedTo + i, movedElements.get(i));
		}

		// notify movement in reverse of direction we are moving
		if (from < to) {
			for (int i = length - 1; i >= 0; i--) {
				notifyItemMoved(from + i, fixedTo + i);
			}
		} else {
			for (int i = 0; i < length; i++) {
				notifyItemMoved(from + i, fixedTo + i);
			}
		}
	}

	@Override
	public void onChangePosition(final int fromPosition, final int toPosition) {
		// Use the one at the top to know what we moved regardless of list mangling
		final WorkItem referenceElement = workItems.get(toPosition < fromPosition ? toPosition : fromPosition);

		// Are we moving stories or tasks?
		if (referenceElement.getType() == WorkItemType.STORY) {

			final Response.Listener<Story> successListener =
					getSuccessListener(R.string.feedback_success_update_story_rank);
			final Response.ErrorListener errorListener = getErrorListener(R.string.feedback_failed_update_story_rank);

			if (fromPosition > toPosition) {
				final Story story = (Story) referenceElement;
				final Story storyTarget = getRelevantStory(toPosition + visibleElementCount(story));

				workItemService.rankStoryOver(
						story, storyTarget, getStoryList(), successListener, errorListener);
			} else {
				final Story storyTarget = (Story) referenceElement;
				final Story story = getRelevantStory(toPosition + visibleElementCount(storyTarget) - 1);

				workItemService.rankStoryUnder(
						story, storyTarget, getStoryList(), successListener, errorListener);
			}
		} else {
			final Task currentTask = (Task) workItems.get(toPosition);

			final Task targetTask;
			if (fromPosition > toPosition) {
				/*
				 when we are moving up, targetTask should be null if target position is equals to
				 its story position + 1
				 */
				targetTask = (toPosition == workItems.indexOf(((Task) workItems.get(fromPosition))
						.getStory()) + 1) ? null : (Task) workItems.get(fromPosition);
			} else {
				targetTask = (Task) workItems.get(fromPosition);
			}

			final Response.Listener<Task> successListener =
					getSuccessListener(R.string.feedback_success_updated_task_rank);
			final Response.ErrorListener errorListener = getErrorListener(R.string.feedback_failed_update_tasks_rank);

			workItemService.rankTaskUnder(currentTask, targetTask, currentTask.getStory().getTasks(),
					successListener, errorListener);
		}
	}

	/**
	 * Retrieves the story to which the item at the given pos belongs.
	 *
	 * @param pos The position to analyze
	 * @return The story to which the given position belongs
	 */
	protected Story getRelevantStory(final int pos) {
		final WorkItem wi = workItems.get(pos);

		if (wi.getType() == WorkItemType.STORY) {
			return (Story) wi;
		}

		return ((Task) wi).getStory();
	}

	@NonNull
	protected Response.ErrorListener getErrorListener(@StringRes final int idMessage) {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(final VolleyError arg0) {
				Toast.makeText(context, idMessage, Toast.LENGTH_SHORT).show();
			}
		};
	}

	@NonNull
	protected <T extends WorkItem> Response.Listener<T> getSuccessListener(@StringRes final int idMessage) {
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



	/**
	 * Filter the list leaving only those that match the text sent.
	 * @param query the text used to filter
	 */
	public void filter(final String query) {
		queryText = query.toLowerCase(Locale.getDefault());
		workItems = new ArrayList<>(originalWorkItems);

		for (final WorkItem w : originalWorkItems) {
			final boolean isStory = w.getType() == WorkItemType.STORY;
			if (isStory && !matchesFilterQuery(w.getName())) {
				int i = 0;
				for (final Task t : ((Story) w).getTasks()) {
					if (matchesFilterQuery(t.getName())) {
						i++;
					} else {
						workItems.remove(t);
					}
				}
				if (i == 0) {
					workItems.remove(w);
				}
			} else if (!isStory && ((Task) w).getStory() == null && !matchesFilterQuery(w.getName())) { //For TWOS
				workItems.remove(w);

			}
		}
		notifyDataSetChanged();
	}

	@Override
	public String toString() {
		return "WorkItemAdapter{"
				+ "workItems=" + workItems
				+ ", originalWorkItems=" + originalWorkItems
				+ ", updateAdapterHelper=" + updateAdapterHelper
				+ ", queryText='" + queryText
				+ '}';
	}

	private boolean matchesFilterQuery(final String text) {
		return text.toLowerCase(Locale.getDefault()).contains(queryText);
	}

}
