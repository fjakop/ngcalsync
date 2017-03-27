/**
 * Copyright © 2012, Frank Jakop
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
