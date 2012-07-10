package de.jakop.ngcalsync.settings;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.notes.NotesHelper;
import de.jakop.ngcalsync.rule.TestdataRule;
import de.jakop.ngcalsync.util.file.IFileAccessor;


/**
 * 
 * @author fjakop
 *
 */
public class SettingsTest {

	/** expected exception */
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/** access to testdata */
	@Rule
	public TestdataRule testdata = new TestdataRule();

	@Mock
	private IFileAccessor fileAccessor;
	@Mock
	private Log log;
	@Mock
	private NotesHelper notesHelper;

	private File settingsFile;
	private File environmentFile;
	private File lastSyncDateFile;


	/** */
	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);

		// get a temp settings file
		lastSyncDateFile = File.createTempFile(getClass().getName() + ".lastsync.", null);
		settingsFile = File.createTempFile(getClass().getName() + ".settings.", null);
		environmentFile = File.createTempFile(getClass().getName() + ".environment.", null);
		FileUtils.writeStringToFile(lastSyncDateFile, "123456");

		doReturn(settingsFile).when(fileAccessor).getFile(Matchers.eq(Constants.FILENAME_SYNC_PROPERTIES));
		doReturn(environmentFile).when(fileAccessor).getFile(Matchers.eq(Constants.FILENAME_ENV_PROPERTIES));
		doReturn(lastSyncDateFile).when(fileAccessor).getFile(Matchers.eq(Constants.FILENAME_LAST_SYNC_TIME));

		doReturn(Boolean.TRUE).when(notesHelper).isNotesInSystemPath();
		doReturn(Boolean.TRUE).when(notesHelper).isNotesInClassPath();

	}

	/** */
	@After
	public void after() {
		FileUtils.deleteQuietly(lastSyncDateFile);
		FileUtils.deleteQuietly(settingsFile);
		FileUtils.deleteQuietly(environmentFile);
	}

	/**
	 * Verifies that environment file is created if missing.
	 * @throws Exception 
	 */
	@Test
	public void testFirstStart_EnvironmentFileMissing_IsCreated_ProgramExits() throws Exception {


		// create settings file
		loadSettings(true);

		// delete the environment file to simulate a first start
		environmentFile.delete();

		doReturn(Boolean.FALSE).when(notesHelper).isNotesInSystemPath();
		doReturn(Boolean.TRUE).when(notesHelper).isNotesInClassPath();

		// load again, exit and environment message happen
		log = mock(Log.class);

		loadSettings(true);

		// verify message
		verify(log, times(1)).info(String.format(Constants.MSG_ENVIRONMENT_CHANGED));

		// verify that all parameters are set with their defaults
		verifyAllParametersAreSetWithDefaults();
	}

	/**
	 * Verifies that settings file is created preset with defaults on first start.
	 * @throws Exception 
	 */
	@Test
	public void testFirstStart_SettingsFileMissing_IsCreated_ProgramExits() throws Exception {

		// delete the settings file to simulate a first start
		settingsFile.delete();

		loadSettings(true);

		// verify message
		verify(log, times(1)).info(
				String.format(Constants.MSG_CONFIGURATION_UPGRADED, settingsFile.getAbsolutePath(),
						"{sync.types,sync.end,sync.start,sync.transfer.title,sync.transfer.description,sync.transfer.location," //
								+ "notes.mail.db.file,notes.domino.server," + "google.calendar.reminderminutes,google.calendar.name,google.account.email," //
								+ "proxy.host,proxy.port,proxy.user,proxy.password}"));


		// verify that all parameters are set with their defaults
		verifyAllParametersAreSetWithDefaults();
	}

	/**
	 * Verifies that missing keys are automatically created and preset with defaults.
	 * @throws Exception 
	 */
	@Test
	public void testParameterAdded_FileIsUpgraded_ProgramExits() throws Exception {

		loadSettings(true);

		// verify message (build parameter keys array from enum, so it has not to be changed each time we add a parameter)
		final StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (final ConfigurationParameter parameter : ConfigurationParameter.values()) {
			builder.append(parameter.getKey()).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("}");

		verify(log, times(1)).info(String.format(Constants.MSG_CONFIGURATION_UPGRADED, settingsFile.getAbsolutePath(), builder.toString()));

		// verify that all parameters are set with their defaults
		verifyAllParametersAreSetWithDefaults();
	}

	/**
	 * Verifies that no message is logged if configuration is up to date.
	 * @throws Exception 
	 */
	@Test
	public void testConfigurationIsUpToDate_NoMessageLogged() throws Exception {

		// create file with defaults
		loadSettings(true);

		// load again, no exit and no message happens
		log = mock(Log.class);
		loadSettings(false);

		verifyNoMoreInteractions(log);

		// verify that all parameters are set with their defaults
		verifyAllParametersAreSetWithDefaults();
	}

	/**
	 * Verifies that every configuration parameter can be obtained by its getter and its default value.
	 * @throws Exception 
	 */
	@Test
	public void testGetValuesWithDefaults() throws Exception {

		// create file with defaults
		final Settings settings = loadSettings(true);

		assertEquals("", settings.getGoogleAccountName());
		assertEquals("", settings.getGoogleCalendarName());
		assertEquals(30, settings.getReminderMinutes());
		assertEquals("", settings.getDominoServer());
		assertEquals("", settings.getNotesCalendarDbFilePath());
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(settings.getProgramStartTime().getTimeInMillis());
		calendar.add(Calendar.DAY_OF_YEAR, -14);
		assertEquals(calendar, settings.getSyncStartDate());
		calendar.add(Calendar.DAY_OF_YEAR, 14);
		calendar.add(Calendar.MONTH, 3);
		assertEquals(calendar, settings.getSyncEndDate());
		assertArrayEquals(new int[] { 3 }, settings.getSyncAppointmentTypes());
		assertEquals("", settings.getProxyHost());
		assertEquals("", settings.getProxyPort());
		assertEquals("", settings.getProxyUserName());
		assertEquals("", settings.getProxyPassword());

		final PrivacySettings privacySettings = settings.getPrivacySettings();
		assertFalse(privacySettings.isTransferDescription());
		assertFalse(privacySettings.isTransferLocation());
		assertFalse(privacySettings.isTransferTitle());

	}

	/**
	 * Verifies that the parameter 'sync.types' is obtained as string array, because it may contain
	 * multiple values.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetSyncAppointmentTypes_ParsedAsStringArray() throws Exception {

		final File file = testdata.getFile(Constants.FILENAME_SYNC_PROPERTIES);
		doReturn(file).when(fileAccessor).getFile(Constants.FILENAME_SYNC_PROPERTIES);
		final Settings settings = loadSettings(false);

		assertArrayEquals(new int[] { 1, 2, 3 }, settings.getSyncAppointmentTypes());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad_UnparseableStartPeriodLength_ThrowsException() throws Exception {

		// #1 for creating the default config file
		loadSettings(true);

		final PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(settingsFile);
		propertiesConfiguration.setProperty("sync.start", "foo-d");
		propertiesConfiguration.save();

		// #2 for testing (no restart)
		final Settings settings = loadSettings(false);

		thrown.expect(ConfigurationException.class);
		thrown.expectMessage(String.format(Constants.MSG_UNABLE_TO_PARSE_DATE_SHIFT, "foo-d"));
		settings.getSyncStartDate();

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad_UnparseableEndPeriodLength_ThrowsException() throws Exception {

		// #1 for creating the default config file
		loadSettings(true);

		final PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(settingsFile);
		propertiesConfiguration.setProperty("sync.end", "foo-d");
		propertiesConfiguration.save();

		// #2 for testing (no restart)
		final Settings settings = loadSettings(false);

		thrown.expect(ConfigurationException.class);
		thrown.expectMessage(String.format(Constants.MSG_UNABLE_TO_PARSE_DATE_SHIFT, "foo-d"));
		settings.getSyncEndDate();

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad_UnparseableStartPeriodType_ThrowsException() throws Exception {

		// #1 for creating the default config file
		loadSettings(true);

		final PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(settingsFile);
		propertiesConfiguration.setProperty("sync.start", "12x");
		propertiesConfiguration.save();

		// #2 for testing (no restart)
		final Settings settings = loadSettings(false);

		thrown.expect(ConfigurationException.class);
		thrown.expectMessage(String.format(Constants.MSG_UNABLE_TO_PARSE_DATE_SHIFT, "12x"));
		settings.getSyncStartDate();

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad_UnparseableEndPeriodType_ThrowsException() throws Exception {

		// #1 for creating the default config file
		loadSettings(true);

		final PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(settingsFile);
		propertiesConfiguration.setProperty("sync.end", "12x");
		propertiesConfiguration.save();

		// #2 for testing (no restart)
		final Settings settings = loadSettings(false);

		thrown.expect(ConfigurationException.class);
		thrown.expectMessage(String.format(Constants.MSG_UNABLE_TO_PARSE_DATE_SHIFT, "12x"));
		settings.getSyncEndDate();

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveSyncLastDateTime() throws Exception {

		final Settings settings = loadSettings(true);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(1234567890);

		settings.setSyncLastDateTime(calendar);

		settings.saveLastSyncDateTime();

		final String actual = FileUtils.readFileToString(lastSyncDateFile);
		assertEquals("1234567890" + System.getProperty("line.separator"), actual);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad_NoSyncLastDateTimeFile_SyncLastDateTimeIs0() throws Exception {

		lastSyncDateFile.delete();

		// #1 for creating the default config file
		loadSettings(true);
		// #2 for testing (no restart)
		final Settings settings = loadSettings(false);

		assertEquals(0, settings.getSyncLastDateTime().getTimeInMillis());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad_SyncLastDateTimeFileIsEmpty_SyncLastDateTimeIs0() throws Exception {

		FileUtils.writeStringToFile(lastSyncDateFile, " ");

		// #1 for creating the default config file
		loadSettings(true);
		// #2 for testing (no restart)
		final Settings settings = loadSettings(false);

		assertEquals(0, settings.getSyncLastDateTime().getTimeInMillis());
	}


	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad_SyncLastDateTimeFileIsFilled() throws Exception {

		FileUtils.writeStringToFile(lastSyncDateFile, "1287");

		// #1 for creating the default config file
		loadSettings(true);
		// #2 for testing (no restart)
		final Settings settings = loadSettings(false);

		assertEquals(1287, settings.getSyncLastDateTime().getTimeInMillis());
	}

	private void verifyAllParametersAreSetWithDefaults() throws ConfigurationException {

		final PropertiesConfiguration configuration = new PropertiesConfiguration();
		configuration.load(settingsFile);

		for (final ConfigurationParameter parameter : ConfigurationParameter.values()) {
			final String key = parameter.getKey();
			assertTrue(configuration.containsKey(key));
			assertEquals(parameter.getDefaultValue(), configuration.getString(key));
		}
	}

	private Settings loadSettings(final boolean shouldExit) throws IOException, ConfigurationException {
		final Settings settings = new Settings(fileAccessor, log, notesHelper);
		final boolean restart = settings.load();
		assertEquals("The program's exit behaviour was incorect.", Boolean.valueOf(shouldExit), Boolean.valueOf(restart));
		return settings;
	}
}
