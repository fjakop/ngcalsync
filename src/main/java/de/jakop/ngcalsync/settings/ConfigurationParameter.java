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
	POPUP_THRESHOLD_LEVEL("popup.threshold.level", "", ConfigurationDescription.get().POPUP_THRESHOLD_LEVEL()); //$NON-NLS-1$ //$NON-NLS-2$

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