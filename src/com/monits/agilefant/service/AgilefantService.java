package com.monits.agilefant.service;

import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;

public interface AgilefantService {

	/**
	 * Sets the domain you are working
	 * @param domain
	 */
	void setDomain(String domain);

	/**
	 * Login in Agilefant with domain entered
	 * @param userName
	 * @param password
	 * @param listener callback if the request was successful
	 * @param error callback if the request failed
	 */
	void login(String userName, String password, Listener<String> listener, ErrorListener error);

	/**
	 * @return All backlogs in JSON format
	 * @throws RequestException
	 */
	List<Product> getAllBacklogs() throws RequestException;

	/**
	 * @return All backlogs which logged user is responsible in JSON format
	 * @throws RequestException
	 */
	List<Project> getMyBacklogs() throws RequestException;

	/**
	 * @param Iteration id
	 * @return Details of iteration in JSON format
	 * @throws RequestException
	 */
	Iteration getIteration(long id) throws RequestException;

	/**
	 * @return The host where it is working
	 */
	String getHost();

	/**
	 * Change spent Effort of task
	 * @param date Date
	 * @param minutesSpent Minutes to enter
	 * @param description Description or comment
	 * @param taskId Task id
	 * @param userId User id
	 * @throws RequestException
	 */
	void taskChangeSpentEffort(long date, long minutesSpent, String description, long taskId, long userId) throws RequestException;

	/**
	 * Change state of task
	 * @param state State to enter
	 * @param taskId Task id
	 * @return Updated task in JSON format
	 * @throws RequestException
	 */
	Task taskChangeState(StateKey state, long taskId) throws RequestException;

	/**
	 * Change effort Left of task
	 * @param effortLeft Hour to enter. This value is converted into minutes from the api side.
	 * @param taskId Task id
	 * @return Updated task in JSON format
	 * @throws RequestException
	 */
	Task changeEffortLeft(double effortLeft, long taskId) throws RequestException;

	/**
	 * Change original estimate of task
	 * @param origalEstimate Hour to enter
	 * @param taskId Task id
	 * @return Updated task in JSON format
	 * @throws RequestException
	 */
	Task changeOriginalEstimate(int origalEstimate, long taskId) throws RequestException;

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
	 * @return a {@link DailyWork} object, in JSON format.
	 * @throws RequestException
	 */
	DailyWork getDailyWork(Long id) throws RequestException;

	/**
	 * Changes the story state.
	 *
	 * @param state State to set
	 * @param storyId the story's id.
	 * @param backlogId the backlog's id.
	 * @param iterationId the iteration's id.
	 * @param tasksToDone whether if all tasks should be also set as done.
	 * @return a {@link Story} in JSON format.
	 * @throws RequestException
	 */
	Story changeStoryState(StateKey state, long storyId, long backlogId, long iterationId, boolean tasksToDone)
			throws RequestException;

	/**
	 * Retrieves the details of the project.
	 *
	 * @param projectId the id of the project.
	 * @return a {@link Project} in JSON format.
	 * @throws RequestException
	 */
	Project getProjectDetails(long projectId) throws RequestException;
}
