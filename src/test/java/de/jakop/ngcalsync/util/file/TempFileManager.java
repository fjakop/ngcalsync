package de.jakop.ngcalsync.util.file;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author fjakop
 *
 */
public class TempFileManager {

	private File tempdir;

	/**
	 * 
	 * @return a temporary directory which is not yet created
	 */
	public File getTempDir() {
		if (tempdir == null || !tempdir.isDirectory()) {

			final long time = System.currentTimeMillis();
			final String dirname = "temp" + time;

			final String settingsDir = System.getProperty("java.io.tmpdir") + File.separator + dirname;
			tempdir = new File(settingsDir);
		}

		return tempdir;
	}

	/**
	 * deletes the temporary directory recursively and quietly
	 */
	public void deleteTempDir() {
		FileUtils.deleteQuietly(tempdir);
	}

	/**
	 * Sets the user's home to the temporary directory
	 */
	public void setUserHomeToTempDir() {
		// we do not want to create temp files in the user's home, so let's set user.home to java.io.tmpdir
		System.setProperty("user.home", getTempDir().getAbsolutePath());
	}
}
