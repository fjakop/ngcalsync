package com.googlecode.syncnotes2google.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.syncnotes2google.Constants;
import com.googlecode.syncnotes2google.Factory;
import com.googlecode.syncnotes2google.IDTable;

import de.bea.domingo.DDatabase;
import de.bea.domingo.DDocument;
import de.bea.domingo.DItem;
import de.bea.domingo.DView;
import de.bea.domingo.DViewEntry;
import de.bea.domingo.service.NotesServiceRuntimeException;
import de.bea.domingo.util.GregorianDateTime;

public class NotesCalendarDAO implements BaseDAO {

  private static DView calView = null;
  private static DDocument workDoc = null;
  private List<BaseDoc> calDoc = new ArrayList<BaseDoc>();

  public void delete(String unid) {
    DDocument doc = Factory.getInstance().getMailDatabase().getDocumentByUNID(unid);
    if (doc != null) {
      doc.remove(true);
      workDoc = null;
    }
  }

  @SuppressWarnings("unchecked")
  public BaseDoc getFirstEntry() {

    // try {
    DDatabase mailDb = Factory.getInstance().getMailDatabase();
    if (calView == null) {
      calView = mailDb.getView(Constants.NOTES_CALENDAR_VIEW);
    }

    // Convert syncStartDate(xs:date) to MM/DD/YYY to create DateTime
    // object.
    // International intl = Factory.getInternational();
    // String syncSt = "";
    // syncSt = syncStartDate.substring(5, 7) + intl.getDateSep() +
    // syncStartDate.substring(8, 10) + intl.getDateSep() +
    // syncStartDate.substring(0, 4);
    // DateTime sdt = Factory.getNotesSession().createDateTime(syncSt);
    Factory factory = Factory.getInstance();

    Calendar sdt = factory.getSettings().getSyncStartDate();
    Calendar edt = factory.getSettings().getSyncEndDate();
    Iterator<DViewEntry> viewEntrys = (Iterator<DViewEntry>)calView.getAllEntriesByKey(sdt, edt, false);

    loop: while (viewEntrys.hasNext()) {
      DViewEntry viewEntry = viewEntrys.next();
      DDocument workDoc = viewEntry.getDocument();

      if ("Appointment".equals(workDoc.getItemValueString("Form"))) {
        // GregorianDateTimeRange dt = (GregorianDateTimeRange)
        // workDoc.getItemValue("StartDateTime").get(0);
        // if (dt.getFrom().before(sdt)) {
        // continue;
        // }

        // If this is a conflict document, skip to next document.
        if (workDoc.hasItem("$Conflict")) {
          continue;
        }

        BaseDoc convDoc = convDoc(workDoc);
        if (convDoc != null) {
          for(BaseDoc bd : calDoc) {
            if (bd.getId().equals(convDoc.getId())) {
              continue loop;
            }
          }
//					displayDoc(workDoc);

          calDoc.add(convDoc);
//					System.out.println(convDoc.getTitle());
        }
      }
    }

    return calDoc.isEmpty() ? null : calDoc.remove(0);
  }

  public BaseDoc getNextEntry() {
    return calDoc.isEmpty() ? null : calDoc.remove(0);
  }

