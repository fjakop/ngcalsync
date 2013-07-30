package de.jakop.ngcalsync.application;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.jakop.ngcalsync.Constants;
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
	private Future<Void> synchronizing;

	@Override
	public void startApplication(final Application application, final Settings settings) {
		log.debug(TechMessage.get().MSG_START_IN_TRAY_MODE());
		settings.setUserInputReceiver(new GuiReceiver());
		moveToTray(application);
	}

	private void moveToTray(final Application application) {

		final JFrame logWindow = new JFrame(UserMessage.get().TITLE_SYNC_LOG_WINDOW());

		final Log4JSwingAppender appender = new Log4JSwingAppender();
		appender.setLayout(new PatternLayout("%5p - %m%n"));
		appender.addObserver(new LogLevelObserver(Level.INFO, logWindow));
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
		rootLogger.setLevel(Level.INFO);

		logWindow.getContentPane().add(appender.getLogPanel());
		logWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		logWindow.pack();
		logWindow.setSize(500, 400);

		final JFrame aboutWindow = new JFrame(UserMessage.get().TITLE_ABOUT_WINDOW());
		final JEditorPane textarea = new JEditorPane("text/html", getApplicationInformation());
		textarea.setEditable(false);
		aboutWindow.getContentPane().add(new JScrollPane(textarea));
		aboutWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		aboutWindow.pack();
		aboutWindow.setSize(500, 400);

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
		final MenuItem aboutItem = new MenuItem(UserMessage.get().MENU_ITEM_ABOUT());
		final MenuItem exitItem = new MenuItem(UserMessage.get().MENU_ITEM_EXIT());

		//Add components to pop-up menu
		popup.add(syncItem);
		popup.add(logItem);
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(exitItem);
		icon.setPopupMenu(popup);

		try {
			tray.add(icon);
		} catch (final AWTException e) {
			log.error(TechMessage.get().MSG_TRAY_ICON_NOT_LOADABLE(), e);
		}

		final ActionListener syncActionListener = new ActionListener() {


			@Override
			public void actionPerformed(final ActionEvent e) {
				if (synchronizing != null && !synchronizing.isDone()) {
					log.warn(UserMessage.get().MSG_SYNC_IN_PROGRESS());
					return;
				}

				final ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(new SynchronizeCallable(application, logWindow));
			}
		};

		syncItem.addActionListener(syncActionListener);
		icon.addActionListener(syncActionListener);

		logItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				logWindow.setVisible(true);
			}
		});

		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				aboutWindow.setVisible(true);
			}
		});

		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				logWindow.dispose();
				aboutWindow.dispose();
				System.exit(0);
			}
		});
	}

	private String getApplicationInformation() {
		final StringBuilder builder = new StringBuilder();
		builder.append("<b>").append(Constants.APPLICATION_NAME).append("</b>").append("<p>");
		builder.append(UserMessage.get().APPLICATION_DESCRIPTION()).append("<p>");
		builder.append("Version ").append(getClass().getPackage().getImplementationVersion()).append("<p>");
		builder.append("").append("<br/>");
		String license;
		try {
			license = IOUtils.toString(getClass().getResourceAsStream("/LICENSE"));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		builder.append(license.replaceAll("\n", "<br/>")).append("<br/>");

		return builder.toString();
	}

	private class SynchronizeCallable implements Callable<Void> {

		private final Application application;
		private final JFrame logwindow;

		public SynchronizeCallable(final Application application, final JFrame logwindow) {
			this.application = application;
			this.logwindow = logwindow;
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

	/**
	 * Makes a {@link Component} visible, when a logging event with a {@link Level} greater
	 * or equal a given {@link Level} is observed. 
	 * 
	 * @author fjakop
	 */
	private class LogLevelObserver implements Observer {

		private final Level popupThreshold;
		private final Component componentToShow;

		/**
		 * 
		 * @param popupThreshold
		 * @param componentToShow
		 */
		public LogLevelObserver(final Level popupThreshold, final Component componentToShow) {
			this.popupThreshold = popupThreshold;
			this.componentToShow = componentToShow;
		}

		@Override
		public void update(final Observable o, final Object arg) {
			final Level level = (Level) arg;
			if (level.isGreaterOrEqual(popupThreshold)) {
				componentToShow.setVisible(true);
			}
		}
	}


}
