package de.jakop.ngcalsync.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.exception.SynchronisationException;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;
import de.jakop.ngcalsync.google.IGoogleCalendarDAO;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;
import de.jakop.ngcalsync.notes.INotesCalendarDAO;
import de.jakop.ngcalsync.obfuscator.ICalendarEventObfuscator;
import de.jakop.ngcalsync.settings.Settings;

/**
 * Synchronizes from {@link INotesCalendarDAO} into the {@link IGoogleCalendarDAO}
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
	public void executeSync(final INotesCalendarDAO notesDao, final IGoogleCalendarDAO googleDao, final ICalendarEventFilter[] filters, final ICalendarEventObfuscator[] obfuscators,
			final Settings settings) {

		Validate.notNull(notesDao);
		Validate.notNull(googleDao);
		Validate.notNull(settings);

		final Collection<CalendarEvent> notesEvents = notesDao.getEntries(filters);
		final Collection<CalendarEvent> googleEntries = googleDao.getEvents(filters);

		// schedule Events existing in Google but not in Notes for removal
		final List<CalendarEvent> removeFromGoogle = getGoogleEntriesToRemove(notesEvents, googleEntries);

		// schedule Events existing in Notes but not in Google for addition
		final List<CalendarEvent> addToGoogle = getGoogleEntriesToAdd(notesEvents, googleEntries);

		// schedule modified Events existing in Notes and Google for update
		final Map<CalendarEvent, CalendarEvent> updateToGoogle = getGoogleEntriesToUpdate(settings, notesEvents, googleEntries);

		// actually do it
		log.info(UserMessage.get().MSG_REMOVING_EVENTS_FROM_GOOGLE(removeFromGoogle.size()));
		for (final CalendarEvent event : removeFromGoogle) {
			delete(googleDao, event.getId());
		}
		log.info(UserMessage.get().MSG_ADDING_EVENTS_TO_GOOGLE(addToGoogle.size()));
		for (final CalendarEvent event : addToGoogle) {
			// obfuscate
			for (final ICalendarEventObfuscator obfuscator : obfuscators) {
				obfuscator.obfuscate(event);
			}
			insert(googleDao, event);
		}
		log.info(UserMessage.get().MSG_UPDATING_EVENTS_TO_GOOGLE(updateToGoogle.size()));
		for (final CalendarEvent event : updateToGoogle.keySet()) {
			// obfuscate
			for (final ICalendarEventObfuscator obfuscator : obfuscators) {
				obfuscator.obfuscate(event);
			}
			update(googleDao, updateToGoogle.get(event).getId(), event);
		}


	}

	private List<CalendarEvent> getGoogleEntriesToRemove(final Collection<CalendarEvent> notesEvents, final Collection<CalendarEvent> googleEntries) {
		final List<CalendarEvent> removeFromGoogle = new ArrayList<CalendarEvent>();
		for (final CalendarEvent baseDoc : googleEntries) {
			if (CollectionUtils.select(notesEvents, new CalendarEventEqualsPredicate(baseDoc)).isEmpty()) {
				removeFromGoogle.add(baseDoc);
				log.debug(TechMessage.get().MSG_SCHEDULING_FOR_REMOVAL(CalendarEventEqualsPredicate.format(baseDoc)));
			}
		}
		return removeFromGoogle;
	}

	private Map<CalendarEvent, CalendarEvent> getGoogleEntriesToUpdate(final Settings settings, final Collection<CalendarEvent> notesEvents,
			final Collection<CalendarEvent> googleEntries) {
		final Map<CalendarEvent, CalendarEvent> updateToGoogle = new HashMap<CalendarEvent, CalendarEvent>();
		for (final CalendarEvent notesEvent : notesEvents) {
			final Collection<CalendarEvent> matchingEntries = CollectionUtils.select(googleEntries, new CalendarEventEqualsPredicate(notesEvent));
			if (!matchingEntries.isEmpty()) {
				checkForDuplicates(notesEvent, matchingEntries);
				final CalendarEvent matchingEntry = matchingEntries.iterator().next();
				// check modification and update eventually
				if (notesEvent.getLastUpdated().after(settings.getSyncLastDateTime())) {
					updateToGoogle.put(notesEvent, matchingEntry);
					log.debug(TechMessage.get().MSG_SCHEDULING_FOR_UPDATE(CalendarEventEqualsPredicate.format(notesEvent)));
				} else {
					log.debug(TechMessage.get().MSG_NO_UPDATE_SCHEDULED(CalendarEventEqualsPredicate.format(notesEvent)));
				}
			}
		}
		return updateToGoogle;
	}

	private void checkForDuplicates(final CalendarEvent notesEvent, final Collection<CalendarEvent> matchingEntries) {
		if (matchingEntries.size() > 1) {
			throw new SynchronisationException(TechMessage.get().MSG_DUPLICATE_MATCH(matchingEntries.size(), notesEvent.format()));
		}
	}

	private List<CalendarEvent> getGoogleEntriesToAdd(final Collection<CalendarEvent> notesEvents, final Collection<CalendarEvent> googleEntries) {

		// schedule Events existing in Notes but not in Google for addition
		final List<CalendarEvent> addToGoogle = new ArrayList<CalendarEvent>();
		for (final CalendarEvent notesEvent : notesEvents) {
			final Collection<CalendarEvent> matchingEntries = CollectionUtils.select(googleEntries, new CalendarEventEqualsPredicate(notesEvent));
			if (matchingEntries.isEmpty()) {
				addToGoogle.add(notesEvent);
				log.debug(TechMessage.get().MSG_SCHEDULING_FOR_ADDITION(CalendarEventEqualsPredicate.format(notesEvent)));
			} else {
				checkForDuplicates(notesEvent, matchingEntries);
			}
		}
		return addToGoogle;
	}

	private void insert(final IGoogleCalendarDAO dao, final CalendarEvent entry) {
		try {
			dao.insert(entry);
		} catch (final SynchronisationException e) {
			// TODO handle this correctly
			log.error(TechMessage.get().MSG_INSERT_ERROR(), e);
			log.error(entry);
		}
	}

	private void delete(final IGoogleCalendarDAO dao, final String id) {
		dao.delete(id);
	}

	private void update(final IGoogleCalendarDAO dao, final String id, final CalendarEvent entry) {
		dao.update(id, entry);
	}
}
