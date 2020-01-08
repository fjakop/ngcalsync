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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;

/**
 *
 * @author fjakop
 *
 */
@SuppressWarnings("nls")
public class CalendarEventEqualsPredicateTest {

	/** */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private CalendarEvent event;

	/** @Before */
	@Before
	public void before() {
		initMocks(this);

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testConstructor_EventIsNull_ThrowsException() throws Exception {
		exception.expect(NullPointerException.class);
		new CalendarEventEqualsPredicate(null);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_CompareToNull_ReturnsFalse() throws Exception {
		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(null));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_StartDatetimeDoesNotMatch_ReturnsFalse() throws Exception {
		final Calendar eventStart = Calendar.getInstance();
		final Calendar eventEnd = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(eventStart);
		when(event.getEndDateTime()).thenReturn(eventEnd);

		final Calendar compareStart = Calendar.getInstance();
		final Calendar compareEnd = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(compareStart);
		when(compareTo.getEndDateTime()).thenReturn(compareEnd);

		eventStart.setTimeInMillis(0);
		eventEnd.setTimeInMillis(0);
		compareStart.setTimeInMillis(1);
		compareEnd.setTimeInMillis(0);

		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_EndDatetimeDoesNotMatch_ReturnsFalse() throws Exception {
		final Calendar eventStart = Calendar.getInstance();
		final Calendar eventEnd = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(eventStart);
		when(event.getEndDateTime()).thenReturn(eventEnd);

		final Calendar compareStart = Calendar.getInstance();
		final Calendar compareEnd = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(compareStart);
		when(compareTo.getEndDateTime()).thenReturn(compareEnd);

		eventStart.setTimeInMillis(0);
		eventEnd.setTimeInMillis(0);
		compareStart.setTimeInMillis(0);
		compareEnd.setTimeInMillis(1);

		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_DatetimesDoMatch_ReturnsTrue() throws Exception {
		final Calendar eventStart = Calendar.getInstance();
		final Calendar eventEnd = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(eventStart);
		when(event.getEndDateTime()).thenReturn(eventEnd);

		final Calendar compareStart = Calendar.getInstance();
		final Calendar compareEnd = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(compareStart);
		when(compareTo.getEndDateTime()).thenReturn(compareEnd);

		eventStart.setTimeInMillis(0);
		eventEnd.setTimeInMillis(0);
		compareStart.setTimeInMillis(0);
		compareEnd.setTimeInMillis(0);

		assertTrue(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_EventStartDatetimeIsNull_ReturnsFalse() throws Exception {
		final Calendar eventEnd = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(null);
		when(event.getEndDateTime()).thenReturn(eventEnd);

		final Calendar compareStart = Calendar.getInstance();
		final Calendar compareEnd = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(compareStart);
		when(compareTo.getEndDateTime()).thenReturn(compareEnd);

		eventEnd.setTimeInMillis(0);
		compareStart.setTimeInMillis(0);
		compareEnd.setTimeInMillis(0);

		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_EventEndDatetimeIsNull_ReturnsFalse() throws Exception {
		final Calendar eventStart = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(eventStart);
		when(event.getEndDateTime()).thenReturn(null);

		final Calendar compareStart = Calendar.getInstance();
		final Calendar compareEnd = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(compareStart);
		when(compareTo.getEndDateTime()).thenReturn(compareEnd);

		eventStart.setTimeInMillis(0);
		compareStart.setTimeInMillis(0);
		compareEnd.setTimeInMillis(0);

		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_CompareStartDatetimeIsNull_ReturnsFalse() throws Exception {
		final Calendar eventStart = Calendar.getInstance();
		final Calendar eventEnd = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(eventStart);
		when(event.getEndDateTime()).thenReturn(eventEnd);

		final Calendar compareEnd = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(null);
		when(compareTo.getEndDateTime()).thenReturn(compareEnd);

		eventStart.setTimeInMillis(0);
		eventEnd.setTimeInMillis(0);
		compareEnd.setTimeInMillis(0);

		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_CompareEndDatetimeIsNull_ReturnsFalse() throws Exception {
		final Calendar eventStart = Calendar.getInstance();
		final Calendar eventEnd = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(eventStart);
		when(event.getEndDateTime()).thenReturn(eventEnd);

		final Calendar compareStart = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(compareStart);
		when(compareTo.getEndDateTime()).thenReturn(null);

		eventStart.setTimeInMillis(0);
		eventEnd.setTimeInMillis(0);
		compareStart.setTimeInMillis(0);

		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_AllDayEventDatetimesAreNull_ReturnsFalse() throws Exception {
		when(event.getStartDateTime()).thenReturn(null);
		when(event.getEndDateTime()).thenReturn(null);

		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(null);
		when(compareTo.getEndDateTime()).thenReturn(null);

		when(Boolean.valueOf(event.isAllDay())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(compareTo.isAllDay())).thenReturn(Boolean.TRUE);

		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_AllDayEventDatetimesDoNotMatch_ReturnsFalse() throws Exception {
		final Calendar eventStart = Calendar.getInstance();
		final Calendar eventEnd = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(eventStart);
		when(event.getEndDateTime()).thenReturn(eventEnd);

		final Calendar compareStart = Calendar.getInstance();
		final Calendar compareEnd = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(compareStart);
		when(compareTo.getEndDateTime()).thenReturn(compareEnd);

		when(Boolean.valueOf(event.isAllDay())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(compareTo.isAllDay())).thenReturn(Boolean.TRUE);

		eventStart.set(2000, 1, 1);
		eventEnd.setTimeInMillis(0);
		compareStart.setTimeInMillis(0);
		compareEnd.setTimeInMillis(0);

		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEvaluate_AllDayEventsAndNotAllDayCompareTo() throws Exception {

		final Calendar eventStart = Calendar.getInstance();
		final Calendar eventEnd = Calendar.getInstance();
		when(event.getStartDateTime()).thenReturn(eventStart);
		when(event.getEndDateTime()).thenReturn(eventEnd);

		final Calendar compareStart = Calendar.getInstance();
		final Calendar compareEnd = Calendar.getInstance();
		final CalendarEvent compareTo = mock(CalendarEvent.class);
		when(compareTo.getStartDateTime()).thenReturn(compareStart);
		when(compareTo.getEndDateTime()).thenReturn(compareEnd);

		eventStart.setTimeInMillis(0);
		eventEnd.setTimeInMillis(0);
		compareStart.setTimeInMillis(0);
		compareEnd.setTimeInMillis(0);

		when(Boolean.valueOf(event.isAllDay())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(compareTo.isAllDay())).thenReturn(Boolean.TRUE);
		assertTrue(new CalendarEventEqualsPredicate(event).evaluate(compareTo));

		when(Boolean.valueOf(event.isAllDay())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(compareTo.isAllDay())).thenReturn(Boolean.FALSE);
		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));

		when(Boolean.valueOf(event.isAllDay())).thenReturn(Boolean.FALSE);
		when(Boolean.valueOf(compareTo.isAllDay())).thenReturn(Boolean.TRUE);
		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));

		when(Boolean.valueOf(event.isAllDay())).thenReturn(Boolean.FALSE);
		when(Boolean.valueOf(compareTo.isAllDay())).thenReturn(Boolean.FALSE);
		assertTrue(new CalendarEventEqualsPredicate(event).evaluate(compareTo));

		when(Boolean.valueOf(event.isAllDay())).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(compareTo.isAllDay())).thenReturn(Boolean.TRUE);
		compareStart.set(2012, 2, 2);
		eventStart.set(2012, 2, 3);
		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));

		compareStart.set(2012, 2, 2);
		eventStart.set(2012, 1, 2);
		assertFalse(new CalendarEventEqualsPredicate(event).evaluate(compareTo));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFormat() throws Exception {
		final Calendar date = Calendar.getInstance();
		date.set(0, 0, 0, 0, 0, 0);
		when(event.getStartDateTime()).thenReturn(date);
		when(event.getEndDateTime()).thenReturn(date);
		when(event.getEventType()).thenReturn(EventType.REMINDER);

		assertEquals("LocalizedUserStrings.REMINDER: 31.12.0002 00:00:00 -> 31.12.0002 00:00:00", CalendarEventEqualsPredicate.format(event));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFormat_ValuesAreNull() throws Exception {
		final Calendar date = Calendar.getInstance();
		date.set(0, 0, 0, 0, 0, 0);
		when(event.getStartDateTime()).thenReturn(null);
		when(event.getEndDateTime()).thenReturn(null);
		when(event.getEventType()).thenReturn(null);

		assertEquals("null: null -> null", CalendarEventEqualsPredicate.format(event));
	}
}
