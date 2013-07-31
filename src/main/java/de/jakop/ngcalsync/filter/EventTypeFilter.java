package de.jakop.ngcalsync.filter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;

/**
 * Filters {@link CalendarEvent}s by {@link de.jakop.ngcalsync.calendar.EventType}.
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
	public boolean accept(final CalendarEvent event) {
		Validate.notNull(event);
		if (event.getEventType() == null) {
			log.debug(TechMessage.get().MSG_EVENT_NOT_ACCEPTED_BY_FILTER_IS_NULL());
			return false;
		}
		if (ArrayUtils.contains(acceptedEventTypes, event.getEventType().getIntegerValue())) {
			log.debug(TechMessage.get().MSG_EVENT_ACCEPTED_BY_FILTER(event.format()));
			return true;
		}
		log.debug(TechMessage.get().MSG_EVENT_NOT_ACCEPTED_BY_FILTER(event.format()));
		return false;
	}

}
