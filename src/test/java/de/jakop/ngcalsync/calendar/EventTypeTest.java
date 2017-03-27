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

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;

/**
 * 
 * @author fjakop
 *
 */
public class EventTypeTest {

	/** expected exception */
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateFromIntValue_ValueOk() throws Exception {
		assertEquals(EventType.ALL_DAY_EVENT, EventType.create(2));
		assertEquals(EventType.ANNIVERSARY, EventType.create(1));
		assertEquals(EventType.MEETING, EventType.create(3));
		assertEquals(EventType.NORMAL_EVENT, EventType.create(0));
		assertEquals(EventType.REMINDER, EventType.create(4));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateFromIntValue_ValueNotAllowed_ThrowsException() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(UserMessage.get().MSG_EVENT_TYPE_S_NOT_RECOGNIZED_CHECK_CONFIG(5));
		EventType.create(5);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetIntegerValue() throws Exception {
		assertEquals(2, EventType.ALL_DAY_EVENT.getIntegerValue());
		assertEquals(1, EventType.ANNIVERSARY.getIntegerValue());
		assertEquals(3, EventType.MEETING.getIntegerValue());
		assertEquals(0, EventType.NORMAL_EVENT.getIntegerValue());
		assertEquals(4, EventType.REMINDER.getIntegerValue());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetName() throws Exception {
		assertEquals(UserMessage.get().ALL_DAY_EVENT(), EventType.ALL_DAY_EVENT.getName());
		assertEquals(UserMessage.get().ANNIVERSARY(), EventType.ANNIVERSARY.getName());
		assertEquals(UserMessage.get().MEETING(), EventType.MEETING.getName());
		assertEquals(UserMessage.get().NORMAL_EVENT(), EventType.NORMAL_EVENT.getName());
		assertEquals(UserMessage.get().REMINDER(), EventType.REMINDER.getName());
	}


}
