package de.jakop.ngcalsync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;

import de.jakop.ngcalsync.oauth.GoogleOAuth2DAO;
import de.jakop.ngcalsync.oauth.PromptReceiver;

/**
 * 
 * @author fjakop
 *
 */
public class Settings {

	private final static String HEADER_COMMENT = "# Configuration file for ngcalsync";

	private enum Parameter {

		SYNC_TYPES("sync.types", "3", "# Types of events to sync\n" + //
				"# 0 = Normal event\n" + //
				"# 1 = Anniversary\n" + //
				"# 2 = All day event\n" + //
				"# 3 = Meeting\n" + //
				"# 4 = Reminder\n" + //
				"# e.g. \"1,3,4\""), //

		SYNC_END("sync.end", "3m", "# Number of days(ex. 15d) or month (ex. 2m) in the future, default 3 month"), //

		SYNC_START("sync.start", "14d", "# Number of days (ex. 15d) or month (ex. 2m) back in time, default 14 days"), //

		NOTES_MAIL_DB_FILE("notes.mail.db.file", "", "# Notes database name\n" + //
				"#  in Notes go to\n" + //
				"#  Notes File/Preferences/Location Preferences.../Mail/'Mail file', if there \n" + //
				"#  are \\ in the path replace them with /"), //

		NOTES_DOMINO_SERVER("notes.domino.server", "", "# Notes server name\n" + //
				"#  in Notes go to\n" + //
				"#  File/Preferences/Location Preferences.../Servers/'Home/mail server', if there \n" + //
				"#  are \\ in the path replace them with /\n" + //
				"#  Leave blank for local."), //

		GOOGLE_CALENDAR_REMINDERMINUTES("google.calendar.reminderminutes", "30", "# Google default reminder time"), //

		GOOGLE_CALENDAR_NAME("google.calendar.name", "", "# Google calendar name to sync with (e.g. \"work\")\n" + //
				"# WARNING #\n" + // 
				"# This calendar's events will be deleted if not present in Lotus Notes"), //

		GOOGLE_ACCOUNT_EMAIL("google.account.email", "", "# Google account email"), //

		PROXY_HOST("proxy.host", "", "# Hostname or IP of the proxy server, if you are behind a proxy"), //

		PROXY_PORT("proxy.port", "", "# Port of the proxy server, if you are behind a proxy"), //

		PROXY_USER("proxy.user", "", "# Username, if the proxy requires authentication"), //

		PROXY_PASSWORD("proxy.password", "", "# Password, if the proxy requires authentication");

		private final String key;
		private final String defaultvalue;
		private final String comment;

		Parameter(String key, String defaultvalue, String comment) {
			this.key = key;
			this.defaultvalue = defaultvalue;
			this.comment = comment;
		}

		String getKey() {
			return key;
		}

		String getDefaultValue() {
			return defaultvalue;
		}

		String getComment() {
			return comment;
		}

	}


	private final Log log = LogFactory.getLog(getClass());

	private PropertiesConfiguration configuration;

	private com.google.api.services.calendar.Calendar calendarService = null;

	private Calendar syncLastDateTime;

	private File settingsDir;

	private File getSettingsDir() {
		if (settingsDir == null) {
			settingsDir = new File(System.getProperty("user.home"), Constants.FILENAME_SETTINGS_DIR);
			if (!settingsDir.isDirectory()) {
				settingsDir.mkdirs();
			}
		}
		return settingsDir;
	}

	/**
	 * 
	 * @param name
	 * @return the {@link File} containing the date of the last sync
	 */
	private File getFile(String name) {
		return new File(getSettingsDir(), name);
	}


	/**
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public void load() throws IOException, ConfigurationException {

		File settingsFile = getFile(Constants.FILENAME_SYNC_PROPERTIES);

		boolean firstStart = !settingsFile.exists();
		configuration = new PropertiesConfiguration(settingsFile);

		// Check if all keys exist and insert new / missing keys with default values
		upgradeConfigurationIfNecessary(settingsFile);

		// on first start the user 
		if (firstStart) {
			log.info(String.format(Constants.MSG_FIRST_START, settingsFile.getAbsolutePath()));
			System.exit(0);
		}

		// letzte Synchronisierung lesen
		File file = getFile(Constants.FILENAME_LAST_SYNC_TIME);
		syncLastDateTime = Calendar.getInstance();
		if (!file.exists()) {
			syncLastDateTime.setTimeInMillis(0);
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = br.readLine();
		if (line != null) {
			syncLastDateTime.setTimeInMillis(Long.parseLong(line.trim()));
		}
	}

	/**
	 * Checks for missing keys in configuration files. Missing keys are added with default values.
	 * Upon missing key detection the program will print a message and exit.
	 * 
	 * @param settingsFile
	 * @throws ConfigurationException
	 */
	private void upgradeConfigurationIfNecessary(File settingsFile) throws ConfigurationException {
		List<String> addedKeys = new ArrayList<String>();
		// duplicate the old configuration and insert new keys
		// appending is not preserving the desired ordering, so we need to copy
		PropertiesConfiguration newConfiguration = new PropertiesConfiguration();

		newConfiguration.getLayout().setHeaderComment(HEADER_COMMENT);

		for (Parameter parameter : Parameter.values()) {
			String key = parameter.getKey();
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
			System.exit(0);
		}
	}

