package com.googlecode.syncnotes2google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Properties;

import com.googlecode.syncnotes2google.util.FileHelper;

public class Settings implements Constants {

  private static final String LAST_SYNC_TIME = "LastSyncTime";
  private String GoogleAccountName;
  private String GooglePassword;
  private String calendarName;
  private int reminderMinutes;
  private String DominoServer;
  private String MailDbFilePath;
  private Calendar syncStartDate;
  private Calendar syncEndDate;
  private String syncDirection;
  private String SyncPriority;
  private Calendar syncLastDateTime;
  private String ProxyHost;
  private String ProxyPort;
  private String ProxyUserName;
  private String ProxyPassword;
  private Calendar syncStart;

  public Settings() throws IOException {
    Properties p = new Properties();
    p.load(new FileInputStream("sync.properties"));
    setGoogleAccountName(getProperty(p, "google.account.email"));
    setGooglePassword(getProperty(p, "google.account.password"));
    setCalendarName(getProperty(p, "google.calendar.name", "Calendar"));
    setReminderMinutes(Integer.parseInt(getProperty(p, "google.calendar.reminderminutes", "15")));

    setDominoServer(getProperty(p, "notes.domino.server"));
    setMailDbFilePath(getProperty(p, "notes.mail.db.file"));

    String direction = getProperty(p, "sync.direction");
    if (!BI_DIRECTION.equals(direction) && !NOTES_TO_GOOGLE.equals(direction) && !GOOGLE_TO_NOTES.equals(direction)) {
      System.out.println("Unknown direction: " + direction + " direction set to default.");
      direction = NOTES_TO_GOOGLE;
    }
    setSyncDirection(direction);
    String start = getProperty(p, "sync.start");
    int periodType;
    int period;
    try {
      periodType = parsePeriodType(start);
      period = parsePeriod(start);
    } catch (FormatException e) {
      periodType = Calendar.DAY_OF_YEAR;
      period = 14;
    }

    Calendar sdt = Calendar.getInstance();
    sdt.add(periodType, -period);
    setSyncStartDate(sdt);

    String end = getProperty(p, "sync.end");
    try {
      periodType = parsePeriodType(end);
      period = parsePeriod(end);
    } catch (FormatException e) {
      periodType = Calendar.MONTH;
      period = 3;
    }
    Calendar edt = Calendar.getInstance();
    edt.add(Calendar.MONTH, +6);
    setSyncEndDate(edt);
    syncStart = Calendar.getInstance();
    try {
      File file = FileHelper.getFile(LAST_SYNC_TIME);
      syncLastDateTime = Calendar.getInstance();
      if (!file.exists()) {
        syncLastDateTime.setTimeInMillis(0);
        return;
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      String line = br.readLine();
      if (line != null) {
        syncLastDateTime.setTimeInMillis(Long.parseLong(line.trim()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getProperty(Properties p, String propertyName) {
    return System.getProperty(propertyName, p.getProperty(propertyName));
  }

  private String getProperty(Properties p, String propertyName,String defaultValue) {
    return System.getProperty(propertyName, p.getProperty(propertyName, defaultValue));
  }

  private int parsePeriod(String start) throws FormatException {
    try {
      return Integer.parseInt(start.substring(0, start.length() - 1));
    } catch (Exception e) {
      throw new FormatException();
    }
  }

  private int parsePeriodType(String start) throws FormatException {
    if (start.endsWith("d")) {
      return Calendar.DAY_OF_YEAR;
    } else if (start.endsWith("m")) {
      return Calendar.MONTH;
    }
    throw new FormatException();
  }

  private class FormatException extends Exception {
    private static final long serialVersionUID = 2485854390296466112L;
  }

  public String getGoogleAccountName() {
    return GoogleAccountName;
  }

  public void setGoogleAccountName(String googleAccountName) {
    GoogleAccountName = googleAccountName;
  }

  public String getGooglePassword() {
    return GooglePassword;
  }

  public void setGooglePassword(String googlePassword) {
    GooglePassword = googlePassword;
  }

  public String getDominoServer() {
    return DominoServer;
  }

  public void setDominoServer(String dominoServer) {
    DominoServer = dominoServer;
  }

  public String getMailDbFilePath() {
    return MailDbFilePath;
  }

  public void setMailDbFilePath(String mailDbFilePath) {
    MailDbFilePath = mailDbFilePath;
  }

  public Calendar getSyncStartDate() {
    return syncStartDate;
  }

  public void setSyncStartDate(Calendar syncStartDate) {
    this.syncStartDate = syncStartDate;
  }

  public Calendar getSyncEndDate() {
    return syncEndDate;
  }

  public void setSyncEndDate(Calendar syncEndDate) {
    this.syncEndDate = syncEndDate;
  }

  public String getSyncDirection() {
    return syncDirection;
  }

  public void setSyncDirection(String syncDirection) {
    this.syncDirection = syncDirection;
  }

  public String getSyncPriority() {
    return SyncPriority;
  }

  public Calendar getSyncLastDateTime() {
    return syncLastDateTime;
  }

  public void setSyncLastDateTime(Calendar syncLastDateTime) {
    this.syncLastDateTime = syncLastDateTime;
  }

  public void saveSetDoc() {
    // Todo save setings
    try {
      File file = FileHelper.getFile(LAST_SYNC_TIME);
      FileWriter fw = new FileWriter(file);
      fw.write(System.currentTimeMillis() + "\n");
      fw.close();
    } catch (Exception e) {
    }
  }

  public void setProxyHost(String proxyHost) {
    ProxyHost = proxyHost;
  }

  public String getProxyHost() {
    return ProxyHost;
  }

  public void setProxyPort(String proxyPort) {
    ProxyPort = proxyPort;
  }

  public String getProxyPort() {
    return ProxyPort;
  }

  public void setProxyUserName(String proxyUserName) {
    ProxyUserName = proxyUserName;
  }

  public String getProxyUserName() {
    return ProxyUserName;
  }

  public void setProxyPassword(String proxyPassword) {
    ProxyPassword = proxyPassword;
  }

  public String getProxyPassword() {
    return ProxyPassword;
  }

  public String getCalendarName() {
    return calendarName;
  }

  public int getReminderMinutes() {
    return reminderMinutes;
  }

  public void setCalendarName(String calendarName) {
    this.calendarName = calendarName;
  }

  public void setReminderMinutes(int reminderMinutes) {
    this.reminderMinutes = reminderMinutes;
  }
  public Calendar getSyncStart() {
    return syncStart;
  }

  public static void main(String[] args) throws IOException {
    new Settings();
  }
}
