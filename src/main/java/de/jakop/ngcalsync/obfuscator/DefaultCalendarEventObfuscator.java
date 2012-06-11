package de.jakop.ngcalsync.obfuscator;

import org.apache.commons.lang3.Validate;

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
		Validate.notNull(privacySettings);
		this.privacySettings = privacySettings;
	}

	@Override
	public void obfuscate(final CalendarEvent event) {
		if (!privacySettings.isTransferTitle()) {
			event.setTitle(event.getEventType().getName());
		}
		if (!privacySettings.isTransferDescription()) {
			event.setContent("");
		}
		if (!privacySettings.isTransferLocation()) {
			event.setLocation("");
		}
	}

}
