package de.jakop.ngcalsync.exception;

/**
 * Exception indicating an error while synchronizing
 * 
 * @author fjakop
 */
public class SynchronisationException extends RuntimeException {

	/**
	 * @param cause
	 */
	public SynchronisationException(final Exception cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 */
	public SynchronisationException(final String message) {
		super(message);
	}

}
