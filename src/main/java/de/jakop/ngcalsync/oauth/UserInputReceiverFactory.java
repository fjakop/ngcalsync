package de.jakop.ngcalsync.oauth;

public class UserInputReceiverFactory {

	private UserInputReceiverFactory() {
		//
	}

	public static IUserInputReceiver createPromptReceiver() {
		return new PromptReceiver();
	}

	public static IUserInputReceiver createGuiReceiver() {
		return new GuiReceiver();
	}

}
