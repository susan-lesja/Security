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

package net.sf.acegisecurity.domain.hibernate;

import net.sf.acegisecurity.domain.PersistableEntity;
import net.sf.acegisecurity.domain.dao.Dao;
import net.sf.acegisecurity.domain.dao.EvictionCapable;
import net.sf.acegisecurity.domain.dao.PaginatedList;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.MatchMode;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.type.Type;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import org.springframework.util.Assert;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;


/**
 * {@link Dao} implementation that uses Hibernate for persistence.
 *
 * @author Ben Alex
 * @version $Id$
 */
public class DaoHibernate extends HibernateDaoSupport implements Dao,
    EvictionCapable {
    //~ Instance fields ========================================================

    /** The class that this instance provides services for */
    private Class supportsClass;

    //~ Methods ================================================================

    public void setSupportsClass(Class supportClass) {
        this.supportsClass = supportClass;
    }

    public Class getSupportsClass() {
        return supportsClass;
    }

    public PersistableEntity create(PersistableEntity value) {
        Assert.notNull(value);
        getHibernateTemplate().save(value);

        return readId(value.getInternalId());
    }

    public PersistableEntity createOrUpdate(PersistableEntity value) {
        Assert.notNull(value);

        if (value.getInternalId() == null) {
            return create(value);
        } else {
            return update(value);
        }
    }

    public void delete(PersistableEntity value) {
        Assert.notNull(value);
        getHibernateTemplate().delete(value);
    }

    public void evict(PersistableEntity entity) {
        Assert.notNull(entity);
        getHibernateTemplate().evict(entity);
    }

    public List findAll() {
        return getHibernateTemplate().loadAll(supportsClass);
    }

    public List findId(Collection ids) {
        Assert.notNull(ids, "Collection of IDs cannot be null");
        Assert.notEmpty(ids, "There must be some values in the Collection list");

        return (List) getHibernateTemplate().execute(getFindByIdCallback(ids));
    }

    public PersistableEntity readId(Serializable id) {
        Assert.notNull(id);

        try {
            return (PersistableEntity) getHibernateTemplate().load(supportsClass,
                id);
        } catch (HibernateObjectRetrievalFailureException notFound) {
            return null;
        }
    }

    public PaginatedList scroll(PersistableEntity value, int firstElement,
        int maxElements, String orderByAsc) {
        Assert.notNull(value);
        Assert.hasText(orderByAsc,
            "An orderByAsc is required (why not use your identity property?)");

        return (PaginatedList) getHibernateTemplate().execute(getFindByValueCallback(
                value, firstElement, maxElements, Order.asc(orderByAsc)));
    }

    public boolean supports(Class clazz) {
        Assert.notNull(clazz);

        return this.supportsClass.equals(clazz);
    }

    public PersistableEntity update(PersistableEntity value) {
        Assert.notNull(value);
        getHibernateTemplate().update(value);

        return readId(value.getInternalId());
    }

    /**
     * Custom initialization behavior. Called by superclass.
     *
     * @throws Exception if initialization fails
     */
    protected final void initDao() throws Exception {
        Assert.notNull(supportsClass, "supportClass is required");
        Assert.isTrue(PersistableEntity.class.isAssignableFrom(supportsClass),
            "supportClass is not an implementation of PersistableEntity");
        initHibernateDao();
    }

    /**
     * Allows subclasses to provide custom initialization behaviour. Called
     * during {@link #initDao()}.
     *
     * @throws Exception
     */
    protected void initHibernateDao() throws Exception {}

    /**
     * Provides a <code>HibernateCallback</code> that will load a list of
     * objects by a <code>Collection</code> of identities.
     *
     * @param ids collection of identities to be loaded
     *
     * @return a <code>List</code> containing the matching objects
     */
    private HibernateCallback getFindByIdCallback(final Collection ids) {
        return new HibernateCallback() {
                public Object doInHibernate(Session session)
                    throws HibernateException {
                    Criteria criteria = session.createCriteria(supportsClass);

                    ClassMetadata classMetadata = getSessionFactory()
                                                      .getClassMetadata(supportsClass);

                    String idPropertyName = classMetadata
                        .getIdentifierPropertyName();
                    criteria.add(Expression.in(idPropertyName, ids));

                    return criteria.list();
                }
            };
    }

    /**
     * Get a new <code>HibernateCallback</code> for finding objects by a bean
     * property values, paginating the results. Properties with null values
     * and collections and empty Strings are ignored, as is any property with
     * the "version" name. If the property is mapped as String find a partial
     * match, otherwise find by exact match.
     *
     * @param bean bean with the values of the parameters
     * @param firstElement the first result, numbered from 0
     * @param count the maximum number of results
     * @param order DOCUMENT ME!
     *
     * @return a PaginatedList containing the requested objects
     */
    private HibernateCallback getFindByValueCallback(final Object bean,
        final int firstElement, final int count, final Order order) {
        return new HibernateCallback() {
                public Object doInHibernate(Session session)
                    throws HibernateException {
                    Criteria criteria = session.createCriteria(bean.getClass());

                    criteria.addOrder(order);

                    ClassMetadata classMetadata = getSessionFactory()
                                                      .getClassMetadata(bean
                            .getClass());

                    /* get persistent properties */
                    Type[] propertyTypes = classMetadata.getPropertyTypes();
                    String[] propertyNames = classMetadata.getPropertyNames();

                    /* for each persistent property of the bean */
                    for (int i = 0; i < propertyNames.length; i++) {
                        String name = propertyNames[i];
                        Object value = classMetadata.getPropertyValue(bean, name);

                        if (value == null) {
                            continue;
                        }

                        // ignore empty Strings
                        if (value instanceof String) {
                            String string = (String) value;

                            if ("".equals(string)) {
                                continue;
                            }
                        }

                        // ignore any collections
                        if (propertyTypes[i].isPersistentCollectionType()) {
                            continue;
                        }

                        Type type = classMetadata.getPropertyType(name);

                        if (name.equals("version")) {
                            continue;
                        }

                        if (type.equals(Hibernate.STRING)) {
                            // if the property is mapped as String, find partial match
                            criteria.add(Expression.ilike(name,
                                    value.toString(), MatchMode.ANYWHERE));
                        } else {
                            // find exact match
                            criteria.add(Expression.eq(name, value));
                        }
                    }

                    /*
                     * TODO Use Criteria.count() when available in next Hibernate
                     * versions
                     */
                    int size = criteria.list().size();

                    List list = criteria.setFirstResult(firstElement)
                                        .setMaxResults(count).list();

                    return new PaginatedList(list, firstElement, count, size);
                }
            };
    }
}
