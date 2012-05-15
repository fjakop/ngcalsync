package de.jakop.ngcalsync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
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

	private static final String SYNC_TYPES = "sync.types";
	private static final String SYNC_END = "sync.end";
	private static final String SYNC_START = "sync.start";
	private static final String NOTES_MAIL_DB_FILE = "notes.mail.db.file";
	private static final String NOTES_DOMINO_SERVER = "notes.domino.server";
	private static final String GOOGLE_CALENDAR_REMINDERMINUTES = "google.calendar.reminderminutes";
	private static final String GOOGLE_CALENDAR_NAME = "google.calendar.name";
	private static final String GOOGLE_ACCOUNT_EMAIL = "google.account.email";
	private static final String PROXY_PORT = "proxy.port";
	private static final String PROXY_HOST = "proxy.host";

	private final Log log = LogFactory.getLog(getClass());

	private String googleAccountName;
	private String googleCalendarName;
	private com.google.api.services.calendar.Calendar calendarService = null;


	private int reminderMinutes;
	private int[] syncAppointmentTypes;

	private String dominoServer;
	private String notesCalendarDbFilePath;

	private Calendar syncStartDate;
	private Calendar syncEndDate;
	private Calendar syncLastDateTime;
	private Calendar syncStart;

	private String proxyHost;
	private String proxyPort;
	private String proxyUserName;
	private String proxyPassword;

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
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void load() throws FileNotFoundException, IOException {

		Properties p = new Properties();

		File settingsFile = getFile(Constants.FILENAME_SYNC_PROPERTIES);
		if (!settingsFile.exists()) {
			InputStream template = getClass().getResourceAsStream("/" + Constants.FILENAME_SYNC_PROPERTIES + ".template");
			FileUtils.copyInputStreamToFile(template, settingsFile);
			log.info(String.format(Constants.MSG_FIRST_START, settingsFile.getAbsolutePath()));
			System.exit(0);
		}

		p.load(new FileInputStream(settingsFile));

		setProxyHost(getProperty(p, PROXY_HOST));
		setProxyPort(getProperty(p, PROXY_PORT));

		setGoogleAccountName(getProperty(p, GOOGLE_ACCOUNT_EMAIL));
		setGoogleCalendarName(getProperty(p, GOOGLE_CALENDAR_NAME, Constants.DEFAULT_GOOGLE_CALENDAR_NAME));
		setReminderMinutes(Integer.parseInt(getProperty(p, GOOGLE_CALENDAR_REMINDERMINUTES, Constants.DEFAULT_GOOGLE_CALENDAR_REMINDERMINUTES)));

		setDominoServer(getProperty(p, NOTES_DOMINO_SERVER));
		setNotesCalendarDbFilePath(getProperty(p, NOTES_MAIL_DB_FILE));

		String start = getProperty(p, SYNC_START);

		int periodType;
		int period;
		try {
			periodType = parsePeriodType(start);
			period = parsePeriod(start);
		} catch (FormatException e) {
			periodType = Calendar.DAY_OF_YEAR;
			period = 14;
		}

		Calendar sdt = Calendar.getInstance();
		sdt.add(periodType, -period);
		setSyncStartDate(sdt);

		String end = getProperty(p, SYNC_END);
		try {
			periodType = parsePeriodType(end);
			period = parsePeriod(end);
		} catch (FormatException e) {
			periodType = Calendar.MONTH;
			period = 3;
		}
		Calendar edt = Calendar.getInstance();
		edt.add(periodType, period);
		setSyncEndDate(edt);
		syncStart = Calendar.getInstance();

		// Art der Kalendereinträge, die synchronisiert werden
		String typesRaw = getProperty(p, SYNC_TYPES, "3");
		String[] types = StringUtils.split(typesRaw, ",");
		syncAppointmentTypes = new int[types.length];
		for (int i = 0; i < types.length; i++) {
			syncAppointmentTypes[i] = Integer.parseInt(types[i].trim());
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
	 * @return Login-Name des Google-Accounts
	 */
	public String getGoogleAccountName() {
		return googleAccountName;
	}

	/**
	 * @param googleAccountName Login-Name des Google-Accounts
	 */
	public void setGoogleAccountName(String googleAccountName) {
		this.googleAccountName = googleAccountName;
	}

	/**
	 * @return Name des Domino-Servers
	 */
	public String getDominoServer() {
		return dominoServer;
	}

	/**
	 * @param dominoServer Name des Domino-Servers
	 */
	public void setDominoServer(String dominoServer) {
		this.dominoServer = dominoServer;
	}

	/**
	 * @return Pfad zur Kalender-Datenbank
	 */
	public String getNotesCalendarDbFilePath() {
		return notesCalendarDbFilePath;
	}

	/**
	 * @param notesCalendarDbFilePath Pfad zur Kalender-Datenbank
	 */
	public void setNotesCalendarDbFilePath(String notesCalendarDbFilePath) {
		this.notesCalendarDbFilePath = notesCalendarDbFilePath;
	}

	/**
	 * @return sync only events starting after this date 
	 */
	public Calendar getSyncStartDate() {
		return syncStartDate;
	}

	/**
	 * @param syncStartDate sync only events starting after this date
	 */
	public void setSyncStartDate(Calendar syncStartDate) {
		this.syncStartDate = syncStartDate;
	}

	/**
	 * @return sync only events starting before this date
	 */
	public Calendar getSyncEndDate() {
		return syncEndDate;
	}

	/**
	 * @param syncEndDate sync only events starting before this date
	 */
	public void setSyncEndDate(Calendar syncEndDate) {
		this.syncEndDate = syncEndDate;
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
	 * @param proxyHost hostname of the proxy, empty if none present
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @return hostname of the proxy, empty if none present
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @param proxyPort port number of the proxy, empty if none present
	 */
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return port number of the proxy, empty if none present
	 */
	public String getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyUserName user name of the proxy user, empty if no authentification required
	 */
	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	/**
	 * @return user name of the proxy user, empty if no authentification required
	 */
	public String getProxyUserName() {
		return proxyUserName;
	}

	/**
	 * @param proxyPassword password of the proxy user, empty if no authentification required
	 */
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	/**
	 * @return password of the proxy user, empty if no authentification required
	 */
	public String getProxyPassword() {
		return proxyPassword;
	}

	/**
	 * @return name of the google calendar to sync into
	 */
	public String getGoogleCalendarName() {
		return googleCalendarName;
	}

	/**
	 * @param calendarName name of the google calendar to sync into
	 */
	public void setGoogleCalendarName(String calendarName) {
		googleCalendarName = calendarName;
	}

	/**
	 * @return default reminder time in minutes
	 */
	public int getReminderMinutes() {
		return reminderMinutes;
	}

	/**
	 * 
	 * @param reminderMinutes default reminder time in minutes
	 */
	public void setReminderMinutes(int reminderMinutes) {
		this.reminderMinutes = reminderMinutes;
	}

	/**
	 * @return start time of this sync run
	 */
	public Calendar getSyncStart() {
		return syncStart;
	}

	/**
	 * @return numeric values of Lotus Notes appointment types to sync
	 * @see de.jakop.ngcalsync.CalendarEvent.EventType
	 */
	public int[] getSyncAppointmentTypes() {
		return syncAppointmentTypes;
	}

	/**
	 * @param syncAppointmentTypes numeric values of Lotus Notes appointment types to sync
	 * @see de.jakop.ngcalsync.CalendarEvent.EventType
	 */
	public void setSyncAppointmentTypes(int[] syncAppointmentTypes) {
		this.syncAppointmentTypes = syncAppointmentTypes;
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





	private String getProperty(Properties p, String propertyName) {
		return System.getProperty(propertyName, p.getProperty(propertyName));
	}

	private String getProperty(Properties p, String propertyName, String defaultValue) {
		return System.getProperty(propertyName, p.getProperty(propertyName, defaultValue));
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
