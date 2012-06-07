package de.jakop.ngcalsync.filter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;

/**
 * Filters {@link CalendarEvent}s by {@link EventType}.
 * 
 * @author fjakop
 */
public class EventTypeFilter implements ICalendarEventFilter {

	private final Log log = LogFactory.getLog(getClass());
	private final int[] acceptedEventTypes;


	/**
	 * 
	 * @param acceptedType
	 */
	public EventTypeFilter(final int... acceptedType) {
		acceptedEventTypes = acceptedType;
	}

	@Override
	public boolean accept(final CalendarEvent calendarEntry) {
		Validate.notNull(calendarEntry);
		if (calendarEntry.getEventType() == null) {
			log.debug("Not accepted - Calendar event is null.");
			return false;
		}
		if (ArrayUtils.contains(acceptedEventTypes, calendarEntry.getEventType().getIntegerValue())) {
			log.debug(String.format("Accepted - %s", calendarEntry.toString()));
			return true;
		}
		log.debug(String.format("Not accepted - %s", calendarEntry.toString()));
		return false;
	}

}
