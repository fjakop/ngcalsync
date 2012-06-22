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
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.jakop.ngcalsync.filter.EventTypeFilter;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;
import de.jakop.ngcalsync.google.GoogleCalendarDAO;
import de.jakop.ngcalsync.notes.NotesCalendarDAO;
import de.jakop.ngcalsync.notes.NotesClientOpenDatabaseStrategy;
import de.jakop.ngcalsync.notes.NotesHelper;
import de.jakop.ngcalsync.obfuscator.DefaultCalendarEventObfuscator;
import de.jakop.ngcalsync.obfuscator.ICalendarEventObfuscator;
import de.jakop.ngcalsync.service.SyncService;
import de.jakop.ngcalsync.settings.Settings;
import de.jakop.ngcalsync.util.file.DefaultFileAccessor;
import de.jakop.ngcalsync.util.file.IFileAccessor;
import de.jakop.ngcalsync.util.logging.Log4JSwingAppender;

/**
 * Starts the application which synchronizes the Lotus Notes calendar events to
 * a configured Google calendar.
 * 
 * @author fjakop
 *
 */
public class StartApplication {

	private static final Log log = LogFactory.getLog(StartApplication.class);

	private final IExitStrategy exitStrategy;
	private final IFileAccessor fileAccessor;

	private Settings settings;

	private final JFrame frame;

	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static void main(final String[] args) throws IOException, ConfigurationException {


		final IExitStrategy exitStrategy = new IExitStrategy() {

			@Override
			public void exit(final int code) {
				System.exit(code);
			}
		};

		new StartApplication(new DefaultFileAccessor(), exitStrategy);

	}

	/**
	 * 
	 * @param fileAccessor
	 * @param exitStrategy
	 */
	public StartApplication(final IFileAccessor fileAccessor, final IExitStrategy exitStrategy) {
		this.exitStrategy = exitStrategy;
		this.fileAccessor = fileAccessor;

		final Log4JSwingAppender appender = new Log4JSwingAppender();
		appender.setLayout(new PatternLayout("%5p - %m%n"));
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
		rootLogger.setLevel(Level.INFO);

		frame = new JFrame("ngcalsync log");
		frame.getContentPane().add(appender.getLogPanel());
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.pack();
		frame.setSize(500, 400);

		moveToTray();
	}

	/**
	 * 
	 * @throws ConfigurationException
	 * @throws IOException 
	 */
	public void synchronize() throws ConfigurationException, IOException {

		log.info(Constants.MSG_SYNC_STARTED);

		reloadSettings();


		final ICalendarEventFilter typeFilter = new EventTypeFilter(settings.getSyncAppointmentTypes());
		final ICalendarEventObfuscator typeObfuscator = new DefaultCalendarEventObfuscator(settings.getPrivacySettings());

		final ICalendarEventFilter[] filters = new ICalendarEventFilter[] { typeFilter };
		final ICalendarEventObfuscator[] obfuscators = new ICalendarEventObfuscator[] { typeObfuscator };

		// Execute synchronization
		final SyncService ss = new SyncService();
		ss.executeSync(new NotesCalendarDAO(new NotesClientOpenDatabaseStrategy(), settings.getDominoServer(), settings.getNotesCalendarDbFilePath(), settings.getSyncStartDate(),
				settings.getSyncEndDate()), new GoogleCalendarDAO(settings), filters, obfuscators, settings);

		// Update Last Sync Execution Date & Time
		settings.setSyncLastDateTime(Calendar.getInstance());
		settings.saveLastSyncDateTime();

		log.info(Constants.MSG_SYNC_ENDED);

	}

	private void reloadSettings() throws ConfigurationException, IOException {
		settings = new Settings(fileAccessor, exitStrategy, LogFactory.getLog(Settings.class), new NotesHelper());
		settings.load();
	}

	private void moveToTray() {
		//Check the SystemTray is supported
		if (!SystemTray.isSupported()) {
			// TODO i18n
			log.info("SystemTray is not supported");
			return;
		}

		final SystemTray tray = SystemTray.getSystemTray();

		final Image image = chooseIcon(tray);
		final TrayIcon trayIcon = new TrayIcon(image, Constants.APPLICATION_NAME);
		final PopupMenu popup = new PopupMenu();
		trayIcon.setImageAutoSize(true);

		// Create a pop-up menu components
		final MenuItem syncItem = new MenuItem("Synchronize");
		final MenuItem logItem = new MenuItem("Show log");
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
				try {
					synchronize();
				} catch (final Exception ex) {
					final String home = System.getenv("user.home");
					final File logfile = new File(home, "ngcalsync.log");
					JOptionPane.showMessageDialog(null, String.format("Oops, sync failed. See logfile %s for details.", logfile.getAbsolutePath()));
				}
			}
		};

		syncItem.addActionListener(syncActionListener);
		trayIcon.addActionListener(syncActionListener);

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
				exitStrategy.exit(0);
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
}
