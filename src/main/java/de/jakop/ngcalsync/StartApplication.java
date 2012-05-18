package de.jakop.ngcalsync;

import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.filter.AppointmentTypeFilter;
import de.jakop.ngcalsync.filter.ICalendarEntryFilter;
import de.jakop.ngcalsync.google.GoogleCalendarDAO;
import de.jakop.ngcalsync.notes.NotesCalendarDAO;
import de.jakop.ngcalsync.notes.NotesClientOpenDatabaseStrategy;
import de.jakop.ngcalsync.obfuscator.DefaultCalendarEventObfuscator;
import de.jakop.ngcalsync.obfuscator.ICalendarEventObfuscator;
import de.jakop.ngcalsync.service.SyncService;
import de.jakop.ngcalsync.settings.Settings;

/**
 * Starts the application which synchronizes the Lotus Notes calendar events to
 * a configured Google calendar.
 * 
 * @author fjakop
 *
 */
public class StartApplication {

	private static final Log log = LogFactory.getLog(StartApplication.class);

	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static void main(final String[] args) throws IOException, ConfigurationException {

		log.info(Constants.MSG_SYNC_STARTED);

		final Settings settings = new Settings();
		settings.load();

		// Execute synchronization
		final SyncService ss = new SyncService();

		final ICalendarEntryFilter typeFilter = new AppointmentTypeFilter(settings.getSyncAppointmentTypes());
		final ICalendarEventObfuscator typeObfuscator = new DefaultCalendarEventObfuscator(settings.getPrivacySettings());

		final ICalendarEntryFilter[] filters = new ICalendarEntryFilter[] { typeFilter };
		final ICalendarEventObfuscator[] obfuscators = new ICalendarEventObfuscator[] { typeObfuscator };
		ss.executeSync(new NotesCalendarDAO(new NotesClientOpenDatabaseStrategy(), settings.getDominoServer(), settings.getNotesCalendarDbFilePath(), settings.getSyncStartDate(),
				settings.getSyncEndDate()), new GoogleCalendarDAO(settings), filters, obfuscators, settings);

		// Update Last Sync Execution Date & Time
		settings.setSyncLastDateTime(Calendar.getInstance());
		settings.save();

		log.info(Constants.MSG_SYNC_ENDED);

	}

}
