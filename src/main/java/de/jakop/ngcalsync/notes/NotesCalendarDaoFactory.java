package de.jakop.ngcalsync.notes;

import de.jakop.ngcalsync.settings.Settings;

/**
 * 
 * @author fjakop
 *
 */
public class NotesCalendarDaoFactory {

	private final IOpenDatabaseStrategy openDatabaseStrategy;

	/**
	 * 
	 * @param openDatabaseStrategy
	 */
	public NotesCalendarDaoFactory(final IOpenDatabaseStrategy openDatabaseStrategy) {
		this.openDatabaseStrategy = openDatabaseStrategy;
	}

	/**
	 * Creates a {@link NotesCalendarDAO}
	 * 
	 * @param settings
	 * @return a new {@link NotesCalendarDAO}
	 */
	public INotesCalendarDAO createNotesCalendarDao(final Settings settings) {
		return new NotesCalendarDAO(openDatabaseStrategy, settings.getDominoServer(), settings.getNotesCalendarDbFilePath(), settings.getSyncStartDate(), settings.getSyncEndDate());
	}
}
