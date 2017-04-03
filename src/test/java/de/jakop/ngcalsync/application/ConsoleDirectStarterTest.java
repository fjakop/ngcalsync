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
package de.jakop.ngcalsync.application;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.jakop.ngcalsync.settings.Settings;

/**
 *
 * @author fjakop
 *
 */
public class ConsoleDirectStarterTest {

	@Mock
	private Application application;

	/** */
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void startApplication_reloadSettingsFails_doesNotSynchronize() throws Exception {

		doReturn(Boolean.TRUE).when(application).reloadSettings();

		new ConsoleDirectStarter().startApplication(application, mock(Settings.class));

		verify(application, times(1)).reloadSettings();
		verifyNoMoreInteractions(application);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void startApplication_reloadSettingsSucceeds_synchronizes() throws Exception {

		doReturn(Boolean.FALSE).when(application).reloadSettings();

		new ConsoleDirectStarter().startApplication(application, mock(Settings.class));

		verify(application, times(1)).reloadSettings();
		verify(application, times(1)).synchronize();
		verifyNoMoreInteractions(application);
	}
}
