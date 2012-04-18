/*
 * This file is part of Domingo
 * an Open Source Java-API to Lotus Notes/Domino
 * hosted at http://domingo.sourceforge.net
 *
 * Copyright (c) 2003-2007 Beck et al. projects GmbH Munich, Germany (http://www.bea.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package de.bea.domingo.proxy;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import lotus.domino.DateRange;
import lotus.domino.DateTime;
import lotus.domino.NotesException;
import lotus.domino.Session;
import de.bea.domingo.DNotesMonitor;
import de.bea.domingo.exception.DominoException;
import de.bea.domingo.i18n.ResourceManager;
import de.bea.domingo.i18n.Resources;
import de.bea.domingo.util.GregorianDate;
import de.bea.domingo.util.GregorianDateTime;
import de.bea.domingo.util.GregorianTime;

/**
 * Proxy to the Notes international settings.
 *
 * @author <a href=mailto:kriede@users.sourceforge.net>Kurt Riede</a>
 */
public final class InternationalProxy implements Serializable {

    private static final int TZ_MINUTES_OFFSET = 100;

    /** serial version ID for serialization. */
    private static final long serialVersionUID = -2957715893148795275L;

    /** Minutes per hour. */
    private static final int MINUTES_PER_HOUR = 60;

    /** Seconds per minute. */
    private static final int SECONDS_PER_MINUTE = 60;

    /** Milli seconds per hour. */
    private static final int MILLIS_PER_SECOND = 1000;

    /** Milli seconds per hour. */
    private static final int MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE;

    /** Milli seconds per hour. */
    private static final int MILLIS_PER_HOUR = MILLIS_PER_MINUTE * MINUTES_PER_HOUR;

    /** Internationalized resources. */
    private static final Resources RESOURCES = ResourceManager.getPackageResources(NotesProxyFactory.class);

    /** Default separator for date components. */
    public static final char DATE_SEPARATOR_DEFAULT = '/';

    /** Default separator for time components. */
    public static final char TIME_SEPARATOR_DEFAULT = ':';

    /**
     * Date format ordered as day/month/year. The slashes will be replaced with
     * the concrete date separator.
     */
    public static final String DATE_FORMAT_DMY = "dd/MM/yy";

    /**
     * Date format ordered as month/day/year. The slashes will be replaced with
     * the concrete date separator.
     */
    public static final String DATE_FORMAT_MDY = "MM/dd/yy";

    /**
     * Date format ordered as year/month/day. The slashes will be replaced with
     * the concrete date separator.
     */
    public static final String DATE_FORMAT_YMD = "yy/MM/dd";

    /**
     * Default date format. The slashes will be replaced with the concrete date
     * separator.
     */
    public static final String DATE_FORMAT_DEFAULT = DATE_FORMAT_DMY;

    /**
     * Time format in 24 hour display. The colons will be replaced with the
     * concrete date separator.
     */
    public static final String TIME_FORMAT_24 = "HH:mm:ss";

    /**
     * Default time format. The colons will be replaced with the concrete date
     * separator.
     */
    public static final String TIME_FORMAT_DEFAULT = "hh:mm:ss a";

    /** Format string for time zones in format of RFC 822 , e.g. <tt>"-0800"</tt>. */
    public static final String TIMEZONE_FORMAT = "Z";

    /** Base Monitor instance. */
    private DNotesMonitor monitor = null;

    /** Associated session (local call or IIOP). */
    private Session session;

    /**
     * Constructor.
     *
     * @param theSession the Notes Session
     * @param theMonitor the monitor
     */
    public InternationalProxy(final Session theSession, final DNotesMonitor theMonitor) {
        super();
        this.session = theSession;
        this.monitor = theMonitor;
    }

    /**
     * Get the current monitor.
     *
     * @return current monitor
     * @see de.bea.domingo.DNotesFactory#getMonitor()
     */
    protected DNotesMonitor getMonitor() {
        return monitor;
    }

