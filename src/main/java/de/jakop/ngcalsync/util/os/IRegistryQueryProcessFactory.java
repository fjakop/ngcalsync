package de.jakop.ngcalsync.util.os;

import java.io.IOException;

/**
 * Creates the process for reading teh windows registry.
 * 
 * @author fjakop
 *
 */
public interface IRegistryQueryProcessFactory {

	/**
	 * 
	 * @param location the treepath location of the key, e.g. "HKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes"
	 * @param key the key name, e.g. "Version"
	 * @return the value of the key
	 * 
	 * @throws IOException
	 */
	public Process createQueryProcess(final String location, final String key) throws IOException;

}
