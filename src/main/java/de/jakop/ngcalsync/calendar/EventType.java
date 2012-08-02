package de.jakop.ngcalsync.calendar;

import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;

/**
 * Types of Events
 * 
 * @author fjakop
 */
public enum EventType {
	/**  */
	NORMAL_EVENT(0, UserMessage.get().NORMAL_EVENT()), //
	/**  */
	ANNIVERSARY(1, UserMessage.get().ANNIVERSARY()), //
	/**  */
	ALL_DAY_EVENT(2, UserMessage.get().ALL_DAY_EVENT()), //
	/**  */
	MEETING(3, UserMessage.get().MEETING()), //+
	/**  */
	REMINDER(4, UserMessage.get().REMINDER());

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
				throw new IllegalArgumentException(UserMessage.get().MSG_EVENT_TYPE_S_NOT_RECOGNIZED_CHECK_CONFIG(type));
		}
	}

}