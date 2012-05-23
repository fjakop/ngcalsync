package de.jakop.ngcalsync.filter;

import de.jakop.ngcalsync.calendar.CalendarEvent;

/**
 * Filter for selection of {@link CalendarEvent}s
 * 
 * @author fjakop
 *
 */
public interface ICalendarEventFilter {

	/**
	 * 
	 * @param calendarEntry
	 * @return whether to accept this {@link CalendarEvent} or not
	 */
	boolean accept(CalendarEvent calendarEntry);

}
