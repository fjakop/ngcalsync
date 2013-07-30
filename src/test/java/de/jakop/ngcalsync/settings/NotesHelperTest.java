package de.jakop.ngcalsync.settings;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jakop.ngcalsync.settings.NotesHelper;


/**
 * 
 * @author fjakop
 *
 */
public class NotesHelperTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIsNotesInSystemPath() throws Exception {
		// do we have Lotus Notes?
		boolean isNotesOnPath;
		try {
			// check for Lotus Notes
			System.loadLibrary("nlsxbe");
			isNotesOnPath = true;
		} catch (final UnsatisfiedLinkError e) {
			isNotesOnPath = false;
		}

		assertEquals(Boolean.valueOf(isNotesOnPath), Boolean.valueOf(new NotesHelper().isNotesInSystemPath()));
	}
}
