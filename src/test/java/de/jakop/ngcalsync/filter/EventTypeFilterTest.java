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

		thrown.expect(NullPointerException.class);
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
