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
			event.setContent(new String());
		}
		if (!privacySettings.isTransferLocation()) {
			event.setLocation(new String());
		}
	}

}