  public String insert(BaseDoc bd) {
    DDocument doc = Factory.getInstance().getMailDatabase().createDocument();
    doc.appendItemValue("Form", "Appointment");
    doc.appendItemValue("Subject", bd.getTitle());
    doc.appendItemValue("Body", bd.getContent());
    doc.appendItemValue("Location", bd.getLocation());
    DItem item = (DItem)doc.appendItemValue("AppointmentType");
    item.setValueString(Integer.toString(bd.getApptype()));
    item.setSummary(true);
    doc.appendItemValue("ExcludeFromView", "D");

    Calendar sdt = bd.getStartDateTime();
    Calendar edt = bd.getEndDateTime();
    switch(bd.getApptype()) {
      case Constants.ALL_DAY_EVENT:
        doc.appendItemValue("StartDateTime", sdt);
        edt.add(Calendar.DAY_OF_YEAR, -1);
        doc.appendItemValue("EndDateTime", edt);

        // In case that All_DAY_EVENT has duration, add all date to
        // CalendarDateTime to show up on Notes calendar.
        Vector<GregorianDateTime> resultdt = new Vector<GregorianDateTime>();
        while (edt.after(sdt)) {
          resultdt.addElement(new GregorianDateTime(sdt.getTime()));
          sdt.add(Calendar.DAY_OF_YEAR, 1);
        }
        doc.appendItemValue("CalendarDateTime", resultdt);

        doc.appendItemValue("orgTable", "P0");
        break;
      case Constants.REMINDER:
        doc.appendItemValue("CalendarDateTime", sdt);
        doc.appendItemValue("StartDateTime", sdt);
        doc.appendItemValue("EndDateTime", edt);
        doc.appendItemValue("orgTable", "C0");
        break;
      case Constants.NORMAL_EVENT:
        doc.appendItemValue("CalendarDateTime", sdt);
        doc.appendItemValue("StartDateTime", sdt);
        doc.appendItemValue("EndDateTime", edt);
        doc.appendItemValue("orgTable", "C0");
        break;
      default:
        doc.appendItemValue("CalendarDateTime", sdt);
        doc.appendItemValue("StartDateTime", sdt);
        doc.appendItemValue("EndDateTime", edt);
        break;
    }

    if (!doc.computeWithForm(true)) {
      return null;
    }
    doc.save(true, true);
    return doc.getUniversalID();
  }

  public BaseDoc select(String unid) {

    try {
      workDoc = Factory.getInstance().getMailDatabase().getDocumentByUNID(unid);
      if (workDoc != null) {
        // setWorkDocNext();
        BaseDoc bd = convDoc(workDoc);
        return bd;
      } else {
        return null;
      }
    } catch (NotesServiceRuntimeException e) {
      // if (e.id == NotesError.NOTES_ERR_BAD_UNID) {
      // return null;
      // }
      // e.printStackTrace();
      // GooCalUtil.logStackTrace(e);
    }
    return null;

  }

  public void update(BaseDoc bd) {
    workDoc.replaceItemValue("Subject", bd.getTitle());
    workDoc.replaceItemValue("Body", bd.getContent());
    workDoc.replaceItemValue("Location", bd.getLocation());
    workDoc.replaceItemValue("StartDateTime", bd.getStartDateTime());
    workDoc.replaceItemValue("StartDate", bd.getStartDateTime());
    workDoc.replaceItemValue("StartTime", bd.getStartDateTime());
    workDoc.replaceItemValue("EndDateTime", bd.getEndDateTime());
    workDoc.replaceItemValue("EndDate", bd.getEndDateTime());
    workDoc.replaceItemValue("EndTime", bd.getEndDateTime());
//		workDoc.replaceItemValue("AppointmentType", Integer.toString(bd.getApptype()));

    switch(bd.getApptype()) {
      case Constants.ALL_DAY_EVENT:
        GregorianDateTime sdt = new GregorianDateTime(bd.getStartDateTime());
        GregorianDateTime edt = new GregorianDateTime(bd.getEndDateTime());
        edt.add(Calendar.DAY_OF_YEAR, -1);

        // In case that All_DAY_EVENT has duration, add all date to
        // CalendarDateTime to show up on Notes calendar.
        Vector<GregorianDateTime> resultdt = new Vector<GregorianDateTime>();
        while (edt.after(sdt)) {
          resultdt.addElement((new GregorianDateTime(sdt.getTime())));
          sdt.add(Calendar.DAY_OF_YEAR, 1);
        }
        workDoc.replaceItemValue("CalendarDateTime", resultdt);

        workDoc.replaceItemValue("EndDateTime", edt);
        workDoc.replaceItemValue("EndDate", edt);
        workDoc.replaceItemValue("EndTime", edt);

        workDoc.replaceItemValue("orgTable", "P0");
        break;
      case Constants.REMINDER:
        workDoc.replaceItemValue("CalendarDateTime", new GregorianDateTime(bd.getStartDateTime()));
        workDoc.replaceItemValue("orgTable", "C0");
        break;
      case Constants.NORMAL_EVENT:
        workDoc.replaceItemValue("CalendarDateTime", new GregorianDateTime(bd.getStartDateTime()));
        workDoc.replaceItemValue("orgTable", "C0");
        break;
      default:
        workDoc.replaceItemValue("CalendarDateTime", new GregorianDateTime(bd.getStartDateTime()));
        break;
    }
    workDoc.computeWithForm(true);
    workDoc.save(true, true);
  }

