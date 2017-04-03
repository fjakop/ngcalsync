/**
 * Copyright © 2012, Frank Jakop
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.jakop.ngcalsync.application;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.quartz.SchedulerException;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.oauth.UserInputReceiverFactory;
import de.jakop.ngcalsync.settings.Settings;
import de.jakop.ngcalsync.tray.StatefulTrayIcon;
import de.jakop.ngcalsync.util.logging.Log4JSwingAppender;

/**
 * Starts the application without immediate synchronisation and moves it to the system tray
 * where the user can produce a popup menu for synchronisation, exit, log etc.
 *
 * @author fjakop
 *
 */
public class TrayStarter implements IApplicationStarter {

	private static final String LOG4J_PATTERN = "%5p - %m%n"; //$NON-NLS-1$

	private final Log log = LogFactory.getLog(getClass());

	private StatefulTrayIcon icon;


	@Override
	public void startApplication(final Application application, final Settings settings) {
		log.debug(TechMessage.get().MSG_START_IN_TRAY_MODE());
		settings.setUserInputReceiver(UserInputReceiverFactory.createGuiReceiver());

		moveToTray(settings, application);
	}

	private void moveToTray(final Settings settings, final Application application) {


		final PopupMenu popup = new PopupMenu();

		// Create a pop-up menu components
		final MenuItem syncItem = new MenuItem(UserMessage.get().MENU_ITEM_SYNCHRONIZE());
		final CheckboxMenuItem schedulerItem = new CheckboxMenuItem(UserMessage.get().MENU_ITEM_SCHEDULER_ACTIVE());
		final MenuItem logItem = new MenuItem(UserMessage.get().MENU_ITEM_SHOW_LOG());
		final MenuItem aboutItem = new MenuItem(UserMessage.get().MENU_ITEM_ABOUT());
		final MenuItem exitItem = new MenuItem(UserMessage.get().MENU_ITEM_EXIT());

		// let the tray icon listen to sync events for state change
		application.addObserver(getTrayIcon());

		//Add components to pop-up menu
		popup.add(syncItem);
		popup.add(schedulerItem);
		popup.add(logItem);
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(exitItem);
		getTrayIcon().setPopupMenu(popup);

		application.reloadSettings();

		final JFrame logWindow = createLogWindow(Level.toLevel(settings.getPopupThresholdLevel(), Level.INFO));
		final JFrame aboutWindow = createAboutWindow();

		final ActionListener syncActionListener = createSyncActionListener(application.getScheduler());
		syncItem.addActionListener(syncActionListener);
		// sync also on double click
		getTrayIcon().addActionListener(syncActionListener);
		getTrayIcon().addMouseListener(createLogMouseListener(logWindow));

		schedulerItem.addItemListener(createSchedulerItemListener(settings, application));
		logItem.addActionListener(createLogActionListener(logWindow));
		aboutItem.addActionListener(createAboutActionListener(aboutWindow));
		exitItem.addActionListener(createExitActionListener(logWindow, aboutWindow));

		schedulerItem.setState(settings.isSchedulerStarted());
		toggleScheduler(settings.isSchedulerStarted(), settings, application);

	}

	private void toggleScheduler(final boolean started, final Settings settings, final Application application) {

		try {
			application.reloadSettings();
			application.getScheduler().schedule(settings.getSyncRecurrenceExpression());
			if (started) {
				application.getScheduler().start();
			} else {
				application.getScheduler().pause();
			}
			// in case of any error it is logged and the scheduler is not started
		} catch (final ParseException ex) {
			log.error(ex);
		} catch (final SchedulerException ex) {
			log.error(ex);
		}
	}

	private ItemListener createSchedulerItemListener(final Settings settings, final Application application) {

		return new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				try {
					final boolean started = e.getStateChange() == ItemEvent.SELECTED;
					toggleScheduler(started, settings, application);
					settings.setSchedulerStarted(started);
					settings.save();
				} catch (final ConfigurationException ex) {
					throw new RuntimeException(ex);
				}
			}
		};
	}

	private ActionListener createExitActionListener(final JFrame logWindow, final JFrame aboutWindow) {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				logWindow.dispose();
				aboutWindow.dispose();
				System.exit(0);
			}
		};
	}

	private ActionListener createAboutActionListener(final JFrame aboutWindow) {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				aboutWindow.setVisible(true);
			}
		};
	}

	private ActionListener createLogActionListener(final JFrame logWindow) {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				logWindow.setVisible(true);
			}
		};
	}

	private MouseAdapter createLogMouseListener(final JFrame logWindow) {
		return new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
					logWindow.setVisible(!logWindow.isVisible());
				}
			}

		};
	}

	private ActionListener createSyncActionListener(final SchedulerFacade scheduler) {
		final ActionListener syncActionListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					scheduler.triggerNow();
				} catch (final SchedulerException ex) {
					log.error(EMPTY, ex);
				}
			}
		};
		return syncActionListener;
	}

	private JFrame createAboutWindow() {
		final JFrame aboutWindow = new JFrame(UserMessage.get().TITLE_ABOUT_WINDOW());
		final JEditorPane textarea = new JEditorPane("text/html", getApplicationInformation()); //$NON-NLS-1$
		textarea.setEditable(false);
		aboutWindow.getContentPane().add(new JScrollPane(textarea));
		aboutWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		aboutWindow.pack();
		aboutWindow.setSize(500, 400);
		return aboutWindow;
	}

	private JFrame createLogWindow(final Level popupThreshold) {
		final JFrame logWindow = new JFrame(UserMessage.get().TITLE_SYNC_LOG_WINDOW());
		final Log4JSwingAppender appender = new Log4JSwingAppender();
		appender.setLayout(new PatternLayout(LOG4J_PATTERN));
		appender.addObserver(new LogLevelObserver(popupThreshold, logWindow));
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
		rootLogger.setLevel(Level.INFO);

		logWindow.getContentPane().add(appender.getLogPanel());
		logWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		logWindow.pack();
		logWindow.setSize(500, 400);

		return logWindow;
	}

	private StatefulTrayIcon getTrayIcon() {
		if (icon == null) {
			try {
				icon = new StatefulTrayIcon();
				SystemTray.getSystemTray().add(icon);
			} catch (final AWTException e) {
				log.error(TechMessage.get().MSG_TRAY_ICON_NOT_LOADABLE(), e);
			} catch (final IOException e) {
				log.error(TechMessage.get().MSG_TRAY_ICON_NOT_LOADABLE(), e);
			}
		}
		return icon;
	}

	private String getApplicationInformation() {
		final StringBuilder builder = new StringBuilder();
		builder.append("<b>").append(Constants.APPLICATION_NAME).append("</b>").append("<p>"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		builder.append(UserMessage.get().APPLICATION_DESCRIPTION()).append("<p>"); //$NON-NLS-1$
		builder.append(UserMessage.get().VERSION()).append(getClass().getPackage().getImplementationVersion()).append("<p>"); //$NON-NLS-1$
		builder.append("<br/>"); //$NON-NLS-1$
		String license;
		try {
			license = IOUtils.toString(getClass().getResourceAsStream("/LICENSE")); //$NON-NLS-1$
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		builder.append(license.replaceAll("\n", "<br/>")).append("<br/>"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		return builder.toString();
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
