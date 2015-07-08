package com.monits.agilefant.exception;

/**
 * Exception call when problem with the connection to the server
 *
 */
public class RequestException extends Exception {

	private static final long serialVersionUID = 793954953359280270L;

	/**
	 * Default constructor
	 */
	public RequestException() {
		super();
	}

	/**
	 * Constructor
	 * @param detailMessage A message explaining the error
	 * @param throwable A backtrace for the the error
	 */
	public RequestException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}

	/**
	 * Constructor
	 * @param detailMessage A message explaining the error
	 */
	public RequestException(final String detailMessage) {
		super(detailMessage);
	}

	/**
	 * Constructor
	 * @param throwable A backtrace for the the error
	 */
	public RequestException(final Throwable throwable) {
		super(throwable);
	}

}
