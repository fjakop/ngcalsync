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
package de.jakop.ngcalsync.google;

import java.util.List;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.exception.SynchronisationException;
import de.jakop.ngcalsync.filter.ICalendarEventFilter;


/**
 * Access to Google calendar
 * 
 * @author fjakop
 */
public interface IGoogleCalendarDAO {

	/**
	 * Unserts a calendar event
	 * 
	 * @param event
	 * @return the id of the inserted event
	 */
	public abstract String insert(CalendarEvent event);

	/**
	 * Updates a calendar event
	 * 
	 * @param id 
	 * @param event
	 */
	public abstract void update(String id, CalendarEvent event);

	/**
	 * Deletes a calendar event
	 * 
	 * @param id
	 */
	public abstract void delete(String id);

	/**
	 * Reads all calendar events
	 * 
	 * @param filters
	 * @return all calendar event
	 */
	public abstract List<CalendarEvent> getEvents(ICalendarEventFilter[] filters) throws SynchronisationException;

}