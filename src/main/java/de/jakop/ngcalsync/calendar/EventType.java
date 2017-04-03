/**
 * Copyright © 2012, Frank Jakop
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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