package de.jakop.ngcalsync.oauth;

/**
 * Receiver for user input
 *
 * @author fjakop
 */
public interface IUserInputReceiver {

	/** 
	 * Waits for a verification code. 
	 * 
	 * @param message the message presented to the user
	 */
	String waitForUserInput(String message);

}