package de.jakop.ngcalsync.oauth;

import javax.swing.JOptionPane;

import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;

/**
 * 
 * @author fjakop
 *
 */
public class GuiReceiver implements IUserInputReceiver {

	@Override
	public String waitForUserInput(final String message) {
		final String code = JOptionPane.showInputDialog(null, message, UserMessage.get().TITLE_INPUT_REQUESTED(), JOptionPane.QUESTION_MESSAGE);
		return code;
	}

}
