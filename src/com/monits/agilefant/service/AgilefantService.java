package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.StateKey;

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
	 * @return If the user is logged
	 * @throws RequestException
	 */
	boolean login(String userName, String password) throws RequestException;

	/**
	 * @return All backlogs in JSON format
	 * @throws RequestException
	 */
	String getAllBacklogs() throws RequestException;

	/**
	 * @param Iteration id
	 * @return Details of iteration in JSON format
	 * @throws RequestException
	 */
	String getIteration(long id) throws RequestException;

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
	String taskChangeState(StateKey state, long taskId) throws RequestException;

	/**
	 * Change effort Left of task
	 * @param effortLeft Hour to enter
	 * @param taskId Task id
	 * @return Updated task in JSON format
	 * @throws RequestException
	 */
	String changeEffortLeft(int effortLeft, long taskId) throws RequestException;

	/**
	 * Change original estimate of task
	 * @param origalEstimate Hour to enter
	 * @param taskId Task id
	 * @return Updated task in JSON format
	 * @throws RequestException
	 */
	String changeOriginalEstimate(int origalEstimate, long taskId) throws RequestException;

	/**
	 * Retrieves the user with the given id.
	 * 
	 * @param id <b>(Optional)</b> the user's id.
	 * @return user in JSON format. Logged user in case no id was given.
	 * @throws RequestException
	 */
	String retrieveUser(Long id) throws RequestException;
}
