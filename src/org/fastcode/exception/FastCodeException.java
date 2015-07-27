package org.fastcode.exception;

public class FastCodeException extends Exception {
	/**
	 *
	 */
	public FastCodeException () {
		super();
	}
	/**
	 *
	 * @param message
	 */
	public FastCodeException (final String message) {
		super(message);
	}
	/**
	 *
	 * @param message
	 * @param throwable
	 */
	public FastCodeException (final String message, final Throwable cause) {
		super(message, cause);
	}
	/**
	 *
	 * @param throwable
	 */
	public FastCodeException (final Throwable cause) {
		super(cause);
	}

}
