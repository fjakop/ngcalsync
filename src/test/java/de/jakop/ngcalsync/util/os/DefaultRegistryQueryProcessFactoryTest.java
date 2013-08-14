package de.jakop.ngcalsync.util.os;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

/**
 * 
 * @author fjakop
 */
public class DefaultRegistryQueryProcessFactoryTest {

	@SuppressWarnings("javadoc")
	@Test
	public void testCreateProcess() throws Exception {
		// Setup
		final DefaultRegistryQueryProcessFactory factorySpy = spy(new DefaultRegistryQueryProcessFactory());
		final Runtime runtimeSpy = spy(Runtime.getRuntime());
		when(factorySpy.getRuntime()).thenReturn(runtimeSpy);

		// Run
		factorySpy.createQueryProcess("foo", "bar");

		// Assert
		verify(runtimeSpy).exec("reg query \"foo\" /v bar");
	}
}
