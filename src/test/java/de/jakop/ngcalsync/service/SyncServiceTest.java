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
package de.jakop.ngcalsync.service;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;
import de.jakop.ngcalsync.exception.SynchronisationException;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;
import de.jakop.ngcalsync.google.IGoogleCalendarDAO;
import de.jakop.ngcalsync.notes.INotesCalendarDAO;
import de.jakop.ngcalsync.obfuscator.ICalendarEventObfuscator;
import de.jakop.ngcalsync.settings.NotesHelper;
import de.jakop.ngcalsync.settings.Settings;
import de.jakop.ngcalsync.util.file.IFileAccessor;

/**
 *
 * @author fjakop
 *
 */
@SuppressWarnings("nls")
public class SyncServiceTest {

	/** expected exception */
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private INotesCalendarDAO notesDAO;

	@Mock
	private IGoogleCalendarDAO googleDAO;

	private Settings settings;
	private final Calendar now = Calendar.getInstance();
	private final Calendar after = Calendar.getInstance();

	/**
	 *
	 */
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		settings = new Settings(mock(IFileAccessor.class), mock(Log.class), mock(NotesHelper.class));
		after.setTimeInMillis(now.getTimeInMillis() + 1);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_NotesDAOIsNull_NotAllowed() throws Exception {
		thrown.expect(NullPointerException.class);
		new SyncService().executeSync(null, null, null, null, null);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_GoogleDAOIsNull_NotAllowed() throws Exception {
		thrown.expect(NullPointerException.class);
		new SyncService().executeSync(notesDAO, null, null, null, null);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_SettingsIsNull_NotAllowed() throws Exception {
		thrown.expect(NullPointerException.class);
		new SyncService().executeSync(notesDAO, googleDAO, null, null, null);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_FiltersArePassedToDAOs() throws Exception {
		final ICalendarEventFilter filter1 = mock(ICalendarEventFilter.class);
		final ICalendarEventFilter filter2 = mock(ICalendarEventFilter.class);
		final ICalendarEventFilter[] filters = new ICalendarEventFilter[] { filter1, filter2 };

		new SyncService().executeSync(notesDAO, googleDAO, filters, null, settings);

		verify(notesDAO, times(1)).getEntries(filters);
		verify(googleDAO, times(1)).getEvents(filters);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_AddEventsToGoogle_ObfuscatorsAreUsed() throws Exception {
		final ICalendarEventObfuscator obfuscator1 = mock(ICalendarEventObfuscator.class);
		final ICalendarEventObfuscator obfuscator2 = mock(ICalendarEventObfuscator.class);

		final CalendarEvent event1 = mock(CalendarEvent.class);
		final CalendarEvent event2 = mock(CalendarEvent.class);
		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(events).when(notesDAO).getEntries(null);
		doReturn(new ArrayList<CalendarEvent>()).when(googleDAO).getEvents(null);

		new SyncService().executeSync(notesDAO, googleDAO, null, new ICalendarEventObfuscator[] { obfuscator1, obfuscator2 }, settings);

		verify(notesDAO, times(1)).getEntries(null);
		verify(googleDAO, times(1)).getEvents(null);

		verify(obfuscator1, times(1)).obfuscate(event1);
		verify(obfuscator1, times(1)).obfuscate(event2);
		verify(obfuscator2, times(1)).obfuscate(event1);
		verify(obfuscator2, times(1)).obfuscate(event2);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_DuplicateEventsMatch_NotAllowed() throws Exception {

		final CalendarEvent event1 = new CalendarEvent();
		event1.setStartDateTime(now);
		event1.setEndDateTime(now);

		final CalendarEvent event2 = new CalendarEvent();
		event2.setStartDateTime(now);
		event2.setEndDateTime(now);

		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(events).when(notesDAO).getEntries(null);
		doReturn(events).when(googleDAO).getEvents(null);

		thrown.expect(SynchronisationException.class);
		new SyncService().executeSync(notesDAO, googleDAO, null, null, settings);

		verifyNoMoreInteractions(notesDAO);
		verifyNoMoreInteractions(googleDAO);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_UpdateEventsToGoogle_ObfuscatorsAreUsed() throws Exception {
		final ICalendarEventObfuscator obfuscator1 = mock(ICalendarEventObfuscator.class);
		final ICalendarEventObfuscator obfuscator2 = mock(ICalendarEventObfuscator.class);

		final CalendarEvent event1 = new CalendarEvent();
		event1.setStartDateTime(now);
		event1.setEndDateTime(now);

		final CalendarEvent event2 = new CalendarEvent();
		final Calendar later = Calendar.getInstance();
		later.setTimeInMillis(now.getTimeInMillis() + 1);
		event2.setStartDateTime(later);
		event2.setEndDateTime(later);

		final Calendar updated = Calendar.getInstance();
		updated.setTimeInMillis(1000000001);
		event1.setLastUpdated(updated);

		final Calendar lastSyncTime = now;
		lastSyncTime.setTimeInMillis(1000000000);
		event2.setLastUpdated(lastSyncTime);
		settings.setSyncLastDateTime(lastSyncTime);

		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(events).when(notesDAO).getEntries(null);
		doReturn(events).when(googleDAO).getEvents(null);

		new SyncService().executeSync(notesDAO, googleDAO, null, new ICalendarEventObfuscator[] { obfuscator1, obfuscator2 }, settings);

		verify(notesDAO, times(1)).getEntries(null);
		verify(googleDAO, times(1)).getEvents(null);

		verify(obfuscator1, times(1)).obfuscate(event1);
		verify(obfuscator1, times(0)).obfuscate(event2);
		verify(obfuscator2, times(1)).obfuscate(event1);
		verify(obfuscator2, times(0)).obfuscate(event2);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_AddEventsToGoogle() throws Exception {

		final CalendarEvent event1 = mock(CalendarEvent.class);
		final CalendarEvent event2 = mock(CalendarEvent.class);
		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(events).when(notesDAO).getEntries(null);
		doReturn(new ArrayList<CalendarEvent>()).when(googleDAO).getEvents(null);

		new SyncService().executeSync(notesDAO, googleDAO, null, new ICalendarEventObfuscator[] {}, settings);

		verify(notesDAO, times(1)).getEntries(null);
		verify(googleDAO, times(1)).getEvents(null);

		verify(googleDAO, times(1)).insert(event1);
		verify(googleDAO, times(1)).insert(event2);

		verify(googleDAO, times(0)).update(Matchers.anyString(), (CalendarEvent) Matchers.anyObject());
		verify(googleDAO, times(0)).delete(Matchers.anyString());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_UpdateEventsToGoogle() throws Exception {
		settings.setSyncLastDateTime(now);

		final CalendarEvent event1 = mock(CalendarEvent.class);
		final CalendarEvent event2 = mock(CalendarEvent.class);

		doReturn(now).when(event1).getStartDateTime();
		doReturn(now).when(event1).getEndDateTime();
		doReturn(EventType.MEETING).when(event1).getEventType();


		doReturn(after).when(event1).getLastUpdated();

		doReturn(after).when(event2).getStartDateTime();
		doReturn(after).when(event2).getEndDateTime();
		doReturn(EventType.NORMAL_EVENT).when(event2).getEventType();
		doReturn(now).when(event2).getLastUpdated();

		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(events).when(notesDAO).getEntries(null);
		doReturn(events).when(googleDAO).getEvents(null);

		new SyncService().executeSync(notesDAO, googleDAO, null, new ICalendarEventObfuscator[] {}, settings);

		verify(notesDAO, times(1)).getEntries(null);
		verify(googleDAO, times(1)).getEvents(null);

		verify(googleDAO, times(1)).update(Matchers.anyString(), Matchers.eq(event1));

		verify(googleDAO, times(0)).insert((CalendarEvent) Matchers.anyObject());
		verify(googleDAO, times(0)).delete(Matchers.anyString());

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_DeleteEventsFromGoogle() throws Exception {
		final CalendarEvent event1 = mock(CalendarEvent.class);
		final CalendarEvent event2 = mock(CalendarEvent.class);
		doReturn("id1").when(event1).getId();
		doReturn("id2").when(event2).getId();

		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(new ArrayList<CalendarEvent>()).when(notesDAO).getEntries(null);
		doReturn(events).when(googleDAO).getEvents(null);

		new SyncService().executeSync(notesDAO, googleDAO, null, new ICalendarEventObfuscator[] {}, settings);

		verify(notesDAO, times(1)).getEntries(null);
		verify(googleDAO, times(1)).getEvents(null);

		verify(googleDAO, times(1)).delete(event1.getId());
		verify(googleDAO, times(1)).delete(event2.getId());

		verify(googleDAO, times(0)).insert((CalendarEvent) Matchers.anyObject());
		verify(googleDAO, times(0)).update(Matchers.anyString(), (CalendarEvent) Matchers.anyObject());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecuteSync_NoChanges() throws Exception {
		settings.setSyncLastDateTime(now);

		final CalendarEvent event1 = mock(CalendarEvent.class);
		final CalendarEvent event2 = mock(CalendarEvent.class);

		doReturn(now).when(event1).getStartDateTime();
		doReturn(now).when(event1).getEndDateTime();
		doReturn(EventType.MEETING).when(event1).getEventType();
		doReturn(now).when(event1).getLastUpdated();

		doReturn(after).when(event2).getStartDateTime();
		doReturn(after).when(event2).getEndDateTime();
		doReturn(EventType.NORMAL_EVENT).when(event2).getEventType();
		doReturn(now).when(event2).getLastUpdated();

		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(events).when(notesDAO).getEntries(null);
		doReturn(events).when(googleDAO).getEvents(null);

		new SyncService().executeSync(notesDAO, googleDAO, null, new ICalendarEventObfuscator[] {}, settings);

		verify(notesDAO, times(1)).getEntries(null);
		verify(googleDAO, times(1)).getEvents(null);

		verifyNoMoreInteractions(googleDAO);

	}

}
