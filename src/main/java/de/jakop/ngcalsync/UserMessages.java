package de.jakop.ngcalsync;

import c10n.annotations.De;
import c10n.annotations.En;

/**
 * 
 * @author fjakop
 *
 */
public interface UserMessages {

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

	/** */
	public static final String MSG_ENTER_CLIENT_ID_AND_SECRET = "Please enter your client ID and secret from the Google APIs Console in %s.";
	/** */
	public static final String MSG_UNABLE_TO_PARSE_DATE_SHIFT = "Unable to parse start date shift '%s'.";
	/** */
	public static final String MSG_FAILED_TO_OPEN_BROWSER = "Failed to open browser.";
	/** */
	public static final String MSG_TRY_TO_OPEN_BROWSER_FOR_URL = "Trying to open a browser for URL %s";
	/** */
	public static final String MSG_OPEN_URL_IN_BROWSER = "Please open the following URL in your browser:";
	/** */
	public static final String MSG_ENTER_CODE = "Please enter code obtained from website: ";
	/** */
	public static final String MSG_SYNC_STARTED = "Synchronisation from Lotus Notes to Google has started.";
	/** */
	public static final String MSG_SYNC_ENDED = "Synchronisation from Lotus Notes to Google has ended.";
	/** */
	public static final String MSG_READING_LOTUS_NOTES_EVENTS = "Retrieving events from Lotus Notes database \"%s\"";
	/** */
	public static final String MSG_READING_GOOGLE_EVENTS = "Retrieving events from Google calendar \"%s\"";
	/** */
	public static final String MSG_REMOVING_EVENTS_FROM_GOOGLE = "Removing %s events from Google";
	/** */
	public static final String MSG_ADDING_EVENTS_TO_GOOGLE = "Adding %s events to Google";
	/** */
	public static final String MSG_UPDATING_EVENTS_TO_GOOGLE = "Updating %s events to Google";


	/* defaults for settings */
	/** */
	public static final String DEFAULT_GOOGLE_CALENDAR_REMINDERMINUTES = "15";
	/** */
	public static final String DEFAULT_GOOGLE_CALENDAR_NAME = "Calendar";

	/* filenames */
	/** */
	public static final String FILENAME_SETTINGS_DIR = ".ngcalsync";
	/** */
	public static final String FILENAME_SYNC_PROPERTIES = "sync.properties";
	/** */
	public static final String FILENAME_ENV_PROPERTIES = "env.properties";
	/** */
	public static final String FILENAME_USER_SECRETS = "user.secrets";
	/** */
	public static final String FILENAME_LAST_SYNC_TIME = "LastSyncTime";

	/* global */
	/** */
	public static final String APPLICATION_NAME = "ngcalsync";
	/** */
	public static final String NOTES_HOME_ENVVAR_NAME = "NOTES_HOME";

	/* event names */
	/**  */
	public static final String NORMAL_EVENT = "Termin";
	/**  */
	public static final String ANNIVERSARY = "Jahrestag";
	/**  */
	public static final String ALL_DAY_EVENT = "Ganztägig";
	/**  */
	public static final String MEETING = "Besprechung";
	/**  */
	public static final String REMINDER = "Erinnerung";


}
