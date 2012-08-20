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
