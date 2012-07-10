package de.jakop.ngcalsync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Starts the application without GUI (a.k.a. console mode) and with immediate synchronisation.
 * The application exits after synchronisation.
 * 
 * @author fjakop
 *
 */
public class ConsoleDirectStarter implements IApplicationStarter {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public void startApplication(final Application application) {
		log.debug("Starting application in console mode.");
		if (application.reloadSettings()) {
			return;
		}
		application.synchronize();
	}
}
