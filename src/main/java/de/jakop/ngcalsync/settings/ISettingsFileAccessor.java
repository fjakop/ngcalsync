package de.jakop.ngcalsync.settings;

import java.io.File;


/**
 * Provides access to the settings directory and its files.
 * 
 * @author fjakop
 *
 */
public interface ISettingsFileAccessor {

	/**
	 * 
	 * @param name
	 * @return the {@link File} for the name from the settings directory
	 */
	public abstract File getFile(final String name);

}