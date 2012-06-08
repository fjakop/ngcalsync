package de.jakop.ngcalsync;

import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

		new StartApplication(new DefaultFileAccessor(), exitStrategy).synchronize();

	}

	/**
	 * 
	 * @param fileAccessor
	 * @param exitStrategy
	 */
	public StartApplication(final IFileAccessor fileAccessor, final IExitStrategy exitStrategy) {
		this.exitStrategy = exitStrategy;
		this.fileAccessor = fileAccessor;
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
}
