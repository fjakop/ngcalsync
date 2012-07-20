package de.jakop.ngcalsync.notes;

import de.bea.domingo.DDatabase;

/**
 * 
 * @author fjakop
 *
 */
public interface IOpenDatabaseStrategy {

	/**
	 * 
	 * @param dominoServer
	 * @param database
	 * @return the open database
	 */
	public DDatabase openDatabase(String dominoServer, String database);

}
