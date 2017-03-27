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
package de.jakop.ngcalsync.google;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
import com.google.api.services.calendar.Calendar.Events.Insert;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;
import de.jakop.ngcalsync.exception.SynchronisationException;
import de.jakop.ngcalsync.google.GoogleCalendarDAOTest.GetEventsTest;
import de.jakop.ngcalsync.google.GoogleCalendarDAOTest.InsertTest;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.settings.Settings;

/**
 * This test class uses intensive mocking, therefore it is split into several subclasses which run
 * as a test suite. I decided not to use different source files because of convenience to
 * MoreUnit-plugin's test detection and association under Eclipse.
 *
 * @author fjakop
 *
 */
@SuppressWarnings("nls")
@RunWith(Suite.class)
@SuiteClasses({ InsertTest.class, GetEventsTest.class })
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

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

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

			final com.google.api.services.calendar.model.EventDateTime start = new com.google.api.services.calendar.model.EventDateTime();
			final com.google.api.services.calendar.model.EventDateTime end = new com.google.api.services.calendar.model.EventDateTime();

			start.setDateTime(new DateTime(now.getTime()));
			end.setDateTime(new DateTime(after.getTime()));

			event1.setStart(start);
			event1.setEnd(end);
			event1.setSummary("myTitle");
			event1.setDescription("myContent");
			event1.setLocation("myLocation");
			event1.setId("myId");
			event1.setUpdated(new DateTime(after.getTime()));

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			final List<CalendarEvent> events = dao.getEvents(null);

			assertEquals(1, events.size());
			final CalendarEvent result = events.get(0);

			assertEquals(EventType.NORMAL_EVENT, result.getEventType());
			assertEquals("myTitle", result.getTitle());
			assertEquals("myContent", result.getContent());
			assertEquals("myLocation", result.getLocation());
			assertEquals("myId", result.getId());
			assertEquals(now, result.getStartDateTime());
			assertEquals(after, result.getEndDateTime());
			assertEquals(after, result.getLastUpdated());
			assertEquals(Boolean.FALSE, Boolean.valueOf(result.isAllDay()));
			assertEquals(Boolean.FALSE, Boolean.valueOf(result.isPrivate()));

		}

		/**
		 *
		 * @throws Exception
		 */
		@Test
		public void testGetEvents_HasMatches_AllDayEvent_Success() throws Exception {

			mockCalendarList(calendar.calendarList());
			mockCalendars(calendar.calendars());
			final com.google.api.services.calendar.model.Events modelEvents = mockEvents(calendar.events(), now, after);

			final ArrayList<com.google.api.services.calendar.model.Event> items = new ArrayList<com.google.api.services.calendar.model.Event>();
			modelEvents.setItems(items);

			final com.google.api.services.calendar.model.Event event1 = new com.google.api.services.calendar.model.Event();
			items.add(event1);

			final com.google.api.services.calendar.model.EventDateTime start = new com.google.api.services.calendar.model.EventDateTime();
			final com.google.api.services.calendar.model.EventDateTime end = new com.google.api.services.calendar.model.EventDateTime();

			start.setDateTime(new DateTime(now.getTime()));
			end.setDateTime(new DateTime(after.getTime()));

			event1.setStart(start);
			event1.setEnd(end);
			event1.setSummary("myTitle");
			event1.setDescription("myContent");
			event1.setLocation("myLocation");
			event1.setId("myId");
			event1.setUpdated(new DateTime(after.getTime()));

			final SimpleDateFormat dateFormatDateOnly = new SimpleDateFormat("yyyy-MM-dd");
			event1.getStart().setDateTime(null);
			event1.getStart().setDate(new DateTime(now.getTime()));
			event1.getEnd().setDate(new DateTime(after.getTime()));

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			final List<CalendarEvent> events = dao.getEvents(null);

			assertEquals(1, events.size());

			final CalendarEvent result = events.get(0);
			assertEquals(EventType.ALL_DAY_EVENT, result.getEventType());
			assertEquals("myTitle", result.getTitle());
			assertEquals("myContent", result.getContent());
			assertEquals("myLocation", result.getLocation());
			assertEquals("myId", result.getId());
			assertEquals(dateFormatDateOnly.format(now.getTime()), dateFormatDateOnly.format(result.getStartDateTime().getTime()));
			assertEquals(dateFormatDateOnly.format(after.getTime()), dateFormatDateOnly.format(result.getEndDateTime().getTime()));
			assertEquals(after, result.getLastUpdated());
			assertEquals(Boolean.TRUE, Boolean.valueOf(result.isAllDay()));
			assertEquals(Boolean.FALSE, Boolean.valueOf(result.isPrivate()));

		}

		/**
		 *
		 * @throws Exception
		 */
		@Test
		public void testGetEvents_HasMatches_Reminder_Success() throws Exception {

			mockCalendarList(calendar.calendarList());
			mockCalendars(calendar.calendars());
			final com.google.api.services.calendar.model.Events modelEvents = mockEvents(calendar.events(), now, after);

			final ArrayList<com.google.api.services.calendar.model.Event> items = new ArrayList<com.google.api.services.calendar.model.Event>();
			modelEvents.setItems(items);

			final com.google.api.services.calendar.model.Event event1 = new com.google.api.services.calendar.model.Event();
			items.add(event1);

			final com.google.api.services.calendar.model.EventDateTime start = new com.google.api.services.calendar.model.EventDateTime();

			start.setDateTime(new DateTime(now.getTime()));

			event1.setStart(start);
			event1.setEnd(start);
			event1.setSummary("myTitle");
			event1.setDescription("myContent");
			event1.setLocation("myLocation");
			event1.setId("myId");
			event1.setUpdated(new DateTime(after.getTime()));

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			final List<CalendarEvent> events = dao.getEvents(null);

			assertEquals(1, events.size());

			final CalendarEvent result = events.get(0);
			assertEquals(EventType.REMINDER, result.getEventType());
			assertEquals("myTitle", result.getTitle());
			assertEquals("myContent", result.getContent());
			assertEquals("myLocation", result.getLocation());
			assertEquals("myId", result.getId());
			assertEquals(now, result.getStartDateTime());
			assertEquals(now, result.getEndDateTime());
			assertEquals(after, result.getLastUpdated());
			assertEquals(Boolean.FALSE, Boolean.valueOf(result.isAllDay()));
			assertEquals(Boolean.FALSE, Boolean.valueOf(result.isPrivate()));

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

			doReturn(list).when(list).setTimeMin(Matchers.eq(new DateTime(start.getTime(), start.getTimeZone())));
			doReturn(list).when(list).setTimeMax(Matchers.eq(new DateTime(end.getTime(), end.getTimeZone())));
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
			after.setTimeInMillis(now.getTimeInMillis() + 1);

			// create an event to be inserted
			doReturn(now).when(myEvent).getStartDateTime();
			doReturn(after).when(myEvent).getEndDateTime();
			doReturn("myTitle").when(myEvent).getTitle();
			doReturn("myContent").when(myEvent).getContent();
			doReturn("myLocation").when(myEvent).getLocation();
			doReturn("myId").when(myEvent).getId();

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
			mockEvents_checkConversion(calendar.events(), myEvent);

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

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

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

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

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

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
			//			mockEvents_checkConversion(calendar.events());

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

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
			//			mockEvents_checkConversion(calendar.events());

			final IGoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

			expected.expect(SynchronisationException.class);
			expected.expectMessage(UserMessage.get().GOOGLE_CALENDAR_S_DOES_NOT_EXIST_CHECK_CONFIG("mycal"));
			dao.insert(myEvent);

		}

		private static void mockEvents_checkConversion(final com.google.api.services.calendar.Calendar.Events events, final CalendarEvent sourceEvent) throws IOException {
			final com.google.api.services.calendar.Calendar.Events.Insert insert = mock(com.google.api.services.calendar.Calendar.Events.Insert.class);
			doAnswer(new Answer<com.google.api.services.calendar.Calendar.Events.Insert>() {
				@Override
				public Insert answer(final InvocationOnMock invocation) throws Throwable {
					assertEquals(2, invocation.getArguments().length);
					final com.google.api.services.calendar.model.Event event = (com.google.api.services.calendar.model.Event) invocation.getArguments()[1];
					assertEquals(sourceEvent.getContent(), event.getDescription());
					assertEquals(sourceEvent.getEndDateTime().getTimeInMillis(), event.getEnd().getDateTime().getValue());
					assertEquals(sourceEvent.getLocation(), event.getLocation());
					assertEquals(sourceEvent.getStartDateTime().getTimeInMillis(), event.getStart().getDateTime().getValue());
					assertEquals(sourceEvent.getTitle(), event.getSummary());
					return insert;
				}
			}).when(events).insert(Matchers.eq("myModelCal-id"), Matchers.any(com.google.api.services.calendar.model.Event.class));

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

}
