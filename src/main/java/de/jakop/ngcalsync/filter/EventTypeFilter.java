package de.jakop.ngcalsync.filter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;

/**
 * Filters {@link CalendarEvent}s by {@link EventType}.
 * 
 * @author fjakop
 */
public class EventTypeFilter implements ICalendarEventFilter {

	private final int[] acceptedEventTypes;

	/**
	 * 
	 * @param acceptedType
	 */
	public EventTypeFilter(int... acceptedType) {
		acceptedEventTypes = acceptedType;
	}

	public boolean accept(CalendarEvent calendarEntry) {
		Validate.notNull(calendarEntry);
		if (calendarEntry.getEventType() == null) {
			return false;
		}
		if (ArrayUtils.contains(acceptedEventTypes, calendarEntry.getEventType().getIntegerValue())) {
			return true;
		}
		return false;
	}

}
