package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class ApplicationRole extends AbstractEntity implements Serializable {

    /** Field description */
    private static final long serialVersionUID = -3758830804718785549L;

    //~--- fields -------------------------------------------------------------

    /** Field description */
    @ManyToMany
    private Set<ApplicationRight> rights = new HashSet<ApplicationRight>();

    /** Field description */
    private String description;

    /** Field description */
    @NotNull
    @Column(nullable = false)
    private String name;

    public ApplicationRole() {
    }

    private ApplicationRole(String id) {
        this.id = id;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public static final EntityManager entityManager() {
        EntityManager em = new ApplicationRole().entityManager;

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
    public static ApplicationRole findSystemRole(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of ApplicationRole");
        }

        return entityManager().find(ApplicationRole.class, id);
    }

    /**
     * Method description
     *
     *
     * @param name statusCode
     *
     * @return Return value
     */
    public static ApplicationRole findSystemRoleByName(String name) {
        ApplicationRole result = null;

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select al from ApplicationRole as al where al.name = :name");

            q.setParameter("name", name);
            result = (ApplicationRole) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param name name
     * @param index index
     * @param maxResults maxResults
     *
     * @return Return value
     */
    public static List<ApplicationRole> findSystemRolesByName(String name, Integer index, Integer maxResults) {
        List<ApplicationRole> result = null;

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name cannot be null");
        }

        try {
            CriteriaBuilder           builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<ApplicationRole> criteria = builder.createQuery(ApplicationRole.class);
            Root<ApplicationRole>          root     = criteria.from(ApplicationRole.class);

            criteria.select(root);

            if (StringUtils.isNotBlank(name)) {
                criteria.where(builder.like(builder.lower(root.get(ApplicationRole_.name)), name.toLowerCase() + "%"));
            }

            criteria.orderBy(builder.asc(root.get(ApplicationRole_.name)));

            TypedQuery<ApplicationRole> query = entityManager().createQuery(criteria);

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
     * @param name name
     *
     * @return Return value
     */
    public static Long findSystemRolesByNameCount(String name) {
        Long result = 0L;

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name cannot be null");
        }

        try {
            CriteriaBuilder     builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<ApplicationRole>    root     = criteria.from(ApplicationRole.class);

            criteria.select(builder.count(root));

            if (StringUtils.isNotBlank(name)) {
                criteria.where(builder.like(builder.lower(root.get(ApplicationRole_.name)), name.toLowerCase() + "%"));
            }

            criteria.orderBy(builder.asc(root.get(ApplicationRole_.name)));
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
    public static List<ApplicationRole> findLastModifiedSystemRoles(Integer maxResults) {
        List<ApplicationRole> result = null;

        try {
            Query q = entityManager().createQuery("select al from ApplicationRole as al order by al.lastUpdate desc");

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
    public String getDescription() {
        return description;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getName() {
        return name;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Set<ApplicationRight> getRights() {
        return rights;
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

        ApplicationRole merged = this.entityManager.merge(this);

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
            ApplicationRole attached = this.entityManager.find(ApplicationRole.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new ApplicationRole(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method description
     *
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method description
     *
     *
     * @param rights rights
     */
    public void setRights(Set<ApplicationRight> rights) {
        this.rights = rights;
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

        sb.append("ApplicationRole {");
        sb.append(super.toString());
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');

        return sb.toString();
    }
}
