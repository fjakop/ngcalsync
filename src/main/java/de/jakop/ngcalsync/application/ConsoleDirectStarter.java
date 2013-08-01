package de.jakop.ngcalsync.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;
import de.jakop.ngcalsync.oauth.UserInputReceiverFactory;
import de.jakop.ngcalsync.settings.Settings;

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
	public void startApplication(final Application application, final Settings settings) {
		log.debug(TechMessage.get().MSG_START_IN_CONSOLE_MODE());
		settings.setUserInputReceiver(UserInputReceiverFactory.createPromptReceiver());
		if (application.reloadSettings()) {
			return;
		}
		application.synchronize();
	}
}
