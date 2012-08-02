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
