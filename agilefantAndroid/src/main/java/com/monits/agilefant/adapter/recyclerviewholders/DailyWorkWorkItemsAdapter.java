package com.monits.agilefant.adapter.recyclerviewholders;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.WorkItemAdapter;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.model.WorkItemType;
import com.monits.agilefant.service.DailyWorkService;
import com.monits.agilefant.service.UserService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by rpereyra on 10/9/15.
 */
public class DailyWorkWorkItemsAdapter extends WorkItemAdapter {

	@Inject
	/* default */ UserService userService;

	@Inject
	/* default */ DailyWorkService dailyWorkService;

	/**
	 * Constructor
	 *
	 * @param context   The context
	 * @param workItems The items
	 */
	public DailyWorkWorkItemsAdapter(final FragmentActivity context, final List<WorkItem> workItems) {
		super(context);
		setWorkItems(workItems);
		AgilefantApplication.getObjectGraph().inject(this);
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

	@Override
	public void onChangePosition(final int fromPosition, final int toPosition) {

		// Use the one at the top to know what we moved regardless of list mangling
		final WorkItem referenceElement = workItems.get(toPosition < fromPosition ? toPosition : fromPosition);

		if (referenceElement.getType() == WorkItemType.TASK) {
			super.onChangePosition(fromPosition, toPosition);
		} else {

			final Story story = getRelevantStory(toPosition);

			/**
			 * Api doesn't receive the target story. Receives the
			 * story over the target story
			 */
			final Story storyOver = getStoryOver(toPosition);


			final Response.Listener<Story> successListener =
					getSuccessListener(R.string.feedback_success_update_story_rank);
			final Response.ErrorListener errorListener = getErrorListener(R.string.feedback_failed_update_story_rank);


			dailyWorkService.rankMyStoryUnder(story, storyOver, userService.getLoggedUser().getId(), successListener,
					errorListener);
		}
	}

	private Story getStoryOver(final int toPosition) {
		final Story targetStory = getRelevantStory(toPosition);
		final int targetStoryPosition = workItems.indexOf(targetStory);

		return targetStoryPosition == 0 ? null : getRelevantStory(targetStoryPosition - 1);
	}

	public List<WorkItem> getWorkItems() {
		return Collections.unmodifiableList(workItems);
	}
}
