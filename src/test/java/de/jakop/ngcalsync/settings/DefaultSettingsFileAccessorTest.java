package de.jakop.ngcalsync.settings;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.util.TempFileManager;

/**
 * 
 * @author fjakop
 *
 */
public class DefaultSettingsFileAccessorTest {

	private TempFileManager manager;

	/** */
	@Before
	public void before() {
		manager = new TempFileManager();
		manager.setUserHomeToTempDir();
	}

	/** */
	@After
	public void after() {
		manager.deleteTempDir();
	}


	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSettingsDir_CreateDirs() throws Exception {

		final File expected = new File(System.getProperty("user.home") + File.separator + Constants.FILENAME_SETTINGS_DIR, "foo");

		final DefaultSettingsFileAccessor accessor = new DefaultSettingsFileAccessor();
		assertEquals(expected, accessor.getFile("foo"));
	}

}
