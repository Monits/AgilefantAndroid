package com.monits.agilefant.service;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.FilterableIteration;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.model.backlog.BacklogElementParameters;

import java.util.List;

public interface AgilefantService {

	/**
	 * Sets the domain you are working
	 * @param domain The domain
	 */
	void setDomain(String domain);

	/**
	 * Login in Agilefant with domain entered
	 * @param userName The username
	 * @param password the password
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void login(String userName, String password, Listener<String> listener, ErrorListener error);

	/**
	 * Request the API to retrieve the iteration with the given ID
	 *
	 * @param id The iteration id
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getIteration(long id, Listener<Iteration> listener, ErrorListener error);

	/**
	 * @return The host where it is working
	 */
	String getHost();

	/**
	 * Change spent Effort of task
	 *
	 * @param date Date
	 * @param minutesSpent Minutes to enter
	 * @param description Description or comment
	 * @param taskId Task id
	 * @param userId User id
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void taskChangeSpentEffort(long date, long minutesSpent, String description, long taskId, long userId,
		Listener<String> listener, ErrorListener error);

	/**
	 * Retrieves the user with the given id.
	 *
	 * @param id <b>(Optional)</b> the user's id.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void retrieveUser(Long id, Listener<User> listener, ErrorListener error) ;

	/**
	 * Retrieves the daily work.
	 *
	 * @param id the logged user's id.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getDailyWork(Long id, Listener<DailyWork> listener, ErrorListener error);

	/**
	 * Changes the story state.
	 *
	 * @param story the story to update.
	 * @param tasksToDone whether if all tasks should be also set as done.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void updateStory(Story story, Boolean tasksToDone, Listener<Story> listener, ErrorListener error);

	/**
	 * Retrieves the details of the project.
	 *
	 * @param projectId the id of the project.
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getProjectDetails(long projectId, Listener<Project> listener, ErrorListener error);

	/**
	 * Retrieves the leaf stories of the given project.
	 *
	 * @param projectId The project id
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getProjectLeafStories(long projectId, Listener<List<Story>> listener, ErrorListener error);

	/**
	 * Retrieves the whole list of users in agilefant.
	 *
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void getFilterableUsers(Listener<List<UserChooser>> listener, ErrorListener error);

	/**
	 * Updates the projects data.
	 *
	 * @param project the project to update.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void updateProject(Project project, Listener<Project> listener, ErrorListener error);

	/**
	 * Updates the task's data.
	 *
	 * @param task the task to update.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void updateTask(Task task, Listener<Task> listener, ErrorListener error);

	/**
	 * Updates the Story Iteration.
	 *
	 * @param backlogId the backlog id
	 * @param story the Story to update.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void moveStory(long backlogId, Story story, Listener<Story> listener, ErrorListener error);


	/**
	 * Updates Task's rank under the given target task.
	 *
	 * @param task the task to be updated in rank.
	 * @param targetTask the task to be ranked under.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankTaskUnder(Task task, Task targetTask, Listener<Task> listener, ErrorListener error);

	/**
	 * Updates Story's rank lower than the given target story.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain above the given story in rank.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryUnder(Story story, Story targetStory, Listener<Story> listener, ErrorListener error);

	/**
	 * Updates Story's rank lower than the given target story.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain above the given story in rank.
	 * @param backlogId the backlogId.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryUnder(Story story, Story targetStory, Long backlogId, Listener<Story> listener, ErrorListener error);

	/**
	 * Updates Story's rank higher than the given target story.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain below the given story in rank.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryOver(Story story, Story targetStory, Listener<Story> listener, ErrorListener error);

	/**
	 * Updates Story's rank higher than the given target story.
	 *
	 * @param story the story to update.
	 * @param targetStory the story which will remain below the given story in rank.
	 * @param backlogId the backlogId.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void rankStoryOver(Story story, Story targetStory, Long backlogId, Listener<Story> listener, ErrorListener error);

	/**
	 * Create Story.
	 *
	 * @param parameters values to execute request.
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void createStory(final BacklogElementParameters parameters, Listener<Story> listener, ErrorListener error);

	/**
	 * Creates task without story
	 * @param parameters values of task
	 * @param listener  callback if the request was successful.
	 * @param errorListener  callback if the request failed.
	 */
	void createTask(BacklogElementParameters parameters, Listener<Task> listener, ErrorListener errorListener);

	/**
	 * Retrieves the list of the ongoing filterable iterations.
	 *
	 * @param listener callback if the request was successful.
	 * @param error callback if the request failed.
	 */
	void getCurrentFilterableIterations(Listener<List<FilterableIteration>> listener, ErrorListener error);

	/**
	 * Adds a request to the queue
	 * @param request The request to add
	 */
	void addRequest(Request<?> request);
}
