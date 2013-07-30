package de.jakop.ngcalsync.exception;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import de.jakop.ngcalsync.exception.SynchronisationException;

/**
 * 
 * @author fjakop
 *
 */
public class SynchronisationExceptionTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSynchronisationException_CauseIsPassedToSuper() throws Exception {
		final Exception cause = new Exception();

		assertSame(cause, new SynchronisationException(cause).getCause());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSynchronisationException_MessageIsPassedToSuper() throws Exception {
		final String message = "foo";

		assertSame(message, new SynchronisationException(message).getMessage());
	}
}
