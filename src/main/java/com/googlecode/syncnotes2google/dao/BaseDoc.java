package com.googlecode.syncnotes2google.dao;

import java.text.DateFormat;
import java.util.Calendar;

public class BaseDoc {

	private String id;
	private String title;
	private String content;
	private Calendar startDateTime; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private Calendar endDateTime; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private String location;
	private Calendar lastupdated; // xs:date format ex) 2009-05-20T12:00:00+09:00
	private int apptype; // Appointment type
	private BaseRecur recur; // recurrence parameter
	private String refId;
	private boolean _private;
	private boolean avalible;

	// This method is for debugging.
	@Override
	public String toString() {
		StringBuffer l = new StringBuffer();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
		l.append("ID             : " + id + "\n");
		l.append("Title          : " + title + "\n");
		l.append("Content        : " + content + "\n");
		l.append("startDateTime  : " + (startDateTime != null ? df.format(startDateTime.getTime()) : "") + "\n");
		l.append("endDateTime    : " + (endDateTime != null ? df.format(endDateTime.getTime()) : "") + "\n");
		l.append("location       : " + location + "\n");
		l.append("lastupdated    : " + (lastupdated != null ? (lastupdated == null ? "" : df.format(lastupdated.getTime())) : "") + "\n");
		l.append("apptype        : " + apptype + "\n");
		if (recur != null) {
			l.append("frequency      : " + recur.getFrequency() + "\n");
			l.append("interval       : " + recur.getInterval() + "\n");
			if (recur.getUntil() != null) {
				l.append("until          : " + df.format(recur.getUntil().getTime()) + "\n");
			}
			if (recur.getRdate() != null) {
				for (Calendar s : recur.getRdate()) {
					l.append("rdate          : " + df.format(s.getTime()) + "\n");
				}
			}
		}
		return l.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		//		return title;
		return "Meeting";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return "";
		//		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Calendar getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Calendar startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Calendar getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Calendar endDateTime) {
		this.endDateTime = endDateTime;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		//		return location;
		return "in der Firma";
	}

	public void setLastUpdated(Calendar lastupdated) {
		this.lastupdated = lastupdated;
	}

	public Calendar getLastUpdated() {
		return lastupdated;
	}

	public void setApptype(int apptype) {
		this.apptype = apptype;
	}

	public int getApptype() {
		return apptype;
	}

	public void setRecur(BaseRecur recur) {
		this.recur = recur;
	}

	public BaseRecur getRecur() {
		return recur;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public void setPrivate(boolean _private) {
		this._private = _private;
	}

	public boolean isPrivate() {
		return _private;
	}

	public void setAvalible(boolean avalible) {
		this.avalible = avalible;
	}

	public boolean isAvalible() {
		return avalible;
	}
}
