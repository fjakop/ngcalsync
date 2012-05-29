package de.jakop.ngcalsync;

/**
 * Exception für Fehler bei Synchronisierung
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
