/* Copyright 2004, 2005 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.acegisecurity.domain.dao;

import net.sf.acegisecurity.domain.PersistableEntity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


/**
 * <p>
 * Represents a paginated <code>List</code>.
 * </p>
 * 
 * <p>
 * Elements in the internal <code>List</code> (see {@link #getList()} represent
 * only part of a larger resultset.
 * </p>
 * 
 * <p>
 * Note that firstElement starts at zero. Any attempt to access other than the
 * current page will cause an error.
 * </p>
 * 
 * <p>
 * This is a read only implementation and many of the  <code>List</code>
 * methods are not implemented.
 * </p>
 *
 * @author Carlos Sanchez
 * @author Ben Alex
 * @version $Id$
 */
public class PaginatedList implements List {
    //~ Instance fields ========================================================

    protected final transient Log logger = LogFactory.getLog(getClass());
    private List list;
    private int firstElement;
    private int maxElements;
    private int size;

    //~ Constructors ===========================================================

    // TODO: Consider removing this constructor
    public PaginatedList() {}

    /**
     * Used to construct a <code>PaginatedList</code> which contains only the
     * given entity.
     *
     * @param entity the entity to include (can be <code>null</code>, which
     *        indicates an empty <code>PaginatedList</code> should be created)
     */
    public PaginatedList(PersistableEntity entity) {
        if (entity == null) {
            this.list = new Vector();
            this.firstElement = 0;
            this.maxElements = Integer.MAX_VALUE;
            this.size = 0;
        } else {
            List list = new Vector();
            list.add(entity);
            this.list = list;
            this.firstElement = 0;
            this.maxElements = Integer.MAX_VALUE;
            this.size = 1;
        }
    }

    public PaginatedList(List list, int firstElement, int maxElements, int size) {
        this.list = list;
        this.firstElement = firstElement;
        this.maxElements = maxElements;
        this.size = size;
    }

    //~ Methods ================================================================

    /**
     * Unsupported operation
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    public void setFirstElement(int firstElement) {
        this.firstElement = firstElement;
    }

    /**
     * First element of this page, starting at zero.
     *
     * @return
     */
    public int getFirstElement() {
        return firstElement;
    }

    /**
     * Calculate the last page number, starting at 0
     *
     * @return
     */
    public int getLastPageNumber() {
        return (size() - 1) / getMaxElements();
    }

    public void setList(List list) {
        this.list = list;
    }

    /**
     * Get list with the elements of this page.
     *
     * @return this page of the results
     */
    public List getList() {
        return list;
    }

    public void setMaxElements(int maxElements) {
        this.maxElements = maxElements;
    }

    /**
     * Max number of elements in the page
     *
     * @return
     */
    public int getMaxElements() {
        return maxElements;
    }

    /**
     * Calculate the page number, starting at 0
     *
     * @return
     */
    public int getPageNumber() {
        return getFirstElement() / getMaxElements();
    }

    /**
     * Number of elements in this page
     *
     * @return
     */
    public int getPageSize() {
        return list.size();
    }

    /**
     * Set the number of elements in all the pages
     *
     * @param size DOCUMENT ME!
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     * @param arg1 DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int arg0, Object arg1) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     * @param arg1 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int arg0, Collection arg1) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#clear()
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @see java.util.List#get(int)
     */
    public Object get(int arg0) {
        return list.get(arg0);
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object arg0) {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator() {
        return new PaginatedListIterator(this);
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#listIterator()
     */
    public ListIterator listIterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#listIterator(int)
     */
    public ListIterator listIterator(int arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#remove(int)
     */
    public Object remove(int arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     * @param arg1 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#set(int, java.lang.Object)
     */
    public Object set(int arg0, Object arg1) {
        throw new UnsupportedOperationException();
    }

    /**
     * Number of elements in all the pages
     *
     * @see java.util.Collection#size()
     */
    public int size() {
        return size;
    }

    /**
     * Unsupported operation
     *
     * @param arg0 DOCUMENT ME!
     * @param arg1 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UnsupportedOperationException
     *
     * @see java.util.List#subList(int, int)
     */
    public List subList(int arg0, int arg1) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public Object[] toArray(Object[] arg0) {
        if (logger.isDebugEnabled()) {
            logger.debug("List size when convert to array "
                + list.toArray().length);
        }

        return list.toArray(arg0);
    }
}
