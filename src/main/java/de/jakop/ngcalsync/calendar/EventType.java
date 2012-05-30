package de.jakop.ngcalsync.calendar;

/**
 * Types of Events
 * 
 * @author fjakop
 * TODO i18n
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
	public static EventType create(final int type) {
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