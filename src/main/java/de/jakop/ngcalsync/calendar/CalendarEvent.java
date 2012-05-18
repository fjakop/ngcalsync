package de.jakop.ngcalsync.calendar;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * DTO für Kalendereinträge
 */
public class CalendarEvent {

	/**
	 * Types of Events
	 * 
	 * @author fjakop
	 *
	 */
	public enum EventType {
		/**  */
		NORMAL_EVENT(0, "Termin"), //
		/**  */
		ANNIVERSARY(1, "Jahrestag"), //
		/**  */
		ALL_DAY_EVENT(2, "Ganztägig"), //
		/**  */
		MEETING(3, "Besprechung"), //+
		/**  */
		REMINDER(4, "Erinnerung");

		private final int value;
		private final String name;

		private EventType(final int value, final String name) {
			this.value = value;
			this.name = name;
		}

		/**
		 * @return integer representation of the {@link EventType}
		 */
		public int getIntegerValue() {
			return value;
		}

		/**
		 * @return human readable name of this {@link EventType}
		 * TODO i18n
		 */
		public String getName() {
			return name;
		}

		/**
		 * Creates {@link EventType} for integer value 
		 * @param type
		 * @return the created {@link EventType}
		 * @throws IllegalArgumentException for invalid integer value
		 */
		public static EventType create(int type) {
			switch (type) {
				case 0:
					return EventType.NORMAL_EVENT;
				case 1:
					return EventType.ANNIVERSARY;
				case 2:
					return EventType.ALL_DAY_EVENT;
				case 3:
					return EventType.MEETING;
				case 4:
					return EventType.REMINDER;
				default:
					throw new IllegalArgumentException(String.format("Appointment type %s not recognized", new Integer(type)));
			}
		}

	}

	private String id;
	private String title;
	private String content;
	private Calendar startDateTime; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private Calendar endDateTime; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private String location;
	private Calendar lastupdated; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private EventType apptype; // Appointment type
	private boolean _private;



	// This method is for debugging.
	@Override
	public String toString() {
		StringBuffer l = new StringBuffer();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
		l.append("ID             : " + id + "\n");
		l.append("Title          : " + title + "\n");
		l.append("Content        : " + content + "\n");
		l.append("startDateTime  : " + (startDateTime != null ? df.format(startDateTime.getTime()) : "") + "\n");
		l.append("endDateTime    : " + (endDateTime != null ? df.format(endDateTime.getTime()) : "") + "\n");
		l.append("location       : " + location + "\n");
		l.append("lastupdated    : " + (lastupdated != null ? (lastupdated == null ? "" : df.format(lastupdated.getTime())) : "") + "\n");
		l.append("apptype        : " + apptype + "\n");

		return l.toString();
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
	public void setId(String id) {
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
	public void setTitle(String title) {
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
	public void setContent(String content) {
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
	public void setStartDateTime(Calendar startDateTime) {
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
	public void setEndDateTime(Calendar endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * @param location Ort des Kalendereintrags
	 */
	public void setLocation(String location) {
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
	public void setLastUpdated(Calendar lastupdated) {
		this.lastupdated = lastupdated;
	}

	/**
	 * @return letze Änderung des Kalendereintrags
	 */
	public Calendar getLastUpdated() {
		return lastupdated;
	}

	/**
	 * @param appType Art des Kalendereintrags
	 */
	public void setApptype(EventType appType) {
		apptype = appType;
	}

	/**
	 * @return Art des Kalendereintrags
	 */
	public EventType getApptype() {
		return apptype;
	}

	/**
	 * @param _private Privat-Flag des Kalendereintrags
	 */
	public void setPrivate(boolean _private) {
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
		return getApptype() == EventType.ALL_DAY_EVENT || getApptype() == EventType.ANNIVERSARY;
	}

}