    /**
     * Creates a Notes <code>DateTime</code> instance from a
     * <code>java.util.Calendar</code>.
     *
     * @param calendar the calendar to convert
     * @return DateTime a Notes DateTime object
     */
    protected DateTime createDateTime(final Calendar calendar) {
        final Calendar correct = (Calendar) calendar.clone();
        try {
            TimeZone gmtZone = TimeZone.getTimeZone("GMT");
            correct.setTimeZone(gmtZone);
            TimeZone timeZone = calendar.getTimeZone();
            correct.add(Calendar.SECOND, -timeZone.getOffset(correct.getTimeInMillis()) / MILLIS_PER_SECOND);
            final DateTime dateTime = session.createDateTime(correct);
            int zoneValue = getNotesTimeZoneValue(timeZone, calendar.getTimeInMillis());
            dateTime.convertToZone(zoneValue, timeZone.inDaylightTime(calendar.getTime()));
            if (!isDateSet(correct)) {
                dateTime.setAnyDate();
            } else if (!isTimeSet(correct)) {
                dateTime.setAnyTime();
            }
            return dateTime;
        } catch (NotesException e) {
            error("Cannot convert calendar to DateTime", e);
        }
        return null;
    }

    /**
     * Converts a Notes DateTime object into a Calendar. <p>Milli seconds are
     * cleared in all cases.</p>
     *
     * @param dateTime a Notes DateTime object
     * @return a Calendar
     */
    protected Calendar createCalendar(final DateTime dateTime) {
        final Date date = createDate(dateTime);
        if (date == null) {
            return null;
        }
        TimeZone zone = getTimeZone(dateTime);
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(date);
        if (!isTimeSet(dateTime)) {
            calendar.add(Calendar.MILLISECOND, zone.getOffset(calendar.getTimeInMillis()));
            return new GregorianDate(calendar.getTime());
        } else if (!isDateSet(dateTime)) {
            calendar.add(Calendar.MILLISECOND, zone.getOffset(calendar.getTimeInMillis()));
            return new GregorianTime(calendar);
        } else {
//            calendar.add(Calendar.MILLISECOND, -getOffset(dateTime));
//            calendar.add(Calendar.MILLISECOND, -zone.getOffset(calendar.getTimeInMillis()));
            return new GregorianDateTime(calendar);
        }
    }

    /**
     * Returns the Java time zone as expected when converting a given Notes
     * date/time instance to a Java date..
     *
     * <p>In case of local calls, the converted Java date is in the local default time zone.
     * In case of remote calls (DIIOP), the converted Java date is always in GMT.
     *
     * @param dateTime a Notes date/tim einstance
     * @return The time zone of the date/time instance when converted to Java
     */
    private TimeZone getTimeZone(final DateTime dateTime) {
        TimeZone zone = TimeZone.getDefault();
        if (dateTime.getClass().getName().equals("lotus.domino.cso.DateTime")) {
            zone = TimeZone.getTimeZone("GMT");
        }
        return zone;
    }

    /**
     * Creates a Notes <code>DateRange</code> instance from two
     * <code>java.util.Calendar</code>s.
     *
     * @param calendar1 the start calendar to convert
     * @param calendar2 the end calendar to convert
     * @return DateRange a Notes DateTime object
     */
    protected DateRange createDateRange(final Calendar calendar1, final Calendar calendar2) {
        final DateTime time1 = createDateTime(calendar1);
        final DateTime time2 = createDateTime(calendar2);
        try {
            return session.createDateRange(time1, time2);
        } catch (NotesException e) {
            error("Cannot convert DDateRange to DateRange", e);
        }
        return null;
    }

    /**
     * Computes the Notes time zone value for a given time zone.
     *
     * <p>For time zones that are not a full hour increment from GMT, the
     * return value is an integer in the format <code>mmhh</code> where
     * <code>mm</code> is the minutes component of the time relative to GMT and
     * <code>hh</code> is the hours component of the time relative to GMT.
     *
     * <p>The offset might have changed in history. For this case, a reference
     * date is given to find the correct offset at the given date.</p>
     *
     * @param zone a Java time zone
     * @param date a reference date for computing the zone
     * @return the Notes time zone value
     * @see #getOffset(DateTime)
     */
    static int getNotesTimeZoneValue(final TimeZone zone, final long date) {
        int offsetMinutes = zone.getRawOffset() / MILLIS_PER_MINUTE;
        int offsetHours = offsetMinutes / MINUTES_PER_HOUR;
        if (MINUTES_PER_HOUR * offsetHours == offsetMinutes) {
            return -offsetHours;
        } else {
            return -(offsetMinutes % MINUTES_PER_HOUR) * TZ_MINUTES_OFFSET - offsetHours;
        }
    }

