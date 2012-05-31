package de.jakop.ngcalsync.google;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Ignore;
import org.junit.Test;

import com.google.api.services.calendar.Calendar;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.settings.Settings;

/**
 * 
 * @author fjakop
 *
 */
public class GoogleCalendarDAOTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testInsert() throws Exception {
		final Settings settings = mock(Settings.class);
		final Calendar calendar = mock(Calendar.class);
		doReturn(calendar).when(settings.getGoogleCalendarService());

		final GoogleCalendarDAO dao = new GoogleCalendarDAO(settings);

		dao.insert(mock(CalendarEvent.class));

		// TODO how to test this?
	}
}
