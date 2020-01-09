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
package de.jakop.ngcalsync.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

/**
 *
 * @author fjakop
 *
 */
@SuppressWarnings("nls")
public class CalendarEventTest {

	private final static String toStringFormat = "" + //
			"ID             : %s%n" + //
			"Title          : %s%n" + //
			"Content        : %s%n" + //
			"StartDateTime  : %s%n" + //
			"EndDateTime    : %s%n" + //
			"Location       : %s%n" + //
			"LastUpdated    : %s%n" + //
			"EventType      : %s";

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testFormat_EmptyObject() throws Exception {
		final CalendarEvent calendarEvent = new CalendarEvent();
		assertEquals(String.format(toStringFormat, null, null, null, null, null, null, null, EventType.NORMAL_EVENT), calendarEvent.format());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testToString_FilledObject() throws Exception {
		final String id = "Id1";
		final String title = "Title1";
		final String content = "Content1";
		final String location = "Location1";
		final Calendar startDateTime = Calendar.getInstance();
		startDateTime.set(2012, 5, 10, 11, 00);

		final Calendar endDateTime = Calendar.getInstance();
		endDateTime.set(2012, 5, 10, 13, 00);

		final Calendar lastUpdated = Calendar.getInstance();
		lastUpdated.set(2012, 5, 6, 10, 22);

		final CalendarEvent calendarEvent = new CalendarEvent();
		calendarEvent.setEventType(EventType.MEETING);
		calendarEvent.setContent(content);
		calendarEvent.setEndDateTime(endDateTime);
		calendarEvent.setId(id);
		calendarEvent.setLastUpdated(lastUpdated);
		calendarEvent.setLocation(location);
		calendarEvent.setPrivate(false);
		calendarEvent.setStartDateTime(startDateTime);
		calendarEvent.setTitle(title);

		assertEquals(String.format(toStringFormat, id, title, content, "10.06.2012 11:00", "10.06.2012 13:00", location, "06.06.2012 10:22", EventType.MEETING), calendarEvent.format());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testIsAllDay() throws Exception {
		final CalendarEvent calendarEvent = new CalendarEvent();
		assertFalse(calendarEvent.isAllDay());

		calendarEvent.setEventType(EventType.MEETING);
		assertFalse(calendarEvent.isAllDay());

		calendarEvent.setEventType(EventType.NORMAL_EVENT);
		assertFalse(calendarEvent.isAllDay());

		calendarEvent.setEventType(EventType.REMINDER);
		assertFalse(calendarEvent.isAllDay());

		calendarEvent.setEventType(EventType.ALL_DAY_EVENT);
		assertTrue(calendarEvent.isAllDay());

		calendarEvent.setEventType(EventType.ANNIVERSARY);
		assertTrue(calendarEvent.isAllDay());

	}

	/**
	 *
	 */
	@Test
	public void testSetGetLocationTitleContentPrivate() {
		final CalendarEvent calendarEvent = new CalendarEvent();
		calendarEvent.setLocation("foo");
		calendarEvent.setTitle("bar");
		calendarEvent.setContent("content");
		calendarEvent.setPrivate(true);

		assertEquals("foo", calendarEvent.getLocation());
		assertEquals("bar", calendarEvent.getTitle());
		assertEquals("content", calendarEvent.getContent());
		assertTrue(calendarEvent.isPrivate());

	}

}
