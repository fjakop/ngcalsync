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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.Validate;

import de.jakop.ngcalsync.calendar.CalendarEvent;

class CalendarEventEqualsPredicate implements Predicate<CalendarEvent> {

	private final CalendarEvent event;

	CalendarEventEqualsPredicate(final CalendarEvent event) {
		Validate.notNull(event);
		this.event = event;
	}

	@Override
	public boolean evaluate(final CalendarEvent object) {

		if (object == null) {
			return false;
		}

		// The event type cannot be evaluated, since Google has no real event types

		final Calendar startDateTime1 = object.getStartDateTime();
		final Calendar startDateTime2 = event.getStartDateTime();
		if (startDateTime1 == null || startDateTime2 == null) {
			return false;
		}

		final Calendar endDateTime1 = object.getEndDateTime();
		final Calendar endDateTime2 = event.getEndDateTime();
		if (endDateTime1 == null || endDateTime2 == null) {
			return false;
		}

		// if one is allDay and the other one not, they are not equal
		if (object.isAllDay() != event.isAllDay()) {
			return false;
		}

		final boolean isAllDay = object.isAllDay();
		if (isAllDay) {
			final boolean yearIsEqual = startDateTime1.get(Calendar.YEAR) == startDateTime2.get(Calendar.YEAR);
			final boolean monthIsEqual = startDateTime1.get(Calendar.MONTH) == startDateTime2.get(Calendar.MONTH);
			final boolean dayIsEqual = startDateTime1.get(Calendar.DAY_OF_YEAR) == startDateTime2.get(Calendar.DAY_OF_YEAR);
			return yearIsEqual && monthIsEqual && dayIsEqual;
		}

		return startDateTime2.equals(startDateTime1) && endDateTime2.equals(endDateTime1);

	}

	/**
	 * For logging, a condensed representation of the event
	 * @param doc
	 * @return a condensed representation of the event
	 */
	public static String format(final CalendarEvent doc) {
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY);
		return new StringBuilder()//
				.append(doc.getEventType() == null ? null : doc.getEventType().getName())//
				.append(": ")// //$NON-NLS-1$
				.append(doc.getStartDateTime() == null ? null : df.format(doc.getStartDateTime().getTime()))//
				.append(" -> ")// //$NON-NLS-1$
				.append(doc.getEndDateTime() == null ? null : df.format(doc.getEndDateTime().getTime()))//
				.toString();
	}

}