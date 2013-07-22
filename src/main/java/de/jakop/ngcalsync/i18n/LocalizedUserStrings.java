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
	 * 
	 */
	@De("Anonymisierte Lotus Notes Kalender-Einträge in einen Google-Kalender synchronisieren")
	@En("Synchronize anonymized Lotus Notes calendar events to Google calendar")
	String APPLICATION_DESCRIPTION();

	/**
	 * @param file {0}
	 * @param keys {1}
	 */
	@De("Die Konfigurationsdatei {0} wurde angepasst, bitte prüfen Sie die Parameter {1} und starten die Anwendung danach neu.")
	@En("The configuration file {0} was upgraded, please check the keys {1} and restart.")
	String MSG_CONFIGURATION_UPGRADED(String file, String keys);

	/**
	 * 
	 */
	@De("Die Konfigurationsdatei wurde angepasst, bitte prüfen Sie diese starten die Synchronisierung danach neu.")
	@En("The configuration was upgraded, please check and restart synchronisation.")
	String MSG_CONFIGURATION_UPGRADED();

	/**
	 * @param file {0}
	 */
	@De("Die Synchronisierung schlug fehl. Details finden Sie in der Datei {0}.")
	@En("The synchronisation failed. See logfile {0} for details.")
	String MSG_SYNC_FAILED(String file);

	/**
	 * 
	 */
	@De("Architektur-Inkompatibilität. Bitte stellen Sie sicher, dass die verwendete Java Runtime dieselbe Architektur (32/64-bit) wie Lotus Notes besitzt.")
	@En("Architecture incompatibility. Please make sure that the Java Runtime is of same architecture (32/64-bit) as your Lotus Notes software.")
	String MSG_ARCHITECTURE_INCOMPATIBILITY();


	/**
	 * @param calendarName {0}
	 */
	@De("Google Kalender \"{0}\" existiert nicht.")
	@En("Google calendar \"{0}\" does not exist.")
	String GOOGLE_CALENDAR_S_DOES_NOT_EXIST_CHECK_CONFIG(String calendarName);


	/**
	 * @param eventType {0}
	 */
	@De("Kalendareintrag-Typ \"{0}\" ist unbekannt.")
	@En("Event type \"{0}\" not recognized")
	String MSG_EVENT_TYPE_S_NOT_RECOGNIZED_CHECK_CONFIG(int eventType);

	/**
	 * 
	 */
	@De("Die Umgebungsvariablen wurden angepasst, bitte starten die Synchronisierung neu.")
	@En("Environment information has changed, please restart the application.")
	String MSG_ENVIRONMENT_CHANGED();

	/**
	 * @param dateShift {0}
	 */
	@De("Die Datumsverschiebung \"{0}\" konnte nicht interpretiert werden, bitte prüfen Sie die Konfiguration.")
	@En("Unable to parse start date shift \"{0}\".")
	String MSG_UNABLE_TO_PARSE_DATE_SHIFT(String dateShift);

	/**
	 * 
	 */
	@De("Es konnte kein Browser geöffnet werden.")
	@En("Failed to open browser.")
	String MSG_FAILED_TO_OPEN_BROWSER();

	/**
	 * @param url {0}
	 */
	@De("Bitte öffnen Sie folgende URL in Ihrem Browser: {0}")
	@En("Please open the following URL in your browser: {0}")
	String MSG_OPEN_URL_IN_BROWSER(String url);

	/**
	 * 
	 */
	@De("Bitte geben Sie den Code von der Webseite ein: ")
	@En("Please enter code obtained from website: ")
	String MSG_ENTER_CODE();

	/**
	 * 
	 */
	@De("Bitte geben Sie den Pfad zu Ihrer Lotus Notes Installation ein: ")
	@En("Please enter path to Lotus Notes installation: ")
	String MSG_ENTER_LOTUS_NOTES_PATH();

	/**
	 * 
	 */
	@De("Die Synchronisierung von Lotus Notes in den Google-Kalender beginnt.")
	@En("Synchronisation from Lotus Notes to Google has started.")
	String MSG_SYNC_STARTED();

	/**
	 * 
	 */
	@De("Die Synchronisierung von Lotus Notes in den Google-Kalender ist beendet.")
	@En("Synchronisation from Lotus Notes to Google has ended.")
	String MSG_SYNC_ENDED();

	/**
	 * 
	 */
	@De("Die Synchronisierung läuft bereits, es wird keine neue gestartet.")
	@En("Synchronisation is already running, a new one cannot be started.")
	String MSG_SYNC_IN_PROGRESS();

	/**
	 * @param database {0}
	 */
	@De("Die Kalendereinträge aus der Lotus Notes Datenbank \"{0}\" werden gelesen.")
	@En("Retrieving events from Lotus Notes database \"{0}\"")
	String MSG_READING_LOTUS_NOTES_EVENTS(String database);

	/**
	 * @param calendar {0}
	 */
	@De("Die Kalendereinträge aus dem Google-Kalender \"{0}\" werden gelesen.")
	@En("Retrieving events from Google calendar \"{0}\"")
	String MSG_READING_GOOGLE_EVENTS(String calendar);

	/**
	 * @param count {0}
	 */
	@De("{0} Kalendereinträge werden aus Google-Kalender entfernt.")
	@En("Removing {0} events from Google")
	String MSG_REMOVING_EVENTS_FROM_GOOGLE(int count);

	/**
	 * @param count {0}
	 */
	@De("{0} Kalendereinträge werden zum Google-Kalender hinzugefügt.")
	@En("Adding {0} events to Google")
	String MSG_ADDING_EVENTS_TO_GOOGLE(int count);

	/**
	 * @param count {0}
	 */
	@De("{0} Kalendereinträge werden im Google-Kalender geändert.")
	@En("Updating {0} events to Google")
	String MSG_UPDATING_EVENTS_TO_GOOGLE(int count);

	/**
	 * 
	 */
	@De("Eingabe erforderlich")
	@En("Input requested")
	String TITLE_INPUT_REQUESTED();

	/**
	 * @param periodType {0}
	 */
	@De("Der Zeitraum-Bezeichner \"{0}\" konnte nicht interpretiert werden.  Gültige Werte sind \"d\" (Tag) oder \"m\" (Monat).")
	@En("Unparseable period type \"{0}\", valid values are \"d\" (day) or \"m\" (month).")
	String MSG_UNPARSEABLE_PERIOD_TYPE(String periodType);

	/**
	 * @param key {0}
	 */
	@De("Der Registry-Schlüssel \"{0}\" konnte nicht gelesen werden.")
	@En("Failed to read registry key \"{0}\".")
	String MSG_FAILED_TO_READ_REGISTRY(String key);

	/**
	 * 
	 */
	@De("Das SystemTray wird nicht unterstützt.")
	@En("SystemTray is not supported.")
	String MSG_TRAY_NOT_SUPPORTED();

	/**
	 * 
	 */
	@De("Synchronisationsverlauf")
	@En("Synchronisation log")
	String TITLE_SYNC_LOG_WINDOW();

	/**
	 * 
	 */
	@De("Anwendungsinformation")
	@En("About this application")
	String TITLE_ABOUT_WINDOW();

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
	@De("Anwendungsinfo")
	@En("About")
	String MENU_ITEM_ABOUT();

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

	/** */
	@De("Startet die Anwendung ohne GUI (a.k.a im Konsolenmodus) und synchronisiert sofort")
	@En("Starts the application without GUI (a.k.a. console mode) and with immediate synchronisation")
	String MSG_COMMAND_OPTION_DESCRIPTION_CONSOLE();
}
