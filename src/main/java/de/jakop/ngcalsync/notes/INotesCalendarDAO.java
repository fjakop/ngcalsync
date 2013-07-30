package de.jakop.ngcalsync.notes;

import java.util.List;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.exception.SynchronisationException;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;


/**
 * Access to Lotus Notes calendar events 
 *
 * @author fjakop
 */
public interface INotesCalendarDAO {

	/**
	 * Reads all calendar events matched by the filters
	 * 
	 * @param filters
	 * @return all matching events
	 */
	public abstract List<CalendarEvent> getEntries(ICalendarEventFilter[] filters) throws SynchronisationException;

}