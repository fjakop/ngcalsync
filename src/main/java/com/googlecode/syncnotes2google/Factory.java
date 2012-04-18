package com.googlecode.syncnotes2google;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.AuthenticationException;

import de.bea.domingo.DDatabase;
import de.bea.domingo.DNotesException;
import de.bea.domingo.DNotesFactory;
import de.bea.domingo.DNotesRuntimeException;
import de.bea.domingo.DSession;

public class Factory {

	private static Factory instance = null;
	private DSession notesSession = null;
	private Settings settings = null;
	private CalendarService calendarService = null;
	private DDatabase mailDatabase = null;

	private String googlePassword;

	private Factory() {
	}

	public void freeNotesObject() {
		if (mailDatabase != null) {
			mailDatabase = null;
		}
		if (notesSession != null) {
			notesSession = null;
		}
	}

	public static Factory getInstance() {
		if (instance == null) {
			instance = new Factory();
		}
		return instance;
	}

	public DSession getNotesSession() {
		if (notesSession == null) {
			try {
				DNotesFactory factory = DNotesFactory.getInstance();
				notesSession = factory.getSession();
			} catch (DNotesRuntimeException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return notesSession;
	}

	public Settings getSettings() {
		if (settings == null) {
			try {
				settings = new Settings();
			} catch (IOException e) {
				System.out.println("Could not instanciate Settings.");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return settings;
	}

	public CalendarService getCalendarService() {

		if (calendarService == null) {
			Settings mySets = getSettings();
			// if (!mySets.getProxyHost().equals("") && !mySets.getProxyPort().equals("")) {
			// System.setProperty("http.proxyHost", mySets.getProxyHost());
			// System.setProperty("http.proxyPort", mySets.getProxyPort());
			// System.setProperty("https.proxyHost", mySets.getProxyHost());
			// System.setProperty("https.proxyPort", mySets.getProxyPort());
			// }
			// if (!mySets.getProxyUserName().equals("") && !mySets.getProxyPassword().equals("")) {
			// System.setProperty("http.proxyUserName", mySets.getProxyUserName());
			// System.setProperty("http.proxyPassword", mySets.getProxyPassword());
			// System.setProperty("https.proxyUserName", mySets.getProxyUserName());
			// System.setProperty("https.proxyPassword", mySets.getProxyPassword());
			// }
			calendarService = new CalendarService("SyncNotes2Google");
			try {
				calendarService.setUserCredentials(mySets.getGoogleAccountName(), getGooglePassword());
			} catch (AuthenticationException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return calendarService;
	}

	private String getGooglePassword() {
		if (googlePassword == null) {
			googlePassword = JOptionPane.showInputDialog(null, "Passwort für Google-Kalendar eingeben");
		}

		return googlePassword;
	}

	public DDatabase getMailDatabase() {

		if (mailDatabase == null) {
			try {
				Settings settings = getSettings();
				DSession notesSession = getNotesSession();
				String dominoServer = settings.getDominoServer();
				String mailDbFilePath = settings.getMailDbFilePath();
				mailDatabase = notesSession.getDatabase(dominoServer, mailDbFilePath);
				if (mailDatabase.isOpen() == false) {
					mailDatabase.open();
				}
				return mailDatabase;
			} catch (DNotesException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return mailDatabase;

	}

}
