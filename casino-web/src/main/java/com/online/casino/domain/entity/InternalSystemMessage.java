/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.InternalSystemMessageSender;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 1/1/11
 * Time: 10:50 PM
 * Responsibility:
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class InternalSystemMessage extends AbstractEntity implements Serializable {

    /** Field description */
    private static final long serialVersionUID = -6978668168340034052L;

    //~--- fields -------------------------------------------------------------

    /** Field description */
    @Column(nullable = false)
    private Boolean viewed = false;

    /** Field description */
    @NotNull
    @Column(nullable = false, length = 4096)
    private String message;

    /** Field description */
    @ManyToOne(targetEntity = ApplicationUser.class, optional = false)
    private ApplicationUser receiver;

    /** Field description */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private InternalSystemMessageSender sender;

    public InternalSystemMessage() {
    }

    private InternalSystemMessage(String id) {
        this.id = id;
    }

    public InternalSystemMessage(ApplicationUser receiver, InternalSystemMessageSender sender, String message) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public static final EntityManager entityManager() {
        EntityManager em = new InternalSystemMessage().entityManager;

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
    public static InternalSystemMessage findInternalSystemMessage(String id) {
        if (id == null) {
            throw new IllegalArgumentException(
                "An identifier is required to retrieve an instance of InternalSystemMessage");
        }

        return entityManager().find(InternalSystemMessage.class, id);
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     *
     * @return Return value
     */
    public static List<InternalSystemMessage> findInternalSystemMessages(String userId) {
        List<InternalSystemMessage> result = null;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<InternalSystemMessage> criteria = builder.createQuery(InternalSystemMessage.class);

            // from
            Root<InternalSystemMessage> root = criteria.from(InternalSystemMessage.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // joining with the player table
            Join<InternalSystemMessage, ApplicationUser> userJoin = root.join(InternalSystemMessage_.receiver);

            // conditional statement - find all records that match system user's id
            criteria.where(builder.equal(userJoin.get(ApplicationUser_.id), userId));
            criteria.orderBy(builder.asc(root.get(InternalSystemMessage_.createdDate)));
            result = entityManager().createQuery(criteria).getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    public static Long findInternalSystemMessageCount(String userId) {
        Long result = null;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // from
            Root<InternalSystemMessage> root = criteria.from(InternalSystemMessage.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(builder.count(root));

            // joining with the player table
            Join<InternalSystemMessage, ApplicationUser> userJoin = root.join(InternalSystemMessage_.receiver);

            // conditional statement - find all records that match system user's id
            criteria.where(builder.equal(userJoin.get(ApplicationUser_.id), userId));
            result = entityManager().createQuery(criteria).getSingleResult();
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
    public String getMessage() {
        return message;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Boolean getViewed() {
        return viewed;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public ApplicationUser getReceiver() {
        return receiver;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public InternalSystemMessageSender getSender() {
        return sender;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param internalSystemMessageId internalSystemMessageId
     */
    public static void markAsRead(String internalSystemMessageId) {
        if (StringUtils.isBlank(internalSystemMessageId)) {
            throw new IllegalArgumentException("internalSystemMessageId cannot be null");
        }

        Query query =
            entityManager().createQuery("update InternalSystemMessage im set im.viewed = :read where im.id = :id");

        query.setParameter("read", true);
        query.setParameter("id", internalSystemMessageId);
        query.executeUpdate();
    }

    /**
     * Method description
     *
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        InternalSystemMessage merged = this.entityManager.merge(this);

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
            InternalSystemMessage attached = this.entityManager.find(InternalSystemMessage.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new InternalSystemMessage(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Method description
     *
     *
     * @param read read
     */
    public void setViewed(Boolean read) {
        this.viewed = read;
    }

    /**
     * Method description
     *
     *
     * @param receiver receiver
     */
    public void setReceiver(ApplicationUser receiver) {
        this.receiver = receiver;
    }

    /**
     * Method description
     *
     *
     * @param sender sender
     */
    public void setSender(InternalSystemMessageSender sender) {
        this.sender = sender;
    }
}
