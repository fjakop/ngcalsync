package de.jakop.ngcalsync.application;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.StartApplication;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.oauth.GuiReceiver;
import de.jakop.ngcalsync.settings.Settings;
import de.jakop.ngcalsync.util.StatefulTrayIcon;
import de.jakop.ngcalsync.util.StatefulTrayIcon.State;
import de.jakop.ngcalsync.util.logging.CompositeAppenderLog4J;

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

		final Display display = new Display();
		final Shell shell = new Shell(display);

		settings.setUserInputReceiver(new GuiReceiver(shell));
		moveToTray(shell, application);


		// Create and check the event loop
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

	private void moveToTray(final Shell parent, final Application application) {

		final Shell logShell = createShell(parent, UserMessage.get().TITLE_SYNC_LOG_WINDOW());

		final CompositeAppenderLog4J appender = new CompositeAppenderLog4J(logShell, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		appender.setLayout(new PatternLayout("%5p - %m%n"));
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
		rootLogger.setLevel(Level.INFO);


		final Shell aboutShell = createShell(parent, UserMessage.get().TITLE_ABOUT_WINDOW());

		final Browser aboutViewer = new Browser(aboutShell, SWT.NONE);
		aboutViewer.setText(getApplicationInformation());

		final Tray tray = logShell.getDisplay().getSystemTray();
		final TrayItem trayItem = new TrayItem(tray, SWT.NONE);

		try {
			icon = new StatefulTrayIcon(trayItem);
			icon.setState(State.NORMAL);
		} catch (final IOException e) {
			log.error(TechMessage.get().MSG_TRAY_ICON_NOT_LOADABLE(), e);
		}
		final Menu popup = new Menu(logShell, SWT.POP_UP);

		// Create a pop-up menu components
		final MenuItem syncItem = createMenuItem(popup, UserMessage.get().MENU_ITEM_SYNCHRONIZE());
		final MenuItem logItem = createMenuItem(popup, UserMessage.get().MENU_ITEM_SHOW_LOG());
		final MenuItem aboutItem = createMenuItem(popup, UserMessage.get().MENU_ITEM_ABOUT());
		final MenuItem exitItem = createMenuItem(popup, UserMessage.get().MENU_ITEM_EXIT());

		trayItem.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(final Event arg0) {
				popup.setVisible(true);
			}
		});

		final SelectionListener syncActionListener = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				final ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(new SynchronizeCallable(parent, application));
			}
		};

		syncItem.addSelectionListener(syncActionListener);


		logItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				logShell.open();
			}
		});

		aboutItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				aboutShell.open();
			}
		});

		exitItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				parent.dispose();
			}
		});
	}

	private Shell createShell(final Shell parent, final String title) {

		final Shell shell = new Shell(parent, SWT.RESIZE | SWT.DIALOG_TRIM);

		// do not dispose shell on closing the window(s)
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(final ShellEvent e) {
				shell.setVisible(false);
				e.doit = false;
			}
		});

		shell.setText(title);
		shell.setLayout(new FillLayout());
		shell.setSize(500, 400);
		return shell;
	}

	private MenuItem createMenuItem(final Menu parent, final String text) {
		final MenuItem item = new MenuItem(parent, SWT.PUSH);
		item.setText(text);
		return item;
	}

	private String getApplicationInformation() {
		final StringBuilder builder = new StringBuilder();
		builder.append("<b>").append(Constants.APPLICATION_NAME).append("</b>").append("<p>");
		builder.append(UserMessage.get().APPLICATION_DESCRIPTION()).append("<p>");
		builder.append("Version ").append(StartApplication.class.getPackage().getImplementationVersion()).append("<p>");
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

		private final Shell parent;
		private final Application application;

		public SynchronizeCallable(final Shell parent, final Application application) {
			this.parent = parent;
			this.application = application;
		}

		@Override
		public Void call() throws Exception {
			try {
				if (application.reloadSettings()) {
					parent.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.open(MessageDialog.INFORMATION, parent, UserMessage.get().TITLE_INPUT_REQUESTED(), UserMessage.get().MSG_CONFIGURATION_UPGRADED(), SWT.BORDER);
						}
					});
					return null;
				}
				icon.setState(State.BLINK);
				application.synchronize();
				icon.setState(State.NORMAL);
			} catch (final Exception ex) {
				log.error(ExceptionUtils.getStackTrace(ex));
				final String home = System.getenv("user.home");
				final File logfile = new File(home, "ngcalsync.log");
				parent.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						// TODO window title
						MessageDialog.open(MessageDialog.ERROR, parent, "", UserMessage.get().MSG_SYNC_FAILED(logfile.getAbsolutePath()), SWT.BORDER);
					}
				});
			}
			return null;
		}
	}




}
