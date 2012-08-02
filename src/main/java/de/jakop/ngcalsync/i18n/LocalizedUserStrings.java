package de.jakop.ngcalsync.i18n;

import c10n.C10N;
import c10n.annotations.De;
import c10n.annotations.En;

/**
 * Contains localisation of all strings that are visible to the normal end user. This is by default
 * <ul>
 * <li>log entries from level INFO up to FATAL</li>
 * <li>expected exception messages</li>
 * <li>dialog or prompt messages</li>
 * <li>event type names</li>
 * </ul>
 * 
 * @author fjakop
 *
 */
public interface LocalizedUserStrings {

	/**
	 * static convenience getter for {@link LocalizedUserStrings}
	 * 
	 * @author fjakop
	 *
	 */
	public final static class UserMessage {

		private UserMessage() {
			// not to be instantiated
		}

		/**
		 * @return the annotated {@link LocalizedUserStrings} interface
		 */
		public static LocalizedUserStrings get() {
			return C10N.get(LocalizedUserStrings.class);
		}
	}

	/**
	 * @param file {0}
	 * @param keys {1}
	 */
	@En("The configuration file {0} was upgraded, please check the keys {1} and restart.")
	String MSG_CONFIGURATION_UPGRADED(String file, String keys);

	/**
	 * @param calendarName {0}
	 */
	@De("Google Kalender \"{0}\" existiert nicht.")
	@En("Google calendar \"{0}\" does not exist.")
	String GOOGLE_CALENDAR_S_DOES_NOT_EXIST_CHECK_CONFIG(String calendarName);


	/**
	 * @param eventType {0}
	 */
	@En("Appointment type {0} not recognized")
	String MSG_EVENT_TYPE_S_NOT_RECOGNIZED_CHECK_CONFIG(int eventType);

	/**
	 * 
	 */
	@En("Environment information has changed, please restart the application.")
	String MSG_ENVIRONMENT_CHANGED();

	/**
	 * @param location {0} 
	 */
	@En("Please enter your client ID and secret from the Google APIs Console in {0}.")
	String MSG_ENTER_CLIENT_ID_AND_SECRET(String location);

	/**
	 * @param dateShift {0}
	 */
	@En("Unable to parse start date shift \"{0}\".")
	String MSG_UNABLE_TO_PARSE_DATE_SHIFT(String dateShift);

	/**
	 * 
	 */
	@En("Failed to open browser.")
	String MSG_FAILED_TO_OPEN_BROWSER();

	/**
	 * @param url {0}
	 */
	@En("Trying to open a browser for URL {0}")
	String MSG_TRY_TO_OPEN_BROWSER_FOR_URL(String url);

	/**
	 * @param url {0}
	 */
	@En("Please open the following URL in your browser: {0}")
	String MSG_OPEN_URL_IN_BROWSER(String url);

	/**
	 * 
	 */
	@En("Please enter code obtained from website: ")
	String MSG_ENTER_CODE();

	/**
	 * 
	 */
	@En("Synchronisation from Lotus Notes to Google has started.")
	String MSG_SYNC_STARTED();

	/**
	 * 
	 */
	@En("Synchronisation from Lotus Notes to Google has ended.")
	String MSG_SYNC_ENDED();

	/**
	 * @param database {0}
	 */
	@En("Retrieving events from Lotus Notes database \"{0}\"")
	String MSG_READING_LOTUS_NOTES_EVENTS(String database);

	/**
	 * @param calendar {0}
	 */
	@En("Retrieving events from Google calendar \"{0}\"")
	String MSG_READING_GOOGLE_EVENTS(String calendar);

	/**
	 * @param count {0}
	 */
	@En("Removing {0} events from Google")
	String MSG_REMOVING_EVENTS_FROM_GOOGLE(int count);

	/**
	 * @param count {0}
	 */
	@En("Adding {0} events to Google")
	String MSG_ADDING_EVENTS_TO_GOOGLE(int count);

	/**
	 * @param count {0}
	 */
	@En("Updating {0} events to Google")
	String MSG_UPDATING_EVENTS_TO_GOOGLE(int count);

	/**
	 * 
	 */
	@En("Please enter code")
	String MSG_ENTER_VERIFICATION_CODE();

	/**
	 * 
	 */
	@En("Verification requested")
	String TITLE_ENTER_VERIFICATION_CODE();

	/**
	 * 
	 */
	@En("Unparseable period type, valid values are \"d\" (day) or \"m\" (month)")
	String MSG_UNPARSEABLE_PERIOD_TYPE();

	/**
	 * @param key {0}
	 */
	@En("Failed to read registry key \"{0}\".")
	String MSG_FAILED_TO_READ_REGISTRY(String key);

	/**
	 * 
	 */
	@En("SystemTray is not supported.")
	String MSG_TRAY_NOT_SUPPORTED();

	/**
	 * 
	 */
	@De("Synchronisationsverlauf")
	@En("Synchronisation log")
	String TITLE_SYNC_LOG_WINDOW();

	/* menu items */

	/**
	 * 
	 */
	@De("Synchronisieren")
	@En("Synchronize")
	String MENU_ITEM_SYNCHRONIZE();

	/**
	 * 
	 */
	@De("Verlauf anzeigen")
	@En("Show log")
	String MENU_ITEM_SHOW_LOG();

	/**
	 * 
	 */
	@De("Beenden")
	@En("Exit")
	String MENU_ITEM_EXIT();


	/* event names */
	/**  */
	@De("Termin")
	@En("Event")
	String NORMAL_EVENT();

	/**  */
	@De("Jahrestag")
	@En("Anniversary")
	String ANNIVERSARY();

	/**  */
	@De("All day event")
	@En("Ganztägig")
	String ALL_DAY_EVENT();

	/**  */
	@De("Meeting")
	@En("Besprechung")
	String MEETING();

	/**  */
	@De("Reminder")
	@En("Erinnerung")
	String REMINDER();

}
