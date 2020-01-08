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
package de.jakop.ngcalsync.settings;

import de.jakop.ngcalsync.i18n.LocalizedConfigurationStrings.ConfigurationDescription;

// no i18n for now
enum ConfigurationParameter {

	SYNC_RECURRENCE("sync.recurrence", "0 */15 * * * ?", ConfigurationDescription.get().SYNC_RECURRENCE()), // //$NON-NLS-1$ //$NON-NLS-2$

	SYNC_SCHEDULER_START("sync.scheduler.start", "false", ConfigurationDescription.get().SYNC_SCHEDULER_START()), // //$NON-NLS-1$ //$NON-NLS-2$

	SYNC_TYPES("sync.types", "3", ConfigurationDescription.get().SYNC_TYPES()), // //$NON-NLS-1$ //$NON-NLS-2$

	SYNC_END("sync.end", "3m", ConfigurationDescription.get().SYNC_END()), // //$NON-NLS-1$ //$NON-NLS-2$

	SYNC_START("sync.start", "14d", ConfigurationDescription.get().SYNC_START()), // //$NON-NLS-1$ //$NON-NLS-2$

	SYNC_TRANSFER_TITLE("sync.transfer.title", "false", ConfigurationDescription.get().SYNC_TRANSFER_TITLE()), // //$NON-NLS-1$ //$NON-NLS-2$

	SYNC_TRANSFER_DESCRIPTION("sync.transfer.description", "false", ConfigurationDescription.get().SYNC_TRANSFER_DESCRIPTION()), // //$NON-NLS-1$ //$NON-NLS-2$

	SYNC_TRANSFER_LOCATION("sync.transfer.location", "false", ConfigurationDescription.get().SYNC_TRANSFER_LOCATION()), // //$NON-NLS-1$ //$NON-NLS-2$

	NOTES_MAIL_DB_FILE("notes.mail.db.file", "", ConfigurationDescription.get().NOTES_MAIL_DB_FILE()), // //$NON-NLS-1$ //$NON-NLS-2$

	NOTES_DOMINO_SERVER("notes.domino.server", "", ConfigurationDescription.get().NOTES_DOMINO_SERVER()), // //$NON-NLS-1$ //$NON-NLS-2$

	GOOGLE_CALENDAR_REMINDERMINUTES("google.calendar.reminderminutes", "30", ConfigurationDescription.get().GOOGLE_CALENDAR_REMINDERMINUTES()), // //$NON-NLS-1$ //$NON-NLS-2$

	GOOGLE_CALENDAR_NAME("google.calendar.name", "", ConfigurationDescription.get().GOOGLE_CALENDAR_NAME()), // //$NON-NLS-1$ //$NON-NLS-2$

	GOOGLE_ACCOUNT_EMAIL("google.account.email", "", ConfigurationDescription.get().GOOGLE_ACCOUNT_EMAIL()), // //$NON-NLS-1$ //$NON-NLS-2$

	PROXY_HOST("proxy.host", "", ConfigurationDescription.get().PROXY_HOST()), // //$NON-NLS-1$ //$NON-NLS-2$

	PROXY_PORT("proxy.port", "", ConfigurationDescription.get().PROXY_PORT()), // //$NON-NLS-1$ //$NON-NLS-2$

	PROXY_USER("proxy.user", "", ConfigurationDescription.get().PROXY_USER()), // //$NON-NLS-1$ //$NON-NLS-2$

	PROXY_PASSWORD("proxy.password", "", ConfigurationDescription.get().PROXY_PASSWORD()), //$NON-NLS-1$ //$NON-NLS-2$

	POPUP_THRESHOLD_LEVEL("popup.threshold.level", "WARN", ConfigurationDescription.get().POPUP_THRESHOLD_LEVEL()); //$NON-NLS-1$ //$NON-NLS-2$

	private final String key;
	private final String defaultvalue;
	private final String comment;

	ConfigurationParameter(final String key, final String defaultvalue, final String comment) {
		this.key = key;
		this.defaultvalue = defaultvalue;
		this.comment = comment;
	}

	String getKey() {
		return key;
	}

	String getDefaultValue() {
		return defaultvalue;
	}

	String getComment() {
		return comment;
	}

}