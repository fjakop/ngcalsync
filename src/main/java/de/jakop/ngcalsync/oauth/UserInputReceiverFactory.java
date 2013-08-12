package de.jakop.ngcalsync.oauth;

/**
 * Creates {@link IUserInputReceiver}s for different use-cases.
 * 
 * @author fjakop
 *
 */
public class UserInputReceiverFactory {

	private UserInputReceiverFactory() {
		//
	}

	/**
	 * Creates an {@link IUserInputReceiver} which can prompt the user on the command line
	 * @return {@link IUserInputReceiver} which can prompt the user on the command line
	 */
	public static IUserInputReceiver createPromptReceiver() {
		return new PromptReceiver();
	}

	/**
	 * Creates an {@link IUserInputReceiver} which can prompt the user in a windowing environment
	 * @return {@link IUserInputReceiver} which can prompt the user in a windowing environment
	 */
	public static IUserInputReceiver createGuiReceiver() {
		return new GuiReceiver();
	}

}
