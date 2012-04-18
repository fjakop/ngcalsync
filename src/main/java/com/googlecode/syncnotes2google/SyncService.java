package com.googlecode.syncnotes2google;

import com.googlecode.syncnotes2google.dao.BaseDAO;
import com.googlecode.syncnotes2google.dao.BaseDoc;
import com.googlecode.syncnotes2google.dao.GoogleCalendarDAO;
import com.googlecode.syncnotes2google.dao.NotesCalendarDAO;

public class SyncService {

	public void executeSync(NotesCalendarDAO fromDao, GoogleCalendarDAO toDao) {
		Settings mySets = Factory.getInstance().getSettings();

		System.out.println("Start " + fromDao.getDirection() + " to " + toDao.getDirection() + " synchronization.");

		BaseDoc entry = fromDao.getFirstEntry();
		while (entry != null) {

			String uid = entry.getRefId();
			if (uid == null) {
				// This if statement is for suppressing warning messages
				// "Recurrence calenar entry is not supported"
				// Without this if statement, warning message would appear on
				// every time to sync.
				if (entry.getLastUpdated().after(mySets.getSyncLastDateTime()) && entry.getLastUpdated().before(mySets.getSyncStart())) {
					if (mySets.getSyncDirection().equals(fromDao.getDirection()) || mySets.getSyncDirection().equals(Constants.BI_DIRECTION)) {
						// check if entry is recurrence.
						if (entry.getRecur() != null) {
							// Google don't accept too much number of recurrence
							// date.
							if (entry.getRecur().getRdate() != null) {
								if (entry.getRecur().getRdate().length > 100) {
									System.out.println("This type of entry is not supported by GooCalSync : " + entry.getTitle());
									entry = fromDao.getNextEntry();
									continue;
								}
							}
						}
						System.out.println("executing insert: " + entry.getTitle());
						insert(toDao, entry);
					}
				}
			} else {
				BaseDoc toEntry = toDao.select(uid);
				if (toEntry == null) {
					if (mySets.getSyncDirection().equals(toDao.getDirection()) || mySets.getSyncDirection().equals(Constants.BI_DIRECTION)) {
						System.out.println("executing delete " + entry.getTitle());
						delete(fromDao, entry);
					}
				} else {
					if (entry.getLastUpdated().after(mySets.getSyncLastDateTime()) && entry.getLastUpdated().before(mySets.getSyncStart())) {
						if (mySets.getSyncDirection().equals(Constants.BI_DIRECTION)) {
							System.out.println("executing update (BI_DIRECTION): " + entry.getTitle());
							update(toDao, entry);
						} else if (mySets.getSyncDirection().equals(fromDao.getDirection())) {
							// check if entry is recurrence.
							System.out.println("executing update (" + fromDao.getDirection() + "): " + entry.getTitle());
							update(toDao, entry);

						} else {
							// In the case of NOTES_TO_GOOGLE, do nothing.
						}
					}
				}
			}
			entry = fromDao.getNextEntry();
		}

	}

	private void insert(BaseDAO dao, BaseDoc entry) {
		String insert = dao.insert(entry);
		if (dao instanceof GoogleCalendarDAO) {
			IDTable.insert(entry.getId(), insert);
		} else {
			IDTable.insert(insert, entry.getId());
		}
	}

	private void delete(BaseDAO dao, BaseDoc entry) {
		dao.delete(entry.getId());
		IDTable.delete(entry.getId());
	}

	private void update(BaseDAO dao, BaseDoc entry) {
		dao.update(entry);
	}
}
