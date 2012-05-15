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

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.bea.domingo.DDatabase;
import de.bea.domingo.DDocument;
import de.bea.domingo.DView;
import de.bea.domingo.DViewEntry;
import de.bea.domingo.service.NotesServiceRuntimeException;
import de.bea.domingo.util.GregorianDateTime;
import de.jakop.ngcalsync.CalendarEvent;
import de.jakop.ngcalsync.CalendarEvent.EventType;
import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.SynchronisationException;
import de.jakop.ngcalsync.filter.ICalendarEntryFilter;

/**
 * Zugriff auf Notes-Kalendereinträge 
 *
 * @author fjakop
 */
public class NotesCalendarDAO {

	private static final String VIEWNAME_CALENDAR = "Calendar";

	private static final String FIELDNAME_APPOINTMENT_TYPE = "AppointmentType";
	private static final String FIELDNAME_ROOM = "Room";
	private static final String FIELDNAME_LOCATION = "Location";
	private static final String FIELDNAME_BODY = "Body";
	private static final String FIELDNAME_SUBJECT = "Subject";
	private static final String FIELDNAME_CONFLICT = "$Conflict";
	private static final String FIELDNAME_FORM = "Form";
	private static final String FIELDNAME_CALENDAR_DATE_TIME = "CalendarDateTime";
	private static final String FIELDNAME_END_DATE_TIME = "EndDateTime";
	private static final String FIELDNAME_START_DATE_TIME = "StartDateTime";
	private static final String FIELDNAME_REPEATS = "Repeats";
	private static final String FIELDNAME_PRIVATE = "OrgConfidential";

