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
		final DateFormat df = DateFormat.getDateTimeInstance();
		return new StringBuilder()//
				.append(doc.getEventType() == null ? null : doc.getEventType().getName())//
				.append(": ")// //$NON-NLS-1$
				.append(doc.getStartDateTime() == null ? null : df.format(doc.getStartDateTime().getTime()))//
				.append(" -> ")// //$NON-NLS-1$
				.append(doc.getEndDateTime() == null ? null : df.format(doc.getEndDateTime().getTime()))//
				.toString();
	}

}