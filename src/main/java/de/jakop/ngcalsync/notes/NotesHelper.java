package de.jakop.ngcalsync.notes;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
			final String libname = "nlsxbe";
			System.loadLibrary(libname);
			log.debug(TechMessage.get().MSG_SUCCESSFULLY_LOADED(libname));
			return true;
		} catch (final UnsatisfiedLinkError e) {
			// Lotus Notes is not in the library path, NOTES_HOME not or incorrectly set
			log.warn(TechMessage.get().MSG_NOTES_NOT_IN_SYSTEM_PATH());
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
			final String className = "lotus.notes.NotesException";
			Class.forName(className);
			log.debug(TechMessage.get().MSG_SUCCESSFULLY_LOADED(className));
			return true;
		} catch (final ClassNotFoundException e) {
			// Lotus Notes jar is not in the classpath, NOTES_HOME not or incorrectly set
			log.warn(TechMessage.get().MSG_NOTES_NOT_IN_CLASSPATH());
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
		String lotusNotesHome = "";
		log.debug(TechMessage.get().MSG_OBTAINING_NOTES_SYSTEM_PATH());
		log.debug(TechMessage.get().MSG_OS_INFO(SystemUtils.OS_NAME, SystemUtils.OS_VERSION, SystemUtils.OS_ARCH));
		if (SystemUtils.IS_OS_WINDOWS) {
			lotusNotesHome = new WindowsRegistry(new DefaultRegistryQueryProcessFactory()).readRegistry("HKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes", "Path");
			while (lotusNotesHome.endsWith("\n")) {
				lotusNotesHome = StringUtils.chomp(lotusNotesHome);
			}
			log.info(TechMessage.get().MSG_PATH_READ_FROM_WINDOWS_REGISTRY(lotusNotesHome));
		}

		// Fallback for Windows, default for other os
		if (StringUtils.isBlank(lotusNotesHome)) {
			lotusNotesHome = receiver.waitForUserInput(UserMessage.get().MSG_ENTER_LOTUS_NOTES_PATH());
		}

		return lotusNotesHome;
	}


}
