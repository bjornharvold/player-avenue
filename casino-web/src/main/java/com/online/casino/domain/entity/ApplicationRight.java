/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.SystemRightType;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 11/03/13
 * @author         Bjorn Harvold
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class ApplicationRight extends AbstractEntity implements Serializable {

    /** Field description */
    private static final long serialVersionUID = -3758830804718785549L;

    //~--- fields -------------------------------------------------------------


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SystemRightType statusCode;

    //~--- methods ------------------------------------------------------------

    public ApplicationRight() {
    }

    public ApplicationRight(String id) {
        this.id = id;
    }

    public ApplicationRight(SystemRightType statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public static final EntityManager entityManager() {
        EntityManager em = new ApplicationRight().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     *
     * @param id id
     *
     * @return Return value
     */
    public static ApplicationRight findSystemRight(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of ApplicationRight");
        }

        return entityManager().find(ApplicationRight.class, id);
    }

    /**
     * Method description
     *
     *
     * @param statusCode statusCode
     *
     * @return Return value
     */
    public static ApplicationRight findSystemRightByStatusCode(SystemRightType statusCode) {
        ApplicationRight result = null;

        if (statusCode == null) {
            throw new IllegalArgumentException("statusCode cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select al from ApplicationRight as al where al.statusCode = :statusCode");

            q.setParameter("statusCode", statusCode);
            result = (ApplicationRight) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param index index
     * @param maxResults maxResults
     *
     * @return Return value
     */
    public static List<ApplicationRight> findSystemRightsByName(Integer index, Integer maxResults) {
        List<ApplicationRight> result = null;

        try {
            CriteriaBuilder           builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<ApplicationRight> criteria = builder.createQuery(ApplicationRight.class);
            Root<ApplicationRight>          root     = criteria.from(ApplicationRight.class);

            criteria.select(root);

            criteria.orderBy(builder.asc(root.get(ApplicationRight_.statusCode)));

            TypedQuery<ApplicationRight> query = entityManager().createQuery(criteria);

            if ((index != null) && (maxResults != null)) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }

            result = query.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public static Long findSystemRightsByNameCount() {
        Long result = 0L;

        try {
            CriteriaBuilder     builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<ApplicationRight>    root     = criteria.from(ApplicationRight.class);

            criteria.select(builder.count(root));

            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param maxResults maxResults
     *
     * @return Return value
     */
    public static List<ApplicationRight> findLastModifiedSystemRights(Integer maxResults) {
        List<ApplicationRight> result = null;

        try {
            Query q = entityManager().createQuery("select al from ApplicationRight as al order by al.lastUpdate desc");

            if (maxResults != null) {
                q.setMaxResults(maxResults);
            }

            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     */
    @Transactional
    public void flush() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        this.entityManager.flush();
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public SystemRightType getStatusCode() {
        return statusCode;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        ApplicationRight merged = this.entityManager.merge(this);

        this.entityManager.flush();
        this.id = merged.getId();
    }

    /**
     * Method description
     *
     */
    @Transactional
    public void persist() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        this.entityManager.persist(this);
    }

    /**
     * Method description
     *
     */
    @Transactional
    public void remove() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            ApplicationRight attached = this.entityManager.find(ApplicationRight.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new ApplicationRight(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param statusCode statusCode
     */
    public void setStatusCode(SystemRightType statusCode) {
        this.statusCode = statusCode;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("ApplicationRight {");
        sb.append(super.toString());
        sb.append(", statusCode='").append(statusCode).append('\'');
        sb.append('}');

        return sb.toString();
    }
}
