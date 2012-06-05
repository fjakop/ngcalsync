package de.jakop.ngcalsync.oauth;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * 
 * @author fjakop
 *
 */
public class PromptReceiverTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetRedirectUri() throws Exception {
		assertEquals("urn:ietf:wg:oauth:2.0:oob", new PromptReceiver().getRedirectUri());
	}

	/**
	* 
	* @throws Exception
	*/
	@Test
	public void testGetCode() throws Exception {

		final InputStream in = new ByteArrayInputStream(new byte[] { 'f', 'o', 'o' });
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		final PromptReceiver promptReceiver = new PromptReceiver(in, new PrintStream(out));
		final String code = promptReceiver.waitForCode();

		// TODO i18n
		assertEquals("Please enter code obtained from website: ", out.toString());
		assertEquals("foo", code);
	}

}