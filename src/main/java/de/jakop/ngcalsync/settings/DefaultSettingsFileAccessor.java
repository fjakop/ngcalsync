package de.jakop.ngcalsync.settings;

import java.io.File;

import de.jakop.ngcalsync.Constants;

/**
 * Provides access to the settings directory and its files.
 *  
 * @author fjakop
 */
public class DefaultSettingsFileAccessor implements ISettingsFileAccessor {

	private File settingsDir;

	/**
	 * @return the settings directory, it will be created, if not present.
	 */
	private File getSettingsDir() {
		if (settingsDir == null) {
			settingsDir = new File(System.getProperty("user.home"), Constants.FILENAME_SETTINGS_DIR);
			if (!settingsDir.isDirectory()) {
				settingsDir.mkdirs();
			}
		}
		return settingsDir;
	}

	@Override
	public File getFile(final String name) {
		return new File(getSettingsDir(), name);
	}

}
