package de.jakop.ngcalsync.notes;

import de.jakop.ngcalsync.settings.Settings;

/**
 * 
 * @author fjakop
 *
 */
public class NotesCalendarDaoFactory {

	/**
	 * Creates a {@link NotesCalendarDAO}
	 * 
	 * @param settings
	 * @return a new {@link NotesCalendarDAO}
	 */
	public INotesCalendarDAO createNotesCalendarDao(final Settings settings) {
		final NotesClientOpenDatabaseStrategy openDatabaseStrategy = new NotesClientOpenDatabaseStrategy();
		return new NotesCalendarDAO(openDatabaseStrategy, settings.getDominoServer(), settings.getNotesCalendarDbFilePath(), settings.getSyncStartDate(), settings.getSyncEndDate());
	}
}
