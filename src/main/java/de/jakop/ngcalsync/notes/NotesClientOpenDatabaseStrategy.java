package de.jakop.ngcalsync.notes;

import de.bea.domingo.DDatabase;
import de.bea.domingo.DNotesException;
import de.bea.domingo.DNotesFactory;
import de.bea.domingo.DSession;

/**
 * 
 * @author fjakop
 *
 */
public class NotesClientOpenDatabaseStrategy implements IOpenDatabaseStrategy {

	@Override
	public DDatabase openDatabase(String dominoServer, String database) {

		try {
			DNotesFactory factory = DNotesFactory.getInstance();
			DSession notesSession = factory.getSession();
			DDatabase db = notesSession.getDatabase(dominoServer, database);
			if (db.isOpen() == false) {
				db.open();
			}
			return db;
		} catch (DNotesException e) {
			throw new RuntimeException(e);
		}
	}

}
