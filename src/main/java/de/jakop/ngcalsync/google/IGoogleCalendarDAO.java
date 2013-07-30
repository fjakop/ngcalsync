package de.jakop.ngcalsync.google;

import java.util.List;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.exception.SynchronisationException;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;


/**
 * Access to Google calendar
 * 
 * @author fjakop
 */
public interface IGoogleCalendarDAO {

	/**
	 * Unserts a calendar event
	 * 
	 * @param event
	 * @return the id of the inserted event
	 */
	public abstract String insert(CalendarEvent event);

	/**
	 * Updates a calendar event
	 * 
	 * @param id 
	 * @param event
	 */
	public abstract void update(String id, CalendarEvent event);

	/**
	 * Deletes a calendar event
	 * 
	 * @param id
	 */
	public abstract void delete(String id);

	/**
	 * Reads all calendar events
	 * 
	 * @param filters
	 * @return all calendar event
	 */
	public abstract List<CalendarEvent> getEvents(ICalendarEventFilter[] filters) throws SynchronisationException;

}