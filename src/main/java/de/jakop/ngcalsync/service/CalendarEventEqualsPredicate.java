package de.jakop.ngcalsync.service;

import java.text.DateFormat;
import java.util.Calendar;

import org.apache.commons.collections15.Predicate;
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

		if (object.getStartDateTime() == null || event.getStartDateTime() == null) {
			return false;
		}
		if (object.getEndDateTime() == null || event.getEndDateTime() == null) {
			return false;
		}

		if (object.isAllDay() && event.isAllDay()) {
			return object.getStartDateTime().get(Calendar.YEAR) == event.getStartDateTime().get(Calendar.YEAR) && //
					object.getStartDateTime().get(Calendar.MONTH) == event.getStartDateTime().get(Calendar.MONTH) && //
					object.getStartDateTime().get(Calendar.DAY_OF_YEAR) == event.getStartDateTime().get(Calendar.DAY_OF_YEAR);
		}

		return event.getStartDateTime().equals(object.getStartDateTime()) && //
				event.getEndDateTime().equals(object.getEndDateTime());

	}

	/**
	 * For logging, a condensed representation of the event
	 * @param doc
	 * @return a condensed representation of the event
	 */
	public static String getComparisonString(final CalendarEvent doc) {
		final DateFormat df = DateFormat.getDateTimeInstance();
		return new StringBuilder()//
				.append(doc.getEventType() == null ? null : doc.getEventType().getName())//
				.append(": ")//
				.append(doc.getStartDateTime() == null ? null : df.format(doc.getStartDateTime().getTime()))//
				.append(" -> ")//
				.append(doc.getEndDateTime() == null ? null : df.format(doc.getEndDateTime().getTime()))//
				.toString();
	}

}