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
package de.jakop.ngcalsync.notes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.bea.domingo.DDatabase;
import de.bea.domingo.DDocument;
import de.bea.domingo.DView;
import de.bea.domingo.DViewEntry;
import de.bea.domingo.service.NotesServiceRuntimeException;
import de.bea.domingo.util.GregorianDateTime;
import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.calendar.EventType;
import de.jakop.ngcalsync.exception.SynchronisationException;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;
import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;

/**
 * Access to Lotus Notes calendar events
 *
 * @author fjakop
 */
class NotesCalendarDAO implements INotesCalendarDAO {

	private static final String VIEWNAME_CALENDAR = "Calendar"; //$NON-NLS-1$

	private static final String FIELDNAME_APPOINTMENT_TYPE = "AppointmentType"; //$NON-NLS-1$
	private static final String FIELDNAME_ROOM = "Room"; //$NON-NLS-1$
	private static final String FIELDNAME_LOCATION = "Location"; //$NON-NLS-1$
	private static final String FIELDNAME_BODY = "Body"; //$NON-NLS-1$
	private static final String FIELDNAME_SUBJECT = "Subject"; //$NON-NLS-1$
	private static final String FIELDNAME_CONFLICT = "$Conflict"; //$NON-NLS-1$
	private static final String FIELDNAME_FORM = "Form"; //$NON-NLS-1$
	private static final String FIELDNAME_CALENDAR_DATE_TIME = "CalendarDateTime"; //$NON-NLS-1$
	private static final String FIELDNAME_END_DATE_TIME = "EndDateTime"; //$NON-NLS-1$
	private static final String FIELDNAME_START_DATE_TIME = "StartDateTime"; //$NON-NLS-1$
	private static final String FIELDNAME_REPEATS = "Repeats"; //$NON-NLS-1$
	private static final String FIELDNAME_PRIVATE = "OrgConfidential"; //$NON-NLS-1$

	private static final String FLAG_APPOINTMENT = "Appointment"; //$NON-NLS-1$

	private final Log log = LogFactory.getLog(getClass());

	private final DDatabase mailDb;
	private final Calendar startDateTime;
	private final Calendar endDateTime;


