package de.jakop.ngcalsync.google;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import de.jakop.ngcalsync.settings.Settings;

/**
 * 
 * @author fjakop
 *
 */
public class GoogleCalendarDaoFactoryTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDao() throws Exception {
		final IGoogleCalendarDAO dao = new GoogleCalendarDaoFactory().createGoogleCalendarDao(mock(Settings.class));
		assertEquals(GoogleCalendarDAO.class, dao.getClass());
	}
}
