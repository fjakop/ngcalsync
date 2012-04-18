package com.googlecode.syncnotes2google;


public interface Constants {

	  public static final String NOTES_CALENDAR_VIEW = "Calendar";

	  // Sync direction
	  public static final String BI_DIRECTION = "bi-direction";
	  public static final String NOTES_TO_GOOGLE = "notes-to-google";
	  public static final String GOOGLE_TO_NOTES = "google-to-notes";
	  
	  // Appointment type
	  public static final int NORMAL_EVENT = 0;
	  public static final int ANNIVERSARY = 1;
	  public static final int ALL_DAY_EVENT = 2;
	  public static final int MEETING = 3;
	  public static final int REMINDER = 4;
	  // public static final int APPOINTMENT = ?;
	  
	  // Frequency type of recurrence calendar entry.
	  public static final int FREQ_NONE   = 0;
	  public static final int FREQ_DAILY   = 1;
	  public static final int FREQ_WEEKLY  = 2;
	  public static final int FREQ_MONTHLY = 3;
	  public static final int FREQ_YEARLY  = 4;
	  public static final int FREQ_OTHER   = 99;
	  
}
