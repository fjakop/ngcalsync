package de.jakop.ngcalsync.obfuscator;

import de.jakop.ngcalsync.CalendarEvent;

/**
 * Obfuscate some of the {@link CalendarEvent}'s values.
 * 
 * @author jakop
 */
public interface ICalendarEntryObfuscator {

	/**
	 * Modifies this {@link CalendarEvent}s values.
	 * @param entry
	 */
	public void obfuscate(CalendarEvent entry);

}
