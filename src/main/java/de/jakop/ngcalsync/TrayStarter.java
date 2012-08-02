package de.jakop.ngcalsync;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.oauth.GuiReceiver;
import de.jakop.ngcalsync.settings.Settings;
import de.jakop.ngcalsync.util.StatefulTrayIcon;
import de.jakop.ngcalsync.util.StatefulTrayIcon.State;
import de.jakop.ngcalsync.util.logging.Log4JSwingAppender;

/**
 * Starts the application without immediate synchronisation and moves it to the system tray
 * where the user can produce a popup menu for synchronisation, exit, log etc.
 * 
 * @author fjakop
 *
 */
public class TrayStarter implements IApplicationStarter {

	private final Log log = LogFactory.getLog(getClass());
	private StatefulTrayIcon icon;

	@Override
	public void startApplication(final Application application, final Settings settings) {
		log.debug(TechMessage.get().MSG_START_IN_TRAY_MODE());
		settings.setVerificationCodeReceiver(new GuiReceiver());
		moveToTray(application);
	}

	void moveToTray(final Application application) {

		final Log4JSwingAppender appender = new Log4JSwingAppender();
		appender.setLayout(new PatternLayout("%5p - %m%n"));
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
		rootLogger.setLevel(Level.INFO);

		final JFrame frame = new JFrame(UserMessage.get().TITLE_SYNC_LOG_WINDOW());
		frame.getContentPane().add(appender.getLogPanel());
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.pack();
		frame.setSize(500, 400);

		final SystemTray tray = SystemTray.getSystemTray();

		try {
			icon = new StatefulTrayIcon();
			icon.setState(State.NORMAL);
		} catch (final IOException e) {
			log.error(TechMessage.get().MSG_TRAY_ICON_NOT_LOADABLE(), e);
		}
		final PopupMenu popup = new PopupMenu();

		// Create a pop-up menu components
		final MenuItem syncItem = new MenuItem(UserMessage.get().MENU_ITEM_SYNCHRONIZE());
		final MenuItem logItem = new MenuItem(UserMessage.get().MENU_ITEM_SHOW_LOG());
		// TODO About dialog
		//		final MenuItem aboutItem = new MenuItem("About");
		final MenuItem exitItem = new MenuItem(UserMessage.get().MENU_ITEM_EXIT());

		//Add components to pop-up menu
		popup.add(syncItem);
		popup.add(logItem);
		popup.addSeparator();
		//		popup.add(aboutItem);
		popup.add(exitItem);

		icon.setPopupMenu(popup);

		try {
			tray.add(icon);
		} catch (final AWTException e) {
			log.error(TechMessage.get().MSG_TRAY_ICON_NOT_ADDABLE(), e);
		}

		final ActionListener syncActionListener = new ActionListener() {


			@Override
			public void actionPerformed(final ActionEvent e) {
				final ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(new SynchronizeCallable(application));
			}
		};

		syncItem.addActionListener(syncActionListener);
		icon.addActionListener(syncActionListener);

		logItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				frame.setVisible(true);
			}
		});

		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
	}


	private class SynchronizeCallable implements Callable<Void> {

		private final Application application;

		public SynchronizeCallable(final Application application) {
			this.application = application;
		}

		@Override
		public Void call() throws Exception {
			try {
				if (application.reloadSettings()) {
					// TODO i18n
					JOptionPane.showMessageDialog(null, String.format("The configuration was upgraded, please check and restart synchronisation."));
					return null;
				}
				icon.setState(State.BLINK);
				application.synchronize();
				icon.setState(State.NORMAL);
			} catch (final Exception ex) {
				log.error(ExceptionUtils.getStackTrace(ex));
				final String home = System.getenv("user.home");
				final File logfile = new File(home, "ngcalsync.log");
				// TODO i18n
				JOptionPane.showMessageDialog(null, String.format("Oops, sync failed. See logfile %s for details.", logfile.getAbsolutePath()));
			}
			return null;
		}
	}


}
