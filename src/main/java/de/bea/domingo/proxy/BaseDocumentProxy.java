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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.DateRange;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.NotesError;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import de.bea.domingo.DBase;
import de.bea.domingo.DBaseDocument;
import de.bea.domingo.DBaseItem;
import de.bea.domingo.DDatabase;
import de.bea.domingo.DDateRange;
import de.bea.domingo.DDocument;
import de.bea.domingo.DEmbeddedObject;
import de.bea.domingo.DNotesException;
import de.bea.domingo.DNotesMonitor;
import de.bea.domingo.DProfileDocument;
import de.bea.domingo.DRichTextItem;
import de.bea.domingo.util.DateUtil;
import de.bea.domingo.util.GregorianDateTimeRange;
import de.bea.domingo.util.Timezones;

/**
 * Represents a document in a database.
 */
public abstract class BaseDocumentProxy extends BaseProxy implements DBaseDocument {

    /**
     * Constructor for DDocumentImpl.
     *
     * @param theFactory the controlling factory
     * @param parent the parent object
     * @param document the notes document
     * @param monitor the monitor
     */
    protected BaseDocumentProxy(final NotesProxyFactory theFactory, final DBase parent,
                                final Document document, final DNotesMonitor monitor) {
        super(theFactory, parent, document, monitor);
        if (document == null) {
            throw new RuntimeException("Document not defined");
        }
    }