  @SuppressWarnings("unchecked")
  private BaseDoc convDoc(DDocument doc) {
    BaseDoc bd = new BaseDoc();
    try {
      String subject = doc.getItemValueString("Subject");
      bd.setTitle(subject != null ? subject : "");
      String body = doc.getItemValueString("Body");
      bd.setContent(body != null ? body : "");

      bd.setId(doc.getUniversalID());
      bd.setRefId(IDTable.getGoogleUID(doc.getUniversalID()));
      String initloc = doc.getItemValueString("Location");
      initloc = (initloc == null ? "" : initloc.trim());
      String room = doc.getItemValueString("Room");
      room = (room == null ? "" : room.trim());

      String loc = "";
      if (initloc.trim().length() != 0 && room.trim().length() != 0) {
        loc = "L: " + initloc + " R: " + room;
      }
      if (initloc.trim().length() == 0 && room.trim().length() != 0) {
        loc = room;
      }
      if (initloc.trim().length() != 0 && room.trim().length() == 0) {
        loc = initloc;
      }
      bd.setLocation(loc);

      // "OrgConfidential" == 1 Private 
      String markPrivate = doc.getItemValueString("OrgConfidential");
      bd.setPrivate(markPrivate != null && markPrivate.trim().equals("1"));
      // "BookFreeTime" == 1 Avalible
      String markAvalible = doc.getItemValueString("BookFreeTime");
      bd.setAvalible(markAvalible != null && markAvalible.trim().equals("1"));

      bd.setLastUpdated(doc.getLastModified());
      String appointType = doc.getItemValueString("AppointmentType");
      bd.setApptype(Integer.parseInt(appointType != null ? appointType : ""));

      if (doc.hasItem("Repeats")) {
        List itemValue = doc.getItemValue("CalendarDateTime");
        bd.setRecur(analyzeRecurrence(itemValue));
      } else {
        BaseRecur recur = new BaseRecur();
        recur.setFrequency(Constants.FREQ_NONE);
        recur.setInterval(0);
        recur.setUntil(null);
        recur.setRdate(null);
        bd.setRecur(recur);
      }
      List startDateTime = doc.getItemValue("StartDateTime");
      GregorianDateTime sdt = (GregorianDateTime)startDateTime.get(0);
      List endDateTime = doc.getItemValue("EndDateTime");
      GregorianDateTime edt = (GregorianDateTime)endDateTime.get(0);
      bd.setStartDateTime(sdt);
      bd.setEndDateTime(edt);
    } catch (NotesServiceRuntimeException e) {
      System.out.println(bd.toString());
      System.out.println(e.toString());
      System.out.println("------------------------------------------------");
      return null;
    }
    return bd;
  }

  @SuppressWarnings("unchecked")
  private void displayDoc(DDocument doc) {
    Iterator i = doc.getItems();
    System.out.println("-------------- " + doc.getItemValueString("Subject") + " -----------------");
    while (i.hasNext()) {

      String next = i.next().toString();
      try {
        String s = doc.getItemValueString(next);
        if (s.length() > 0) {

          if (!s.startsWith("CN=") && !next.equals("Body")) {
            System.out.print(next + " = ");
            System.out.println(s);
          }
        } else {
          List itemValue = doc.getItemValue(next);
          if (itemValue.size() > 0) {
            System.out.print(next + " = ");
            System.out.println(itemValue);
          } else {
            // System.out.println();
          }
        }
      } catch (Exception e) {
        System.out.println(next + " --> " + e.getMessage());
      }
    }
    System.out.println("-------------------------------");
  }

