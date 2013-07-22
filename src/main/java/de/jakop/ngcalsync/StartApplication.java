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

import c10n.C10N;
import c10n.C10NConfigBase;
import c10n.annotations.DefaultC10NAnnotations;
import c10n.annotations.En;

import de.jakop.ngcalsync.application.Application;
import de.jakop.ngcalsync.application.ConsoleDirectStarter;
import de.jakop.ngcalsync.application.IApplicationStarter;
import de.jakop.ngcalsync.application.TrayStarter;
import de.jakop.ngcalsync.google.GoogleCalendarDaoFactory;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.notes.NotesCalendarDaoFactory;
import de.jakop.ngcalsync.notes.NotesClientOpenDatabaseStrategy;
import de.jakop.ngcalsync.notes.NotesHelper;
import de.jakop.ngcalsync.service.SyncService;
import de.jakop.ngcalsync.settings.Settings;
import de.jakop.ngcalsync.util.file.DefaultFileAccessor;

/**
 * Starts the application which synchronizes the Lotus Notes calendar events to
 * a configured Google calendar.
 * 
 * @author fjakop
 *
 */
public class StartApplication {

	private static final Log log = LogFactory.getLog(StartApplication.class);
	private static final String COMMAND_OPTION_CONSOLE = "console";

	private StartApplication() {
		// this starter class is not meant to be instantiated
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ConfigurationException 
	 * @throws ParseException 
	 */
	public static void main(final String[] args) throws IOException, ConfigurationException, ParseException {

		C10N.configure(new C10NConfigBase() {
			@Override
			protected void configure() {
				install(new DefaultC10NAnnotations());
				bindAnnotation(En.class);
			}
		});

		final DefaultFileAccessor fileAccessor = new DefaultFileAccessor();
		final Settings settings = new Settings(fileAccessor, LogFactory.getLog(Settings.class), new NotesHelper());

		final Application application = new Application(settings, new SyncService(), new NotesCalendarDaoFactory(new NotesClientOpenDatabaseStrategy()), new GoogleCalendarDaoFactory());

		final CommandLine commandLine = new BasicParser().parse(
				new Options().addOption(COMMAND_OPTION_CONSOLE, false, "Starts the application without GUI (a.k.a. console mode) and with immediate synchronisation"), args);

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
		starter.startApplication(application, settings);
	}

}
