package de.jakop.ngcalsync.google;

import de.jakop.ngcalsync.settings.Settings;

/**
 * 
 * @author fjakop
 *
 */
public class GoogleCalendarDaoFactory {

	/**
	 * Creates a {@link GoogleCalendarDAO}
	 * 
	 * @param settings
	 * @return a new {@link GoogleCalendarDAO}
	 */
	public IGoogleCalendarDAO createGoogleCalendarDao(final Settings settings) {
		return new GoogleCalendarDAO(settings);
	}
}
