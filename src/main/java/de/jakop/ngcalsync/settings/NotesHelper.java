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
package de.jakop.ngcalsync.settings;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.oauth.IUserInputReceiver;
import de.jakop.ngcalsync.util.os.DefaultRegistryQueryProcessFactory;
import de.jakop.ngcalsync.util.os.WindowsRegistry;

/**
 *
 * @author fjakop
 *
 */
public class NotesHelper {

	private final Log log = LogFactory.getLog(NotesHelper.class);

	/**
	 *
	 * @return <code>true</code>, if Lotus Notes native library could be loaded from os path
	 */
	public boolean isNotesInSystemPath() {
		log.debug(TechMessage.get().MSG_CHECKING_NOTES_IN_SYSTEM_PATH());
		try {
			// check for Lotus Notes
			final String libname = "nlsxbe"; //$NON-NLS-1$
			System.loadLibrary(libname);
			log.debug(TechMessage.get().MSG_SUCCESSFULLY_LOADED(libname));
			return true;
		} catch (final UnsatisfiedLinkError e) {
			// Lotus Notes is not in the library path, NOTES_HOME not or incorrectly set
			log.warn(TechMessage.get().MSG_NOTES_NOT_IN_SYSTEM_PATH());
			if (e.getMessage().contains("64-bit")) { //$NON-NLS-1$
				log.fatal(UserMessage.get().MSG_ARCHITECTURE_INCOMPATIBILITY(), e);
				throw new IllegalStateException(e);
			}
			log.warn(e.getMessage());
			return false;
		}
	}

	/**
	 *
	 * @return <code>true</code>, if Lotus Notes java classes could be loaded from the classpath
	 */
	public boolean isNotesInClassPath() {
		log.debug(TechMessage.get().MSG_CHECKING_NOTES_IN_CLASSPATH());
		try {
			// check for Lotus Notes java classes
			final String className = "lotus.notes.NotesException"; //$NON-NLS-1$
			Class.forName(className);
			log.debug(TechMessage.get().MSG_SUCCESSFULLY_LOADED(className));
			return true;
		} catch (final ClassNotFoundException e) {
			// Lotus Notes jar is not in the classpath, NOTES_HOME not or incorrectly set
			log.warn(TechMessage.get().MSG_NOTES_NOT_IN_CLASSPATH());
			log.debug(TechMessage.get().MSG_SOURCE_EXCEPTION_WAS(), e);
			return false;
		}
	}

	/**
	 * Obtains Lotus Notes installation path. For MS Windows it is read from registry, other os require
	 * the user to enter the path.
	 *
	 * @return the installation path
	 */
	public String getLotusNotesPath(final IUserInputReceiver receiver) {
		// check os and try to determine path to Lotus Notes
		String lotusNotesHome = new String();
		log.debug(TechMessage.get().MSG_OBTAINING_NOTES_SYSTEM_PATH());
		log.debug(TechMessage.get().MSG_OS_INFO(SystemUtils.OS_NAME, SystemUtils.OS_VERSION, SystemUtils.OS_ARCH));

		if (StringUtils.isBlank(lotusNotesHome) && SystemUtils.IS_OS_WINDOWS) {
			lotusNotesHome = findFromWindowsRegistry();
			log.info(TechMessage.get().MSG_PATH_READ_FROM_WINDOWS_REGISTRY(lotusNotesHome));
		}

		if (StringUtils.isBlank(lotusNotesHome)) {
			lotusNotesHome = findFromSystemVariable();
			log.info(TechMessage.get().MSG_PATH_READ_FROM_SYSTEM_VARIABLE(lotusNotesHome));
		}

		// Fallback for Windows, default for other os
		if (StringUtils.isBlank(lotusNotesHome)) {
			lotusNotesHome = receiver.waitForUserInput(UserMessage.get().MSG_ENTER_LOTUS_NOTES_PATH());
		}

		while (lotusNotesHome != null && lotusNotesHome.endsWith("\n")) { //$NON-NLS-1$
			lotusNotesHome = StringUtils.chomp(lotusNotesHome);
		}

		return lotusNotesHome;
	}

	private String findFromSystemVariable() {
		return System.getenv(Constants.NOTES_HOME_ENVVAR_NAME);
	}

	private String findFromWindowsRegistry() {

		// Map of possible Lotus Notes locations in the Windows registry
		final Map<String, String> registryKeys = new HashedMap<String, String>();
		registryKeys.put("HKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes", "Path"); //$NON-NLS-1$//$NON-NLS-2$
		registryKeys.put("HKEY_CURRENT_USER\\Software\\Lotus\\Notes\\Installer", "PROGDIR"); //$NON-NLS-1$ //$NON-NLS-2$
		registryKeys.put("HKEY_CURRENT_USER\\Software\\IBM\\Notes\\Installer", "PROGDIR"); //$NON-NLS-1$ //$NON-NLS-2$

		final WindowsRegistry windowsRegistry = new WindowsRegistry(new DefaultRegistryQueryProcessFactory());
		final Iterator<String> locations = registryKeys.keySet().iterator();
		String lotusNotesHome = null;
		while (lotusNotesHome == null && locations.hasNext()) {
			final String location = locations.next();
			log.warn(TechMessage.get().MSG_CHECKING_REGISTRY_KEY_(location, registryKeys.get(location)));
			lotusNotesHome = windowsRegistry.readRegistry(location, registryKeys.get(location));
		}

		return lotusNotesHome;
	}
}
