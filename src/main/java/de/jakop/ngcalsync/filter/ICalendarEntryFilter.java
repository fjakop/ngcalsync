package de.jakop.ngcalsync.filter;

import de.jakop.ngcalsync.CalendarEvent;

/**
 * Filter for selection of {@link CalendarEvent}s
 * 
 * @author jakop
 *
 */
public interface ICalendarEntryFilter {

	/**
	 * 
	 * @param calendarEntry
	 * @return whether to accept this {@link CalendarEvent} or not
	 */
	boolean accept(CalendarEvent calendarEntry);

}