	private static final String FLAG_APPOINTMENT = "Appointment";

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
	public NotesCalendarDAO(IOpenDatabaseStrategy openDatabaseStrategy, String dominoServer, String mailDatabase, Calendar startDateTime, Calendar endDateTime) {
		mailDb = openDatabaseStrategy.openDatabase(dominoServer, mailDatabase);
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	/**
	 * Gibt alle Kalendereinträge zurück
	 * 
	 * @param filters
	 * @return alle Kalendereinträge
	 */
	public List<CalendarEvent> getEntries(final ICalendarEntryFilter[] filters) throws SynchronisationException {
		log.info(String.format(Constants.MSG_READING_LOTUS_NOTES_EVENTS, mailDb.getFilePath()));

		Predicate<CalendarEvent> predicate = new Predicate<CalendarEvent>() {
			public boolean evaluate(CalendarEvent baseDoc) {
				for (ICalendarEntryFilter filter : filters) {
					if (!filter.accept(baseDoc)) {
						return false;
					}
				}
				return true;
			}
		};

		List<CalendarEvent> selected = new ArrayList<CalendarEvent>(CollectionUtils.select(getCalendarEntries(), predicate));

		Collections.sort(selected, new Comparator<CalendarEvent>() {
			public int compare(CalendarEvent o1, CalendarEvent o2) {
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

		Set<String> processedNotesDocuments = new HashSet<String>();
		List<CalendarEvent> entries = new ArrayList<CalendarEvent>();

		DView calView = mailDb.getView(VIEWNAME_CALENDAR);

		@SuppressWarnings("unchecked")
		Iterator<DViewEntry> viewEntries = calView.getAllEntriesByKey(startDateTime, endDateTime, false);

		while (viewEntries.hasNext()) {
			DViewEntry viewEntry = viewEntries.next();
			DDocument currentWorkDoc = viewEntry.getDocument();

			// ist es schon prozessiert worden (ein Notes-Dokument kann mehrfach auftreten, wenn es wiederholend ist)
			if (!processedNotesDocuments.contains(currentWorkDoc.getUniversalID())) {

				if (FLAG_APPOINTMENT.equals(currentWorkDoc.getItemValueString(FIELDNAME_FORM))) {
					// If this is a conflict document, skip to next document.
					if (currentWorkDoc.hasItem(FIELDNAME_CONFLICT)) {
						continue;
					}

					Collection<CalendarEvent> convDocs = convDoc(currentWorkDoc);
					entries.addAll(convDocs);
					processedNotesDocuments.add(currentWorkDoc.getUniversalID());
				}
			}
		}

		return entries;
	}


	private Collection<CalendarEvent> convDoc(DDocument doc) {
		if (doc.hasItem(FIELDNAME_REPEATS)) {
			return inflateRecurringEvents(doc);
		} else {
			Collection<CalendarEvent> docs = new ArrayList<CalendarEvent>();
			docs.add(convSingleDoc(doc));
			return docs;
		}
	}

	private CalendarEvent convSingleDoc(DDocument doc) {

		//		String generateXML = doc.generateXML();
		//		log.debug(generateXML);

		CalendarEvent bd = new CalendarEvent();
		try {
			String subject = doc.getItemValueString(FIELDNAME_SUBJECT);
			bd.setTitle(StringUtils.trimToEmpty(subject));
			String body = doc.getItemValueString(FIELDNAME_BODY);
			bd.setContent(StringUtils.trimToEmpty(body));

			bd.setId(doc.getUniversalID());
			String initloc = doc.getItemValueString(FIELDNAME_LOCATION);
			initloc = (StringUtils.trimToEmpty(initloc));
			String room = doc.getItemValueString(FIELDNAME_ROOM);
			room = (StringUtils.trimToEmpty(room));

			String loc = "";
			if (initloc.length() != 0 && room.length() != 0) {
				loc = "L: " + initloc + " R: " + room;
			}
			if (initloc.length() == 0 && room.length() != 0) {
				loc = room;
			}
			if (initloc.length() != 0 && room.length() == 0) {
				loc = initloc;
			}
			bd.setLocation(loc);

			// "OrgConfidential" == 1 Private 
			String markPrivate = doc.getItemValueString(FIELDNAME_PRIVATE);
			bd.setPrivate(StringUtils.trimToEmpty(markPrivate).equals("1"));

			bd.setLastUpdated(doc.getLastModified());
			String appointType = doc.getItemValueString(FIELDNAME_APPOINTMENT_TYPE);
			int type = Integer.parseInt(StringUtils.trimToEmpty(appointType));
			bd.setApptype(EventType.create(type));

			@SuppressWarnings("unchecked")
			List<GregorianDateTime> currentStartDateTime = doc.getItemValue(FIELDNAME_START_DATE_TIME);
			GregorianDateTime sdt = currentStartDateTime.get(0);

			@SuppressWarnings("unchecked")
			List<GregorianDateTime> currentEndDateTime = doc.getItemValue(FIELDNAME_END_DATE_TIME);
			GregorianDateTime edt = currentEndDateTime.get(0);

			Calendar newStartDateTime = Calendar.getInstance();
			newStartDateTime.setTime(sdt.getTime());

			Calendar newEndDateTime = Calendar.getInstance();
			newEndDateTime.setTime(edt.getTime());

			bd.setStartDateTime(newStartDateTime);
			bd.setEndDateTime(newEndDateTime);
		} catch (NotesServiceRuntimeException e) {
			log.error(bd.toString());
			log.error(e.toString());
			log.error("------------------------------------------------");
			return null;
		}
		return bd;
	}

	private Collection<CalendarEvent> inflateRecurringEvents(DDocument doc) {
		Collection<CalendarEvent> docs = new ArrayList<CalendarEvent>();
		@SuppressWarnings("unchecked")
		List<GregorianDateTime> recurrenceItemValue = doc.getItemValue(FIELDNAME_CALENDAR_DATE_TIME);
		for (GregorianDateTime entryStartDateTime : recurrenceItemValue) {
			if (entryStartDateTime.compareTo(startDateTime) >= 0 && entryStartDateTime.compareTo(endDateTime) <= 0) {
				CalendarEvent event = convSingleDoc(doc);
				int year = entryStartDateTime.getYear();
				int month = entryStartDateTime.getMonth();
				int day = entryStartDateTime.getDay();
				event.getStartDateTime().set(year, month, day);
				event.getEndDateTime().set(year, month, day);

				docs.add(event);
			}
		}
		return docs;
	}

}
