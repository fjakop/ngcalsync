package de.jakop.ngcalsync.settings;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.notes.NotesHelper;
import de.jakop.ngcalsync.oauth.GoogleOAuth2DAO;
import de.jakop.ngcalsync.oauth.PromptReceiver;
import de.jakop.ngcalsync.util.file.IFileAccessor;

/**
 * 
 * @author fjakop
 * 
 */
public class Settings {

	private final static String HEADER_COMMENT = "# Configuration file for ngcalsync";

	private final Log log;
	private final IFileAccessor fileAccessor;
	private final NotesHelper notesHelper;

	private PropertiesConfiguration configuration;
	private PrivacySettings privacySettings;
	private com.google.api.services.calendar.Calendar calendarService = null;

	private Calendar syncLastDateTime;
	private final Calendar startTime = Calendar.getInstance();

	private Calendar syncEndDate;
	private Calendar syncStartDate;

	/**
	 * 
	 * @param fileAccessor
	 * @param notesHelper 
	 */
	public Settings(final IFileAccessor fileAccessor, final Log log, final NotesHelper notesHelper) {
		Validate.notNull(fileAccessor);
		Validate.notNull(log);
		Validate.notNull(notesHelper);
		this.fileAccessor = fileAccessor;
		this.log = log;
		this.notesHelper = notesHelper;
	}

