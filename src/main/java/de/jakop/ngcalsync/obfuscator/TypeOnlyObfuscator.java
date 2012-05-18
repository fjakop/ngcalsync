package de.jakop.ngcalsync.obfuscator;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.CalendarEvent.EventType;

/**
 * Obfuscates everything but the dates and sets the {@link CalendarEvent}'s title to the 
 * representation of its {@link EventType}
 * 
 * @author fjakop
 *
 */
public class TypeOnlyObfuscator implements ICalendarEntryObfuscator {

	public void obfuscate(CalendarEvent entry) {
		entry.setContent("");
		entry.setLocation("");
		entry.setTitle(entry.getApptype().getName());
	}

}
