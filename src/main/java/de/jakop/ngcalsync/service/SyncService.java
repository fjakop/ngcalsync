package de.jakop.ngcalsync.service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.CalendarEvent;
import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.Settings;
import de.jakop.ngcalsync.SynchronisationException;
import de.jakop.ngcalsync.filter.ICalendarEntryFilter;
import de.jakop.ngcalsync.google.GoogleCalendarDAO;
import de.jakop.ngcalsync.notes.NotesCalendarDAO;
import de.jakop.ngcalsync.obfuscator.ICalendarEntryObfuscator;

/**
 * Synchronisiert vom {@link NotesCalendarDAO} in das {@link GoogleCalendarDAO}
 * 
 * @author fjakop
 *
 */
public class SyncService {

	private final Log log = LogFactory.getLog(getClass());

	/**
	 * 
	 * @param notesDao
	 * @param googleDao
	 * @param settings TODO
	 */
	public void executeSync(NotesCalendarDAO notesDao, GoogleCalendarDAO googleDao, ICalendarEntryFilter[] filters, ICalendarEntryObfuscator[] obfuscators, Settings settings) {
		Collection<CalendarEvent> notesEntries = notesDao.getEntries(filters);
		Collection<CalendarEvent> googleEntries = googleDao.getEntries(filters);

		// schedule Events existing in Google but not in Notes for removal
		List<CalendarEvent> removeFromGoogle = new ArrayList<CalendarEvent>();
		for (CalendarEvent baseDoc : googleEntries) {
			if (CollectionUtils.select(notesEntries, new BaseDocEqualsPredicate(baseDoc)).isEmpty()) {
				removeFromGoogle.add(baseDoc);
				log.debug(String.format("Scheduling for removal: %s", BaseDocEqualsPredicate.getComparisonString(baseDoc)));
			}
		}

		// schedule Events existing in Notes but not in Google for addition
		List<CalendarEvent> addToGoogle = new ArrayList<CalendarEvent>();
		Map<CalendarEvent, CalendarEvent> updateToGoogle = new HashMap<CalendarEvent, CalendarEvent>();
		for (CalendarEvent notesEntry : notesEntries) {
			Collection<CalendarEvent> matchingEntries = CollectionUtils.select(googleEntries, new BaseDocEqualsPredicate(notesEntry));
			if (matchingEntries.isEmpty()) {
				addToGoogle.add(notesEntry);
				log.debug(String.format("Scheduling for addition: %s", BaseDocEqualsPredicate.getComparisonString(notesEntry)));
			} else {
				if (matchingEntries.size() > 1) {
					throw new SynchronisationException(String.format("Duplicate match (%s) for %s", new Integer(matchingEntries.size()), notesEntry));
				}
				CalendarEvent matchingEntry = matchingEntries.iterator().next();
				// check modification and update eventually
				if (notesEntry.getLastUpdated().after(settings.getSyncLastDateTime())) {
					updateToGoogle.put(notesEntry, matchingEntry);
					log.debug(String.format("Scheduling for update: %s", BaseDocEqualsPredicate.getComparisonString(notesEntry)));
				} else {
					log.debug(String.format("No update scheduled (not modified): %s", BaseDocEqualsPredicate.getComparisonString(notesEntry)));
				}
			}
		}

		// actually do it

		log.info(String.format(Constants.MSG_REMOVING_EVENTS_FROM_GOOGLE, new Integer(removeFromGoogle.size())));
		for (CalendarEvent baseDoc : removeFromGoogle) {
			delete(googleDao, baseDoc.getId());
		}

		log.info(String.format(Constants.MSG_ADDING_EVENTS_TO_GOOGLE, new Integer(addToGoogle.size())));
		for (CalendarEvent baseDoc : addToGoogle) {
			// obfuscate
			for (ICalendarEntryObfuscator obfuscator : obfuscators) {
				obfuscator.obfuscate(baseDoc);
			}
			insert(googleDao, baseDoc);
		}

		log.info(String.format(Constants.MSG_UPDATING_EVENTS_TO_GOOGLE, new Integer(updateToGoogle.size())));
		for (CalendarEvent notesEntry : updateToGoogle.keySet()) {
			// obfuscate
			for (ICalendarEntryObfuscator obfuscator : obfuscators) {
				obfuscator.obfuscate(notesEntry);
			}
			update(googleDao, updateToGoogle.get(notesEntry).getId(), notesEntry);
		}


	}

	static class BaseDocEqualsPredicate implements Predicate<CalendarEvent> {

		private CalendarEvent baseDoc;

		public BaseDocEqualsPredicate(CalendarEvent baseDoc) {
			Validate.notNull(baseDoc);
			this.baseDoc = baseDoc;
		}

		public boolean evaluate(CalendarEvent object) {

			if (object.isAllDay() && baseDoc.isAllDay()) {
				return object.getStartDateTime().get(Calendar.YEAR) == baseDoc.getStartDateTime().get(Calendar.YEAR) && //
						object.getStartDateTime().get(Calendar.MONTH) == baseDoc.getStartDateTime().get(Calendar.MONTH) && //
						object.getStartDateTime().get(Calendar.DAY_OF_YEAR) == baseDoc.getStartDateTime().get(Calendar.DAY_OF_YEAR);
			}
			if (object.getStartDateTime() == null || baseDoc.getStartDateTime() == null) {
				return false;
			}
			if (object.getEndDateTime() == null || baseDoc.getEndDateTime() == null) {
				return false;
			}

			return baseDoc.getStartDateTime().equals(object.getStartDateTime()) && //
					baseDoc.getEndDateTime().equals(object.getEndDateTime());

		}

		/**
		 * For logging, a condensed representation of the event
		 * @param doc
		 * @return a condensed representation of the event
		 */
		public static String getComparisonString(CalendarEvent doc) {
			DateFormat df = DateFormat.getDateTimeInstance();
			return new StringBuilder()//
					.append(doc.getApptype() == null ? null : doc.getApptype().getName())//
					.append(": ")//
					.append(doc.getStartDateTime() == null ? null : df.format(doc.getStartDateTime().getTime()))//
					.append(" -> ")//
					.append(doc.getEndDateTime() == null ? null : df.format(doc.getEndDateTime().getTime()))//
					.toString();
		}

	}

	class BaseDocLastUpdatedAfterPredicate implements Predicate<CalendarEvent> {

		private Calendar calendar;

		public BaseDocLastUpdatedAfterPredicate(Calendar calendar) {
			Validate.notNull(calendar);
			this.calendar = calendar;
		}

		public boolean evaluate(CalendarEvent object) {
			if (object.getLastUpdated() == null || object.getLastUpdated().after(calendar)) {
				return true;
			}
			return false;
		}
	}

	private void insert(GoogleCalendarDAO dao, CalendarEvent entry) {
		try {
			dao.insert(entry);
		} catch (SynchronisationException e) {
			log.error("Error inserting entry");
			log.error(entry);
			e.printStackTrace();
		}
	}

	private void delete(GoogleCalendarDAO dao, String id) {
		dao.delete(id);
	}

	private void update(GoogleCalendarDAO dao, String id, CalendarEvent entry) {
		dao.update(id, entry);
	}
}
