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

		final ICalendarEventFilter typeFilter = new EventTypeFilter(settings.getSyncAppointmentTypes());
		final ICalendarEventObfuscator typeObfuscator = new DefaultCalendarEventObfuscator(settings.getPrivacySettings());

		final ICalendarEventFilter[] filters = new ICalendarEventFilter[] { typeFilter };
		final ICalendarEventObfuscator[] obfuscators = new ICalendarEventObfuscator[] { typeObfuscator };
		ss.executeSync(new NotesCalendarDAO(new NotesClientOpenDatabaseStrategy(), settings.getDominoServer(), settings.getNotesCalendarDbFilePath(), settings.getSyncStartDate(),
				settings.getSyncEndDate()), new GoogleCalendarDAO(settings), filters, obfuscators, settings);

		// Update Last Sync Execution Date & Time
		settings.setLastSyncDateTime(Calendar.getInstance());
		settings.saveLastSyncDateTime();

		log.info(Constants.MSG_SYNC_ENDED);

	}

	//	public final static void setJavaLibraryPath(final String path) throws NoSuchFieldException, IllegalAccessException {
	//
	//		final String newPath = path + File.pathSeparator + System.getProperty("java.library.path");
	//		System.setProperty("java.library.path", newPath);
	//
	//		final Field field = java.lang.ClassLoader.class.getDeclaredField("sys_paths");
	//		field.setAccessible(true);
	//		if (field != null) {
	//			field.set(java.lang.System.class.getClassLoader(), null);
	//		}
	//	}
	//
	//	public static void addDir(final String s) throws IOException {
	//		try {
	//			// This enables the java.library.path to be modified at runtime
	//			// From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
	//			//
	//			final Field field = ClassLoader.class.getDeclaredField("sys_paths");
	//			field.setAccessible(true);
	//			final String[] paths = (String[]) field.get(null);
	//			for (final String path : paths) {
	//				if (s.equals(path)) {
	//					return;
	//				}
	//			}
	//			final String[] tmp = new String[paths.length + 1];
	//			System.arraycopy(paths, 0, tmp, 0, paths.length);
	//			tmp[paths.length] = s;
	//			field.set(null, tmp);
	//
	//			System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
	//		} catch (final IllegalAccessException e) {
	//			throw new IOException("Failed to get permissions to set library path");
	//		} catch (final NoSuchFieldException e) {
	//			throw new IOException("Failed to get field handle to set library path");
	//		}
	//	}
}
