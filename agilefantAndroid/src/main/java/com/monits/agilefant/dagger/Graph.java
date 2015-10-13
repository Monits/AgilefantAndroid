package com.monits.agilefant.dagger;

import com.monits.agilefant.activity.BaseActivity;
import com.monits.agilefant.activity.DailyWorkActivity;
import com.monits.agilefant.activity.HomeActivity;
import com.monits.agilefant.activity.SplashActivity;
import com.monits.agilefant.adapter.ProjectLeafStoriesRecyclerAdapter;
import com.monits.agilefant.adapter.TasksRecyclerAdapter;
import com.monits.agilefant.adapter.recyclerviewholders.IterationViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.StoryItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.TaskItemViewHolder;
import com.monits.agilefant.fragment.backlog.AbstractCreateBacklogElementFragment;
import com.monits.agilefant.fragment.backlog.AllBacklogsFragment;
import com.monits.agilefant.fragment.backlog.MyBacklogsFragment;
import com.monits.agilefant.fragment.backlog.story.CreateLeafStoryFragment;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.backlog.task.CreateDailyWorkTaskFragment;
import com.monits.agilefant.fragment.backlog.task.CreateTaskWithoutStory;
import com.monits.agilefant.fragment.dailywork.MyTasksFragment;
import com.monits.agilefant.fragment.iteration.IterationBurndownFragment;
import com.monits.agilefant.fragment.iteration.SpentEffortFragment;
import com.monits.agilefant.fragment.iteration.StoriesFragment;
import com.monits.agilefant.fragment.project.ProjectDetailsFragment;
import com.monits.agilefant.fragment.project.ProjectFragment;
import com.monits.agilefant.fragment.project.ProjectLeafStoriesFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment;
import com.monits.agilefant.listeners.implementations.StoryAdapterViewActionListener;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;

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
	 * @param taskAdapterViewActionListener Injects TaskAdapterViewActionListener
	 */
	void inject(final TaskAdapterViewActionListener taskAdapterViewActionListener);

	/**
	 * @param storyAdapterViewActionListener Injects StoryAdapterViewActionListener
	 */
	void inject(final StoryAdapterViewActionListener storyAdapterViewActionListener);

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
	 * @param projectFragment Injects ProjectFragment
	 */
	void inject(final ProjectFragment projectFragment);

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
	 * @param createLeafStoryFragment Injects CreateLeafStoryFragment
	 */
	void inject(final CreateLeafStoryFragment createLeafStoryFragment);

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
}
