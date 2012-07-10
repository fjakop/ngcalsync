package de.jakop.ngcalsync;

/**
 * Implementators start the Application in different modes, e.g. tray mode or console mode.
 * 
 * @author fjakop
 *
 */
public interface IApplicationStarter {

	/**
	 * Starts the application.
	 * 
	 * @param application
	 */
	void startApplication(Application application);

}
