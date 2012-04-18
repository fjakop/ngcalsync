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

package de.bea.domingo;

import java.io.Writer;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Base interface for all concrete document interfaces.
 *
 * @author <a href=mailto:kriede@users.sourceforge.net>Kurt Riede</a>
 */
public interface DBaseDocument extends DBase {

    /**
     * Return the database that contains a document.
     *
     * @return the database that contains a document
     */
    DDatabase getParentDatabase();

    /**
     * Returns the first item of a specified name in a document.
     *
     * <p>A document may contain more than one item of the same name.
     * To access other than the first item, use the Items property.</p>
     * <p>If the value of a field is computed for display, the value is not
     * stored as an item and is inaccessible from a Document object.
     * In some cases, you can access the field value another way.
     * For example, if a document has a DateComposed field computed for display
     * with the formula @Created, use getCreated.</p>
     *
     * <p><b>Using this method to get normal items (non-rich text)</b><br/>
     * To get a normal item, explicitly cast the return value from getFirstItem to DItem.
     * <pre>
     * DDocument doc;
     * //...set value of doc...
     * DItem item = (DItem)doc.getFirstItem("Body");
     * </pre></p>
     *
     * <p><b>Using this method to get rich text items</b><br/>
     * To get a rich text item, explicitly cast the return value from getFirstItem to DRichTextItem.
     * <pre>
     * DDocument doc;
     * //...set value of doc...
     * DRichTextItem rtitem = (DRichTextItem)doc.getFirstItem("Body");
     * </pre></p>
     *
     * @param name name of an item
     * @return DBaseItem
     */
    DBaseItem getFirstItem(String name);

    /**
     * Iterator for all items in a document.
     *
     * <p><b>Note</b></p>
     * Values set as <code>int</code>, are handled (by Notes) as a type
     * Number and returned to Java as a Double.
     *
     * @return Iterator for all items in a document. These are normal
     *         <code>DItem</code> as well as <code>DRichTextItems</code>
     */
    Iterator getItems();

    /**
     * The date a document was created.
     *
     * @return Calendar created date
     */
    Calendar getCreated();

    /**
     * Creates a new empty item in a document. A empty item has a value of type
     * String that is an empty String ("").
     *
     * <p><b>Note</b><br/>
     * In general, replaceItemValue is favored over appendItemValue. If an
     * item of the same name already exists in a document, appendItemValue
     * creates a second item of the same name, and the duplicate items are not
     * accessible through most methods. If you are creating a new document,
     * appendItemValue is safe.</p>
     *
     * @param name name of an item
     * @return DItem
     */
    DBaseItem appendItemValue(String name);

    /**
     * Creates a new item in a document and sets the item value.
     *
     * <p><b>Note</b><br/>
     * In general, replaceItemValue is favored over appendItemValue. If an
     * item of the same name already exists in a document, appendItemValue
     * creates a second item of the same name, and the duplicate items are not
     * accessible through most methods. If you are creating a new document,
     * appendItemValue is safe.</p>
     *
     * @param name name of an item
     * @param value The value of the new item.
     * @return DItem
     */
    DBaseItem appendItemValue(String name, String value);

    /**
     * Creates a new item in a document and sets the item value.
     *
     * <p><b>Note</b><br/>
     * In general, replaceItemValue is favored over appendItemValue. If an
     * item of the same name already exists in a document, appendItemValue
     * creates a second item of the same name, and the duplicate items are not
     * accessible through most methods. If you are creating a new document,
     * appendItemValue is safe.</p>
     *
     * <p><b>Note</b></p>
     * The value given as <code>int</code> is handled (by Notes) as a type
     * Number and returned to Java as a Double.
     *
     * @param name name of an item
     * @param value The value of the new item.
     * @return DItem
     */
    DBaseItem appendItemValue(String name, int value);

    /**
     * Creates a new item in a document and sets the item value.
     *
     * <p><b>Note</b><br/>
     * In general, replaceItemValue is favored over appendItemValue. If an
     * item of the same name already exists in a document, appendItemValue
     * creates a second item of the same name, and the duplicate items are not
     * accessible through most methods. If you are creating a new document,
     * appendItemValue is safe.</p>
     *
     * @param name name of an item
     * @param value The value of the new item.
     * @return DItem
     */
    DBaseItem appendItemValue(String name, double value);

    /**
     * Creates a new item in a document and sets the item value.
     * The milliseconds are cut off.
     *
     * <p><b>Note</b><br/>
     * In general, replaceItemValue is favored over appendItemValue. If an
     * item of the same name already exists in a document, appendItemValue
     * creates a second item of the same name, and the duplicate items are not
     * accessible through most methods. If you are creating a new document,
     * appendItemValue is safe.</p>
     *
     * @param name name of an item
     * @param value The value of the new item.
     * @return DItem
     */
    DBaseItem appendItemValue(String name, Calendar value);