  @SuppressWarnings("unchecked")
  private BaseRecur analyzeRecurrence(List vdt) {

    BaseRecur recur = new BaseRecur();
    recur.setFrequency(Constants.FREQ_OTHER);
    recur.setInterval(0);
    recur.setUntil(null);

    if (vdt.size() == 1) {
      recur.setFrequency(Constants.FREQ_NONE);
      return recur;
    }
    recur.setCount(vdt.size());
    int i;
    for(i = 1; vdt.size() > i; i++) {

      GregorianDateTime dta = (GregorianDateTime)vdt.get(i - 1);
      GregorianDateTime dtb = (GregorianDateTime)vdt.get(i);
      boolean supportedFlag = false;

      // check whether daily or not
      dta.add(Calendar.DAY_OF_YEAR, 1);
      if (dta.equals(dtb)) {
        supportedFlag = true;
        if (i == 1) {
          recur.setFrequency(Constants.FREQ_DAILY);
          recur.setInterval(1);
        } else {
          if (recur.getFrequency() != Constants.FREQ_DAILY) {
            recur.setFrequency(Constants.FREQ_OTHER);
            break;
          }
        }
      }
      dta.add(Calendar.DAY_OF_YEAR, -1);

      // check whether weekly or not
      dta.add(Calendar.DAY_OF_YEAR, 7);

      if (dta.equals(dtb)) {
        supportedFlag = true;
        if (i == 1) {
          recur.setFrequency(Constants.FREQ_WEEKLY);
          recur.setInterval(1);
        } else {
          if (recur.getFrequency() != Constants.FREQ_WEEKLY) {
            recur.setFrequency(Constants.FREQ_OTHER);
            break;
          }
        }
      }
      dta.add(Calendar.DAY_OF_YEAR, -7);

      // check whether bi-weekly or not
      dta.add(Calendar.DAY_OF_YEAR, 14);
      if (dta.equals(dtb)) {
        supportedFlag = true;
        if (i == 1) {
          recur.setFrequency(Constants.FREQ_WEEKLY);
          recur.setInterval(2);
        } else {
          if (recur.getFrequency() != Constants.FREQ_WEEKLY && recur.getInterval() != 2) {
            recur.setFrequency(Constants.FREQ_OTHER);
            break;
          }
        }
      }
      dta.add(Calendar.DAY_OF_YEAR, -14);

      // check whether monthly or not
      dta.add(Calendar.MONTH, 1);
      if (dta.equals(dtb)) {
        supportedFlag = true;
        if (i == 1) {
          recur.setFrequency(Constants.FREQ_MONTHLY);
          recur.setInterval(1);
        } else {
          if (recur.getFrequency() != Constants.FREQ_MONTHLY) {
            recur.setFrequency(Constants.FREQ_OTHER);
            break;
          }
        }
      }
      dta.add(Calendar.MONTH, -1);

      // check whether yearly or not
      dta.add(Calendar.YEAR, 1);
      if (dta.equals(dtb)) {
        supportedFlag = true;
        if (i == 1) {
          recur.setFrequency(Constants.FREQ_YEARLY);
          recur.setInterval(1);
        } else {
          if (recur.getFrequency() != Constants.FREQ_YEARLY) {
            recur.setFrequency(Constants.FREQ_OTHER);
            break;
          }
        }
      }
      dta.add(Calendar.YEAR, -1);

      // check whether recurrence type is supported.
      if (supportedFlag == false) {
        recur.setFrequency(Constants.FREQ_OTHER);
        break;
      }
    }

    if (recur.getFrequency() == Constants.FREQ_OTHER) {
      Calendar rdatelist[] = new Calendar[vdt.size()];
      for(i = 0; i < vdt.size(); i++) {
        rdatelist[i] = (GregorianDateTime)vdt.get(i);
      }
      recur.setRdate(rdatelist);
    } else {
      // set GMT time to UNTIL in accordance with iCAl recurrence
      // specification.
      GregorianDateTime zdt = (GregorianDateTime)vdt.get(i - 1);
      // zdt.adjustHour(zdt.getTimeZone());
      recur.setUntil(zdt);
    }

    return recur;
  }

  public String getDirection() {
    return Constants.NOTES_TO_GOOGLE;
  }

  @Override
  public String toString() {
    return "Notes DAO";
  }
}
