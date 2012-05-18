package de.jakop.ngcalsync.obfuscator;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.settings.PrivacySettings;

/**
 * Obfuscates title, description and location, if requested.  
 * 
 * @author fjakop
 */
public class DefaultCalendarEventObfuscator implements ICalendarEventObfuscator {

	private final PrivacySettings privacySettings;

	/**
	 * 
	 * @param privacySettings
	 */
	public DefaultCalendarEventObfuscator(final PrivacySettings privacySettings) {
		this.privacySettings = privacySettings;
	}

	@Override
	public void obfuscate(final CalendarEvent event) {
		if (!privacySettings.isTransferTitle()) {
			event.setTitle(event.getApptype().getName());
		}
		if (!privacySettings.isTransferDescription()) {
			event.setContent("");
		}
		if (!privacySettings.isTransferLocation()) {
			event.setLocation("");
		}
	}

}