    /**
     * Creates a new item in a document and sets the item value.
     *
     * <p><b>Note</b><br/>
     * In general, replaceItemValue is favored over appendItemValue. If an
     * item of the same name already exists in a document, appendItemValue
     * creates a second item of the same name, and the duplicate items are not
     * accessible through most methods. If you are creating a new document,
     * appendItemValue is safe.</p>
     *
     * @param name name of an item
     * @param values The value of the new item as a list of String, Integer,
     * Double, or java.util.Calendar elements
     * @return DItem
     */
    DBaseItem appendItemValue(String name, List values);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     *
     * @param name name of an item
     * @param value The value of the new item.
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, String value);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     *
     * @param name name of an item
     * @param value The value of the new item.
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, int value);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     *
     * @param name name of an item
     * @param value The value of the new item.
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, Integer value);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     *
     * @param name name of an item
     * @param value The value of the new item
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, double value);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     *
     * @param name name of an item
     * @param value The value of the new item
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, Double value);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     *
     * The milliseconds are cut off.
     *
     * @param name name of an item
     * @param value The value of the new item
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, Calendar value);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     *
     * @param name name of an item
     * @param value The value of the new item
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, TimeZone value);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     * <p>If both calendars are null, an item with an empty string is created.
     * If only one calendar is not null, this calendar is stored as a single
     * date value.</p>
     *
     * The milliseconds are cut off.
     *
     * @param name name of an item
     * @param value The value of the new item
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, DDateRange value);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name,
     * this method creates a new item and adds it to the document.</p>
     * <p>If both calendars are null, an item with an empty string is created.
     * If only one calendar is not null, this calendar is stored as a single
     * date value.</p>
     * The milliseconds are cut off.
     *
     * @param name name of an item
     * @param calendar1 start date/time of range
     * @param calendar2 end date/time of range
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, Calendar calendar1, Calendar calendar2);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned the specified value.
     * <p>If the document does not contain an item with the specified name, this
     * method creates a new item and adds it to the document.</p>
     *
     * @param name name of an item
     * @param values The new value of the item as a list of String, Integer,
     * Double, or java.util.Calendar elements
     * @return DItem
     */
    DBaseItem replaceItemValue(String name, List values);

    /**
     * Saves any changes you have made to a document.
     *
     * <p><b>Note</b>
     * If the document is a <code>DProfileDocument</code>, a call to the
     * <code>save</code> method is only successful to a newly created document
     * if a item was appended.
     * </p>
     *
     * @return <code>true</code> of successfully saved, else <code>false</code>
     * @throws DNotesRuntimeException if the document cannot be saved
     * @see #save(boolean)
     * @see #save(boolean, boolean)
     */
    boolean save() throws DNotesRuntimeException;

    /**
     * Saves any changes you have made to a document.
     *
     * <p><b>Note</b>
     * If the document is a <code>DProfileDocument</code>, a call to the
     * <code>save</code> method is only successful to a newly created document
     * if a item was appended.
     * </p>
     *
     * @param force If true, the document is saved even if someone else edits
     *              and saves the document while the program is running. The
     *              last version of the document that was saved wins; the
     *              earlier version is discarded. If false, and someone else
     *              edits the document while the program is running, the
     *              makeresponse argument determines what happens.
     * @return <code>true</code> of successfully saved, else <code>false</code>
     * @throws DNotesRuntimeException if the document cannot be saved
     * @see #save()
     * @see #save(boolean, boolean)
     */
    boolean save(boolean force) throws DNotesRuntimeException;

    /**
     * Saves any changes you have made to a document.
     *
     * <p><b>Note</b>
     * If the document is a <code>DProfileDocument</code>, a call to the
     * <code>save</code> method is only successful to a newly created document
     * if a item was appended.
     * </p>
     *
     * @param force If true, the document is saved even if someone else edits
     *              and saves the document while the program is running. The
     *              last version of the document that was saved wins; the
     *              earlier version is discarded. If false, and someone else
     *              edits the document while the program is running, the
     *              makeresponse argument determines what happens.
     * @param makeresponse If true, the current document becomes a response to
     *              the original document (this is what the replicator does
     *              when there's a replication conflict). If false, the save is
     *              canceled. If the force parameter is true, the makeresponse
     *              parameter has no effect.
     * @return <code>true</code> of successfully saved, else <code>false</code>
     * @throws DNotesRuntimeException if the document cannot be saved
     * @see #save()
     * @see #save(boolean)
     */
    boolean save(boolean force, boolean makeresponse) throws DNotesRuntimeException;

    /**
     * Returns a representation of a file attachment.
     * You can use this method to find file attachments that are not contained
     * in a rich text item (such as an attachment in a Release 2 database) as
     * well as file attachments that are contained in a rich text item.
     *
     * <p>The Parent property for the returned EmbeddedObject returns null,
     * since it was not accessed through a RichTextItem.</p>
     *
     * @param filename The file name of the file attachment.
     * @return A representation of the file attachment. Returns null if an
     * attachment with the specified name is not found.
     */
    DEmbeddedObject getAttachment(String filename);

    /**
     * Returns all embedded objects.
     *
     * This method does include OLE/2 and OLE/1 objects created in Release 4
     * and higher. It also includes objects in the document that were
     * originally embedded in the document's form. Such objects must have been
     * activated, modified, and re-saved in order to be returned by this
     * property (otherwise they remain a part of the form, not the document).
     *
     * <p>The list is empty if the document contains no embedded objects.</p>
     *
     * <p><b>Note:</b></p>
     * <p>Embedded objects and object links are not supported for OS/2, UNIX, and
     * the Macintosh. File attachments are supported on all platforms.</p>
     *
     * @return an iterator supplying all embedded objects
     */
    Iterator getEmbeddedObjects();

    /**
     * Returns all embedded objects that are attachments.
     *
     * The list is empty if the document contains no attachments.
     * @return a list of all embedded objects
     */
    List getAttachments();

    /**
     * Removes an item from a document.
     *
     * @param name The name of the item to remove from the document. If more
     * than one item has the specified name, all items with this name are
     * removed. If there is no item with the specified name, the method does
     * nothing.
     *
     * <p>You can achieve the same result with remove in Item.
     * To keep the changes to the document, you must call save after removing
     * the item.</p>
     */
    void removeItem(String name);

    /**
     * Permanently removes a document from a database.
     *
     * If another user modifies the document after your program opens it,
     * the document is not removed.
     *
     * @return <code>true</code> if the document is successfully removed or if
     * the document is not contained in a database (then a exception should be
     * logged),
     * <code>false</code> if the document is not deleted because another user
     * modified it and the force parameter is set to false.
     */
    boolean remove();

    /**
     * Permanently removes a document from a database.
     *
     * @param force If true, the document is removed even if another user
     *              modifies the document after your program opens it.
     *              If false, the document is not removed if another user
     *              modifies it.
     * @return <code>true</code> if the document is successfully removed or if
     * the document is not contained in a database (then a exception should be
     * logged),
     * <code>false</code> if the document is not deleted because another user
     * modified it and the force parameter is set to false.
     */
    boolean remove(boolean force);

    /**
     * Creates a new rich text item in a document.
     *
     * @param name The name of the new rich-text item.
     * @return The newly created item.
     */
    DRichTextItem createRichTextItem(String name);

    /**
     * Returns the value of an item.
     *
     * <p>If multiple items have the same name, this method returns the value of
     * the first item. Use the Items property to get all the items.</p>
     * <p>If the item has no value, this method returns an empty vector.</p>
     * <p>If no item with the specified name exists, this method returns an
     * empty vector. It does not throw an exception. Use hasItem to verify the
     * existence of an item.</p>
     * <p>This property returns the same value(s) for an item as getValues in
     * Item.</p>
     *
     * @param name name of an item
     * @return The value or values contained in the item. The data type of the
     * value depends on the data type of the item.
     */
    List getItemValue(String name);

    /**
     * Returns the value of an item with a single text value.
     *
     * <p>If multiple items have the same name, this method returns the value of
     * the first item. Use the Items property to get all the items.</p>
     * <p> The value of the item cannot be null, but
     * if the value is numeric or Calendar, this method returns an empty String.</p>
     * <p>If no item with the specified name exists, this method returns null.
     * It does not throw an exception. Use hasItem to verify the existence of an
     * item.</p>
     * <p>If the item has multiple values, this method returns the first
     * value.</p>
     *
     * @param name The name of the item.
     * @return The value of the item as a String.
     */
    String getItemValueString(String name);

    /**
     * Returns the value of an item with a single Calendar value.
     * The date/time value of the item is stored in the calendar in the default
     * time zone of the client.
     *
     * <p>If multiple items have the same name, this method returns the value of
     * the first item. Use the Items property to get all the items.</p>
     * <p>If value is numeric or text, this method returns <code>null</code>.</p>
     * <p>If no item with the specified name exists, this method returns
     * <code>null</code>.
     * It does not throw an exception. Use hasItem to verify the existence of an
     * item.</p>
     * <p>If the item has multiple values, this method returns the first
     * value.</p>
     *
     * @param name The name of the item.
     * @return The value of the item as a Calendar or <tt>null</tt> if the item doesn't contain a date/time value
     */
    Calendar getItemValueDate(String name);

    /**
     * Returns the value of an item with two Calendar value.
     *
     * <p>If multiple items have the same name, this method returns the value of
     * the first item. Use the Items property to get all the items.</p>
     * <p>If value is numeric or text, this method returns <code>null</code>.</p>
     * <p>If no item with the specified name exists, this method returns
     * <code>null</code>.
     * It does not throw an exception. Use hasItem to verify the existence of an
     * item.</p>
     *
     * @param name The name of the item.
     * @return The value of the item as DDateRange or <tt>null</tt> if the
     * item doesn't contain any date/time information.The second value may be
     * <code>null</code> if only one date/time was specified.
     */
    DDateRange getItemValueDateRange(String name);

    /**
     * Returns the value of an item with a single int value.
     *
     * <p>If multiple items have the same name, this method returns the value of
     * the first item. Use the Items property to get all the items.</p>
     * <p>If the value is text and is empty, this method
     * returns <code>null</code>.</p>
     * <p>If the value is text and cannot be converted to am integer, this
     * method returns <code>null</code>.</p>
     * <p>If no item with the specified name exists, this method
     * returns <code>null</code>.
     * It does not throw an exception. Use hasItem to verify the existence of
     * an item.</p>
     * <p>If the item has multiple values, this method returns the first
     * value.</p>
     * <p>A decimal number is rounded down if the fraction is less than 0.5 and
     * up if the fraction is 0.5 or greater.</p>
     *
     * @param name The name of the item.
     * @return The value of the item, rounded to the nearest integer
     *         or <code>null</code>
     */
    Integer getItemValueInteger(String name);

    /**
     * Returns the value of an item with a single double value.
     *
     * <p>If multiple items have the same name, this method returns the value of
     * the first item. Use the Items property to get all the items.</p>
     * <p>If the value is text and is empty, this method
     * returns <code>null</code>.</p>
     * <p>If the value is text and cannot be converted to a double, this
     * method returns <code>null</code>.</p>
     * <p>If no item with the specified name exists, this method returns <code>null</code>.
     * It does not throw an exception. Use hasItem to verify the existence of
     * an item.</p>
     * <p>If the item has multiple values, this method returns the first
     * value.</p>
     *
     * @param name The name of the item.
     * @return The value of the item as a Double
     *         or <code>null</code>
     */
    Double getItemValueDouble(String name);

    /**
     * Indicates whether an item exists in the document.
     *
     * @param name The name of an item.
     * @return <code>true</code> if an item with name exists in the document,
     * else <code>false</code>
     */
    boolean hasItem(String name);

    /**
     * The names of the people who have saved a document.
     *
     * <p>If a name is hierarchical, this property returns the fully
     * distinguished name.</p>
     * <p>This property does not return the names of people who have permission
     * to edit a document (as found in an item of type Authors).
     * Therefore, the people returned by the Authors property and the people
     * listed in an Authors item may differ.</p>
     *
     * @return List of Strings with authors names. In case of a normal document
     *         this method call returns a empty list for a not-saved-document.
     *         In case of a profile document, this method call returns a list
     *         with one entry, even if the profile document is requested for
     *         the first time.
     */
    List getAuthors();

    /**
     * The date a document was last modified or read.
     *
     * <p>The value returned is exact only to the day, not the hour.
     * If the document is edited, the property is always updated.
     * If the document is read more than once during the same 24hour period,
     * the value is only updated the first time accessed.</p>
     *
     * @return last accessed date
     */
    Calendar getLastAccessed();

    /**
     * The date a document was last modified.
     *
     * @return last modified date. In case of a normal document
     *         this method call returns a empty list for a not-saved-document.
     *         In case of a profile document, this method call returns a list
     *         with one entry, even if the profile document is requested for
     *         the first time.
     */
    Calendar getLastModified();

    /**
     * Returns the number of values of an item.
     *
     * <p>If multiple items have the same name, this method returns the number of
     * values in the first item. Use the Items property to get all the items.</p>
     * <p>If the item has no value, this method returns <code>0</code>.</p>
     * <p>If no item with the specified name exists, this method returns
     * <code>0</code>. It does not throw an exception. Use hasItem to verify the
     * existence of an item.</p>
     * <p>This property returns the same value for an item as getSize() in
     * DItem.</p>
     *
     * @param name name of an item
     * @return The number of values contained in the item.
     */
    int getItemValueSize(String name);

    /**
     * Copies all items in the current document into the destination document.
     * The item names are unchanged.
     *
     * <p>If you are not copying to a newly created document, you should
     * probably specify true for the second parameter. See appendItemValue for
     * a note about appending items to existing documents.</p>
     *
     * @param doc The destination document
     * @param replace If <code>true</code>, the items in the destination
     *        document are replaced. If <code>false</code>, the items in the
     *        destination document are appended.
     */
    void copyAllItems(DBaseDocument doc, boolean replace);

    /**
     * Validates a document by executing the default value, translation, and
     * validation formulas, if any are defined in the document form.
     *
     * <p><b>Usage</b><p>
     * The form is as follows:
     * <ol>
     * <li>The form stored in the document, if any.</li>
     * <li>The value of the Form item, if no form is stored in the document.</li>
     * <li>The database default form, if the document does not have a Form
     * item.</li>
     * </ol>
     * In the user interface, you must use a form to create a document. The
     * document must meet the form requirements for input validation, and the
     * user interface informs you if the document does not meet these
     * requirements. Programatically you can create a document without a form.
     * The computeWithForm method provides a means of checking that the data you
     * placed in a document meets the requirements of a form, although (unlike
     * in the user interface) you can still save a document if computeWithForm
     * returns false or throws an exception.</p>
     *
     * @param raiseError If true, an error is raised if the validation fails. If
     *            false, no error is raised; instead, the method returns false
     *            if validation fails.
     * @return <code>true</code> if there are no errors in the document, else
     *         <code>false</code>
     * @throws DNotesRuntimeException if the parameter raiseError is
     *             <code>true</code> and if there are errors in the document
     */
    boolean computeWithForm(boolean raiseError) throws DNotesRuntimeException;

    /**
     * Validates a document by executing the default value, translation, and
     * validation formulas, if any are defined in the document form.
     *
     * <p><b>Usage</b><p>
     * The form is as follows:
     * <ol>
     * <li>The form stored in the document, if any.</li>
     * <li>The value of the Form item, if no form is stored in the document.</li>
     * <li>The database default form, if the document does not have a Form
     * item.</li>
     * </ol>
     * In the user interface, you must use a form to create a document. The
     * document must meet the form requirements for input validation, and the
     * user interface informs you if the document does not meet these
     * requirements. Programatically you can create a document without a form.
     * The computeWithForm method provides a means of checking that the data you
     * placed in a document meets the requirements of a form, although (unlike
     * in the user interface) you can still save a document if computeWithForm
     * returns false or throws an exception.</p>
     *
     * @return <code>true</code> if there are no errors in the document, else
     *         <code>false</code>
     *
     * @see #computeWithForm(boolean)
     */
    boolean computeWithForm();

    /**
     * The recycle method unconditionally destroys an object and returns its
     * memory to the system.
     *
     * <p>Different to the Notes Java-API, usually it is not necessary to use
     * this method. One remaining case might be a document that was changed in
     * another session (e.g. by a server agent) while still keeping a reference
     * to the document.</p>
     */
    void recycle();

    /**
     * Copies an item into the current document and optionally assigns the
     * copied item a new name.
     *
     * @param item The item, usually from another document, that you want to
     *            copy. Cannot be null.
     * @param name The name to assign to the copied item. Specify null to retain
     *            the existing name of the item.
     * @return A copy of the specified item parameter, identical except for its
     *         newname.
     */
    DBaseItem copyItem(DBaseItem item, String name);

    /**
     * Copies an item into the current document and optionally assigns the
     * copied item a new name.
     *
     * @param item The item, usually from another document, that you want to
     *            copy. Cannot be null.
     * @return A copy of the specified item parameter
     */
    DBaseItem copyItem(DBaseItem item);

    /**
     * Creates a new document that is formatted as a reply to the current
     * document.
     *
     * <p>The new document does not contain a Subject item. If you want one,
     * the program must explicitly add it to the document.</p>
     *
     * <p>The new document does not automatically get mailed. If you want to
     * mail it, the program must explicitly call the send method.</p>
     *
     * @param toAll If <code>true</code>, the new document recipient list
     *            contains all the recipients of the original. If
     *            <code>false</code>, the new document recipient list
     *            contains only the sender of the original.
     * @return A reply to the current document.
     */
    DDocument createReplyMessage(boolean toAll);

    /**
     * Encrypts a document.
     *
     * <p>The encrypted document is not saved until you call save. Only the
     * items for which isEncrypted is true are encrypted. Items for which
     * isEncrypted is false remain visible to any user, even if the user does
     * not have the proper encryption key.</p>
     *
     * <p>If the EncryptionKeys property is set with one or more named keys,
     * those keys are used to encrypt the document. Any user who has one of the
     * encryption keys can decrypt the document. If there are no encryption keys
     * specified, the document is encrypted with the user's public key, in which
     * case only the user who encrypted the document can decrypt it.</p>
     *
     * <p>If the program is running on a server, it must have permission to use
     * Encrypt.</p>
     *
     * <p>Since mail encryption works differently, do not use this method if
     * you want to mail an encrypted document. Instead, set the EncryptOnSend
     * property to true, and use the send method.</p>
     *
     * <p>You cannot use the encrypt method on a Document object returned by
     * getDocumentContext.</p>
     */
    void encrypt();

    /**
     * A list of values, each element of which corresponds to a column value in
     * the document's parent view. The first value in the vector is the value
     * that appears in the view's first column for the document, the second
     * value is the one that appears in the second column, and so on. The value
     * of each element of the vector is the result of the corresponding column's
     * formula and the items on the current document. Some elements in the
     * vector might have no value.
     *
     * @return column values
     */
    List getColumnValues();

    /**
     * Returns the key(s) used to encrypt a document. The encrypt method uses
     * these keys when it encrypts the document.
     *
     * <p>Each element in EncryptionKeys contains the name of an encryption key
     * that you want to use to encrypt the document. The document can be
     * decrypted by any user who posesses one of the keys. If there are no
     * encryption keys specified for a document, the document is encrypted with
     * the current user's public key and can only be decrypted by that user.</p>
     *
     * <p>You must call the encrypt and save methods to actually encrypt the
     * document. Since encryption works differently when a document is mailed,
     * the EncryptionKeys property has no effect when a document is encrypted
     * when mailed.</p>
     *
     * <p>The name of each encryption key in a document is stored in a text
     * item called SecretEncryptionKeys. This property returns the contents of
     * the item.</p>
     *
     * @return the key(s) used to encrypt a document
     */
    List getEncryptionKeys();

    /**
     * Sets the key(s) used to encrypt a document. The encrypt method uses these
     * keys when it encrypts the document.
     *
     * @param keys the key(s) used to encrypt a document
     * @see #getEncryptionKeys()
     */
    void setEncryptionKeys(List keys);

    /**
     * Returns as an object the value of an item containing custom data.
     *
     * @param name The name of the item.
     * @return An object that receives the value of the item. Must have the same
     *         class definition as the object written to the item.
     * @since Notes/Domino Release 6
     */
    Object getItemValueCustomData(String name);

    /**
     * Returns as an object the value of an item containing custom data.
     *
     * @param name The name of the item.
     * @param dataTypeName The name of the data type. If specified, this name
     *            must match the data type name specified when the item was
     *            written. If omitted, no name checking occurs
     * @return An object that receives the value of the item. Must have the same
     *         class definition as the object written to the item.
     * @since Notes/Domino Release 6
     */
    Object getItemValueCustomData(String name, String dataTypeName);

    /**
     * Returns as a byte array the value of an item containing custom data.
     *
     * @param name The name of the item.
     * @param dataTypeName The name of the data type. If specified, this name
     *            must match the data type name specified when the item was
     *            written. If omitted, no name checking occurs
     * @return Array of type Byte. The value of the item.
     * @since Notes/Domino Release 6
     */
    byte[] getItemValueCustomDataBytes(String name, String dataTypeName);

    /**
     * Returns the value of a date-time item in a document.
     *
     * @param name The name of the item.
     * @return The value or values contained in the item. Each element in the
     *         vector corresponds to a value in the item and is of type DateTime
     *         or DateRange. If the item contains a single value, the vector has
     *         one element.
     * @since Notes/Domino Release 6.5
     */
    List getItemValueDateTimeArray(String name);

    /**
     * The name of the person who created the signature, if a document is
     * signed.
     *
     * <p>If a document is not signed, returns an empty string.</p>
     *
     * <p>If the signer is not trusted, returns an empty string.</p>
     *
     * @return the signer
     */
    String getSigner();

    /**
     * The size of a database, in bytes.
     *
     * @return size of a database, in bytes
     */
    int getSize();

    /**
     * The universal ID, which uniquely identifies a document across all
     * replicas of a database. In character format, the universal ID is a
     * 32-character combination of hexadecimal digits (0-9, A-F).
     *
     * <p>If two documents in replica databases share the same universal ID,
     * the documents are replicas.</p>
     *
     * <p>If you modify the UNID of an existing document, it becomes a new
     * document.</p>
     *
     * <p>Saving a document with the same UNID as an existing document an
     * exception.</p>
     *
     * @param unid The universal ID is also known as the unique ID or UNID.
     */
    void setUniversalID(String unid);

    /**
     * The name of the certificate that verified a signature, if a document is
     * signed.
     *
     * <p>This property is an empty string if the document is not signed.</p>
     *
     * <p>This property is an empty string if the signer is not trusted.</p>
     *
     * @return name of the certificate that verified a signature
     */
    String getVerifier();

    /**
     * Indicates whether a document contains one or more embedded objects,
     * object links, or file attachments.
     *
     * <p>Note Embedded objects and object links are not supported for OS/2,
     * UNIX, and the Macintosh. File attachments are.</p>
     *
     * @return <code>true</code> if the document contains one or more embedded
     *         objects, object links, or file attachments , else
     *         <code>false</code>
     */
    boolean hasEmbedded();

    /**
     * Indicates whether a document is encrypted.
     *
     * @return <code>true</code> if the document is encrypted, else
     *         <code>false</code>
     */
    boolean isEncrypted();

    /**
     * Indicates whether a document is encrypted when mailed.
     *
     * @return <code>true</code> if the document is encrypted when mailed,
     *         else <code>false</code>
     */
    boolean isEncryptOnSend();

    /**
     * Indicates whether a Document object is a profile document.
     *
     * @return <code>true</code> if the document is a profile document, else
     *         <code>false</code>
     */
    boolean isProfile();

    /**
     * Indicates whether a document contains a signature.
     *
     * @return <code>true</code> if the document contains one or more
     *         signatures, else <code>false</code>
     */
    boolean isSigned();

    /**
     * Indicates whether a Document object represents an existing document (not
     * a deletion stub) initially.
     *
     * @return <code>true</code> if the document represents an existing
     *         document, else <code>false</code>
     */
    boolean isValid();

    /**
     * Indicates whether a document is saved to a database when mailed. Applies
     * only to new documents that have not yet been saved.
     *
     * @return <code>true</code> if the document is saved when mailed, else
     *         <code>false</code>
     */
    boolean isSaveMessageOnSend();

    /**
     * Indicates whether a document was mailed by a Domino program.
     *
     * @return <code>true</code> if the document was mailed by a program, else
     *         <code>false</code>
     */
    boolean isSentByAgent();

    /**
     * Indicates whether a document is signed when mailed.
     *
     * @return <code>true</code> if the document is signed when mailed, else
     *         <code>false</code>
     */
    boolean isSignOnSend();

    /**
     * Indicates whether a Document object represents an existing document (not
     * a deletion stub) on an ongoing basis.
     *
     * @return <code>true</code> if the document is a deletion stub
     *         <code>false</code> if the document exists
     */
    boolean isDeleted();

    /**
     * Permanently deletes a document from a database, doing a hard deletion
     * even if soft deletions are enabled.
     *
     * <p>This method does a hard deletion even if "Allow soft deletions" is
     * enabled. See remove to do a soft deletion.</p>
     *
     * <p>If you access a NotesDocument object through the Document property of
     * NotesUIDocument, you can't delete the back-end document. If you could,
     * the UI rendition would have no basis. You must access the document
     * strictly through the back-end.</p>
     *
     * <p>A deleted document cannot be used as a basis for navigation in a view
     * or a document collection.</p>
     *
     * <p>You cannot use the remove method on a Document object returned by
     * {@link DAgentContext#getDocumentContext()}.</p>
     *
     * @param force If <code>true</code>, the document is deleted even if
     *            another user modifies the document after the program opens it.
     *            If <code>false</code>, the document is not deleted if
     *            another user modifies it.
     * @return <code>true</code> if the document is successfully deleted,
     *         <code>false</code> if the document is not deleted, because
     *         another user modified it and the force parameter is set to false
     */
    boolean removePermanently(boolean force);

    /**
     * Creates a picture of a document and places it into a rich-text item that
     * you specify.
     *
     * <p>The picture is created using both the document and its form.
     * Therefore, the input translation and validation formulas of the form are
     * executed.</p>
     *
     * <p>If the target rich text item is in a new document, you must save the
     * document before calling renderToRTItem.</p>
     *
     * @param richtextitem The destination for the picture. Cannot be null.
     * @return If <code>true</code>, the method is successful. If
     *         <code>false</code>, the method is not successful and the rich
     *         text item remains unchanged. This can happen if an input
     *         validation formula fails on the document form.
     */
    boolean renderToRTItem(DRichTextItem richtextitem);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned custom data from a byte array. If the document does not contain
     * an item with the specified name, this method creates a new item and adds
     * it to the document.
     *
     * @param name the name of the item
     * @param type name for the data type. When getting custom data, use this
     *            name for verification.
     * @param obj object that contains the custom data. The class that defines
     *            this object must implement Serializable. If desired, you can
     *            override readObject and writeObject.
     * @return DItem
     */
    DBaseItem replaceItemValueCustomData(String name, String type, Object obj);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned custom data from a byte array. If the document does not contain
     * an item with the specified name, this method creates a new item and adds
     * it to the document.
     *
     * @param name the name of the item
     * @param obj object that contains the custom data. The class that defines
     *            this object must implement Serializable. If desired, you can
     *            override readObject and writeObject.
     * @return DItem
     */
    DBaseItem replaceItemValueCustomData(String name, Object obj);

    /**
     * Replaces all items of the specified name with one new item, which is
     * assigned custom data from a byte array. If the document does not contain
     * an item with the specified name, this method creates a new item and adds
     * it to the document.
     *
     * @param name the name of the item
     * @param type name for the data type. When getting custom data, use this
     *            name for verification.
     * @param bytes byte array that contains the custom data
     * @return DItem
     */
    DBaseItem replaceItemValueCustomDataBytes(String name, String type, byte[] bytes);

    /**
     * Generates an XML representation of a document to the Writer. The XML
     * conforms to the Domino Document DTD.
     *
     * <p>This method takes the same arguments as the transformXML method in
     * EmbeddedObject, Item, MIMEEntity, and RichTextItem. The transformXML
     * method reads the XML from an item, attachment, MIMEEntity, or rich text
     * item and transforms the XML. The generateXML method generates the XML
     * from the document and then transforms it.</p>
     *
     * <p>The generateXML method supports the following simple items: Text,
     * Text list, Number, Number list, Datetime, Datetime list</p>
     *
     * <p>To generate form semantics, you must call computeWithForm method
     * before calling the generateXML method.</p>
     *
     * @return The XML representation of the document.
     */
    String generateXML();

    /**
     * Generates an XML representation of a document to the Writer. The XML
     * conforms to the Domino Document DTD.
     *
     * <p>This method takes the same arguments as the transformXML method in
     * EmbeddedObject, Item, MIMEEntity, and RichTextItem. The transformXML
     * method reads the XML from an item, attachment, MIMEEntity, or rich text
     * item and transforms the XML. The generateXML method generates the XML
     * from the document and then transforms it.</p>
     *
     * <p>The generateXML method supports the following simple items: Text,
     * Text list, Number, Number list, Datetime, Datetime list</p>
     *
     * <p>To generate form semantics, you must call computeWithForm method
     * before calling the generateXML method.</p>
     *
     * @param writer The Writer that will receive the result XML.
     */
    void generateXML(Writer writer);

    /**
     * Gets the text values of the Received items in a mail document.
     *
     * <p>This method applies to Received items generated from an Internet mail
     * message. The items can be in MIME or Notes format.</p>
     *
     * <p>If the document has no Received items, this method returns a vector
     * of one element whose value is an empty string.</p>
     *
     * <p>A Received item with an incorrect format (not an Internet mail
     * message) throws an exception.</p>
     *
     * @return List of strings. The text values of the Received items, one item
     *         per element.
     * @since Notes/Domino Release 6.5
     */
    List getReceivedItemText();

    /**
     * The names of the holders of a lock.
     *
     * <p>If the document is locked, the vector contains the names of the lock
     * holders. The document can be locked by one or more users or groups.</p>
     *
     * <p>If the document is not locked, the vector contains one element whose
     * value is an empty string ("").</p>
     *
     * @return names of the holders of a lock.
     * @since Notes/Domino Release 6.5
     */
    List getLockHolders();

    /**
     * Locks a document.
     *
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    boolean lock();

    /**
     * Locks a document.
     *
     * @param provisionalOk <code>true</code> to permit the placement of a
     *            provisional lock, <code>false</code> (default) to not permit
     *            a provisional lock
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    boolean lock(boolean provisionalOk);

    /**
     * Locks a document.
     *
     * @param name the name of the lock holder
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    boolean lock(String name);

    /**
     * Locks a document.
     *
     * @param name the name of the lock holder
     * @param provisionalOk <code>true</code> to permit the placement of a
     *            provisional lock, <code>false</code> (default) to not permit
     *            a provisional lock
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    boolean lock(String name, boolean provisionalOk);

    /**
     * Locks a document.
     *
     * @param names the names of the lock holders
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    boolean lock(List names);

    /**
     * Locks a document.
     *
     * <p> IsDocumentLockingEnabled in Database must be true or this method
     * throws an exception.</p>
     *
     * <p>This method:</p>
     *
     * <ul><li>places a persistent lock if the administration (master lock)
     * server is available.</li> <li>places a provisional lock if the
     * administration server is not available and the second parameter is true.</li>
     * <li>throws an exception if the administration server is not available
     * and the second parameter is false.</li> </ul>
     *
     * <p>The following actions occur depending on the current lock status:</p>
     *
     * <ul> <li>If the document is not locked, this method places the lock and
     * returns true.</li> <li>If the document is locked and the current user
     * is one of the lock holders, this method returns true.</li> <li>If the
     * document is locked and the current user is not one of the lock holders,
     * this method returns false.</li> </ul> <p>If the document is modified by
     * another user before the lock can be placed, this method throws an
     * exception.</p>
     *
     * @param names The names of the lock holders. Each lock holder must be a
     *            user or group. Defaults to one lock holder: the effective
     *            user. The empty string ("") is not permitted.
     * @param provisionalOk <code>true</code> to permit the placement of a
     *            provisional lock, <code>false</code> (default) to not permit
     *            a provisional lock
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @since Notes/Domino Release 6.5
     */
    boolean lock(List names, boolean provisionalOk);

    /**
     * Locks a document provisionally.
     *
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    boolean lockProvisional();

    /**
     * Locks a document provisionally.
     *
     * @param name The name of the lock holders. The lock holder must be a user
     *            or group. Defaults to the effective user. The empty string
     *            ("") is not permitted.
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    boolean lockProvisional(String name);

    /**
     * Locks a document provisionally.
     *
     * @param names The names of the lock holders. Each lock holder must be a
     *            user or group. Defaults to one lock holder: the effective
     *            user. The empty string ("") is not permitted.
     * @return <code>true</code> if the lock is placed, <code>false</code>
     *         if the lock is not placed
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    boolean lockProvisional(List names);

    /**
     * Unlocks a document.
     *
     * <p>{@link DDatabase#isDocumentLockingEnabled()} in Database must be true or this
     * method throws an exception. This method throws an exception if the
     * current user is not one of the lock holders and does not have lock
     * breaking authority.</p>
     * @see #lock(List, boolean)
     * @since Notes/Domino Release 6.5
     */
    void unlock();
}
