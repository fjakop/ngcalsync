package de.jakop.ngcalsync.obfuscator;

import de.jakop.ngcalsync.calendar.CalendarEvent;

/**
 * Obfuscate some of the {@link CalendarEvent}'s values.
 * 
 * @author fjakop
 */
public interface ICalendarEntryObfuscator {

	/**
	 * Modifies this {@link CalendarEvent}s values.
	 * @param entry
	 */
	public void obfuscate(CalendarEvent entry);

}
