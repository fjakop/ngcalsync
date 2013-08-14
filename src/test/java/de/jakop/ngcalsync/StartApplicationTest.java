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
