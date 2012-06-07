package de.jakop.ngcalsync.settings;

enum ConfigurationParameter {

	SYNC_TYPES("sync.types", "3", "# Types of events to sync\n" + //
			"# 0 = Normal event\n" + //
			"# 1 = Anniversary\n" + //
			"# 2 = All day event\n" + //
			"# 3 = Meeting\n" + //
			"# 4 = Reminder\n" + //
			"# e.g. \"1,3,4\""), //

	SYNC_END("sync.end", "3m", "# Number of days(ex. 15d) or month (ex. 2m) in the future, default 3 month"), //

	SYNC_START("sync.start", "14d", "# Number of days (ex. 15d) or month (ex. 2m) back in time, default 14 days"), //

	SYNC_TRANSFER_TITLE("sync.transfer.title", "false", "# Transfer original event title to Google (true|false)"), //

	SYNC_TRANSFER_DESCRIPTION("sync.transfer.description", "false", "# Transfer original event description to Google (true|false)"), //

	SYNC_TRANSFER_LOCATION("sync.transfer.location", "false", "# Transfer original event location to Google (true|false)"), //

	NOTES_MAIL_DB_FILE("notes.mail.db.file", "", "# Notes database name\n" + //
			"#  in Notes go to\n" + //
			"#  Notes File/Preferences/Location Preferences.../Mail/'Mail file', if there \n" + //
			"#  are \\ in the path replace them with /"), //

	NOTES_DOMINO_SERVER("notes.domino.server", "", "# Notes server name\n" + //
			"#  in Notes go to\n" + //
			"#  File/Preferences/Location Preferences.../Servers/'Home/mail server', if there \n" + //
			"#  are \\ in the path replace them with /\n" + //
			"#  Leave blank for local."), //

	GOOGLE_CALENDAR_REMINDERMINUTES("google.calendar.reminderminutes", "30", "# Google default reminder time"), //

	GOOGLE_CALENDAR_NAME("google.calendar.name", "", "# Google calendar name to sync with (e.g. \"work\")\n" + //
			"# WARNING #\n" + // 
			"# This calendar's events will be deleted if not present in Lotus Notes"), //

	GOOGLE_ACCOUNT_EMAIL("google.account.email", "", "# Google account email"), //

	PROXY_HOST("proxy.host", "", "# Hostname or IP of the proxy server, if you are behind a proxy"), //

	PROXY_PORT("proxy.port", "", "# Port of the proxy server, if you are behind a proxy"), //

	PROXY_USER("proxy.user", "", "# Username, if the proxy requires authentication"), //

	PROXY_PASSWORD("proxy.password", "", "# Password, if the proxy requires authentication");

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