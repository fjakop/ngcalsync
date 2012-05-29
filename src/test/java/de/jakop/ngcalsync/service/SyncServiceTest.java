package de.jakop.ngcalsync.service;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.jakop.ngcalsync.SynchronisationException;
import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;
import de.jakop.ngcalsync.google.GoogleCalendarDAO;
import de.jakop.ngcalsync.notes.NotesCalendarDAO;
import de.jakop.ngcalsync.obfuscator.ICalendarEventObfuscator;
import de.jakop.ngcalsync.settings.Settings;

/**
 * 
 * @author fjakop
 *
 */
public class SyncServiceTest {

	@Mock
	private NotesCalendarDAO notesDAO;

	@Mock
	private GoogleCalendarDAO googleDAO;

	private Settings settings;

	/**
	 * 
	 */
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		settings = new Settings();
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testExecuteSync_NotesDAOIsNull_NotAllowed() throws Exception {
		new SyncService().executeSync(null, null, null, null, null);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testExecuteSync_GoogleDAOIsNull_NotAllowed() throws Exception {
		new SyncService().executeSync(notesDAO, null, null, null, null);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testExecuteSync_SettingsIsNull_NotAllowed() throws Exception {
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
		verify(googleDAO, times(1)).getEntries(filters);
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
		doReturn(new ArrayList<CalendarEvent>()).when(googleDAO).getEntries(null);

		new SyncService().executeSync(notesDAO, googleDAO, null, new ICalendarEventObfuscator[] { obfuscator1, obfuscator2 }, settings);

		verify(notesDAO, times(1)).getEntries(null);
		verify(googleDAO, times(1)).getEntries(null);

		verify(obfuscator1, times(1)).obfuscate(event1);
		verify(obfuscator1, times(1)).obfuscate(event2);
		verify(obfuscator2, times(1)).obfuscate(event1);
		verify(obfuscator2, times(1)).obfuscate(event2);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test(expected = SynchronisationException.class)
	public void testExecuteSync_DuplicateEventsMatch_NotAllowed() throws Exception {

		final CalendarEvent event1 = new CalendarEvent();
		final Calendar now = Calendar.getInstance();
		event1.setStartDateTime(now);
		event1.setEndDateTime(now);

		final CalendarEvent event2 = new CalendarEvent();
		event2.setStartDateTime(now);
		event2.setEndDateTime(now);

		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(events).when(notesDAO).getEntries(null);
		doReturn(events).when(googleDAO).getEntries(null);

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
		final Calendar now = Calendar.getInstance();
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
		settings.setLastSyncDateTime(lastSyncTime);

		final List<CalendarEvent> events = Arrays.asList(event1, event2);

		doReturn(events).when(notesDAO).getEntries(null);
		doReturn(events).when(googleDAO).getEntries(null);

		new SyncService().executeSync(notesDAO, googleDAO, null, new ICalendarEventObfuscator[] { obfuscator1, obfuscator2 }, settings);

		verify(notesDAO, times(1)).getEntries(null);
		verify(googleDAO, times(1)).getEntries(null);

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
	@Ignore
	public void testExecuteSync_AddEventsToGoogle() throws Exception {
		fail("Not implemented yet");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testExecuteSync_UpdateEventsToGoogle() throws Exception {
		fail("Not implemented yet");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testExecuteSync_DeleteEventsFromGoogle() throws Exception {
		fail("Not implemented yet");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testExecuteSync_NoChanges() throws Exception {
		fail("Not implemented yet");
	}

}
