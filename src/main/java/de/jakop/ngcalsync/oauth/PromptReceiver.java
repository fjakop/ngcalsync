package de.jakop.ngcalsync.oauth;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;

import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;


/**
 * Verification code receiver that prompts user to paste the code copied from the browser.
 *
 * @author Yaniv Inbar
 */
public class PromptReceiver implements VerificationCodeReceiver {

	private final InputStream in;
	private final PrintStream out;

	/**
	 * 
	 */
	public PromptReceiver() {
		in = System.in;
		out = System.out;
	}

	/**
	 * 
	 * @param in
	 * @param out
	 */
	public PromptReceiver(final InputStream in, final PrintStream out) {
		this.in = in;
		this.out = out;
	}

	@Override
	public String waitForCode() {
		String code;
		do {
			out.print(UserMessage.get().MSG_ENTER_CODE());
			code = new Scanner(in).nextLine();
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