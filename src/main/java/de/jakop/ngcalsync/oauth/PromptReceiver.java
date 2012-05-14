package de.jakop.ngcalsync.oauth;

import java.util.Scanner;

import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;

import de.jakop.ngcalsync.Constants;


/**
 * Verification code receiver that prompts user to paste the code copied from the browser.
 *
 * @author Yaniv Inbar
 */
public class PromptReceiver implements VerificationCodeReceiver {


	@Override
	public String waitForCode() {
		String code;
		do {
			System.out.print(Constants.MSG_ENTER_CODE);
			code = new Scanner(System.in).nextLine();
		} while (code.isEmpty());
		return code;
	}

	@Override
	public String getRedirectUri() {
		return GoogleOAuthConstants.OOB_REDIRECT_URI;
	}

	@Override
	public void stop() {
		// nop
	}
}