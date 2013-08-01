package de.jakop.ngcalsync.application;

import java.util.concurrent.Callable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.util.StatefulTrayIcon;
import de.jakop.ngcalsync.util.StatefulTrayIcon.State;

class SynchronizeCallable implements Callable<Void> {

	private final Log log = LogFactory.getLog(getClass());

	private final Application application;
	private final JFrame logwindow;
	private final StatefulTrayIcon icon;

	/**
	 * 
	 * @param icon
	 * @param application
	 * @param logwindow
	 */
	public SynchronizeCallable(final StatefulTrayIcon icon, final Application application, final JFrame logwindow) {
		this.application = application;
		this.logwindow = logwindow;
		this.icon = icon;
	}

	@Override
	public Void call() throws Exception {
		try {
			if (application.reloadSettings()) {
				JOptionPane.showMessageDialog(null, UserMessage.get().MSG_CONFIGURATION_UPGRADED());
				return null;
			}
			icon.setState(State.BLINK);
			application.synchronize();
		} catch (final Exception ex) {
			log.error(ExceptionUtils.getStackTrace(ex));
			logwindow.setVisible(true);
		} finally {
			icon.setState(State.NORMAL);
		}
		return null;
	}
}