package de.jakop.ngcalsync.calendar;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * DTO für Kalendereinträge
 */
public class CalendarEvent {

	private final static DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	private final static String toStringFormat = "" + //
			"ID             : %s%n" + //
			"Title          : %s%n" + //
			"Content        : %s%n" + //
			"StartDateTime  : %s%n" + //
			"EndDateTime    : %s%n" + //
			"Location       : %s%n" + //
			"LastUpdated    : %s%n" + //
			"EventType      : %s";

	private String id;
	private String title;
	private String content;
	private Calendar startDateTime;
	private Calendar endDateTime;
	private String location;
	private Calendar lastUpdated;
	private EventType eventType = EventType.NORMAL_EVENT;
	private boolean _private;



	// This method is for debugging.
	@Override
	public String toString() {
		return String.format(toStringFormat, //
				id, //
				title, //
				content, //
				format(startDateTime), //
				format(endDateTime), //
				location, //
				format(lastUpdated), //
				eventType);
	}

	private static String format(final Calendar date) {
		if (date == null) {
			return null;
		}
		return df.format(date.getTime());
	}

	/**
	 * @return technische Id des Kalendereintrags
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id technische Id des Kalendereintrags
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * @return Titel des Kalendereintrags
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title Titel des Kalendereintrags
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return Beschreibung des Kalendereintrags
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content Beschreibung des Kalendereintrags
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	/**
	 * @return Startzeit des Kalendereintrags
	 */
	public Calendar getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @param startDateTime Startzeit des Kalendereintrags
	 */
	public void setStartDateTime(final Calendar startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * @return Endzeit des Kalendereintrags
	 */
	public Calendar getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @param endDateTime Endzeit des Kalendereintrags
	 */
	public void setEndDateTime(final Calendar endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * @param location Ort des Kalendereintrags
	 */
	public void setLocation(final String location) {
		this.location = location;
	}

	/**
	 * @return Ort des Kalendereintrags
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param lastupdated Letze Änderung des Kalendereintrags
	 */
	public void setLastUpdated(final Calendar lastupdated) {
		lastUpdated = lastupdated;
	}

	/**
	 * @return letze Änderung des Kalendereintrags
	 */
	public Calendar getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param appType Art des Kalendereintrags
	 */
	public void setEventType(final EventType appType) {
		eventType = appType;
	}

	/**
	 * @return Art des Kalendereintrags
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param _private Privat-Flag des Kalendereintrags
	 */
	public void setPrivate(final boolean _private) {
		this._private = _private;
	}

	/**
	 * @return Privat-Flag des Kalendereintrags
	 */
	public boolean isPrivate() {
		return _private;
	}

	/**
	 * 
	 * @return <code>true</code>, if all day event
	 */
	public boolean isAllDay() {
		return getEventType() == EventType.ALL_DAY_EVENT || getEventType() == EventType.ANNIVERSARY;
	}

}
