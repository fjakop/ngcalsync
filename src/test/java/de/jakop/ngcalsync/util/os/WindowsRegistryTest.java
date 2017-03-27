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
package de.jakop.ngcalsync.util.os;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.mockito.Matchers;

/**
 * 
 * @author fjakop
 *
 */
public class WindowsRegistryTest {

	/**
	 * @throws Exception
	 */
	@Test
	public void testReadRegistry_valueDoesNotContainTab_returnsNull() throws Exception {
		final Process process = mock(Process.class);
		when(process.getInputStream()).thenReturn(IOUtils.toInputStream("\r\n! REG.EXE  Path  REG_SZ  C:\\foo"));

		final IRegistryQueryProcessFactory factory = mock(IRegistryQueryProcessFactory.class);
		when(factory.createQueryProcess("foo", "bar")).thenReturn(process);

		final String result = new WindowsRegistry(factory).readRegistry("foo", "bar");
		assertNull(result);

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testReadRegistry_createProcessThrowsIOException_returnsNull() throws Exception {
		final IRegistryQueryProcessFactory factory = mock(IRegistryQueryProcessFactory.class);
		when(factory.createQueryProcess("foo", "bar")).thenThrow(new IOException());

		final WindowsRegistry windowsRegistry = new WindowsRegistry(factory);
		final Log logMock = mock(Log.class);
		windowsRegistry.log = logMock;
		final String result = windowsRegistry.readRegistry("foo", "bar");

		verify(logMock).error(Matchers.eq("LocalizedUserStrings.MSG_FAILED_TO_READ_REGISTRY(\"bar\")"), Matchers.any(IOException.class));
		assertNull(result);

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testReadRegistry_realWindowsXPOutput_returnsCorrectValue() throws Exception {
		final Process process = mock(Process.class);
		when(process.getInputStream()).thenReturn(
				IOUtils.toInputStream("\r\n! REG.EXE VERSION 3.0\r\n\r\nHKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes\r\n    Path\tREG_SZ\tC:\\Programme\\IBM\\Lotus\\Notes\\\r\n\r\n"));

		final IRegistryQueryProcessFactory factory = mock(IRegistryQueryProcessFactory.class);
		when(factory.createQueryProcess("HKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes", "Path")).thenReturn(process);

		final String result = new WindowsRegistry(factory).readRegistry("HKEY_LOCAL_MACHINE\\Software\\Lotus\\Notes", "Path");
		assertEquals("C:\\Programme\\IBM\\Lotus\\Notes\\\r\n\r\n", result);

	}
}
