package com.monits.agilefant.service;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Rankable;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;

/**
 * Manages metrics in Agilefant
 * @author Ivan Corbalan
 *
 */
public class MetricsServiceImpl implements MetricsService {

	@Inject
	private AgilefantService agilefantService;

	@Override
	public void taskChangeSpentEffort(final Date date, final long minutesSpent,
			final String description, final Task task, final long userId, final Listener<String> listener, final ErrorListener error) {

		final Task fallbackTask = task.clone();
		task.setEffortSpent(task.getEffortSpent() + minutesSpent);

		agilefantService.taskChangeSpentEffort(
				date.getTime(),
				minutesSpent,
				description,
				task.getId(),
				userId,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void taskChangeState(final StateKey state, final Task task, final Listener<Task> listener, final ErrorListener error) {
		final Task fallbackTask = task.clone();
		task.setState(state, true);

		agilefantService.updateTask(
				task,
				new Listener<Task>() {

					@Override
					public void onResponse(final Task response) {
						task.updateValues(response);

						listener.onResponse(response);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void changeEffortLeft(final double effortLeft, final Task task, final Listener<Task> listener, final ErrorListener error) {

		// Updating current task prior to sending request to the API
		final Task fallbackTask = task.clone();
		task.setEffortLeft(Double.valueOf(effortLeft * 60).longValue());

		agilefantService.updateTask(
				task,
				new Listener<Task>() {

					@Override
					public void onResponse(final Task response) {
						task.updateValues(response);

						listener.onResponse(response);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void changeStoryState(final StateKey state, final Story story, final boolean tasksToDone, final Listener<Story> listener, final ErrorListener error) {
		final Story fallbackStory = story.clone();
		final List<Task> currentTasks = story.getTasks();

		story.setState(state);
		if (tasksToDone) {
			for (final Task task : currentTasks) {
				task.setState(StateKey.DONE, true);
			}
		}

		agilefantService.updateStory(
				story,
				tasksToDone,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						story.setState(fallbackStory.getState());
						if (tasksToDone) {
							final List<Task> fallbackTasks = fallbackStory.getTasks();
							for (int i = 0; i < currentTasks.size(); i++) {
								currentTasks.get(i).setState(fallbackTasks.get(i).getState());
							}
						}

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void changeStoryResponsibles(final List<User> responsibles, final Story story,
			final Listener<Story> listener, final ErrorListener error) {

		final Story fallbackStory = story.clone();
		story.setResponsibles(responsibles);

		agilefantService.updateStory(
				story,
				null,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						story.updateValues(fallbackStory);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void changeTaskResponsibles(final List<User> responsibles, final Task task,
			final Listener<Task> listener, final ErrorListener error) {

		final Task fallbackTask = task.clone();
		task.setResponsibles(responsibles);

		agilefantService.updateTask(
				task,
				new Listener<Task>() {

					@Override
					public void onResponse(final Task response) {
						task.updateValues(response);

						listener.onResponse(response);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						task.updateValues(fallbackTask);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void moveStory(final Story story, final Iteration iteration,
			final Listener<Story> listener, final ErrorListener error) {

		final Story fallbackStory = story.clone();
		story.setIteration(iteration);

		final long backlogId;
		if (iteration != null) {
			backlogId = iteration.getId();
		} else {
			backlogId = story.getBacklog().getId();
		}

		agilefantService.moveStory(
				backlogId,
				story,
				new Listener<Story>() {

					@Override
					public void onResponse(final Story storyOk) {
						// The story from the response is incomplete, sending the one we have updated.
						listener.onResponse(story);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						story.setIteration(fallbackStory.getIteration());

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void rankTaskUnder(final Task task, final Task targetTask, final List<Task> allTasks,
			final Listener<Task> listener, final ErrorListener error) {

		final List<Task> fallbackTasksList = new LinkedList<Task>();
		copyAndSetRank(allTasks, fallbackTasksList);

		agilefantService.rankTaskUnder(
				task,
				targetTask,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allTasks, fallbackTasksList);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void rankStoryOver(final Story story, final Story targetStory, final List<Story> allStories,
			final Listener<Story> listener, final ErrorListener error) {

		final List<Story> fallbackStoryList = new LinkedList<Story>();
		copyAndSetRank(allStories, fallbackStoryList);

		agilefantService.rankStoryOver(
				story,
				targetStory,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allStories, fallbackStoryList);

						error.onErrorResponse(arg0);
					}
				});
	}

	@Override
	public void rankStoryUnder(final Story story, final Story targetStory, final List<Story> allStories,
			final Listener<Story> listener, final ErrorListener error) {

		final List<Story> fallbackStoryList = new LinkedList<Story>();
		copyAndSetRank(allStories, fallbackStoryList);

		agilefantService.rankStoryUnder(
				story,
				targetStory,
				listener,
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						rollbackRanks(allStories, fallbackStoryList);

						error.onErrorResponse(arg0);
					}
				});
	}

	/**
	 * This method clones the source items into the fallback list, and updates the ranks of the source list.
	 *
	 * @param source the original list.
	 * @param copy the list where cloned items will be added.
	 */
	public <T extends Rankable<T>> void copyAndSetRank(final List<T> source, final List<T> copy) {

		for (int i = 0; i < source.size(); i++) {
			final T itemAt = source.get(i);

			copy.add(itemAt.clone());

			// Rank is equal to the index in the list.
			itemAt.setRank(i);
		}
	}

	/**
	 * Updates the ranks of the items in the source list with the ones in the fallbacklist.
	 *
	 * @param source the list to be updated.
	 * @param fallbackList the list containing the values to be updated with.
	 */
	public <T extends Rankable<T>> void rollbackRanks(final List<T> source, final List<T> fallbackList) {

		for (int i = 0; i < source.size(); i++) {
			final T currentTaskAt = source.get(i);

			final int indexOfFallbackTask = fallbackList.indexOf(currentTaskAt);
			final T fallbackTask = fallbackList.get(indexOfFallbackTask);

			currentTaskAt.setRank(fallbackTask.getRank());
		}
	}
}