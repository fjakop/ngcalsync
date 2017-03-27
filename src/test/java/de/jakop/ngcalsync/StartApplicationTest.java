/**
 * Copyright © 2012, Frank Jakop
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.jakop.ngcalsync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author fjakop
 *
 */
public class StartApplicationTest {

	/** expect an exception */
	@Rule
	public ExpectedException thrown = ExpectedException.none();


	/**
	 * No arguments on the command line result in no options.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseCommandLine_NoArguments_HasNoOptions() throws Exception {
		// Setup
		final StartApplication main = new StartApplication();

		// Run
		final CommandLine cmd = main.parseCommandLine(new String[] { "" });

		// Assert
		assertEquals(0, cmd.getOptions().length);
	}

	/**
	 * Invalid arguments on the command line result in an exception.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseCommandLine_InvalidArguments_ThrowsException() throws Exception {
		// Setup
		final StartApplication main = new StartApplication();

		thrown.expect(UnrecognizedOptionException.class);
		thrown.expectMessage("Unrecognized option: -foo");

		// Run
		main.parseCommandLine(new String[] { "-foo" });
	}

	/**
	 * Argument "-console" is valid.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseCommandLine_ArgumentConsole_HasOptionConsole() throws Exception {
		// Setup
		final StartApplication main = new StartApplication();

		// Run
		final CommandLine cmd = main.parseCommandLine(new String[] { "-console" });

		// Assert
		assertEquals(1, cmd.getOptions().length);
		assertEquals("console", cmd.getOptions()[0].getOpt());

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testModeSelection_TrayIsSupported_TrayIsSelected() throws Exception {
		fail("not yet implemented");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testModeSelection_TrayIsNotSupported_ConsoleIsSelected() throws Exception {
		fail("not yet implemented");
	}

}
