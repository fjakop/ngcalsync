package de.jakop.ngcalsync.calendar;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
		thrown.expectMessage(String.format("Appointment type %s not recognized", new Integer(5)));
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
		assertEquals("Ganztägig", EventType.ALL_DAY_EVENT.getName());
		assertEquals("Jahrestag", EventType.ANNIVERSARY.getName());
		assertEquals("Besprechung", EventType.MEETING.getName());
		assertEquals("Termin", EventType.NORMAL_EVENT.getName());
		assertEquals("Erinnerung", EventType.REMINDER.getName());
	}


}