	/**
	 *
	 * @param dominoServer
	 * @param mailDatabase
	 * @param startDateTime
	 * @param endDateTime
	 * @param openDatabaseStrategy
	 */
	NotesCalendarDAO(final IOpenDatabaseStrategy openDatabaseStrategy, final String dominoServer, final String mailDatabase, final Calendar startDateTime,
			final Calendar endDateTime) {
		mailDb = openDatabaseStrategy.openDatabase(dominoServer, mailDatabase);
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	/* (non-Javadoc)
	 * @see de.jakop.ngcalsync.notes.INotesCalendarDAO#getEntries(de.jakop.ngcalsync.filter.ICalendarEventFilter[])
	 */
	@Override
	public List<CalendarEvent> getEntries(final ICalendarEventFilter[] filters) throws SynchronisationException {
		log.info(UserMessage.get().MSG_READING_LOTUS_NOTES_EVENTS(mailDb.getFilePath()));

		final Predicate<CalendarEvent> predicate = new Predicate<CalendarEvent>() {
			@Override
			public boolean evaluate(final CalendarEvent baseDoc) {
				for (final ICalendarEventFilter filter : filters) {
					if (!filter.accept(baseDoc)) {
						return false;
					}
				}
				return true;
			}
		};

		final List<CalendarEvent> selected = new ArrayList<CalendarEvent>(CollectionUtils.select(getCalendarEntries(), predicate));

		Collections.sort(selected, new Comparator<CalendarEvent>() {
			@Override
			public int compare(final CalendarEvent o1, final CalendarEvent o2) {
				if (o1.getStartDateTime().before(o2.getStartDateTime())) {
					return -1;
				}
				if (o1.getStartDateTime().after(o2.getStartDateTime())) {
					return 1;
				}
				return 0;
			}
		});
		return selected;
	}

	private List<CalendarEvent> getCalendarEntries() {

		final Set<String> processedNotesDocuments = new HashSet<String>();
		final List<CalendarEvent> entries = new ArrayList<CalendarEvent>();

		final DView calView = mailDb.getView(VIEWNAME_CALENDAR);

		@SuppressWarnings("unchecked")
		final Iterator<DViewEntry> viewEntries = calView.getAllEntriesByKey(startDateTime, endDateTime, false);

		while (viewEntries.hasNext()) {
			final DViewEntry viewEntry = viewEntries.next();
			final DDocument currentWorkDoc = viewEntry.getDocument();

			if (currentWorkDoc != null) {
				log.debug(TechMessage.get().MSG_PROCESSING_DOCUMENT_UNID(currentWorkDoc.getUniversalID()));

				// ist es schon prozessiert worden (ein Notes-Dokument kann mehrfach auftreten, wenn es wiederholend ist)
				if (!processedNotesDocuments.contains(currentWorkDoc.getUniversalID())) {

					if (FLAG_APPOINTMENT.equals(currentWorkDoc.getItemValueString(FIELDNAME_FORM))) {
						// If this is a conflict document, skip to next document.
						if (currentWorkDoc.hasItem(FIELDNAME_CONFLICT)) {
							log.debug(TechMessage.get().MSG_DOCUMENT_WITH_UNID_IS_CONFLICT_DOCUMENT(currentWorkDoc.getUniversalID()));
							continue;
						}

						final Collection<CalendarEvent> convDocs = convDoc(currentWorkDoc);
						entries.addAll(convDocs);
						processedNotesDocuments.add(currentWorkDoc.getUniversalID());
					} else {
						log.debug(TechMessage.get().MSG_DOCUMENT_WITH_UNID_IS_NOT_AN_APPOINTMENT(currentWorkDoc.getUniversalID()));
					}
				} else {
					log.debug(TechMessage.get().MSG_DOCUMENT_WITH_UNID_ALREADY_PROCESSED(currentWorkDoc.getUniversalID()));
				}
			}
		}

		return entries;
	}


	private Collection<CalendarEvent> convDoc(final DDocument doc) {
		if (doc.hasItem(FIELDNAME_REPEATS)) {
			return inflateRecurringEvents(doc);
		} else {
			final Collection<CalendarEvent> docs = new ArrayList<CalendarEvent>();
			docs.add(convSingleDoc(doc));
			return docs;
		}
	}

	private CalendarEvent convSingleDoc(final DDocument doc) {
		log.debug(TechMessage.get().MSG_CONVERTING_DOCUMENT_UNID(doc.getUniversalID()));

		final CalendarEvent bd = new CalendarEvent();
		try {
			final String subject = doc.getItemValueString(FIELDNAME_SUBJECT);
			bd.setTitle(StringUtils.trimToEmpty(subject));
			final String body = doc.getItemValueString(FIELDNAME_BODY);
			bd.setContent(StringUtils.trimToEmpty(body));

			bd.setId(doc.getUniversalID());
			String initloc = doc.getItemValueString(FIELDNAME_LOCATION);
			initloc = StringUtils.trimToEmpty(initloc);
			String room = doc.getItemValueString(FIELDNAME_ROOM);
			room = StringUtils.trimToEmpty(room);

			String loc = new String();
			if (initloc.length() != 0 && room.length() != 0) {
				loc = "L: " + initloc + " R: " + room; //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (initloc.length() == 0 && room.length() != 0) {
				loc = room;
			}
			if (initloc.length() != 0 && room.length() == 0) {
				loc = initloc;
			}
			bd.setLocation(loc);

			// "OrgConfidential" == 1 Private
			final String markPrivate = doc.getItemValueString(FIELDNAME_PRIVATE);
			bd.setPrivate(StringUtils.trimToEmpty(markPrivate).equals("1")); //$NON-NLS-1$

			bd.setLastUpdated(doc.getLastModified());
			final String appointType = doc.getItemValueString(FIELDNAME_APPOINTMENT_TYPE);
			final int type = Integer.parseInt(StringUtils.trimToEmpty(appointType));
			bd.setEventType(EventType.create(type));

			@SuppressWarnings("unchecked")
			final List<GregorianDateTime> currentStartDateTime = doc.getItemValue(FIELDNAME_START_DATE_TIME);
			final GregorianDateTime sdt = currentStartDateTime.get(0);

			@SuppressWarnings("unchecked")
			final List<GregorianDateTime> currentEndDateTime = doc.getItemValue(FIELDNAME_END_DATE_TIME);
			final GregorianDateTime edt = currentEndDateTime.get(0);

			final Calendar newStartDateTime = Calendar.getInstance();
			newStartDateTime.setTime(sdt.getTime());

			final Calendar newEndDateTime = Calendar.getInstance();
			newEndDateTime.setTime(edt.getTime());

			bd.setStartDateTime(newStartDateTime);
			bd.setEndDateTime(newEndDateTime);
			log.debug(TechMessage.get().MSG_CONVERSION_RESULT(doc.getUniversalID(), bd.format()));

		} catch (final NotesServiceRuntimeException e) {
			log.error(bd.format(), e);
			log.error("------------------------------------------------"); //$NON-NLS-1$
			return null;
		}
		return bd;
	}

	private Collection<CalendarEvent> inflateRecurringEvents(final DDocument doc) {
		final Collection<CalendarEvent> docs = new ArrayList<CalendarEvent>();
		@SuppressWarnings("unchecked")
		final List<GregorianDateTime> recurrenceItemValue = doc.getItemValue(FIELDNAME_CALENDAR_DATE_TIME);
		for (final GregorianDateTime entryStartDateTime : recurrenceItemValue) {
			if (entryStartDateTime.compareTo(startDateTime) >= 0 && entryStartDateTime.compareTo(endDateTime) <= 0) {
				final CalendarEvent event = convSingleDoc(doc);
				final int year = entryStartDateTime.getYear();
				final int month = entryStartDateTime.getMonth();
				final int day = entryStartDateTime.getDay();
				event.getStartDateTime().set(year, month, day);
				event.getEndDateTime().set(year, month, day);

				docs.add(event);
			}
		}
		return docs;
	}

}
