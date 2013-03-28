package com.monits.agilefant.exception;

/**
 * Exception call when problem with the connection to the server
 *
 */
public class RequestException extends Exception {

	private static final long serialVersionUID = 793954953359280270L;

	public RequestException() {
		super();
	}

	public RequestException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public RequestException(String detailMessage) {
		super(detailMessage);
	}

	public RequestException(Throwable throwable) {
		super(throwable);
	}

}
