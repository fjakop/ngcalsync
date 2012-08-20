package de.jakop.ngcalsync.application;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.jakop.ngcalsync.filter.ICalendarEventFilter;
import de.jakop.ngcalsync.google.GoogleCalendarDaoFactory;
import de.jakop.ngcalsync.google.IGoogleCalendarDAO;
import de.jakop.ngcalsync.notes.INotesCalendarDAO;
import de.jakop.ngcalsync.notes.NotesCalendarDaoFactory;
import de.jakop.ngcalsync.obfuscator.ICalendarEventObfuscator;
import de.jakop.ngcalsync.service.SyncService;
import de.jakop.ngcalsync.settings.PrivacySettings;
import de.jakop.ngcalsync.settings.Settings;

/**
 * 
 * @author fjakop
 *
 */
public class ApplicationTest {

	/** */
	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Mock
	private Settings settings;
	@Mock
	private SyncService service;
	@Mock
	private NotesCalendarDaoFactory notesCalendarDaoFactory;
	@Mock
	private GoogleCalendarDaoFactory googleCalendarDaoFactory;
	@Mock
	private INotesCalendarDAO notesCalendarDao;
	@Mock
	private IGoogleCalendarDAO googleCalendarDao;


	/** */
	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);

		doReturn(notesCalendarDao).when(notesCalendarDaoFactory).createNotesCalendarDao(settings);
		doReturn(googleCalendarDao).when(googleCalendarDaoFactory).createGoogleCalendarDao(settings);
	}

	/** 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSettingsAreNull_ThrowException() throws Exception {
		expected.expect(NullPointerException.class);
		new Application(null, null, null, null);
	}

	/** 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testServiceIsNull_ThrowException() throws Exception {
		expected.expect(NullPointerException.class);
		new Application(mock(Settings.class), null, null, null);
	}

	/** 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotesCalendarDaoFactoryIsNull_ThrowException() throws Exception {
		expected.expect(NullPointerException.class);
		new Application(mock(Settings.class), service, null, null);
	}

	/** 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGoogleCalendarDaoFactoryIsNull_ThrowException() throws Exception {
		expected.expect(NullPointerException.class);
		new Application(mock(Settings.class), service, notesCalendarDaoFactory, null);
	}



	/** 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSynchronize() throws Exception {
		final Application application = new Application(settings, service, notesCalendarDaoFactory, googleCalendarDaoFactory);
		doReturn(mock(PrivacySettings.class)).when(settings).getPrivacySettings();

		application.synchronize();

		verify(service, times(1)).executeSync(Matchers.eq(notesCalendarDao), Matchers.eq(googleCalendarDao), (ICalendarEventFilter[]) Matchers.any(),
				(ICalendarEventObfuscator[]) Matchers.any(), Matchers.eq(settings));
	}

	/** 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReloadSettings_Success() throws Exception {
		final Application application = new Application(settings, service, notesCalendarDaoFactory, googleCalendarDaoFactory);

		application.reloadSettings();

		verify(settings, times(1)).load();
		verifyNoMoreInteractions(settings);
	}

	/** 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReloadSettings_ThrowsConfigurationException_ThrowsRuntimeException() throws Exception {
		final Application application = new Application(settings, service, notesCalendarDaoFactory, googleCalendarDaoFactory);

		doThrow(new ConfigurationException()).when(settings).load();

		expected.expect(RuntimeException.class);

		application.reloadSettings();

		verify(settings, times(1)).load();
		verifyNoMoreInteractions(settings);
	}

	/** 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReloadSettings_ThrowsIOException_ThrowsRuntimeException() throws Exception {
		final Application application = new Application(settings, service, notesCalendarDaoFactory, googleCalendarDaoFactory);

		doThrow(new IOException()).when(settings).load();

		expected.expect(RuntimeException.class);

		application.reloadSettings();

		verify(settings, times(1)).load();
		verifyNoMoreInteractions(settings);
	}

}
