package de.jakop.ngcalsync.notes;

import java.util.List;

import de.jakop.ngcalsync.SynchronisationException;
import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;


/**
 * Zugriff auf Notes-Kalendereinträge 
 *
 * @author fjakop
 */
public interface INotesCalendarDAO {

	/**
	 * Gibt alle Kalendereinträge zurück
	 * 
	 * @param filters
	 * @return alle Kalendereinträge
	 */
	public abstract List<CalendarEvent> getEntries(ICalendarEventFilter[] filters) throws SynchronisationException;

}