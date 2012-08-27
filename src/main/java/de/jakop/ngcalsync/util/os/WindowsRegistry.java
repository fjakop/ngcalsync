package de.jakop.ngcalsync.util.os;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;

/**
 * Read keys from Windows registry. <br>
 * Tested with
 * <ul>
 * <li>Windows XP</li>
 * <li>Windows 7</li>
 * </ul>
 * 
 * @author fjakop
 *
 */
public final class WindowsRegistry {

	private final Log log = LogFactory.getLog(getClass());

	private final IRegistryQueryProcessFactory processFactory;

	/**
	 * 
	 * @param processFactory
	 */
	public WindowsRegistry(final IRegistryQueryProcessFactory processFactory) {
		Validate.notNull(processFactory);
		this.processFactory = processFactory;
	}

	/**
	 * Reads the value of the registry key at the given location
	 * 
	 * @param location path in the registry
	 * @param key registry key
	 * @return registry value or null if not found
	 */
	public final String readRegistry(final String location, final String key) {
		try {
			// Run reg query, then read output with StreamReader (internal class)
			final Process process = processFactory.createQueryProcess(location, key);

			final String output = IOUtils.toString(process.getInputStream());

			// the output has the following format:
			// \n<Version information>\n\n<key>\t<registry type>\t<value>
			if (!output.contains("\t")) {
				return null;
			}

			// Parse out the value
			final String[] parsed = output.split("\t");
			return parsed[parsed.length - 1];
		} catch (final IOException e) {
			log.error(UserMessage.get().MSG_FAILED_TO_READ_REGISTRY(key), e);
			// returning null is perfectly legal
			return null;
		}

	}



}