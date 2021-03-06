package com.monits.agilefant.dagger;

import com.monits.agilefant.activity.BaseActivity;
import com.monits.agilefant.activity.DailyWorkActivity;
import com.monits.agilefant.activity.HomeActivity;
import com.monits.agilefant.activity.SplashActivity;
import com.monits.agilefant.adapter.ProjectLeafStoriesRecyclerAdapter;
import com.monits.agilefant.adapter.TasksRecyclerAdapter;
import com.monits.agilefant.adapter.WorkItemAdapter;
import com.monits.agilefant.adapter.recyclerviewholders.DailyWorkWorkItemsAdapter;
import com.monits.agilefant.adapter.recyclerviewholders.IterationViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.StoryItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolder;
import com.monits.agilefant.fragment.backlog.AbstractCreateBacklogElementFragment;
import com.monits.agilefant.fragment.backlog.AllBacklogsFragment;
import com.monits.agilefant.fragment.backlog.MyBacklogsFragment;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.backlog.task.CreateDailyWorkTaskFragment;
import com.monits.agilefant.fragment.backlog.task.CreateTaskWithoutStory;
import com.monits.agilefant.fragment.dailywork.MyQueueWorkFragment;
import com.monits.agilefant.fragment.dailywork.MyTasksFragment;
import com.monits.agilefant.fragment.iteration.IterationBurndownFragment;
import com.monits.agilefant.fragment.iteration.SpentEffortFragment;
import com.monits.agilefant.fragment.iteration.StoriesFragment;
import com.monits.agilefant.fragment.iteration.TaskWithoutStoryFragment;
import com.monits.agilefant.fragment.project.ProjectDetailsFragment;
import com.monits.agilefant.fragment.project.ProjectLeafStoriesFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment;
import com.monits.agilefant.helper.ProjectHelper;
import com.monits.agilefant.listeners.SearchListener;
import com.monits.agilefant.listeners.SuggestionListener;


/**
 * Created by edipasquale on 27/08/15.
 */
public interface Graph {
	/**
	 * @param splashActivity Injects SplashActivity
	 */
	void inject(final SplashActivity splashActivity);

	/**
	 * @param homeActivity Injects HomeActivity
	 */
	void inject(final HomeActivity homeActivity);

	/**
	 * @param myBacklogsFragment Injects MyBackLogsFragment
	 */
	void inject(final MyBacklogsFragment myBacklogsFragment);

	/**
	 * @param allBacklogsFragment Injects AllBackLogsFragment
	 */
	void inject(final AllBacklogsFragment allBacklogsFragment);

	/**
	 * @param iterationViewHolder Injects IterationViewHolder
	 */
	void inject(final IterationViewHolder iterationViewHolder);

	/**
	 * @param dailyWorkActivity Injects DailyWorkActivity
	 */
	void inject(final DailyWorkActivity dailyWorkActivity);

	/**
	 * @param myTasksFragment Injects MyTaskFragment
	 */
	void inject(final MyTasksFragment myTasksFragment);

	/**
	 * @param iterationBurndownFragment Injects IterationBurndownFragment
	 */
	void inject(final IterationBurndownFragment iterationBurndownFragment);

	/**
	 * @param userChooserFragment Injects userChooserFragment
	 */
	void inject(final UserChooserFragment userChooserFragment);

	/**
	 * @param storiesFragment Injects StoriesFragment
	 */
	void inject(final StoriesFragment storiesFragment);

	/**
	 * @param spentEffortFragment SpentEffortFragment
	 */
	void inject(final SpentEffortFragment spentEffortFragment);

	/**
	 * @param projectDetailsFragment Injects ProjectDetailsFragment
	 */
	void inject(final ProjectDetailsFragment projectDetailsFragment);

	/**
	 * @param projectLeafStoriesFragment Injects ProjectLeafStoriesFragment
	 */
	void inject(final ProjectLeafStoriesFragment projectLeafStoriesFragment);

	/**
	 * @param abstractCreateBacklogElementFragment AbstractCreateBacklogElementFragment
	 */
	void inject(final AbstractCreateBacklogElementFragment abstractCreateBacklogElementFragment);

	/**
	 * @param createStoryFragment Injects CreateStoryFragment
	 */
	void inject(final CreateStoryFragment createStoryFragment);

	/**
	 * @param createDailyWorkTaskFragment Injects CreateDailyWorkTaskFragment
	 */
	void inject(final CreateDailyWorkTaskFragment createDailyWorkTaskFragment);

	/**
	 * @param createTaskWithoutStory Injects CreateTaskWithoutStory
	 */
	void inject(final CreateTaskWithoutStory createTaskWithoutStory);

	/**
	 * @param baseActivity Injects BaseActivity
	 */
	void inject(final BaseActivity baseActivity);

	/**
	 * @param taskItemViewHolder Injects TasksViews
	 */
	void inject(final TaskItemViewHolder taskItemViewHolder);

	/**
	 * @param storyItemViewHolder Injects story views
	 */
	void inject(final StoryItemViewHolder storyItemViewHolder);

	/**
	 * @param tasksRecyclerAdapter Injects TaskRecyclerAdapter
	 */
	void inject(final TasksRecyclerAdapter tasksRecyclerAdapter);

	/**
	 * @param projectLeafStoriesRecyclerAdapter Injects ProjectLeafStoriesRecyclerAdapter
	 */
	void inject(final ProjectLeafStoriesRecyclerAdapter projectLeafStoriesRecyclerAdapter);

	/**
	 * @param workItemAdapter Injects WorkItemAdapter
	 */
	void inject(final WorkItemAdapter workItemAdapter);

	/**
	 * @param projectHelper Injects ProjectHelper
	 */
	void inject(final ProjectHelper projectHelper);

	/**
	 * @param dailyWorkWorkItemsAdapter Injects DailyWorkWorkItemsAdapter
	 */
	void inject(final DailyWorkWorkItemsAdapter dailyWorkWorkItemsAdapter);

	/**
	 * @param taskWithoutStoryFragment Injects TaskWithoutStoryFragment
	 */
	void inject(final TaskWithoutStoryFragment taskWithoutStoryFragment);

	/**
	 * @param myQueueWorkFragment Injects MyQueueWorkFragment
	 */
	void inject(final MyQueueWorkFragment myQueueWorkFragment);

	/**
	 * @param searchListener Injects SearchListener
	 */
	void inject(final SearchListener searchListener);

	/**
	 * @param suggestionListener Injects SearchAdapter
	 */
	void inject(final SuggestionListener suggestionListener);
}
