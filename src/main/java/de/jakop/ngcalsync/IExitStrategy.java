package de.jakop.ngcalsync;

/**
 * Strategy for exiting the program.
 * 
 * @author fjakop
 *
 */
public interface IExitStrategy {

	/**
	 * Exits the program with given code.
	 * 
	 * @param code
	 */
	public void exit(int code);

}
