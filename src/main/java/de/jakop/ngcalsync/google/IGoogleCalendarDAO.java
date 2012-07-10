package de.jakop.ngcalsync.google;

import java.util.List;

import de.jakop.ngcalsync.SynchronisationException;
import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;


/**
 * Zugriff auf Google-Kalendereinträge 
 * 
 * @author fjakop
 *
 */
public interface IGoogleCalendarDAO {

	/**
	 * Fügt einen Kalendereintrag ein.
	 * 
	 * @param event
	 * @return die Id des eingefügten Kalendereintrags
	 */
	public abstract String insert(CalendarEvent event);

	/**
	 * Ändert einen Kalendereintrag.
	 * @param id 
	 * @param event
	 */
	public abstract void update(String id, CalendarEvent event);

	/**
	 * Löscht den Kalendereintrag zur Id.
	 * 
	 * @param id
	 */
	public abstract void delete(String id);

	/**
	 * Gibt alle Kalendereinträge zurück
	 * 
	 * @param filters
	 * @return alle Kalendereinträge
	 * FIXME filters auswerten
	 */
	public abstract List<CalendarEvent> getEvents(ICalendarEventFilter[] filters) throws SynchronisationException;

}