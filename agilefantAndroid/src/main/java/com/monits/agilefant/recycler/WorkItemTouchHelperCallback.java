package com.monits.agilefant.recycler;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.model.WorkItemType;
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
		final WorkItem originalWorkItem = dragAndDropListener.getItem(viewHolder.getAdapterPosition());
		final WorkItem targetWorkItem = dragAndDropListener.getItem(target.getAdapterPosition());

		if (!canMoveOver(originalWorkItem, targetWorkItem)) {
			return false;
		}

		originalPosition = viewHolder.getAdapterPosition();
		newPosition = target.getAdapterPosition();
		dragAndDropListener.onMove(originalPosition, newPosition);
		return true;
	}

	/**
	 * Check if the given items satisfy the conditions to be dragged.
	 *
	 * @param originalWorkItem The work item that is being dragged by the user.
	 * @param targetWorkItem The work item over which the currently active item is being dragged.
	 * @return True if the items meet all the condition, otherwise false.
	 */
	protected boolean canMoveOver(final WorkItem originalWorkItem, final WorkItem targetWorkItem) {
		// Validate that the items that are moving are the same type.
		if (originalWorkItem.getType() != targetWorkItem.getType()) {
			return false;
		}

		// if both are type task, check if are from the same story.
		if (originalWorkItem.getType() == WorkItemType.TASK && targetWorkItem.getType() == WorkItemType.TASK) {

			final Story originParent = ((Task) originalWorkItem).getStory();
			final Story targetParent = ((Task) targetWorkItem).getStory();

			if (!(originParent == null && targetParent == null) // for tasks without story
					&& (originParent == null || targetParent == null
					|| !originParent.equals(targetParent))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void onSelectedChanged(final RecyclerView.ViewHolder viewHolder, final int actionState) {
		super.onSelectedChanged(viewHolder, actionState);

		if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && newPosition != originalPosition) {
			dragAndDropListener.onChangePosition(originalPosition, newPosition);
		} else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
			// Drag along the story's task's if expanded
			mapToSubitems(viewHolder, (RecyclerView) viewHolder.itemView.getParent(),
				new Function<RecyclerView.ViewHolder>() {
					@Override
					public void apply(final RecyclerView.ViewHolder viewHolder) {
						WorkItemTouchHelperCallback.super.onSelectedChanged(viewHolder, actionState);
					}
				});
		}
	}

	@Override
	public void onChildDrawOver(final Canvas c, final RecyclerView recyclerView,
								final RecyclerView.ViewHolder viewHolder, final float dX,
								final float dY, final int actionState, final boolean isCurrentlyActive) {
		super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

		// Drag along the story's task's if expanded
		if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
			mapToSubitems(viewHolder, recyclerView, new Function<RecyclerView.ViewHolder>() {
				@Override
				public void apply(final RecyclerView.ViewHolder viewHolder) {
					WorkItemTouchHelperCallback.super.onChildDrawOver(c, recyclerView, viewHolder,
							dX, dY, actionState, isCurrentlyActive);
				}
			});
		}
	}

	@Override
	public void onChildDraw(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
							final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

		// Drag along the story's task's if expanded
		if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
			mapToSubitems(viewHolder, recyclerView, new Function<RecyclerView.ViewHolder>() {
				@Override
				public void apply(final RecyclerView.ViewHolder viewHolder) {
					WorkItemTouchHelperCallback.super.onChildDraw(c, recyclerView, viewHolder,
							dX, dY, actionState, isCurrentlyActive);
				}
			});
		}
	}

	@Override
	public void clearView(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
		super.clearView(recyclerView, viewHolder);

		// Drag along the story's task's if expanded
		mapToSubitems(viewHolder, recyclerView, new Function<RecyclerView.ViewHolder>() {
			@Override
			public void apply(final RecyclerView.ViewHolder viewHolder) {
				WorkItemTouchHelperCallback.super.clearView(recyclerView, viewHolder);
			}
		});
	}

	@Override
	public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction) {
		// do nothing, in this case we are not interested Swipe
	}

	@Override
	public boolean isItemViewSwipeEnabled() {
		return false;
	}

	@Override
	public String toString() {
		return "WorkItemTouchHelperCallback{"
				+ "originalPosition=" + originalPosition
				+ ", newPosition=" + newPosition
				+ '}';
	}

	/**
	 * Applies the given function on all children of the element bound to the given viewholder.
	 *
	 * @param viewHolder The viewHolder to whose children the function must be applied.
	 * @param recyclerView the recycler view we are dealing with.
	 * @param func The function to be applied.
	 */
	private void mapToSubitems(@NonNull final RecyclerView.ViewHolder viewHolder,
							@NonNull final RecyclerView recyclerView,
							@NonNull final Function<RecyclerView.ViewHolder> func) {
		final int storyPos = viewHolder.getAdapterPosition();

		// The viewholder is not yet bound
		if (storyPos == RecyclerView.NO_POSITION) {
			return;
		}

		final WorkItem wi = dragAndDropListener.getItem(storyPos);

		// Only stories have subitems
		if (wi.getType() == WorkItemType.STORY) {
			final Story story = (Story) wi;

			if (story.isExpanded()) {
				for (int i = 1; i <= story.getTasks().size(); i++) {
					final RecyclerView.ViewHolder taskHolder =
							recyclerView.findViewHolderForAdapterPosition(storyPos + i);
					if (taskHolder == null) {
						break;
					}

					func.apply(taskHolder);
				}
			}
		}
	}

	/**
	 * An arbitrary function.
	 *
	 * @param <T> The type of arguments for the function.
	 */
	private interface Function<T> {
		void apply(@NonNull T t);
	}
}