	/**
	 * @return <code>true</code>, if a restart is supposed
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public boolean load() throws IOException, ConfigurationException {

		final File settingsFile = fileAccessor.getFile(Constants.FILENAME_SYNC_PROPERTIES);
		final File environmentFile = fileAccessor.getFile(Constants.FILENAME_ENV_PROPERTIES);

		configuration = new PropertiesConfiguration(settingsFile);

		// Check if all keys exist and insert new / missing keys with default values
		boolean restart = upgradeConfigurationIfNecessary(settingsFile);
		// Check if environment information is present and valid
		restart = createEnvironmentInformationIfNecessary(environmentFile) || restart;

		// on configuration/environment changes a restart is necessary
		if (restart) {
			return restart;
		}


		// letzte Synchronisierung lesen
		final File file = fileAccessor.getFile(Constants.FILENAME_LAST_SYNC_TIME);
		syncLastDateTime = cloneStartTime();
		syncLastDateTime.setTimeInMillis(0);

		if (!file.exists()) {
			return false;
		}

		final String line = FileUtils.readFileToString(file);

		if (StringUtils.isBlank(line)) {
			return false;
		}

		syncLastDateTime.setTimeInMillis(Long.parseLong(line.trim()));

		syncStartDate = readSyncStartDate();
		syncEndDate = readSyncEndDate();

		return false;
	}

	/**
	 * Checks for missing keys in configuration files. Missing keys are added with default values.
	 * Upon missing key detection the program will print a message.
	 * 
	 * @param settingsFile
	 * @return <code>true</code>, if at least one key was added and the configuration was upgraded
	 * @throws ConfigurationException
	 */
	private boolean upgradeConfigurationIfNecessary(final File settingsFile) throws ConfigurationException {
		final List<String> addedKeys = new ArrayList<String>();
		// duplicate the old configuration and insert new keys
		// appending is not preserving the desired ordering, so we need to copy
		final PropertiesConfiguration newConfiguration = new PropertiesConfiguration();

		newConfiguration.getLayout().setHeaderComment(HEADER_COMMENT);

		for (final ConfigurationParameter parameter : ConfigurationParameter.values()) {
			final String key = parameter.getKey();
			if (configuration.containsKey(key)) {
				newConfiguration.addProperty(key, configuration.getProperty(key));
			} else {
				newConfiguration.addProperty(key, parameter.getDefaultValue());
				addedKeys.add(key);
			}
			newConfiguration.getLayout().setBlancLinesBefore(key, 1);
			newConfiguration.getLayout().setComment(key, parameter.getComment());
		}
		if (!addedKeys.isEmpty()) {
			// save the new config, if the old lacked a key
			newConfiguration.save(settingsFile);
			log.info(String.format(Constants.MSG_CONFIGURATION_UPGRADED, settingsFile.getAbsolutePath(), ArrayUtils.toString(addedKeys.toArray())));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Creates the file with the necessary native environment information, e.g. path to Lotus Notes
	 * if necessary.
	 *
	 * @param environmentInformationFile
	 * @return <code>true</code>, if the file has been created and a restart is necessary
	 */
	private boolean createEnvironmentInformationIfNecessary(final File environmentInformationFile) {

		// Notes classes can be loaded, no changes necessary
		if (notesHelper.isNotesInSystemPath() && notesHelper.isNotesInClassPath()) {
			return false;
		}

		final String lotusNotesHome = notesHelper.getLotusNotesPath();

		PropertiesConfiguration envProperties;
		try {
			envProperties = new PropertiesConfiguration(environmentInformationFile);
			envProperties.getLayout().setGlobalSeparator("=");
			envProperties.setProperty(Constants.NOTES_HOME_ENVVAR_NAME, lotusNotesHome);
			envProperties.save();
			log.info(String.format(Constants.MSG_ENVIRONMENT_CHANGED));
			return true;
		} catch (final ConfigurationException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * @return Login-Name des Google-Accounts
	 */
	public String getGoogleAccountName() {
		return configuration.getString(ConfigurationParameter.GOOGLE_ACCOUNT_EMAIL.getKey());
	}

	/**
	 * @return Name des Domino-Servers
	 */
	public String getDominoServer() {
		return configuration.getString(ConfigurationParameter.NOTES_DOMINO_SERVER.getKey());
	}

	/**
	 * @return Pfad zur Kalender-Datenbank
	 */
	public String getNotesCalendarDbFilePath() {
		return configuration.getString(ConfigurationParameter.NOTES_MAIL_DB_FILE.getKey());
	}

	/**
	 * @return sync only events starting after this date
	 */
	public Calendar getSyncStartDate() {
		return syncStartDate;
	}

	private Calendar readSyncStartDate() throws ConfigurationException {
		final String start = configuration.getString(ConfigurationParameter.SYNC_START.getKey());
		final DateShift dateShift = parseDateShift(start);

		final Calendar sdt = cloneStartTime();
		sdt.add(dateShift.periodType, -dateShift.periodLength);

		return sdt;
	}

	/**
	 * @return sync only events starting before this date
	 */
	public Calendar getSyncEndDate() {
		return syncEndDate;
	}

	private Calendar readSyncEndDate() throws ConfigurationException {

		final String end = configuration.getString(ConfigurationParameter.SYNC_END.getKey());
		final DateShift dateShift = parseDateShift(end);

		final Calendar edt = cloneStartTime();
		edt.add(dateShift.periodType, dateShift.periodLength);

		return edt;
	}

	/**
	 * @return last sync start time
	 */
	public Calendar getSyncLastDateTime() {
		return syncLastDateTime;
	}

	/**
	 * @param syncLastDateTime last sync start time
	 */
	public void setSyncLastDateTime(final Calendar syncLastDateTime) {
		this.syncLastDateTime = syncLastDateTime;
	}

	/**
	 * Save last sync time to file.
	 */
	public void saveLastSyncDateTime() {
		try {
			final File file = fileAccessor.getFile(Constants.FILENAME_LAST_SYNC_TIME);
			FileUtils.writeStringToFile(file, String.format("%s%n", Long.valueOf(syncLastDateTime.getTimeInMillis())));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return hostname of the proxy, empty if none present
	 */
	public String getProxyHost() {
		return configuration.getString(ConfigurationParameter.PROXY_HOST.getKey());
	}

	/**
	 * @return port number of the proxy, empty if none present
	 */
	public String getProxyPort() {
		return configuration.getString(ConfigurationParameter.PROXY_PORT.getKey());
	}

	/**
	 * @return user name of the proxy user, empty if no authentification required
	 */
	public String getProxyUserName() {
		return configuration.getString(ConfigurationParameter.PROXY_USER.getKey());
	}

	/**
	 * @return password of the proxy user, empty if no authentification required
	 */
	public String getProxyPassword() {
		return configuration.getString(ConfigurationParameter.PROXY_PASSWORD.getKey());
	}

	/**
	 * @return name of the google calendar to sync into
	 */
	public String getGoogleCalendarName() {
		return configuration.getString(ConfigurationParameter.GOOGLE_CALENDAR_NAME.getKey());
	}

	/**
	 * @return default reminder time in minutes
	 */
	public int getReminderMinutes() {
		return Integer.parseInt(configuration.getString(ConfigurationParameter.GOOGLE_CALENDAR_REMINDERMINUTES.getKey()));
	}

	/**
	 * @return numeric values of Lotus Notes appointment types to sync
	 * @see de.jakop.ngcalsync.calendar.EventType
	 */
	public int[] getSyncAppointmentTypes() {

		String[] types = configuration.getStringArray(ConfigurationParameter.SYNC_TYPES.getKey());
		if (types.length == 0) {
			types = new String[] { ConfigurationParameter.SYNC_TYPES.getDefaultValue() };
		}
		final int[] syncAppointmentTypes = new int[types.length];
		for (int i = 0; i < types.length; i++) {
			syncAppointmentTypes[i] = Integer.parseInt(types[i].trim());
		}

		return syncAppointmentTypes;
	}

	/**
	 * 
	 * @return the settings for protecting privacy of event's data
	 */
	public PrivacySettings getPrivacySettings() {
		if (privacySettings == null) {
			privacySettings = new PrivacySettings(//
					configuration.getBoolean(ConfigurationParameter.SYNC_TRANSFER_TITLE.getKey()), //
					configuration.getBoolean(ConfigurationParameter.SYNC_TRANSFER_DESCRIPTION.getKey()), //
					configuration.getBoolean(ConfigurationParameter.SYNC_TRANSFER_LOCATION.getKey()));
		}

		return privacySettings;
	}

	/**
	 * Liefert den Zugriff auf den Google-Kalender
	 * 
	 * @return the service
	 */
	public com.google.api.services.calendar.Calendar getGoogleCalendarService() {

		if (calendarService == null) {
			if (!StringUtils.isBlank(getProxyHost()) && !StringUtils.isBlank(getProxyPort())) {
				System.setProperty("http.proxyHost", getProxyHost());
				System.setProperty("http.proxyPort", getProxyPort());
				System.setProperty("https.proxyHost", getProxyHost());
				System.setProperty("https.proxyPort", getProxyPort());
			}

			try {
				final HttpTransport httpTransport = new NetHttpTransport();
				final JacksonFactory jsonFactory = new JacksonFactory();

				final List<String> scopes = Arrays.asList(CalendarScopes.CALENDAR);
				final GoogleOAuth2DAO googleOAuth2DAO = new GoogleOAuth2DAO(httpTransport, jsonFactory, new PromptReceiver(), fileAccessor.getFile(Constants.FILENAME_USER_SECRETS));
				final Credential credential = googleOAuth2DAO.authorize(scopes, getGoogleAccountName());

				calendarService = com.google.api.services.calendar.Calendar.builder(httpTransport, jsonFactory).setApplicationName(Constants.APPLICATION_NAME)
						.setHttpRequestInitializer(credential).build();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		return calendarService;
	}

	/* for JUnit-Tests */
	protected Calendar getProgramStartTime() {
		return startTime;
	}

	private DateShift parseDateShift(final String shiftExpression) throws ConfigurationException {
		final DateShift dateShift = new DateShift();
		try {
			dateShift.periodType = parsePeriodType(shiftExpression);
			dateShift.periodLength = parsePeriod(shiftExpression);
		} catch (final ParseException e) {
			throw new ConfigurationException(String.format(Constants.MSG_UNABLE_TO_PARSE_DATE_SHIFT, shiftExpression), e);
		} catch (final NumberFormatException e) {
			throw new ConfigurationException(String.format(Constants.MSG_UNABLE_TO_PARSE_DATE_SHIFT, shiftExpression), e);
		}
		return dateShift;
	}


	private int parsePeriod(final String start) throws NumberFormatException {
		return Integer.parseInt(start.substring(0, start.length() - 1));
	}

	private int parsePeriodType(final String start) throws ParseException {
		if (start.endsWith("d")) {
			return Calendar.DAY_OF_YEAR;
		} else if (start.endsWith("m")) {
			return Calendar.MONTH;
		}
		// TODO i18n
		throw new ParseException("Unparseable period type, valid values are 'd' (day) or 'm' (month)", start.length() - 1);
	}

	private Calendar cloneStartTime() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startTime.getTimeInMillis());
		return calendar;
	}

	private final class DateShift {
		int periodType;
		int periodLength;
	}
}
