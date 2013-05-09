package com.monits.agilefant.service;

import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.DailyWork;

public interface DailyWorkService {

	/**
	 * @return the daily work, an object carrying tasks and stories to be done.
	 * @throws RequestException
	 */
	DailyWork getDailyWork() throws RequestException;
}