    /**
     * Returns the raw offset of the time zone in milli seconds for a given Notes time zone value.
     *
     * @param dateTime a Notes date/time value
     * @return war offset in milli seconds as used in Java time zones
     * @see #getNotesTimeZoneValue(TimeZone, long)
     */
    int getOffset(final DateTime dateTime) {
        int zoneValue;
        try {
            zoneValue = dateTime.getTimeZone();
            int offset = getRawOffset(zoneValue);
            if (dateTime.isDST()) {
                offset -= MILLIS_PER_HOUR;
            }
            return offset;
        } catch (NotesException e) {
            getMonitor().warn("Cannot get time zone from Notes date/time", e);
            return 0;
        }
    }

    /**
     * Returns the offset in milli seconds for a given Notes time zone value.
     *
     * @param zoneValue Notes time zone value
     * @return offset in milli seconds as used in Java time zones
     * @see #getOffset(DateTime)
     */
    protected static int getRawOffset(final int zoneValue) {
        if (zoneValue < TZ_MINUTES_OFFSET && zoneValue > -TZ_MINUTES_OFFSET) {
            return zoneValue * MILLIS_PER_HOUR;
        } else {
            return (zoneValue % TZ_MINUTES_OFFSET) * MILLIS_PER_HOUR + (zoneValue / TZ_MINUTES_OFFSET) * MILLIS_PER_MINUTE;
        }
    }

    /**
     * Checks if a DateTime object defines a date.
     *
     * @param dateTime a Notes DateTime object
     * @return <code>true</code> if a date is defined, else <code>false</code>
     */
    protected boolean isDateSet(final DateTime dateTime) {
        try {
            return !"".equals(dateTime.getDateOnly());
        } catch (NotesException e) {
            error("Cannot check if date is set.", e);
            return false;
        }
    }

    /**
     * Checks if a DateTime object defines a time.
     *
     * @param dateTime a Notes DateTime object
     * @return <code>true</code> if a time is defined, else <code>false</code>
     */
    protected boolean isTimeSet(final DateTime dateTime) {
        try {
            return !"".equals(dateTime.getTimeOnly());
        } catch (NotesException e) {
            error("Cannot check if time is set.", e);
            return false;
        }
    }

    /**
     * Checks if a Calendar object defines a time.
     *
     * @param calendar a Calendar object
     * @return <code>true</code> if a time is defined, else <code>false</code>
     */
    protected boolean isTimeSet(final Calendar calendar) {
        return calendar.isSet(Calendar.HOUR_OF_DAY) || calendar.isSet(Calendar.MINUTE) || calendar.isSet(Calendar.SECOND)
                || calendar.isSet(Calendar.MILLISECOND);
    }

    /**
     * Checks if a Calendar object defines a date.
     *
     * @param calendar a Calendar object
     * @return <code>true</code> if a date is defined, else <code>false</code>
     */
    protected boolean isDateSet(final Calendar calendar) {
        return calendar.isSet(Calendar.YEAR) || calendar.isSet(Calendar.MONTH) || calendar.isSet(Calendar.DATE);
    }

    /**
     * Converts a Notes DateTime into a <code>java.util.Calendar</code>. For
     * invalid dates (e.g. no date- and no time-component available) a null
     * value is returned.
     *
     * @param dateTime a Notes DateTime object
     * @return a <code>java.util.Calendar</code> object or <code>null</code>
     *         if dateTime is invalid
     */
    private Date createDate(final DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            return dateTime.toJavaDate();
        } catch (NotesException e) {
            return null;
        }
    }

    /**
     * Sends an error to the current monitor.
     *
     * @param message the message
     * @param e the NotesException, can be null
     */
    protected void error(final String message, final NotesException e) {
        getMonitor().error(this.getClass().getName() + ":" + message, new DominoException(e));
    }

    /**
     * Sends a warning to the current monitor.
     *
     * @param message the message
     * @param e the NotesException, can be null
     */
    protected void info(final String message, final NotesException e) {
        getMonitor().info(this.getClass().getName() + ":" + message, new DominoException(e));
    }

    /**
     * Sends an info to the current monitor.
     *
     * @param message the message
     * @param e the NotesException, can be null
     */
    protected void warn(final String message, final NotesException e) {
        getMonitor().warn(this.getClass().getName() + ":" + message, new DominoException(e));
    }
}
