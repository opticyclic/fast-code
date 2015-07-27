package org.fastcode.exception;


	public class FastCodeRepositoryException extends Exception {
		/**
		 *
		 */
		public FastCodeRepositoryException () {
			super();
		}
		/**
		 *
		 * @param message
		 */
		public FastCodeRepositoryException (final String message) {
			super(message);
		}
		/**
		 *
		 * @param message
		 * @param throwable
		 */
		public FastCodeRepositoryException (final String message, final Throwable cause) {
			super(message, cause);
		}
		/**
		 *
		 * @param throwable
		 */
		public FastCodeRepositoryException (final Throwable cause) {
			super(cause);
		}
	}
