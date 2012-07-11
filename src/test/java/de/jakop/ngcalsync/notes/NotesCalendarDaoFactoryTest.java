package de.jakop.ngcalsync.notes;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import de.jakop.ngcalsync.settings.Settings;

/**
 * 
 * @author fjakop
 *
 */
public class NotesCalendarDaoFactoryTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDao() throws Exception {
		final INotesCalendarDAO dao = new NotesCalendarDaoFactory(mock(IOpenDatabaseStrategy.class)).createNotesCalendarDao(mock(Settings.class));
		assertEquals(NotesCalendarDAO.class, dao.getClass());
	}
}
