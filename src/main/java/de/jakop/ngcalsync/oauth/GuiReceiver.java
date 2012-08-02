package de.jakop.ngcalsync.oauth;

import javax.swing.JOptionPane;

import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;

import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;

/**
 * 
 * @author fjakop
 *
 */
public class GuiReceiver implements VerificationCodeReceiver {

	@Override
	public String getRedirectUri() {
		return GoogleOAuthConstants.OOB_REDIRECT_URI;
	}

	@Override
	public String waitForCode() {
		final String code = JOptionPane.showInputDialog(null, UserMessage.get().MSG_ENTER_VERIFICATION_CODE(), UserMessage.get().TITLE_ENTER_VERIFICATION_CODE(),
				JOptionPane.QUESTION_MESSAGE);
		return code;
	}

	@Override
	public void stop() {
		// nop
	}

}
