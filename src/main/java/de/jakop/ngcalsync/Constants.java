package de.jakop.ngcalsync;

/**
 * 
 * @author fjakop
 *
 */
public class Constants {

	/* messages */
	/** */
	public static final String MSG_FIRST_START = "First start detected. Please edit %s according to your needs and restart.";
	/** */
	public static final String MSG_ENTER_CLIENT_ID_AND_SECRET = "Please enter your client ID and secret from the Google APIs Console in %s.";
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
	public static final String FILENAME_USER_SECRETS = "user.secrets";
	/** */
	public static final String FILENAME_LAST_SYNC_TIME = "LastSyncTime";

	/* global */
	/** */
	public static final String APPLICATION_NAME = "ngcalsync";


}
