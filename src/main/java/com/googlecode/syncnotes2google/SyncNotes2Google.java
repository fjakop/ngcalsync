package com.googlecode.syncnotes2google;

import java.io.IOException;
import java.util.Calendar;

import com.googlecode.syncnotes2google.dao.GoogleCalendarDAO;
import com.googlecode.syncnotes2google.dao.NotesCalendarDAO;

public class SyncNotes2Google {

	public static void main(String[] args) throws IOException {

		System.out.println("SyncNotes2Google has started.");

		try {
			// Execute synchronization
			SyncService ss = new SyncService();

			ss.executeSync(new NotesCalendarDAO(), new GoogleCalendarDAO());

			// Update Last Sync Execution Date & Time
			Settings mySets = Factory.getInstance().getSettings();
			mySets.setSyncLastDateTime(Calendar.getInstance());
			mySets.saveSetDoc();
		} finally {
			IDTable.save();
		}

		// Recycle Notes related objects.
		// If don't do this, Notes initialization failure would occur after several executions.
		// Factory.freeNotesObject();

		System.out.println("SyncNotes2Google has ended.");

	}

}
