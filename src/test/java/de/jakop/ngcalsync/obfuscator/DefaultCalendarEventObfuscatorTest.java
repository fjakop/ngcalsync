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
package de.jakop.ngcalsync.obfuscator;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;
import de.jakop.ngcalsync.settings.PrivacySettings;

/**
 *
 * @author fjakop
 *
 */
@SuppressWarnings("nls")
public class DefaultCalendarEventObfuscatorTest {

	/** expected exception */
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testConstructor_NullPrivacySettings_ThrowsException() throws Exception {
		thrown.expect(NullPointerException.class);
		new DefaultCalendarEventObfuscator(null);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testDoObfuscate() throws Exception {
		final PrivacySettings privacySettings = new PrivacySettings(false, false, false);

		final CalendarEvent event = spy(new CalendarEvent());
		event.setEventType(EventType.MEETING);

		final DefaultCalendarEventObfuscator obfuscator = new DefaultCalendarEventObfuscator(privacySettings);

		verify(event, times(0)).setTitle("");
		verify(event, times(0)).setLocation("");
		verify(event, times(0)).setContent("");

		obfuscator.obfuscate(event);

		verify(event, times(1)).setTitle(EventType.MEETING.getName());
		verify(event, times(1)).setLocation("");
		verify(event, times(1)).setContent("");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testDoNotObfuscate() throws Exception {
		final PrivacySettings privacySettings = new PrivacySettings(true, true, true);

		final CalendarEvent event = spy(new CalendarEvent());

		final DefaultCalendarEventObfuscator obfuscator = new DefaultCalendarEventObfuscator(privacySettings);

		obfuscator.obfuscate(event);

		verifyNoMoreInteractions(event);
	}


}
