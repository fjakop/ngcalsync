package de.jakop.ngcalsync;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

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

	@Override
	public void startApplication(final Application application) {
		log.debug("Starting application in tray mode.");
		moveToTray(application);
	}

	void moveToTray(final Application application) {

		final Log4JSwingAppender appender = new Log4JSwingAppender();
		appender.setLayout(new PatternLayout("%5p - %m%n"));
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
		rootLogger.setLevel(Level.INFO);

		// FIXME i18n
		final JFrame frame = new JFrame("ngcalsync log");
		frame.getContentPane().add(appender.getLogPanel());
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.pack();
		frame.setSize(500, 400);

		final SystemTray tray = SystemTray.getSystemTray();

		final Image image = chooseIcon(tray);
		final TrayIcon trayIcon = new TrayIcon(image, Constants.APPLICATION_NAME);
		final PopupMenu popup = new PopupMenu();
		trayIcon.setImageAutoSize(true);

		// FIXME i18n
		// Create a pop-up menu components
		final MenuItem syncItem = new MenuItem("Synchronize");
		final MenuItem logItem = new MenuItem("Show log");
		// TODO About dialog
		//		final MenuItem aboutItem = new MenuItem("About");
		final MenuItem exitItem = new MenuItem("Exit");

		//Add components to pop-up menu
		popup.add(syncItem);
		popup.add(logItem);
		popup.addSeparator();
		//		popup.add(aboutItem);
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (final AWTException e) {
			log.error("TrayIcon could not be added.", e);
		}

		final ActionListener syncActionListener = new ActionListener() {


			@Override
			public void actionPerformed(final ActionEvent e) {
				final ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.submit(new SynchronizeCallable(application, tray));
			}
		};

		syncItem.addActionListener(syncActionListener);
		trayIcon.addActionListener(syncActionListener);

		logItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				log.debug("Showing log viewer.");
				frame.setVisible(true);
			}
		});

		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				log.debug("Exiting the application.");
				frame.dispose();
				System.exit(0);
			}
		});
	}

	private Image chooseIcon(final SystemTray tray) {
		// must be descending!
		final int[] availableSizes = new int[] { 76, 48, 32, 24, 20, 16 };
		final int size = (int) tray.getTrayIconSize().getWidth();
		int leastDifference = 100;
		for (final int availableSize : availableSizes) {
			final int difference = availableSize - size;
			if (difference < leastDifference && difference >= 0) {
				leastDifference = difference;
			}
		}

		final String chosenSize = String.valueOf(size + leastDifference);

		final URL imageURL = getClass().getResource(String.format("/images/icon%sx%s.png", chosenSize, chosenSize));
		final Image image = new ImageIcon(imageURL).getImage();
		return image;
	}

	private class SynchronizeCallable implements Callable<Void> {

		private final Application application;
		private final SystemTray tray;

		public SynchronizeCallable(final Application application, final SystemTray tray) {
			this.application = application;
			this.tray = tray;
		}

		@Override
		public Void call() throws Exception {
			try {
				if (application.reloadSettings()) {
					// TODO i18n
					JOptionPane.showMessageDialog(null, String.format("The configuration was upgraded, please check and restart synchronisation."));
					return null;
				}
				final TrayIconRunnable trayIconRunnable = new TrayIconRunnable(tray);
				final Thread trayIconThread = new Thread(trayIconRunnable);
				trayIconThread.start();
				application.synchronize();
				trayIconRunnable.finish();
				trayIconThread.join();
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

	private class TrayIconRunnable implements Runnable {

		private boolean finish = false;
		private final SystemTray tray;

		/**
		 * 
		 * @param tray
		 */
		public TrayIconRunnable(final SystemTray tray) {
			this.tray = tray;
		}

		@Override
		public void run() {
			final TrayIcon[] trayIcons = tray.getTrayIcons();
			while (!finish) {
				try {

					for (final TrayIcon trayIcon : trayIcons) {
						tray.remove(trayIcon);
					}
					Thread.sleep(500);

					for (final TrayIcon trayIcon : trayIcons) {
						tray.add(trayIcon);
					}
					Thread.sleep(500);
				} catch (final Exception e) {
					// does not really matter
				}
			}
		}

		public void finish() {
			finish = true;
		}
	}
}
