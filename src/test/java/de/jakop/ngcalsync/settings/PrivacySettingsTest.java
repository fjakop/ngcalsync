package de.jakop.ngcalsync.settings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author fjakop
 *
 */
public class PrivacySettingsTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllGetters() throws Exception {
		final PrivacySettings settings = new PrivacySettings(false, true, false);

		assertFalse(settings.isTransferTitle());
		assertTrue(settings.isTransferDescription());
		assertFalse(settings.isTransferLocation());
	}
}
