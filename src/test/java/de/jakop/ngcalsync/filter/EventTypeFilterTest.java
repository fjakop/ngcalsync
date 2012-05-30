package de.jakop.ngcalsync.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;

/**
 * 
 * @author fjakop
 *
 */
public class EventTypeFilterTest {

	/** expected exception */
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CalendarEvent event;

	private EventTypeFilter filter;

	/**
	 * 
	 */
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAccept_NullEvent_NotAllowed() throws Exception {
		filter = new EventTypeFilter();

		thrown.expect(IllegalArgumentException.class);
		filter.accept(null);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAccept_EventTypeIsNull_NotAccepted() throws Exception {
		doReturn(null).when(event).getEventType();

		filter = new EventTypeFilter();
		assertFalse(filter.accept(event));

		filter = new EventTypeFilter(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		assertFalse(filter.accept(event));

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAccept_EventTypeNotInAcceptedList_NotAccepted() throws Exception {
		doReturn(EventType.NORMAL_EVENT).when(event).getEventType();

		filter = new EventTypeFilter(1, 2, 3, 4);
		assertFalse(filter.accept(event));

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAccept_EventTypeIsInAcceptedList_IsAccepted() throws Exception {
		doReturn(EventType.NORMAL_EVENT).when(event).getEventType();

		filter = new EventTypeFilter(0, 1, 2, 3, 4);
		assertTrue(filter.accept(event));

	}
}
