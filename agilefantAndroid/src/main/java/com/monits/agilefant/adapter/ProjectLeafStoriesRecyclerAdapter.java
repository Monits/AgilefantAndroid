package com.monits.agilefant.adapter;

import android.content.Context;
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
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.WorkItemViewHolderUpdateTracker;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.recycler.DragAndDropListener;
import com.monits.agilefant.service.WorkItemService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by edipasquale on 09/10/15.
 */
public class ProjectLeafStoriesRecyclerAdapter extends RecyclerView.Adapter<WorkItemViewHolder<Story>>
		implements DragAndDropListener, WorkItemViewHolderUpdateTracker {

	private final FragmentActivity fragmentActivity;
	private final LayoutInflater inflater;

	// All stories
	private List<Story> originalStories;
	// Stories to show
	private List<Story> stories;

	private final Project project;
	private final UpdateAdapterHelper updateAdapterHelper;

	@Inject
	/* default */ WorkItemService workItemService;

	/**
	 * @param context The context
	 * @param project The project
	 */
	public ProjectLeafStoriesRecyclerAdapter(final FragmentActivity context, final Project project) {
		this.fragmentActivity = context;
		this.project = project;
		this.inflater = (LayoutInflater) fragmentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		updateAdapterHelper = new UpdateAdapterHelper(this);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public WorkItemViewHolder<Story> onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View view = inflater.inflate(R.layout.item_project_leaf_story, parent, false);
		return new StoryItemViewHolder(view, fragmentActivity, this);
	}

	@Override
	public void onBindViewHolder(final WorkItemViewHolder<Story> holder, final int position) {
		holder.onBindView(stories.get(position));
	}

	@Override
	public int getItemCount() {
		return (stories == null) ? 0 : stories.size();
	}

	/**
	 * @param stories List of stories
	 */
	public void setStories(final List<Story> stories) {
		this.stories = stories;
		originalStories = stories;
		notifyDataSetChanged();
	}

	/**
	 * @param position item position
	 * @return Story from list at received position
	 */
	private Story getStory(final int position) {
		return stories.get(position);
	}

	@Override
	public void onMove(final int fromPosition, final int toPosition) {
		Collections.swap(stories, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
	}

	@Override
	public void onChangePosition(final int fromPosition, final int toPosition) {
		final Response.Listener<Story> successListener = new Response.Listener<Story>() {

			@Override
			public void onResponse(final Story arg0) {
				Toast.makeText(fragmentActivity,
						R.string.feedback_success_update_story_rank, Toast.LENGTH_SHORT).show();
			}
		};

		final Response.ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError arg0) {
				Toast.makeText(fragmentActivity,
						R.string.feedback_failed_update_story_rank, Toast.LENGTH_SHORT).show();
			}
		};

		if (fromPosition < toPosition) {
			workItemService.rankStoryOver(
					getStory(fromPosition), getStory(toPosition), project.getId(),
					stories, successListener, errorListener);
		} else {
			workItemService.rankStoryUnder(
					getStory(fromPosition), getStory(toPosition), project.getId(),
					stories, successListener, errorListener);
		}
	}

	/**
	 * This method filters the result by a received string
	 * @param input The string used for filtering
	 */
	public void filter(final String input) {
		final String lowerInput = input.toLowerCase(Locale.getDefault());

		// Result list
		final List<Story> result = new ArrayList<>();

		for (final Story story : originalStories) {
			if (story.getName().toLowerCase(Locale.getDefault()).contains(lowerInput)) {
				result.add(story);
			}
		}

		stories = result;
		notifyDataSetChanged();
	}

	@Override
	public WorkItem getItem(final int position) {
		return stories.get(position);
	}

	@Override
	public String toString() {
		return "ProjectLeafStoriesRecyclerAdapter{"
				+ "taskList=" + stories
				+ "project=" + project
				+ '}';
	}

	@Override
	public void onUpdate(final WorkItem updatedStory) {
		updateAdapterHelper.updateItem(stories, updatedStory);
	}
}
