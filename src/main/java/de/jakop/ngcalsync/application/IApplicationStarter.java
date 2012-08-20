package de.jakop.ngcalsync.application;

import de.jakop.ngcalsync.settings.Settings;

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
	 * @param settings
	 */
	void startApplication(Application application, Settings settings);

}
