/**
 * Copyright © 2012, Frank Jakop
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the <organization> nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
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
package de.jakop.ngcalsync.calendar;

import de.jakop.ngcalsync.util.Dates;

import java.util.Calendar;

/**
 * Value object for calendar events
 */
public class CalendarEvent {

    private final static String toStringFormat = "" + // //$NON-NLS-1$
            "ID             : %s%n" + // //$NON-NLS-1$
            "Title          : %s%n" + // //$NON-NLS-1$
            "Content        : %s%n" + // //$NON-NLS-1$
            "StartDateTime  : %s%n" + // //$NON-NLS-1$
            "EndDateTime    : %s%n" + // //$NON-NLS-1$
            "Location       : %s%n" + // //$NON-NLS-1$
            "LastUpdated    : %s%n" + // //$NON-NLS-1$
            "EventType      : %s"; //$NON-NLS-1$

    private String id;
    private String title;
    private String content;
    private Calendar startDateTime;
    private Calendar endDateTime;
    private String location;
    private Calendar lastUpdated;
    private EventType eventType = EventType.NORMAL_EVENT;
    private boolean _private;


    /**
     * @return The string representing this object's values.
     */
    public String format() {
        return String.format(toStringFormat, //
                id, //
                title, //
                content, //
                format(startDateTime), //
                format(endDateTime), //
                location, //
                format(lastUpdated), //
                eventType);
    }

    private static String format(final Calendar date) {
        if (date == null) {
            return null;
        }
        return Dates.DATE_FORMAT.format(date.getTime());
    }

    /**
     * @return event's technical id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id event's technical id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return event's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title event's title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return event's description
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content event's description
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * @return event's starting time
     */
    public Calendar getStartDateTime() {
        return startDateTime;
    }

    /**
     * @param startDateTime event's starting time
     */
    public void setStartDateTime(final Calendar startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     * @return event's ending time
     */
    public Calendar getEndDateTime() {
        return endDateTime;
    }

    /**
     * @param endDateTime event's ending time
     */
    public void setEndDateTime(final Calendar endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * @param location event's location
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * @return event's location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param lastupdated last update of this event
     */
    public void setLastUpdated(final Calendar lastupdated) {
        lastUpdated = lastupdated;
    }

    /**
     * @return last update of this event
     */
    public Calendar getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param type event's type
     */
    public void setEventType(final EventType type) {
        eventType = type;
    }

    /**
     * @return event's type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * @param _private event's privacy flag
     */
    public void setPrivate(final boolean _private) {
        this._private = _private;
    }

    /**
     * @return event's privacy flag
     */
    public boolean isPrivate() {
        return _private;
    }

    /**
     *
     * @return <code>true</code>, if event's type is all day event or anniversary
     */
    public boolean isAllDay() {
        return getEventType() == EventType.ALL_DAY_EVENT || getEventType() == EventType.ANNIVERSARY;
    }

}
