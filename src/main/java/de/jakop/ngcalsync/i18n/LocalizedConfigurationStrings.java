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
package de.jakop.ngcalsync.i18n;

import com.github.rodionmoiseev.c10n.C10N;
import com.github.rodionmoiseev.c10n.annotations.En;

/**
 * Contains localisation of all strings that are visible in the configuration file(s).
 *
 * @author fjakop
 *
 */
public interface LocalizedConfigurationStrings {

	/**
	 * static convenience getter for {@link LocalizedConfigurationStrings}
	 *
	 * @author fjakop
	 *
	 */
	public final static class ConfigurationDescription {

		private ConfigurationDescription() {
			// not to be instantiated
		}

		/**
		 * @return the annotated {@link LocalizedConfigurationStrings} interface
		 */
		public static LocalizedConfigurationStrings get() {
			return C10N.get(LocalizedConfigurationStrings.class);
		}
	}

	@SuppressWarnings("javadoc")
	@En("# Configuration file for ngcalsync")
	String CONFIG_FILE_HEADER();

	@SuppressWarnings("javadoc")
	@En("# Cron expression for the recurrence of synchronisation when the scheduler is active\n" + //
			"# Default is every 15 minutes\n" + //
			"# see http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06.html")
	String SYNC_RECURRENCE();

	@SuppressWarnings("javadoc")
	@En("# Start the scheduler on application startup")
	String SYNC_SCHEDULER_START();

	@SuppressWarnings("javadoc")
	@En("# Types of events to sync\n" + //
			"# 0 = Normal event\n" + //
			"# 1 = Anniversary\n" + //
			"# 2 = All day event\n" + //
			"# 3 = Meeting\n" + //
			"# 4 = Reminder\n" + //
			"# e.g. \"1,3,4\"")
	String SYNC_TYPES();

	@SuppressWarnings("javadoc")
	@En("# Number of days(ex. 15d) or month (ex. 2m) in the future, default 3 month")
	String SYNC_END();

	@SuppressWarnings("javadoc")
	@En("# Number of days (ex. 15d) or month (ex. 2m) back in time, default 14 days")
	String SYNC_START();

	@SuppressWarnings("javadoc")
	@En("# Transfer original event title to Google (true|false)")
	String SYNC_TRANSFER_TITLE();

	@SuppressWarnings("javadoc")
	@En("# Transfer original event description to Google (true|false)")
	String SYNC_TRANSFER_DESCRIPTION();

	@SuppressWarnings("javadoc")
	@En("# Transfer original event location to Google (true|false)")
	String SYNC_TRANSFER_LOCATION();

	@SuppressWarnings("javadoc")
	@En("# Notes database name\n" + //
			"#  in Notes go to\n" + //
			"#  Notes File/Preferences/Location Preferences.../Mail/'Mail file', if there \n" + //
			"#  are \\ in the path replace them with /")
	String NOTES_MAIL_DB_FILE();

	@SuppressWarnings("javadoc")
	@En("# Notes server name\n" + //
			"#  in Notes go to\n" + //
			"#  File/Preferences/Location Preferences.../Servers/'Home/mail server', if there \n" + //
			"#  are \\ in the path replace them with /\n" + //
			"#  Leave blank for local.")
	String NOTES_DOMINO_SERVER();

	@SuppressWarnings("javadoc")
	@En("# Google default reminder time")
	String GOOGLE_CALENDAR_REMINDERMINUTES();

	@SuppressWarnings("javadoc")
	@En("# Google calendar name to sync with (e.g. \"work\")\n" + //
			"# WARNING #\n" + //
			"# This calendar's events will be deleted if not present in Lotus Notes")
	String GOOGLE_CALENDAR_NAME();

	@SuppressWarnings("javadoc")
	@En("# Google account email")
	String GOOGLE_ACCOUNT_EMAIL();

	@SuppressWarnings("javadoc")
	@En("# Hostname or IP of the proxy server, if you are behind a proxy")
	String PROXY_HOST();

	@SuppressWarnings("javadoc")
	@En("# Port of the proxy server, if you are behind a proxy")
	String PROXY_PORT();

	@SuppressWarnings("javadoc")
	@En("# Username, if the proxy requires authentication")
	String PROXY_USER();

	@SuppressWarnings("javadoc")
	@En("# Password, if the proxy requires authentication")
	String PROXY_PASSWORD();

	@SuppressWarnings("javadoc")
	@En("# When new logentry is equals or higher the threshold level the application log window will get visible. Sensible values include INFO, WARN, ERROR")
	String POPUP_THRESHOLD_LEVEL();

}
