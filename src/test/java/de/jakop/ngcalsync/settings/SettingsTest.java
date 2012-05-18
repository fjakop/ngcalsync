package de.jakop.ngcalsync.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.IExitStrategy;
import de.jakop.ngcalsync.settings.ISettingsFileAccessor;
import de.jakop.ngcalsync.settings.Settings;


/**
 * 
 * @author fjakop
 *
 */
public class SettingsTest {

	@Mock
	private ISettingsFileAccessor settingsFileAccessor;
	@Mock
	private IExitStrategy exitStrategy;
	@Mock
	private Log log;

	private File settingsFile;

	/** */
	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);

		// get a temp settings file
		settingsFile = File.createTempFile(getClass().getName(), null);
		doReturn(settingsFile).when(settingsFileAccessor).getFile(Matchers.eq(Constants.FILENAME_SYNC_PROPERTIES));
		doReturn(File.createTempFile(getClass().getName(), null)).when(settingsFileAccessor).getFile(Matchers.eq(Constants.FILENAME_LAST_SYNC_TIME));

		// do not actually exit the program, but do not run further either
		doThrow(new RuntimeException("#1")).when(exitStrategy).exit(0);
	}

	/**
	 * Verifies that settings file is created preset with defaults on first start.
	 * @throws Exception 
	 */
	@Test
	public void testFirstStart_FileIsCreated_ProgramExits() throws Exception {

		// delete the settings file to simulate a first start
		settingsFile.delete();

		loadSettings();

		// verify message
		verify(log, times(1)).info(String.format(Constants.MSG_FIRST_START, settingsFile.getAbsolutePath()));

		// verify exit
		verify(exitStrategy, times(1)).exit(0);
		verifyNoMoreInteractions(exitStrategy);

		// verify that all parameters are set with their defaults
		verifyAllParametersAreSetWithDefaults();
	}

	/**
	 * Verifies that missing keys are automatically created and preset with defaults.
	 * @throws Exception 
	 */
	@Test
	public void testParameterAdded_FileIsUpgraded_ProgramExits() throws Exception {

		loadSettings();

		// verify message (build parameter keys array from enum, so it has not to be changed each time we add a parameter)
		final StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (final Settings.Parameter parameter : Settings.Parameter.values()) {
			builder.append(parameter.getKey()).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("}");

		verify(log, times(1)).info(String.format(Constants.MSG_CONFIGURATION_UPGRADED, settingsFile.getAbsolutePath(), builder.toString()));

		// verify exit
		verify(exitStrategy, times(1)).exit(0);
		verifyNoMoreInteractions(exitStrategy);

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
		loadSettings();

		// load again, no exit and no message happens
		log = mock(Log.class);
		exitStrategy = mock(IExitStrategy.class);
		new Settings(settingsFileAccessor, exitStrategy, log).load();

		verifyNoMoreInteractions(log);
		verifyNoMoreInteractions(exitStrategy);

		// verify that all parameters are set with their defaults
		verifyAllParametersAreSetWithDefaults();
	}

	private void verifyAllParametersAreSetWithDefaults() throws ConfigurationException {

		final PropertiesConfiguration configuration = new PropertiesConfiguration();
		configuration.load(settingsFile);

		for (final Settings.Parameter parameter : Settings.Parameter.values()) {
			final String key = parameter.getKey();
			assertTrue(configuration.containsKey(key));
			assertEquals(parameter.getDefaultValue(), configuration.getString(key));
		}
	}

	private Settings loadSettings() throws IOException, ConfigurationException {
		final Settings settings = new Settings(settingsFileAccessor, exitStrategy, log);
		try {
			settings.load();
			fail("The exit strategy was supposed to throw an exception, but did not.");
		} catch (final RuntimeException e) {
			// did we exit with "our" exception?
			assertEquals("#1", e.getMessage());
		}
		return settings;
	}
}
