package de.jakop.ngcalsync.notes;

import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
		// TODO i18n
		log.debug("Checking for Lotus Notes in system path...");
		try {
			// check for Lotus Notes
			// TODO i18n
			System.loadLibrary("nlsxbe");
			log.debug("nlsxbe successfully loaded");
			return true;
		} catch (final UnsatisfiedLinkError e) {
			// TODO i18n
			// Lotus Notes is not in the library path, NOTES_HOME not or incorrectly set
			log.warn(String.format("Lotus Notes is not in the library path."));
			return false;
		}
	}

	/**
	 * 
	 * @return <code>true</code>, if Lotus Notes java classes could be loaded from the classpath
	 */
	public boolean isNotesInClassPath() {
		// TODO i18n
		log.debug("Checking for Lotus Notes jar in classpath...");
		try {
			// check for Lotus Notes java classes
			Class.forName("lotus.notes.NotesException");
			// TODO i18n
			log.debug("lotus.notes.NotesException successfully loaded");
			return true;
		} catch (final ClassNotFoundException e) {
			// TODO i18n
			// Lotus Notes jar is not in the classpath, NOTES_HOME not or incorrectly set
			log.warn(String.format("Lotus Notes jar is not in the classpath."));
			return false;
		}
	}

	/**
	 * Obtains Lotus Notes installation path. For MS Windows it is read from registry, other os require 
	 * the user to enter the path.
	 * 
	 * @return the installation path
	 */
	public String getLotusNotesPath() {
		// check os and try to determine path to Lotus Notes
		String lotusNotesHome = "";
		// TODO i18n
		log.debug("Trying to obtain Lotus Notes path");
		// TODO i18n
		log.debug(String.format("OS info: %s-%s-%s", SystemUtils.OS_NAME, SystemUtils.OS_VERSION, SystemUtils.OS_ARCH));
		if (SystemUtils.IS_OS_WINDOWS) {
			// TODO i18n
			log.debug("OS is Windows");
			lotusNotesHome = WindowsRegistry.readRegistry("HKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes", "Path");
			while (lotusNotesHome.endsWith("\n")) {
				lotusNotesHome = StringUtils.chomp(lotusNotesHome);
			}
			// TODO i18n
			log.info(String.format("Path to Lotus Notes read from Windows registry was %s.", lotusNotesHome));
		} else {
			do {
				System.out.print("Please enter path to Lotus Notes installation: ");
				lotusNotesHome = new Scanner(System.in).nextLine();
			} while (lotusNotesHome.isEmpty());
		}
		return lotusNotesHome;
	}


}
