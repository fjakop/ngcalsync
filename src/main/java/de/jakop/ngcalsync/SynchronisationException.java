package de.jakop.ngcalsync;

/**
 * Exception für Fehler bei Synchronisierung
 * @author jakop
 */
public class SynchronisationException extends RuntimeException {

	/**
	 * @param cause
	 */
	public SynchronisationException(Exception cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SynchronisationException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 */
	public SynchronisationException(String message) {
		super(message);
	}

}
