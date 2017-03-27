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
package de.jakop.ngcalsync.filter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.calendar.CalendarEvent;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;

/**
 * Filters {@link CalendarEvent}s by {@link de.jakop.ngcalsync.calendar.EventType}.
 * 
 * @author fjakop
 */
public class EventTypeFilter implements ICalendarEventFilter {

	private final Log log = LogFactory.getLog(getClass());
	private final int[] acceptedEventTypes;


	/**
	 * 
	 * @param acceptedType
	 */
	public EventTypeFilter(final int... acceptedType) {
		acceptedEventTypes = acceptedType;
	}

	@Override
	public boolean accept(final CalendarEvent event) {
		Validate.notNull(event);
		if (event.getEventType() == null) {
			log.debug(TechMessage.get().MSG_EVENT_NOT_ACCEPTED_BY_FILTER_IS_NULL());
			return false;
		}
		if (ArrayUtils.contains(acceptedEventTypes, event.getEventType().getIntegerValue())) {
			log.debug(TechMessage.get().MSG_EVENT_ACCEPTED_BY_FILTER(event.format()));
			return true;
		}
		log.debug(TechMessage.get().MSG_EVENT_NOT_ACCEPTED_BY_FILTER(event.format()));
		return false;
	}

}