    /**
     * Creates or returns a cached implementation of the requested document
     * interface.
     *
     * @param theFactory the controlling factory
     * @param parent the parent object
     * @param document the associated Notes document
     * @param monitor the monitor
     *
     * @return implementation of interface DDocument or null
     */
    static BaseDocumentProxy getInstance(final NotesProxyFactory theFactory, final DBase parent,
                                         final Document document, final DNotesMonitor monitor) {
        if (document == null) {
            return null;
        }
        BaseDocumentProxy documentProxy = (BaseDocumentProxy) theFactory.getBaseCache().get(document);
        if (documentProxy == null) {
            boolean isProfile = false;
            try {
                isProfile = document.isProfile();
            } catch (NotesException e) {
                isProfile = false;
            }
            if (isProfile) {
                documentProxy = new ProfileDocumentProxy(theFactory, parent, document, monitor);
            } else {
                documentProxy = new DocumentProxy(theFactory, parent, document, monitor);
            }
            documentProxy.setMonitor(monitor);
            theFactory.getBaseCache().put(document, documentProxy);
        }
        return documentProxy;
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getParentDatabase()
     */
    public final DDatabase getParentDatabase() {
        getFactory().preprocessMethod();
        try {
            final Database database = getDocument().getParentDatabase();
            return DatabaseProxy.getInstance(getFactory(), null, database, getMonitor(), true);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.parentdatabase"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getFirstItem(String)
     */
    public final DBaseItem getFirstItem(final String itemName) {
        getFactory().preprocessMethod();
        try {
            final Item item = getDocument().getFirstItem(itemName);
            if (item != null) {
                return BaseItemProxy.getInstance(getFactory(), this, item, getMonitor());
            }
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.firstitem.1", itemName), e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getCreated()
     */
    public final Calendar getCreated() {
        getFactory().preprocessMethod();
        try {
            final DateTime dateTime = getDocument().getCreated();
            final Calendar calendar = createCalendar(dateTime);
            getFactory().recycle(dateTime);
            return calendar;
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.createddate"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#save()
     */
    public final boolean save() {
        getFactory().preprocessMethod();
        try {
            return getDocument().save();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.save.document"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#save(boolean, boolean)
     */
    public final boolean save(final boolean force, final boolean makeresponse) {
        getFactory().preprocessMethod();
        try {
            return getDocument().save(force, makeresponse);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot save document", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#appendItemValue(java.lang.String)
     */
    public final DBaseItem appendItemValue(final String name) {
        getFactory().preprocessMethod();
        try {
            final Item item = getDocument().appendItemValue(name, "");
            return BaseItemProxy.getInstance(getFactory(), this, item, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.append.value.1") + name, e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#appendItemValue(java.lang.String, int)
     */
    public final DBaseItem appendItemValue(final String name, final int value) {
        getFactory().preprocessMethod();
        try {
            final Item item = getDocument().appendItemValue(name, value);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, item, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.append.value.integer.1") + name, e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#appendItemValue(java.lang.String, double)
     */
    public final DBaseItem appendItemValue(final String name, final double value) {
        getFactory().preprocessMethod();
        try {
            final Item item = getDocument().appendItemValue(name, value);
            return  BaseItemProxy.getInstance(getFactory(), this, item, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.append.value.double.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, java.util.Calendar)
     */
    public final DBaseItem replaceItemValue(final String name, final Calendar value) {
        if (value == null) {
            return replaceItemValue(name, EMPTY_STRING);
        }
        getFactory().preprocessMethod();
        try {
            final DateTime dateTime = createDateTime(value);
            final Item notesItem = getDocument().replaceItemValue(name, dateTime);
            getFactory().recycle(dateTime);
            return BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.set.value.calendar.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, java.util.TimeZone)
     */
    public final DBaseItem replaceItemValue(final String name, final TimeZone value) {
        String s = EMPTY_STRING;
        if (value == null) {
            getMonitor().warn("time zone is null; storing an empty string in item " + name);
        } else {
            s = Timezones.getLotusTimeZoneString(value);
            if (s.startsWith("Unknown")) {
                getMonitor().warn("Unknown time zone identifier (using default): " + s);
                s = TimeZone.getDefault().getID();
                s = Timezones.getLotusTimeZoneString(s);
            }
        }
        return replaceItemValue(name, s);
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, de.bea.domingo.DDateRange)
     */
    public final DBaseItem replaceItemValue(final String name, final DDateRange value) {
        return replaceItemValue(name, value.getFrom(), value.getTo());
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, java.util.Calendar, java.util.Calendar)
     */
    public final DBaseItem replaceItemValue(final String name, final Calendar calendar1, final Calendar calendar2) {
        if (calendar1 == null && calendar2 == null) {
            return replaceItemValue(name, EMPTY_STRING);
        } else if (calendar1 == null) {
            return replaceItemValue(name, calendar2);
        } else if (calendar2 == null) {
            return replaceItemValue(name, calendar1);
        }
        getFactory().preprocessMethod();
        try {
            final DateRange dateRange = createDateRange(calendar1, calendar2);
            final Item notesItem = getDocument().replaceItemValue(name, dateRange);
            getFactory().recycle(dateRange);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.set.daterange"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, java.util.List)
     */
    public final DBaseItem replaceItemValue(final String name, final List values) {
        if (values == null || values.size() == 0) {
            return replaceItemValue(name, EMPTY_STRING);
        }
        getFactory().preprocessMethod();
        final List convertedKeys = convertCalendarsToNotesDateTime(values);
        final Vector vector = convertListToVector(convertedKeys);
        try {
            final Item notesItem = getDocument().replaceItemValue(name, vector);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.replace.value.1", name), e);
        } finally {
            recycleDateTimeList(convertedKeys);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#save(boolean)
     */
    public final boolean save(final boolean force) {
        getFactory().preprocessMethod();
        try {
            return getDocument().save(force);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.save"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#createRichTextItem(String)
     */
    public final DRichTextItem createRichTextItem(final String name) {
        getFactory().preprocessMethod();
        try {
            final RichTextItem item = getDocument().createRichTextItem(name);
            return (DRichTextItem) BaseItemProxy.getInstance(getFactory(), this, item, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.create.rti.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#remove()
     */
    public final boolean remove() {
        return remove(false);
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#remove(boolean)
     */
    public final boolean remove(final boolean force) {
        getFactory().preprocessMethod();
        try {
            return getDocument().remove(force);
        } catch (NotesException e) {
            if (e.id == NotesError.NOTES_ERR_DELETED) {
                return false;
            }
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.remove"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getAttachment(String)
     */
    public final DEmbeddedObject getAttachment(final String attachmentName) {
        getFactory().preprocessMethod();
        try {
            final EmbeddedObject eo = getDocument().getAttachment(attachmentName);
            final DEmbeddedObject proxy =
                EmbeddedObjectProxy.getInstance(getFactory(), this, eo, getMonitor());
            return proxy;
        } catch (NotesException e) {
            String msg = RESOURCES.getString("basedocument.cannot.get.attachment.1", attachmentName);
            throw newRuntimeException(msg, e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#removeItem(String)
     */
    public final void removeItem(final String name) {
        getFactory().preprocessMethod();
        try {
            getDocument().removeItem(name);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.remove.item.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValue(String)
     */
    public final List getItemValue(final String name) {
        getFactory().preprocessMethod();
        try {
            Vector vector = null;
            final Item item = getDocument().getFirstItem(name);
            if (item != null && (item.getType() == Item.DATETIMES)) {
                vector = ((Document) getNotesObject()).getItemValueDateTimeArray(name);
                List list = convertNotesDateTimesToCalendar(vector);
                final String text = getDocument().getFirstItem(name).abstractText(DATETIME_STRING_LENGTH + 2, false, false);
                if (text != null && text.length() > 0) {
//                    if (text.indexOf('-') >= 0) { // we have a date/time range
//                        GregorianDateTimeRange range = new GregorianDateTimeRange((Calendar) list.get(0), (Calendar) list.get(1));
//                        List result = new ArrayList(1);
//                        result.add(range);
//                        return result;
//                    } else { // we have a list of dates/times
                        return list;
//                    }
                }
                return new ArrayList(0);
            } else {
                vector = getDocument().getItemValue(name);
            }
            if (vector == null) {
                vector = new Vector();
            }
            return Collections.unmodifiableList(vector);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.value.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueString(java.lang.String)
     */
    public final String getItemValueString(final String name) {
        getFactory().preprocessMethod();
        try {
            final String value = getDocument().getItemValueString(name);
            if (value != null) {
                return value;
            }
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.value.string.1", name), e);
        }
        return EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#hasItem(String)
     */
    public final boolean hasItem(final String name) {
        getFactory().preprocessMethod();
        try {
            return getDocument().hasItem(name);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.existence.1", name), e);
        }
    }

    /**
     * Returns the associated Notes document.
     *
     * @return the Notes document object
     */
    final Document getDocument() {
        return (Document) getNotesObject();
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueDate(java.lang.String)
     */
    public final Calendar getItemValueDate(final String name) {
        getFactory().preprocessMethod();
        try {
            Vector vector = ((Document) getNotesObject()).getItemValueDateTimeArray(name);
            Calendar calendar = null;
            if (vector != null && vector.size() > 0) {
                calendar = createCalendar((DateTime) vector.get(0));
                recycleDateTimeList(vector);
            }
            return calendar;
        } catch (NotesException e) {
            if (e.id == NotesError.NOTES_ERR_NOT_A_DATE_ITEM) {
                return null;
            }
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.value.calendar.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueDateRange(java.lang.String)
     */
    public final DDateRange getItemValueDateRange(final String name) {
        getFactory().preprocessMethod();
        try {
            Vector vector = ((Document) getNotesObject()).getItemValue(name);
            if (vector == null || vector.size() == 0) {
                return null;
            }
            Calendar start = null;
            Calendar end = null;
            Object object = vector.get(0);
            if (object instanceof DateRange) {
                start = createCalendar(((DateRange) object).getStartDateTime());
                end = createCalendar(((DateRange) object).getEndDateTime());
            } else if (object instanceof DateTime) {
                start = createCalendar((DateTime) object);
                end = null;
                if (vector.size() > 1) {
                    end = createCalendar((DateTime) vector.get(1));
                }
            }
            DDateRange range = null;
            range = new GregorianDateTimeRange(start, end);
            recycleDateTimeList(vector);
            return range;
        } catch (NotesException e) {
            if (e.id == NotesError.NOTES_ERR_NOT_A_DATE_ITEM) {
                return null;
            }
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.value.calendar.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueInteger(java.lang.String)
     */
    public final Integer getItemValueInteger(final String name) {
        getFactory().preprocessMethod();
        try {
            if (!getDocument().hasItem(name)) {
                return null;
            }
            final Item item = getDocument().getFirstItem(name);
            if (item == null) {
                return null;
            }
            if (item.getType() == Item.NUMBERS) {
                final double value = getDocument().getItemValueDouble(name);
                return new Integer((int) Math.round(value));
            } else if (item.getType() == Item.TEXT) {
                final String value = getDocument().getItemValueString(name);
                if (value == null || value.length() == 0) {
                    return null;
                }
                try {
                    return new Integer(value);
                } catch (NumberFormatException e) {
                    getMonitor().warn(RESOURCES.getString("basedocument.cannot.parse.integer.1", value));
                    return null;
                }
            } else {
                return null;
            }
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.value.integer.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueDouble(java.lang.String)
     */
    public final Double getItemValueDouble(final String name) {
        getFactory().preprocessMethod();
        try {
            if (!getDocument().hasItem(name)) {
                return null;
            }
            final Item item = getDocument().getFirstItem(name);
            if (item == null) {
                return null;
            }
            if (item.getType() == Item.NUMBERS) {
                final double value = getDocument().getItemValueDouble(name);
                return new Double(value);
            } else if (item.getType() == Item.TEXT) {
                final String value = getDocument().getItemValueString(name);
                if (value == null || value.length() == 0) {
                    return null;
                }
                try {
                    return new Double(value);
                } catch (NumberFormatException e) {
                    getMonitor().warn(RESOURCES.getString("basedocument.cannot.parse.double.1", value));
                    return null;
                }
            } else {
                return null;
            }
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.value.double.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getAuthors()
     */
    public final List getAuthors() {
        getFactory().preprocessMethod();
        try {
            Vector authors = getDocument().getAuthors();
            return authors == null ? new ArrayList(0) : new ArrayList(authors);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.authors"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getLastAccessed()
     */
    public final Calendar getLastAccessed() {
        getFactory().preprocessMethod();
        try {
            final DateTime dateTime = getDocument().getLastAccessed();
            if (dateTime != null) {
                final Calendar calendar = createCalendar(dateTime);
                getFactory().recycle(dateTime);
                return calendar;
            }
            return null;
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.lastaccessed"), e);
        }
    }

    /**
     * Notes formula expression to evaluate the last modified date
     * in a standard (local independent) string format.
     */
    private static final String MODIFIED_FORMULA = "_date := @Modified; "
            + "@Text(@Year(_date)) + \"-\" + "
            + "@Right(\"0\" + @Text(@Month(_date)); 2) + \"-\" + "
            + "@Right(\"0\" + @Text(@Day(_date)); 2) + \" \" + "
            + "@Right(\"0\" + @Text(@Hour(_date)); 2) + \":\" + "
            + "@Right(\"0\" + @Text(@Minute(_date)); 2) + \":\" + "
            + "@Right(\"0\" + @Text(@Second(_date)); 2)";

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getLastModified()
     */
    public final Calendar getLastModified() {
        getFactory().preprocessMethod();
        try {
            final String temp = (String) this.getParentDatabase().getSession().evaluate(MODIFIED_FORMULA, this).get(0);
            final Calendar lastModified = DateUtil.parseDate(temp, false);
            return lastModified;
        } catch (DNotesException e) {
            getMonitor().warn("Cannot get last modified date; trying to get last modified in file.");
            try {
                final DateTime dateTime = getDocument().getLastModified();
                if (dateTime != null) {
                    final Calendar calendar = createCalendar(dateTime);
                    getFactory().recycle(dateTime);
                    return calendar;
                }
            } catch (NotesException e1) {
                getMonitor().fatalError(RESOURCES.getString("basedocument.cannot.get.lastmodified"), e);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItems()
     */
    public final Iterator getItems() {
        getFactory().preprocessMethod();
        Vector items = null;
        try {
            items = getDocument().getItems();
            return new ItemsIterator(items);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.items"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#appendItemValue(java.lang.String, java.util.List)
     */
    public final DBaseItem appendItemValue(final String name, final List values) {
        if (values == null) {
            return replaceItemValue(name, EMPTY_STRING);
        }
        getFactory().preprocessMethod();
        final List convertedKeys = convertCalendarsToNotesDateTime(values);
        final Vector vector = convertListToVector(convertedKeys);
        try {
            final Item item = getDocument().appendItemValue(name, vector);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, item, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.append.value.list.1", name), e);
        } finally {
            recycleDateTimeList(convertedKeys);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, int)
     */
    public final DBaseItem replaceItemValue(final String name, final int value) {
        return replaceItemValue(name, new Integer(value));
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, java.lang.Integer)
     */
    public final DBaseItem replaceItemValue(final String name, final Integer value) {
        if (value == null) {
            return replaceItemValue(name, EMPTY_STRING);
        }
        getFactory().preprocessMethod();
        try {
            final Item notesItem;
            notesItem = getDocument().replaceItemValue(name, value);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.replace.value.list.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, double)
     */
    public final DBaseItem replaceItemValue(final String name, final double value) {
        return replaceItemValue(name, new Double(value));
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, java.lang.Double)
     */
    public final DBaseItem replaceItemValue(final String name, final Double value) {
        if (value == null) {
            return replaceItemValue(name, EMPTY_STRING);
        }
        getFactory().preprocessMethod();
        try {
            final Item notesItem = getDocument().replaceItemValue(name, value);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.replace.value.double.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#appendItemValue(java.lang.String, java.lang.String)
     */
    public final DBaseItem appendItemValue(final String name, final String value) {
        if (value == null) {
            return replaceItemValue(name, EMPTY_STRING);
        }
        getFactory().preprocessMethod();
        try {
            final Item notesItem = this.getDocument().appendItemValue(name, value);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.append.value.string.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#appendItemValue(java.lang.String, java.util.Calendar)
     */
    public final DBaseItem appendItemValue(final String name, final Calendar value) {
        if (value == null) {
            return appendItemValue(name, EMPTY_STRING);
        }
        Item notesItem;
        try {
            final DateTime dateTime = createDateTime(value);
            notesItem = getDocument().appendItemValue(name, dateTime);
            getFactory().recycle(dateTime);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.append.value.calendar.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValue(java.lang.String, java.lang.String)
     */
    public final DBaseItem replaceItemValue(final String name, final String value) {
        if (value == null) {
            return replaceItemValue(name, EMPTY_STRING);
        }
        try {
            final Item notesItem = getDocument().replaceItemValue(name, value);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.replace.value.string.1", name), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getEmbeddedObjects()
     */
    public final Iterator getEmbeddedObjects() {
        getFactory().preprocessMethod();
        try {
            final Vector vector = getDocument().getEmbeddedObjects();
            final List list = new ArrayList();
            for (final Iterator it = vector.iterator(); it.hasNext();) {
                final EmbeddedObject eo = (EmbeddedObject) it.next();
                final DEmbeddedObject deo = EmbeddedObjectProxy.getInstance(getFactory(), this, eo, getMonitor());
                list.add(deo);
            }
            return list.iterator();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.embeddedobject"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getAttachments()
     */
    public final List getAttachments() {
        getFactory().preprocessMethod();
        List list = new ArrayList();
        try {
            if (getDocument().hasEmbedded()) {
                Iterator iterator = getDocument().getItems().iterator();
                while (iterator.hasNext()) {
                    Item it = (Item) iterator.next();
                    if (it.getType() == Item.ATTACHMENT) {
                        list.add(getDocument().getAttachment(it.getValueString()).getName());
                    }
                }
            }
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.attachments"), e);
        }
        return list;
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueSize(java.lang.String)
     */
    public final int getItemValueSize(final String name) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getFirstItem(name).getValues().size();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.valuesize.1", name), e);
        }
    }


    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#copyAllItems(de.bea.domingo.DBaseDocument, boolean)
     */
    public final void copyAllItems(final DBaseDocument doc, final boolean replace) {
        if (!(doc instanceof BaseProxy)) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.copy.allitems"), new ClassCastException(doc.getClass().getName()));
        }
        getFactory().preprocessMethod();
        try {
            final Base notesDoc = ((BaseProxy) doc).getNotesObject();
            ((Document) getNotesObject()).copyAllItems((Document) notesDoc, replace);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.copy.allitems"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#computeWithForm(boolean)
     */
    public final boolean computeWithForm(final boolean raiseError) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).computeWithForm(true, raiseError);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot copy all items", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#copyItem(de.bea.domingo.DBaseItem, java.lang.String)
     */
    public final DBaseItem copyItem(final DBaseItem item, final String s) {
        getFactory().preprocessMethod();
        if (!(item instanceof ItemProxy)) {
            throw newRuntimeException("invalid item");
        }
        try {
            final Item notesItem = (Item) ((BaseProxy) item).getNotesObject();
            final Item newItem = ((Document) getNotesObject()).copyItem(notesItem, s);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, newItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException("Cannot copy item", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#copyItem(de.bea.domingo.DBaseItem)
     */
    public final DBaseItem copyItem(final DBaseItem item) {
        getFactory().preprocessMethod();
        try {
            final Item notesItem = (Item) ((BaseProxy) item).getNotesObject();
            final Item newItem = ((Document) getNotesObject()).copyItem(notesItem);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, newItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException("Cannot copy item", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#createReplyMessage(boolean)
     */
    public final DDocument createReplyMessage(final boolean flag) {
        getFactory().preprocessMethod();
        try {
            Document document = ((Document) getNotesObject()).createReplyMessage(flag);
            return (DDocument) BaseDocumentProxy.getInstance(getFactory(), this, document, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.create.reply"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#encrypt()
     */
    public final void encrypt() {
        getFactory().preprocessMethod();
        try {
             ((Document) getNotesObject()).encrypt();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.encrypt.document"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#generateXML()
     */
    public final String generateXML() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).generateXML();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.generate.xml"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#generateXML(java.io.Writer)
     */
    public final void generateXML(final Writer writer) {
        getFactory().preprocessMethod();
        try {
            ((Document) getNotesObject()).generateXML(writer);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot generate XML", e);
        } catch (IOException e) {
            throw newRuntimeException("Cannot generate XML", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getColumnValues()
     */
    public final List getColumnValues() {
        getFactory().preprocessMethod();
        try {
            final Vector vector = ((Document) getNotesObject()).getColumnValues();
            final List convertedValues = convertNotesDateTimesToCalendar(vector);
            recycleDateTimeList(vector);
            return Collections.unmodifiableList(convertedValues);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.columnvalues"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getEncryptionKeys()
     */
    public final List getEncryptionKeys() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getEncryptionKeys();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.encryptionkeys"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueCustomData(java.lang.String, java.lang.String)
     */
    public final Object getItemValueCustomData(final String name, final String s1) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getItemValueCustomData(name, s1);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.customdata"), e);
        } catch (IOException e) {
            throw newRuntimeException("Cannot get custom data", e);
        } catch (ClassNotFoundException e) {
            throw newRuntimeException("Cannot get custom data", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueCustomData(java.lang.String)
     */
    public final Object getItemValueCustomData(final String name) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getItemValueCustomData(name);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot get custom data", e);
        } catch (IOException e) {
            throw newRuntimeException("Cannot get custom data", e);
        } catch (ClassNotFoundException e) {
            throw newRuntimeException("Cannot get custom data", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueCustomDataBytes(java.lang.String, java.lang.String)
     */
    public final byte[] getItemValueCustomDataBytes(final String name, final String s1) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getItemValueCustomDataBytes(name, s1);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot get custom data", e);
        } catch (IOException e) {
            throw newRuntimeException("Cannot get custom data", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getItemValueDateTimeArray(java.lang.String)
     */
    public final List getItemValueDateTimeArray(final String name) {
        getFactory().preprocessMethod();
        try {
            Vector vector = ((Document) getNotesObject()).getItemValueDateTimeArray(name);
            final List convertedValues = convertNotesDateTimesToCalendar(vector);
            recycleDateTimeList(vector);
            return Collections.unmodifiableList(convertedValues);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.datetimearray"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getLockHolders()
     */
    public final List getLockHolders() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getLockHolders();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.lockholders"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getReceivedItemText()
     */
    public final List getReceivedItemText() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getReceivedItemText();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.receiveditemtext"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getSigner()
     */
    public final String getSigner() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getSigner();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.signer"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getSize()
     */
    public final int getSize() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getSize();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.size"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#getVerifier()
     */
    public final String getVerifier() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).getVerifier();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.get.verifier"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#hasEmbedded()
     */
    public final boolean hasEmbedded() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).hasEmbedded();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.embedded"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isDeleted()
     */
    public final boolean isDeleted() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).isDeleted();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.deleted"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isEncrypted()
     */
    public final boolean isEncrypted() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).isEncrypted();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.encrypted"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isEncryptOnSend()
     */
    public final boolean isEncryptOnSend() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).isEncryptOnSend();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.shouldbe.encrypted"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isProfile()
     */
    public final boolean isProfile() {
        return this instanceof DProfileDocument;
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isSaveMessageOnSend()
     */
    public final boolean isSaveMessageOnSend() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).isSaveMessageOnSend();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.shouldbe.savedonsend"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isSentByAgent()
     */
    public final boolean isSentByAgent() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).isSentByAgent();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.sentbyagent"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isSigned()
     */
    public final boolean isSigned() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).isSigned();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.signed"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isSignOnSend()
     */
    public final boolean isSignOnSend() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).isSignOnSend();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.shouldbe.signedonsend"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#isValid()
     */
    public final boolean isValid() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).isValid();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.check.valid"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lock()
     */
    public final boolean lock() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lock();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.lock"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lock(boolean)
     */
    public final boolean lock(final boolean provisionalOk) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lock(provisionalOk);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot lock", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lock(java.util.List, boolean)
     */
    public final boolean lock(final List names, final boolean provisionalOk) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lock(convertListToVector(names), provisionalOk);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot lock", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lock(java.util.List)
     */
    public final boolean lock(final List names) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lock(convertListToVector(names));
        } catch (NotesException e) {
            throw newRuntimeException("Cannot lock", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lock(java.lang.String, boolean)
     */
    public final boolean lock(final String name, final boolean provisionalOk) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lock(name, provisionalOk);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot lock", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lock(java.lang.String)
     */
    public final boolean lock(final String name) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lock(name);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot lock", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lockProvisional()
     */
    public final boolean lockProvisional() {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lockProvisional();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.lock.provisional"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lockProvisional(java.util.List)
     */
    public final boolean lockProvisional(final List names) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lockProvisional(convertListToVector(names));
        } catch (NotesException e) {
            throw newRuntimeException("Cannot lock provisional", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#lockProvisional(java.lang.String)
     */
    public final boolean lockProvisional(final String name) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).lockProvisional(name);
        } catch (NotesException e) {
            throw newRuntimeException("Cannot lock provisional", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#removePermanently(boolean)
     */
    public final boolean removePermanently(final boolean force) {
        getFactory().preprocessMethod();
        try {
            return ((Document) getNotesObject()).removePermanently(force);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.remove.permanently"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#renderToRTItem(de.bea.domingo.DRichTextItem)
     */
    public final boolean renderToRTItem(final DRichTextItem richTextItem) {
        getFactory().preprocessMethod();
        try {
            final RichTextItem rtItem = (RichTextItem) ((RichTextItemProxy) richTextItem).getNotesObject();
            return ((Document) getNotesObject()).renderToRTItem(rtItem);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.render.rti"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValueCustomData(java.lang.String, java.lang.Object)
     */
    public final DBaseItem replaceItemValueCustomData(final String name, final Object obj) {
        getFactory().preprocessMethod();
        try {
            final Item notesItem = ((Document) getNotesObject()).replaceItemValueCustomData(name, obj);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.replce.value.customdata"), e);
        } catch (IOException e) {
            throw newRuntimeException("Cannot replace item values custom data", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValueCustomData(java.lang.String, java.lang.String, java.lang.Object)
     */
    public final DBaseItem replaceItemValueCustomData(final String name, final String type, final Object obj) {
        getFactory().preprocessMethod();
        try {
            final Item notesItem = ((Document) getNotesObject()).replaceItemValueCustomData(name, type, obj);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException("Cannot replace item values custom data", e);
        } catch (IOException e) {
            throw newRuntimeException("Cannot replace item values custom data", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#replaceItemValueCustomDataBytes(java.lang.String, java.lang.String, byte[])
     */
    public final DBaseItem replaceItemValueCustomDataBytes(final String s, final String s1, final byte[] abyte0) {
        getFactory().preprocessMethod();
        try {
            final Item notesItem = ((Document) getNotesObject()).replaceItemValueCustomDataBytes(s, s1, abyte0);
            return (DBaseItem) BaseItemProxy.getInstance(getFactory(), this, notesItem, getMonitor());
        } catch (NotesException e) {
            throw newRuntimeException("Cannot replace item values custom data", e);
        } catch (IOException e) {
            throw newRuntimeException("Cannot replace item values custom data", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#setEncryptionKeys(java.util.List)
     */
    public final void setEncryptionKeys(final List keys) {
        getFactory().preprocessMethod();
        try {
            ((Document) getNotesObject()).setEncryptionKeys(convertListToVector(keys));
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.set.encryptionkeys"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#setUniversalID(java.lang.String)
     */
    public final void setUniversalID(final String unid) {
        getFactory().preprocessMethod();
        try {
            ((Document) getNotesObject()).setUniversalID(unid);
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.set.universalid"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#unlock()
     */
    public final void unlock() {
        getFactory().preprocessMethod();
        try {
            ((Document) getNotesObject()).unlock();
        } catch (NotesException e) {
            throw newRuntimeException(RESOURCES.getString("basedocument.cannot.unlock"), e);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.bea.domingo.DBaseDocument#computeWithForm()
     */
    public final boolean computeWithForm() {
        return computeWithForm(true);
    }

    ////////////////////////////////////////////////
    //    Iterator classes
    ////////////////////////////////////////////////

    /**
     * A <code>ItemsIterator</code> allows iteration over a set of
     * <code>lotus.domino.Item</code>.
     *
     * #see getItems
     *
     * @author MarcusT
     */
    protected final class ItemsIterator implements Iterator {

        /** The iterator of the notes items.*/
        private final Iterator items;

        /**
         * Constructs an Iterator by use of a vector of
         * <code>lotus.domino.Item</code>s.
         *
         * @param itemsVector a vector of <code>lotus.domino.Item</code>s
         */
        protected ItemsIterator(final Vector itemsVector) {
            this.items = itemsVector.iterator();
        }

        /**
         * Indicates whether this iterator has a next element or not.
         *
         * @return boolean true if there is a next
         */
        public boolean hasNext() {
            return items.hasNext();
        }

        /**
         * Returns the next element of this iterator (hasNext()==true)
         * or <code>null</code> if hasNext()==false.
         *
         * @return an <code>DBaseItem</code>
         */
        public Object next() {
            final Item notesItem = (Item) items.next();
            final DBaseItem proxy = BaseItemProxy.getInstance(getFactory(),
                    BaseDocumentProxy.this, notesItem, getMonitor());
            return proxy;
        }

        /**
         * Throws an UnsupportedOperationException: The <tt>remove</tt>
         * operation is not supported by this Iterator.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @see de.bea.domingo.DBaseDocument#recycle()
     */
    public final void recycle() {
        getFactory().recycle(this);
        clearNotesObject();
    }
}
