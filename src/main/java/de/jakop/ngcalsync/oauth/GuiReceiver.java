package de.jakop.ngcalsync.oauth;

import javax.swing.JOptionPane;

import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;

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
		// TODO i18n
		final String code = JOptionPane.showInputDialog(null, "Please enter code", "Verification requested", JOptionPane.QUESTION_MESSAGE);
		return code;
	}

	@Override
	public void stop() {
		// nop
	}

}
