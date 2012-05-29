package de.jakop.ngcalsync.obfuscator;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;
import de.jakop.ngcalsync.settings.PrivacySettings;

/**
 * 
 * @author fjakop
 *
 */
public class DefaultCalendarEventObfuscatorTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_NullPrivacySettings_ThrowsException() throws Exception {
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
