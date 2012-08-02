package de.jakop.ngcalsync;

/**
 * 
 * @author fjakop
 *
 */
public interface Constants {

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

}