	private String getString(Parameter parameter) {
		return configuration.getString(parameter.getKey(), parameter.getDefaultValue());
	}

	/**
	 * @return Login-Name des Google-Accounts
	 */
	public String getGoogleAccountName() {
		return getString(Parameter.GOOGLE_ACCOUNT_EMAIL);
	}

	/**
	 * @return Name des Domino-Servers
	 */
	public String getDominoServer() {
		return getString(Parameter.NOTES_DOMINO_SERVER);
	}

	/**
	 * @return Pfad zur Kalender-Datenbank
	 */
	public String getNotesCalendarDbFilePath() {
		return getString(Parameter.NOTES_MAIL_DB_FILE);
	}

	/**
	 * @return sync only events starting after this date
	 *  
	 * @throws ConfigurationException 
	 */
	public Calendar getSyncStartDate() throws ConfigurationException {

		String start = getString(Parameter.SYNC_START);
		int periodType;
		int period;
		try {
			periodType = parsePeriodType(start);
			period = parsePeriod(start);
		} catch (FormatException e) {
			throw new ConfigurationException(String.format(Constants.MSG_UNABLE_TO_PARSE_DATE_SHIFT, start), e);
		}

		Calendar sdt = Calendar.getInstance();
		sdt.add(periodType, -period);

		return sdt;
	}

	/**
	 * @return sync only events starting before this date
	 * 
	 * @throws ConfigurationException 
	 */
	public Calendar getSyncEndDate() throws ConfigurationException {

		String end = getString(Parameter.SYNC_END);
		int periodType;
		int period;
		try {
			periodType = parsePeriodType(end);
			period = parsePeriod(end);
		} catch (FormatException e) {
			throw new ConfigurationException(String.format(Constants.MSG_UNABLE_TO_PARSE_DATE_SHIFT, end), e);
		}

		Calendar edt = Calendar.getInstance();
		edt.add(periodType, period);

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
	public void setSyncLastDateTime(Calendar syncLastDateTime) {
		this.syncLastDateTime = syncLastDateTime;
	}

	/**
	 * Save user settings to file.
	 */
	public void save() {
		try {
			File file = getFile(Constants.FILENAME_LAST_SYNC_TIME);
			FileWriter fw = new FileWriter(file);
			fw.write(System.currentTimeMillis() + "\n");
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return hostname of the proxy, empty if none present
	 */
	public String getProxyHost() {
		return getString(Parameter.PROXY_HOST);
	}

	/**
	 * @return port number of the proxy, empty if none present
	 */
	public String getProxyPort() {
		return getString(Parameter.PROXY_PORT);
	}

	/**
	 * @return user name of the proxy user, empty if no authentification required
	 */
	public String getProxyUserName() {
		return getString(Parameter.PROXY_USER);
	}

	/**
	 * @return password of the proxy user, empty if no authentification required
	 */
	public String getProxyPassword() {
		return getString(Parameter.PROXY_PASSWORD);
	}

	/**
	 * @return name of the google calendar to sync into
	 */
	public String getGoogleCalendarName() {
		return getString(Parameter.GOOGLE_CALENDAR_NAME);
	}

	/**
	 * @return default reminder time in minutes
	 */
	public int getReminderMinutes() {
		return Integer.parseInt(getString(Parameter.GOOGLE_CALENDAR_REMINDERMINUTES));
	}

	/**
	 * @return numeric values of Lotus Notes appointment types to sync
	 * @see de.jakop.ngcalsync.calendar.CalendarEvent.EventType
	 */
	public int[] getSyncAppointmentTypes() {

		String typesRaw = getString(Parameter.SYNC_TYPES);
		String[] types = StringUtils.split(typesRaw, ",");
		int[] syncAppointmentTypes = new int[types.length];
		for (int i = 0; i < types.length; i++) {
			syncAppointmentTypes[i] = Integer.parseInt(types[i].trim());
		}

		return syncAppointmentTypes;
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
				HttpTransport httpTransport = new NetHttpTransport();
				JacksonFactory jsonFactory = new JacksonFactory();

				List<String> scopes = Arrays.asList(CalendarScopes.CALENDAR);
				GoogleOAuth2DAO googleOAuth2DAO = new GoogleOAuth2DAO(httpTransport, jsonFactory, new PromptReceiver(), getFile(Constants.FILENAME_USER_SECRETS));
				Credential credential = googleOAuth2DAO.authorize(scopes, getGoogleAccountName());

				calendarService = com.google.api.services.calendar.Calendar.builder(httpTransport, jsonFactory).setApplicationName(Constants.APPLICATION_NAME)
						.setHttpRequestInitializer(credential).build();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return calendarService;
	}

	private int parsePeriod(String start) throws FormatException {
		try {
			return Integer.parseInt(start.substring(0, start.length() - 1));
		} catch (Exception e) {
			throw new FormatException();
		}
	}

	private int parsePeriodType(String start) throws FormatException {
		if (start.endsWith("d")) {
			return Calendar.DAY_OF_YEAR;
		} else if (start.endsWith("m")) {
			return Calendar.MONTH;
		}
		throw new FormatException();
	}

	private class FormatException extends Exception {
		private static final long serialVersionUID = 2485854390296466112L;
	}

}
