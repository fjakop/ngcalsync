package de.jakop.ngcalsync.filter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;

import de.jakop.ngcalsync.CalendarEvent;
import de.jakop.ngcalsync.CalendarEvent.EventType;

/**
 * Filters {@link CalendarEvent}s by {@link EventType}.
 * 
 * @author jakop
 */
public class AppointmentTypeFilter implements ICalendarEntryFilter {

	private final int[] acceptedAppointmentTypes;

	/**
	 * 
	 * @param acceptedType
	 */
	public AppointmentTypeFilter(int... acceptedType) {
		acceptedAppointmentTypes = acceptedType;
	}

	public boolean accept(CalendarEvent calendarEntry) {
		Validate.notNull(calendarEntry);
		if (calendarEntry.getApptype() == null) {
			return false;
		}
		if (ArrayUtils.contains(acceptedAppointmentTypes, calendarEntry.getApptype().getIntegerValue())) {
			return true;
		}
		return false;
	}

}
