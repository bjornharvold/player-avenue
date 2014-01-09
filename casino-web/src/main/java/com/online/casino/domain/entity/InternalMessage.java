/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
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
public class InternalMessage extends AbstractEntity implements Serializable {

    /** Field description */
    private static final long serialVersionUID = 5558656289690586389L;

    //~--- fields -------------------------------------------------------------

    /** Field description */
    @NotNull
    @Column(nullable = false)
    private Boolean viewed = false;

    /** Field description */
    @NotNull
    @Column(nullable = false, length = 4096)
    private String message;

    /** Field description */
    @ManyToOne(targetEntity = Player.class, optional = false)
    private Player receiver;

    /** Field description */
    @ManyToOne(targetEntity = Player.class, optional = false)
    private Player sender;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     */
    public InternalMessage() {}

    private InternalMessage(String id) {
        this.id = id;
    }

    /**
     * Constructs ...
     *
     *
     * @param receiver receiver
     * @param sender sender
     * @param message message
     */
    public InternalMessage(Player receiver, Player sender, String message) {
        this.message  = message;
        this.receiver = receiver;
        this.sender   = sender;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public static final EntityManager entityManager() {
        EntityManager em = new InternalMessage().entityManager;

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
    public static InternalMessage findInternalMessage(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of InternalMessage");
        }

        return entityManager().find(InternalMessage.class, id);
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     *
     * @return Return value
     */
    public static Long findInternalMessageCount(String userId) {
        Long result = 0L;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // from
            Root<InternalMessage> root = criteria.from(InternalMessage.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(builder.count(root));

            // joining with the player table
            Join<InternalMessage, Player> playerJoin = root.join(InternalMessage_.receiver);
            Join<Player, ApplicationUser>      userJoin   = playerJoin.join(Player_.applicationUser);

            // conditional statement - find all records that match system user's id
            criteria.where(builder.equal(userJoin.get(ApplicationUser_.id), userId));
            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     *
     * @return Return value
     */
    public static List<InternalMessage> findInternalMessages(String userId) {
        List<InternalMessage> result = null;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<InternalMessage> criteria = builder.createQuery(InternalMessage.class);

            // from
            Root<InternalMessage> root = criteria.from(InternalMessage.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // joining with the player table
            Join<InternalMessage, Player> playerJoin = root.join(InternalMessage_.receiver);
            Join<Player, ApplicationUser>      userJoin   = playerJoin.join(Player_.applicationUser);

            // conditional statement - find all records that match system user's id
            criteria.where(builder.equal(userJoin.get(ApplicationUser_.id), userId));
            criteria.orderBy(builder.asc(root.get(InternalMessage_.createdDate)));
            result = entityManager().createQuery(criteria).getResultList();
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
    public Player getReceiver() {
        return receiver;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Player getSender() {
        return sender;
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

        InternalMessage merged = this.entityManager.merge(this);

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
            InternalMessage attached = this.entityManager.find(InternalMessage.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new InternalMessage(id).remove();
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
    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    /**
     * Method description
     *
     *
     * @param sender sender
     */
    public void setSender(Player sender) {
        this.sender = sender;
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

        sb.append("InternalMessage {");
        sb.append(super.toString());
        sb.append(", read=").append(viewed);
        sb.append(", message='").append(message).append('\'');
        sb.append(", receiver=").append(receiver);
        sb.append(", sender=").append(sender);
        sb.append('}');

        return sb.toString();
    }
}
