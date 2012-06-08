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

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.SynchronisationException;
import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;
import de.jakop.ngcalsync.google.GoogleCalendarDAO;
import de.jakop.ngcalsync.notes.NotesCalendarDAO;
import de.jakop.ngcalsync.obfuscator.ICalendarEventObfuscator;
import de.jakop.ngcalsync.settings.Settings;

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
	 * @param settings 
	 */
	public void executeSync(final NotesCalendarDAO notesDao, final GoogleCalendarDAO googleDao, final ICalendarEventFilter[] filters, final ICalendarEventObfuscator[] obfuscators,
			final Settings settings) {

		Validate.notNull(notesDao);
		Validate.notNull(googleDao);
		Validate.notNull(settings);

		final Collection<CalendarEvent> notesEntries = notesDao.getEntries(filters);
		final Collection<CalendarEvent> googleEntries = googleDao.getEvents(filters);

		// schedule Events existing in Google but not in Notes for removal
		final List<CalendarEvent> removeFromGoogle = new ArrayList<CalendarEvent>();
		for (final CalendarEvent baseDoc : googleEntries) {
			if (CollectionUtils.select(notesEntries, new CalendarEventEqualsPredicate(baseDoc)).isEmpty()) {
				removeFromGoogle.add(baseDoc);
				// TODO i18n
				log.debug(String.format("Scheduling for removal: %s", CalendarEventEqualsPredicate.getComparisonString(baseDoc)));
			}
		}

		// schedule Events existing in Notes but not in Google for addition
		final List<CalendarEvent> addToGoogle = new ArrayList<CalendarEvent>();
		final Map<CalendarEvent, CalendarEvent> updateToGoogle = new HashMap<CalendarEvent, CalendarEvent>();
		for (final CalendarEvent notesEntry : notesEntries) {
			final Collection<CalendarEvent> matchingEntries = CollectionUtils.select(googleEntries, new CalendarEventEqualsPredicate(notesEntry));
			if (matchingEntries.isEmpty()) {
				addToGoogle.add(notesEntry);
				// TODO i18n
				log.debug(String.format("Scheduling for addition: %s", CalendarEventEqualsPredicate.getComparisonString(notesEntry)));
			} else {
				if (matchingEntries.size() > 1) {
					// TODO i18n
					throw new SynchronisationException(String.format("Duplicate match (%s) for %s", new Integer(matchingEntries.size()), notesEntry));
				}
				final CalendarEvent matchingEntry = matchingEntries.iterator().next();
				// check modification and update eventually
				if (notesEntry.getLastUpdated().after(settings.getSyncLastDateTime())) {
					updateToGoogle.put(notesEntry, matchingEntry);
					// TODO i18n
					log.debug(String.format("Scheduling for update: %s", CalendarEventEqualsPredicate.getComparisonString(notesEntry)));
				} else {
					// TODO i18n
					log.debug(String.format("No update scheduled (not modified): %s", CalendarEventEqualsPredicate.getComparisonString(notesEntry)));
				}
			}
		}

		// actually do it

		log.info(String.format(Constants.MSG_REMOVING_EVENTS_FROM_GOOGLE, new Integer(removeFromGoogle.size())));
		for (final CalendarEvent event : removeFromGoogle) {
			delete(googleDao, event.getId());
		}

		log.info(String.format(Constants.MSG_ADDING_EVENTS_TO_GOOGLE, new Integer(addToGoogle.size())));
		for (final CalendarEvent event : addToGoogle) {
			// obfuscate
			for (final ICalendarEventObfuscator obfuscator : obfuscators) {
				obfuscator.obfuscate(event);
			}
			insert(googleDao, event);
		}

		log.info(String.format(Constants.MSG_UPDATING_EVENTS_TO_GOOGLE, new Integer(updateToGoogle.size())));
		for (final CalendarEvent event : updateToGoogle.keySet()) {
			// obfuscate
			for (final ICalendarEventObfuscator obfuscator : obfuscators) {
				obfuscator.obfuscate(event);
			}
			update(googleDao, updateToGoogle.get(event).getId(), event);
		}


	}

	static class CalendarEventEqualsPredicate implements Predicate<CalendarEvent> {

		private final CalendarEvent event;

		public CalendarEventEqualsPredicate(final CalendarEvent baseDoc) {
			Validate.notNull(baseDoc);
			event = baseDoc;
		}

		@Override
		public boolean evaluate(final CalendarEvent object) {

			if (!object.getEventType().equals(event.getEventType())) {
				return false;
			}

			if (object.isAllDay() && event.isAllDay()) {
				return object.getStartDateTime().get(Calendar.YEAR) == event.getStartDateTime().get(Calendar.YEAR) && //
						object.getStartDateTime().get(Calendar.MONTH) == event.getStartDateTime().get(Calendar.MONTH) && //
						object.getStartDateTime().get(Calendar.DAY_OF_YEAR) == event.getStartDateTime().get(Calendar.DAY_OF_YEAR);
			}
			if (object.getStartDateTime() == null || event.getStartDateTime() == null) {
				return false;
			}
			if (object.getEndDateTime() == null || event.getEndDateTime() == null) {
				return false;
			}

			return event.getStartDateTime().equals(object.getStartDateTime()) && //
					event.getEndDateTime().equals(object.getEndDateTime());

		}

		/**
		 * For logging, a condensed representation of the event
		 * @param doc
		 * @return a condensed representation of the event
		 */
		public static String getComparisonString(final CalendarEvent doc) {
			final DateFormat df = DateFormat.getDateTimeInstance();
			return new StringBuilder()//
					.append(doc.getEventType() == null ? null : doc.getEventType().getName())//
					.append(": ")//
					.append(doc.getStartDateTime() == null ? null : df.format(doc.getStartDateTime().getTime()))//
					.append(" -> ")//
					.append(doc.getEndDateTime() == null ? null : df.format(doc.getEndDateTime().getTime()))//
					.toString();
		}

	}

	class CalendarEventLastUpdatedAfterPredicate implements Predicate<CalendarEvent> {

		private final Calendar calendar;

		public CalendarEventLastUpdatedAfterPredicate(final Calendar calendar) {
			Validate.notNull(calendar);
			this.calendar = calendar;
		}

		@Override
		public boolean evaluate(final CalendarEvent object) {
			if (object.getLastUpdated() == null || object.getLastUpdated().after(calendar)) {
				return true;
			}
			return false;
		}
	}

	private void insert(final GoogleCalendarDAO dao, final CalendarEvent entry) {
		try {
			dao.insert(entry);
		} catch (final SynchronisationException e) {
			log.error("Error inserting entry");
			log.error(entry);
			e.printStackTrace();
		}
	}

	private void delete(final GoogleCalendarDAO dao, final String id) {
		dao.delete(id);
	}

	private void update(final GoogleCalendarDAO dao, final String id, final CalendarEvent entry) {
		dao.update(id, entry);
	}
}
