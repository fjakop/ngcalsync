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
package de.jakop.ngcalsync;

import java.awt.SystemTray;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.rodionmoiseev.c10n.C10N;
import com.github.rodionmoiseev.c10n.C10NConfigBase;
import com.github.rodionmoiseev.c10n.annotations.DefaultC10NAnnotations;
import com.github.rodionmoiseev.c10n.annotations.En;

import de.jakop.ngcalsync.application.Application;
import de.jakop.ngcalsync.application.ConsoleDirectStarter;
import de.jakop.ngcalsync.application.IApplicationStarter;
import de.jakop.ngcalsync.application.TrayStarter;
import de.jakop.ngcalsync.google.GoogleCalendarDaoFactory;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.notes.NotesCalendarDaoFactory;
import de.jakop.ngcalsync.notes.NotesClientOpenDatabaseStrategy;
import de.jakop.ngcalsync.service.SyncService;
import de.jakop.ngcalsync.settings.NotesHelper;
import de.jakop.ngcalsync.settings.Settings;
import de.jakop.ngcalsync.util.file.DefaultFileAccessor;

/**
 * Starts the application which synchronizes the Lotus Notes calendar events to
 * a configured Google calendar.
 *
 * @author fjakop
 *
 */
public final class StartApplication {

	private static final Log log = LogFactory.getLog(StartApplication.class);
	private static final String COMMAND_OPTION_CONSOLE = "console"; //$NON-NLS-1$

	StartApplication() {
		// this starter class is not meant to be instantiated from outside
	}


	IApplicationStarter createStarter(final CommandLine commandLine) {
		IApplicationStarter starter;
		//Check the SystemTray is supported
		if (!SystemTray.isSupported()) {
			log.info(UserMessage.get().MSG_TRAY_NOT_SUPPORTED());
		}
		if (!SystemTray.isSupported() || commandLine.hasOption(COMMAND_OPTION_CONSOLE)) {
			starter = new ConsoleDirectStarter();
		} else {
			starter = new TrayStarter();
		}
		return starter;
	}


	Application initApplication(final Settings settings) {
		return new Application(settings, new SyncService(), new NotesCalendarDaoFactory(new NotesClientOpenDatabaseStrategy()), new GoogleCalendarDaoFactory());
	}


	Settings initSettings(final DefaultFileAccessor fileAccessor, final NotesHelper notesHelper) {
		return new Settings(fileAccessor, LogFactory.getLog(Settings.class), notesHelper);
	}


	/**
	 *
	 * @param args
	 * @throws IOException
	 * @throws ConfigurationException
	 * @throws ParseException
	 */
	public static void main(final String[] args) throws IOException, ConfigurationException, ParseException {

		final StartApplication main = new StartApplication();

		main.i18n();

		final Settings settings = main.initSettings(new DefaultFileAccessor(), new NotesHelper());
		final Application application = main.initApplication(settings);
		final CommandLine commandLine = main.parseCommandLine(args);
		final IApplicationStarter starter = main.createStarter(commandLine);
		starter.startApplication(application, settings);

	}

	void i18n() {
		C10N.configure(new C10NConfigBase() {
			@Override
			protected void configure() {
				install(new DefaultC10NAnnotations());
				bindAnnotation(En.class);
			}
		});
	}

	CommandLine parseCommandLine(final String[] args) throws ParseException {
		final Options options = new Options()//
				.addOption(COMMAND_OPTION_CONSOLE, false, UserMessage.get().MSG_COMMAND_OPTION_DESCRIPTION_CONSOLE())//
		;
		final CommandLine commandLine = new BasicParser().parse(options, args);
		return commandLine;
	}
}
