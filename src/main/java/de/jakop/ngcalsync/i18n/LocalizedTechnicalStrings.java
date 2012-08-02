package de.jakop.ngcalsync.i18n;

import c10n.C10N;
import c10n.annotations.En;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.util.StatefulTrayIcon.State;

/**
 * Contains localisation of all strings that are not visible to the normal end user, such as
 * <ul>
 * <li>log entries of level DEBUG down to TRACE</li>
 * <li>runtime exception messages</li>
 * </ul>
 * 
 * @author fjakop
 *
 */
public interface LocalizedTechnicalStrings {

	/**
	 * static convenience getter for {@link LocalizedTechnicalStrings}
	 * 
	 * @author fjakop
	 *
	 */
	public final static class TechMessage {

		private TechMessage() {
			// not to be instantiated
		}

		/**
		 * @return the annotated {@link LocalizedTechnicalStrings} interface
		 */
		public static LocalizedTechnicalStrings get() {
			return C10N.get(LocalizedTechnicalStrings.class);
		}
	}

	/* filters */

	/**
	 * 
	 */
	@En("not accepted - calendar event is null.")
	String MSG_EVENT_NOT_ACCEPTED_BY_FILTER_IS_NULL();

	/**
	 * @param event {0}
	 */
	@En("not accepted - {0}")
	String MSG_EVENT_NOT_ACCEPTED_BY_FILTER(String event);

	/**
	 * @param event {0}
	 */
	@En("accepted - {0}")
	String MSG_EVENT_ACCEPTED_BY_FILTER(String event);

	/* Google */

	/**
	 * @param title {0}
	 */
	@En("executing insert: {0}")
	String MSG_EXECUTING_INSERT(String title);

	/**
	 * @param title {0}
	 */
	@En("executing update: {0}")
	String MSG_EXECUTING_UPDATE(String title);

	/**
	 * @param id {0}
	 */
	@En("executing delete: {0}")
	String MSG_EXECUTING_DELETE(String id);

	/* Notes */

	/**
	 * @param unid {0}
	 */
	@En("Converting document with UNID \"{0}\"")
	String MSG_CONVERTING_DOCUMENT_UNID(String unid);

	/**
	 * @param unid {0}
	 * @param result {1}
	 */
	@En("Conversion of document with UNID \"{0}\" results in {1}")
	String MSG_CONVERSION_RESULT(String unid, String result);

	/**
	 * @param unid {0}
	 */
	@En("Processing document with UNID \"{0}\"")
	String MSG_PROCESSING_DOCUMENT_UNID(String unid);

	/**
	 * @param unid {0}
	 */
	@En("Document with UNID \"{0}\" is a conflict document")
	String MSG_DOCUMENT_WITH_UNID_IS_CONFLICT_DOCUMENT(String unid);

	/**
	 * @param unid {0}
	 */
	@En("Document with UNID \"{0}\" has no appointment flag")
	String MSG_DOCUMENT_WITH_UNID_IS_NOT_AN_APPOINTMENT(String unid);

	/**
	 * @param unid {0}
	 */
	@En("Document with UNID \"{0}\" is already processed")
	String MSG_DOCUMENT_WITH_UNID_ALREADY_PROCESSED(String unid);

	/**
	 * 
	 */
	@En("Checking for Lotus Notes in system path...")
	String MSG_CHECKING_NOTES_IN_SYSTEM_PATH();

	/**
	 * @param library {0}
	 */
	@En("{0} successfully loaded")
	String MSG_SUCCESSFULLY_LOADED(String library);

	/**
	 * 
	 */
	@En("Lotus Notes is not in the library path.")
	String MSG_NOTES_NOT_IN_SYSTEM_PATH();

	/**
	 * 
	 */
	@En("Lotus Notes is not in the classpath.")
	String MSG_NOTES_NOT_IN_CLASSPATH();

	/**
	 * 
	 */
	@En("Checking for Lotus Notes in classpath...")
	String MSG_CHECKING_NOTES_IN_CLASSPATH();

	/**
	 * 
	 */
	@En("Trying to obtain Lotus Notes path...")
	String MSG_OBTAINING_NOTES_SYSTEM_PATH();

	/**
	 * @param name {0}
	 * @param version {1}
	 * @param arch {2}
	 */
	@En("OS info: {0}-{1}-{2}")
	String MSG_OS_INFO(String name, String version, String arch);

	/**
	 * @param path {0}
	 */
	@En("Path to Lotus Notes read from Windows registry was {0}.")
	String MSG_PATH_READ_FROM_WINDOWS_REGISTRY(String path);

	/**
	 * @param event {0}
	 */
	@En("Scheduling for removal: {0}.")
	String MSG_SCHEDULING_FOR_REMOVAL(String event);

	/**
	 * @param event {0}
	 */
	@En("Scheduling for addition: {0}.")
	String MSG_SCHEDULING_FOR_ADDITION(String event);

	/**
	 * @param event {0}
	 */
	@En("Scheduling for update: {0}.")
	String MSG_SCHEDULING_FOR_UPDATE(String event);

	/**
	 * @param event {0}
	 */
	@En("No update scheduled (not modified): {0}.")
	String MSG_NO_UPDATE_SCHEDULED(String event);

	/**
	 * @param matchCount {0}
	 * @param event {1}
	 */
	@En("Duplicate match ({0}) for {1}")
	String MSG_DUPLICATE_MATCH(int matchCount, CalendarEvent event);

	/**
	 * @param state {0}
	 */
	@En("State {0} is not supported.")
	String MSG_STATE_NOT_SUPPORTED(State state);

	/**
	 * 
	 */
	@En("Starting application in console mode.")
	String MSG_START_IN_CONSOLE_MODE();

	/**
	 * 
	 */
	@En("Starting application in tray mode.")
	String MSG_START_IN_TRAY_MODE();

	/**
	 * 
	 */
	@En("TrayIcon could not be loaded.")
	String MSG_TRAY_ICON_NOT_LOADABLE();

	/**
	 * 
	 */
	@En("TrayIcon could not be added to tray.")
	String MSG_TRAY_ICON_NOT_ADDABLE();

}
