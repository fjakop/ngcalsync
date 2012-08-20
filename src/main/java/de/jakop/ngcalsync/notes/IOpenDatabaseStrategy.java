package de.jakop.ngcalsync.notes;

import de.bea.domingo.DDatabase;

/**
 * 
 * @author fjakop
 *
 */
interface IOpenDatabaseStrategy {

	/**
	 * 
	 * @param dominoServer
	 * @param database
	 * @return the open database
	 */
	public DDatabase openDatabase(String dominoServer, String database);

}
