package de.jakop.ngcalsync.google;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.api.client.util.DateTime;

import de.jakop.ngcalsync.SynchronisationException;
import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.google.GoogleCalendarDAOTest.InsertTest;
import de.jakop.ngcalsync.settings.Settings;

/**
 * This test class uses extensive mocking, therefore it is split into several subclasses which run
 * as a test suite. I decided not to use different source files because of convenience to 
 * MoreUnit-plugin's test detection and association under Eclipse.
 *  
 * @author fjakop
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ InsertTest.class })
public class GoogleCalendarDAOTest {

	private static void mockCalendarList(final com.google.api.services.calendar.Calendar.CalendarList calendarList) throws IOException {
		// CalendarList creates a list with all Google calendars
		final com.google.api.services.calendar.Calendar.CalendarList.List list = mock(com.google.api.services.calendar.Calendar.CalendarList.List.class);
		doReturn(list).when(calendarList).list();

		final com.google.api.services.calendar.model.CalendarList modelCalendarList = new com.google.api.services.calendar.model.CalendarList();
		doReturn(modelCalendarList).when(list).execute();

		final List<com.google.api.services.calendar.model.CalendarListEntry> entries = new ArrayList<com.google.api.services.calendar.model.CalendarListEntry>();
		modelCalendarList.setItems(entries);

		final com.google.api.services.calendar.model.CalendarListEntry calendarListEntry = new com.google.api.services.calendar.model.CalendarListEntry();
		entries.add(calendarListEntry);

		calendarListEntry.setSummary("mycal");
		calendarListEntry.setId("mycal-id");
	}

	private static void mockCalendars(final com.google.api.services.calendar.Calendar.Calendars calendars) throws IOException {
		// Calendars create Get
		final com.google.api.services.calendar.Calendar.Calendars.Get get = mock(com.google.api.services.calendar.Calendar.Calendars.Get.class);
		doReturn(get).when(calendars).get(Matchers.eq("mycal-id"));

		// Get creates model.Calendar (necessary for the retrieval of the correct Google calendar)
		final com.google.api.services.calendar.model.Calendar modelCalendar = new com.google.api.services.calendar.model.Calendar();
		doReturn(modelCalendar).when(get).execute();
		modelCalendar.setId("myModelCal-id");
	}


	/**
	 * Tests the getEntries' logic
	 *  
	 * @author fjakop
	 */
	public static class GetEventsTest {

		/** */
		@Rule
		public ExpectedException expected = ExpectedException.none();

		private final Calendar now = Calendar.getInstance();
		private final Calendar after = Calendar.getInstance();

		private Settings settings;

		private com.google.api.services.calendar.Calendar calendar;

		/**
		 * @throws Exception  */
		@Before
		public void before() throws Exception {
			settings = mock(Settings.class);
			calendar = mock(com.google.api.services.calendar.Calendar.class);

			after.setTimeInMillis(now.getTimeInMillis() + 1);

			// Settings creates Calendar (service) 
			doReturn(calendar).when(settings).getGoogleCalendarService();
			doReturn("mycal").when(settings).getGoogleCalendarName();
			doReturn(now).when(settings).getSyncStartDate();
			doReturn(after).when(settings).getSyncEndDate();

			// Calendar (Service) creates 
			// --> CalendarList
			// --> Calendars
			// --> Events
			final com.google.api.services.calendar.Calendar.CalendarList calendarList = mock(com.google.api.services.calendar.Calendar.CalendarList.class);
			doReturn(calendarList).when(calendar).calendarList();

			final com.google.api.services.calendar.Calendar.Calendars calendars = mock(com.google.api.services.calendar.Calendar.Calendars.class);
			doReturn(calendars).when(calendar).calendars();

			final com.google.api.services.calendar.Calendar.Events events = mock(com.google.api.services.calendar.Calendar.Events.class);
			doReturn(events).when(calendar).events();

		}

		/**
		 * 
		 * @throws Exception
		 */
		@Test
		public void testGetEvents_NoMatches_Success() throws Exception {

			mockCalendarList(calendar.calendarList());
			mockCalendars(calendar.calendars());
			mockEvents(calendar.events(), now, after);

			final GoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			final List<CalendarEvent> events = dao.getEvents(null);

			assertEquals(0, events.size());

		}

		/**
		 * 
		 * @throws Exception
		 */
		@Test
		public void testGetEvents_HasMatches_Success() throws Exception {

			mockCalendarList(calendar.calendarList());
			mockCalendars(calendar.calendars());
			final com.google.api.services.calendar.model.Events modelEvents = mockEvents(calendar.events(), now, after);

			final ArrayList<com.google.api.services.calendar.model.Event> items = new ArrayList<com.google.api.services.calendar.model.Event>();
			modelEvents.setItems(items);

			final com.google.api.services.calendar.model.Event event1 = new com.google.api.services.calendar.model.Event();
			items.add(event1);

			// create an event to be converted
			final CalendarEvent myEvent = mock(CalendarEvent.class);
			doReturn(now).when(myEvent).getStartDateTime();
			doReturn(after).when(myEvent).getEndDateTime();
			doReturn(after).when(myEvent).getLastUpdated();
			doReturn("myTitle").when(myEvent).getTitle();
			doReturn("myContent").when(myEvent).getContent();
			doReturn("myLocation").when(myEvent).getLocation();

			final com.google.api.services.calendar.model.EventDateTime start = new com.google.api.services.calendar.model.EventDateTime();
			final com.google.api.services.calendar.model.EventDateTime end = new com.google.api.services.calendar.model.EventDateTime();

			start.setDateTime(new DateTime(myEvent.getStartDateTime().getTime()));
			end.setDateTime(new DateTime(myEvent.getEndDateTime().getTime()));

			event1.setStart(start);
			event1.setEnd(end);
			event1.setSummary(myEvent.getTitle());
			event1.setDescription(myEvent.getContent());
			event1.setLocation(myEvent.getLocation());
			event1.setId(myEvent.getId());
			event1.setUpdated(new DateTime(myEvent.getLastUpdated().getTime()));

			final GoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			final List<CalendarEvent> events = dao.getEvents(null);

			assertEquals(1, events.size());

			assertEquals(myEvent.getTitle(), event1.getSummary());
			assertEquals(myEvent.getContent(), event1.getDescription());
			assertEquals(myEvent.getLocation(), event1.getLocation());
			assertEquals(myEvent.getId(), event1.getId());
			assertEquals(new DateTime(myEvent.getLastUpdated().getTime()), event1.getUpdated());

		}

		private static com.google.api.services.calendar.model.Events mockEvents(final com.google.api.services.calendar.Calendar.Events events, final Calendar start, final Calendar end)
				throws IOException {
			final com.google.api.services.calendar.Calendar.Events.List list = mock(com.google.api.services.calendar.Calendar.Events.List.class);
			doAnswer(new Answer<com.google.api.services.calendar.Calendar.Events.List>() {
				@Override
				public com.google.api.services.calendar.Calendar.Events.List answer(final InvocationOnMock invocation) throws Throwable {
					final Object[] arguments = invocation.getArguments();
					assertTrue(arguments.length == 1);
					assertEquals("myModelCal-id", arguments[0]);

					return list;
				}
			}).when(events).list(Matchers.eq("myModelCal-id"));

			doReturn(list).when(list).setTimeMin(Matchers.eq(new DateTime(start.getTime(), start.getTimeZone()).toStringRfc3339()));
			doReturn(list).when(list).setTimeMax(Matchers.eq(new DateTime(end.getTime(), end.getTimeZone()).toStringRfc3339()));
			doReturn(list).when(list).setMaxResults(Integer.valueOf(65535));
			doReturn(list).when(list).setOrderBy(Matchers.eq("starttime"));
			doReturn(list).when(list).setSingleEvents(Boolean.TRUE);

			final com.google.api.services.calendar.model.Events modelEvents = new com.google.api.services.calendar.model.Events();
			doReturn(modelEvents).when(list).execute();

			return modelEvents;
		}
	}


	/**
	 * Tests the insert's logic
	 *  
	 * @author fjakop
	 */
	public static class InsertTest {

		/** */
		@Rule
		public ExpectedException expected = ExpectedException.none();

		private final Calendar now = Calendar.getInstance();
		private final Calendar after = Calendar.getInstance();

		private CalendarEvent myEvent;
		private Settings settings;

		private com.google.api.services.calendar.Calendar calendar;


		/** */
		@Before
		public void before() {
			settings = mock(Settings.class);
			calendar = mock(com.google.api.services.calendar.Calendar.class);
			myEvent = mock(CalendarEvent.class);
			initMocks(now, after, myEvent, settings, calendar);
		}

		private static void initMocks(final Calendar now, final Calendar after, final CalendarEvent myEvent, final Settings settings,
				final com.google.api.services.calendar.Calendar calendar) {
			after.setTimeInMillis(now.getTimeInMillis() + 1);

			// create an event to be inserted
			doReturn(now).when(myEvent).getStartDateTime();
			doReturn(after).when(myEvent).getEndDateTime();
			doReturn("myTitle").when(myEvent).getTitle();
			doReturn("myContent").when(myEvent).getContent();
			doReturn("myLocation").when(myEvent).getLocation();

			doReturn(new Integer(15)).when(settings).getReminderMinutes();
			doReturn("mycal").when(settings).getGoogleCalendarName();

			// Settings creates Calendar (service) 
			doReturn(calendar).when(settings).getGoogleCalendarService();

			// Calendar (Service) creates 
			// --> CalendarList
			// --> Calendars
			// --> Events
			final com.google.api.services.calendar.Calendar.CalendarList calendarList = mock(com.google.api.services.calendar.Calendar.CalendarList.class);
			doReturn(calendarList).when(calendar).calendarList();

			final com.google.api.services.calendar.Calendar.Calendars calendars = mock(com.google.api.services.calendar.Calendar.Calendars.class);
			doReturn(calendars).when(calendar).calendars();

			final com.google.api.services.calendar.Calendar.Events events = mock(com.google.api.services.calendar.Calendar.Events.class);
			doReturn(events).when(calendar).events();

		}

		/**
		 * 
		 * @throws Exception
		 */
		@Test
		public void testInsert_Success() throws Exception {

			mockCalendarList(calendar.calendarList());
			mockCalendars(calendar.calendars());
			mockEvents(calendar.events());

			final GoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			final String id = dao.insert(myEvent);

			assertEquals("myModelEvent-id", id);
		}

		/**
		 * 
		 * @throws Exception
		 */
		@Test
		public void testInsert_InsertThrowsIOException() throws Exception {


			// Calendar (Service) creates 
			// --> CalendarList
			// --> Calendars
			// --> Events
			final com.google.api.services.calendar.Calendar.CalendarList calendarList = mock(com.google.api.services.calendar.Calendar.CalendarList.class);
			doReturn(calendarList).when(calendar).calendarList();

			final com.google.api.services.calendar.Calendar.Calendars calendars = mock(com.google.api.services.calendar.Calendar.Calendars.class);
			doReturn(calendars).when(calendar).calendars();

			final com.google.api.services.calendar.Calendar.Events events = mock(com.google.api.services.calendar.Calendar.Events.class);
			doReturn(events).when(calendar).events();

			mockCalendarList(calendar.calendarList());
			mockCalendars(calendar.calendars());
			mockEvents_InsertThrowsIOException(calendar.events());

			final GoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			expected.expect(SynchronisationException.class);
			expected.expectMessage("IOException from insert()");
			dao.insert(myEvent);

		}

		/**
		 * 
		 * @throws Exception
		 */
		@Test
		public void testInsert_Insert_ExecuteThrowsIOException() throws Exception {

			mockCalendarList(calendar.calendarList());
			mockCalendars(calendar.calendars());
			mockEvents_InsertExecuteThrowsIOException(calendar.events());

			final GoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			expected.expect(SynchronisationException.class);
			expected.expectMessage("IOException from insert.execute()");
			dao.insert(myEvent);

		}

		/**
		 * 
		 * @throws Exception
		 */
		@Test
		public void testInsert_CalendarList_List_ThrowsIOException() throws Exception {

			mockCalendarList_listThrowsIOException(calendar.calendarList());
			mockCalendars(calendar.calendars());
			mockEvents(calendar.events());

			final GoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			expected.expect(SynchronisationException.class);
			expected.expectMessage("IOException from list()");
			dao.insert(myEvent);

		}

		/**
		 * 
		 * @throws Exception
		 */
		@Test
		public void testInsert_CalendarList_List_CalendarIsNull() throws Exception {

			mockCalendarList(calendar.calendarList());
			mockCalendars_GetExecuteReturnsNull(calendar.calendars());
			mockEvents(calendar.events());

			final GoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			expected.expect(SynchronisationException.class);
			expected.expectMessage("Google calendar 'mycal' does not exist.");
			dao.insert(myEvent);

		}

		private static void mockEvents(final com.google.api.services.calendar.Calendar.Events events) throws IOException {
			final com.google.api.services.calendar.Calendar.Events.Insert insert = mock(com.google.api.services.calendar.Calendar.Events.Insert.class);
			doReturn(insert).when(events).insert(Matchers.eq("myModelCal-id"), Matchers.any(com.google.api.services.calendar.model.Event.class));

			final com.google.api.services.calendar.model.Event modelEvent = new com.google.api.services.calendar.model.Event();
			doReturn(modelEvent).when(insert).execute();
			modelEvent.setId("myModelEvent-id");
		}

		private static void mockEvents_InsertThrowsIOException(final com.google.api.services.calendar.Calendar.Events events) throws IOException {
			doThrow(new IOException("IOException from insert()")).when(events).insert(Matchers.eq("myModelCal-id"), Matchers.any(com.google.api.services.calendar.model.Event.class));
		}

		private static void mockEvents_InsertExecuteThrowsIOException(final com.google.api.services.calendar.Calendar.Events events) throws IOException {
			final com.google.api.services.calendar.Calendar.Events.Insert insert = mock(com.google.api.services.calendar.Calendar.Events.Insert.class);
			doReturn(insert).when(events).insert(Matchers.eq("myModelCal-id"), Matchers.any(com.google.api.services.calendar.model.Event.class));
			doThrow(new IOException("IOException from insert.execute()")).when(insert).execute();
		}

		private void mockCalendars_GetExecuteReturnsNull(final com.google.api.services.calendar.Calendar.Calendars calendars) throws IOException {
			// Calendars create Get
			final com.google.api.services.calendar.Calendar.Calendars.Get get = mock(com.google.api.services.calendar.Calendar.Calendars.Get.class);
			doReturn(get).when(calendars).get(Matchers.eq("mycal-id"));

			// Get creates model.Calendar but does not find a calendar with given id
			doReturn(null).when(get).execute();
		}


		private static void mockCalendarList_listThrowsIOException(final com.google.api.services.calendar.Calendar.CalendarList calendarList) throws IOException {
			// CalendarList creates a list with all Google calendars
			doThrow(new IOException("IOException from list()")).when(calendarList).list();
		}

	}


	private static void assertEventDataIsUpdated(final CalendarEvent myEvent, final com.google.api.services.calendar.model.Event googleEvent) {
		assertEquals(myEvent.getContent(), googleEvent.getDescription());
		assertEquals(myEvent.getId(), googleEvent.getId());
		assertEquals(myEvent.getLocation(), googleEvent.getLocation());
		assertEquals(myEvent.getStartDateTime().getTimeInMillis(), googleEvent.getStart().getDateTime().getValue());
		assertEquals(myEvent.getEndDateTime().getTimeInMillis(), googleEvent.getEnd().getDateTime().getValue());
		assertEquals(myEvent.getTitle(), googleEvent.getSummary());
	}
}
