/**
 * Copyright © 2012, Frank Jakop
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.jakop.ngcalsync.util.os;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.annotations.VisibleForTesting;

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

	private static final String TAB = "\t"; //$NON-NLS-1$

	@VisibleForTesting
	Log log = LogFactory.getLog(getClass());

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
		String value = null;
		try {
			// Run reg query, then read output with StreamReader (internal class)
			final Process process = processFactory.createQueryProcess(location, key);

			final String output = IOUtils.toString(process.getInputStream());

			// Windows XP
			value = findKeyOnWindowsXP(output);

			// Windows 7
			if (value == null) {
				value = findKeyOnWindows7(location, key, output);
			}

			return value;

		} catch (final IOException e) {
			log.error(UserMessage.get().MSG_FAILED_TO_READ_REGISTRY(key), e);
			// returning null is perfectly legal
			return null;
		}

	}

	private String findKeyOnWindowsXP(final String output) {
		// the output has the following format:
		// \n<Version information>\n\n<key>\t<registry type>\t<value>
		if (!output.contains(TAB)) {
			return null;
		}

		// Parse out the value
		final String[] parsed = output.split(TAB);
		return parsed[parsed.length - 1];
	}

	private String findKeyOnWindows7(final String location, final String key, final String output) {
		// the output has the following format:
		// \r\n<location>\r\n    <key>    REG_SZ    <value>\r\n\r\n
		// example:
		// \r\nHKEY_CURRENT_USER\\Software\\IBM\\Notes\\Installer\r\n    PROGDIR    REG_SZ    c:\\Program Files (x86)\\IBM\\Notes\\\r\n\r\n

		final Pattern pattern = Pattern.compile("\\r\\n" + Pattern.quote(location) + "\\s+" + Pattern.quote(key) + "\\s+REG_SZ\\s+(.*)\\r\\n\\r\\n"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		final Matcher matcher = pattern.matcher(output);
		final boolean found = matcher.find();
		String value = null;
		if (found) {
			value = matcher.group(1);
		}
		return value;
	}
}