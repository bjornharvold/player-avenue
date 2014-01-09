/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GamblerAccountEntryType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Oct 19, 2010
 * Time: 10:07:15 AM
 * Responsibility: The queued gambler entity is persisted when a player goes from observing the game
 * to joining it. On the next hand, the queued gambler will become a bona fide gambler entity if the table
 * has the space for him.
 */
@Entity
@Configurable
public class QueuedGambler extends AbstractEntity implements Serializable {

    /**
     * Field description
     */
    private static final long serialVersionUID = -3230179233239398889L;

    //~--- fields -------------------------------------------------------------

    /**
     * Player account id
     */
    @NotNull
    @Column(nullable = false)
    private String accountId;

    /**
     * amount
     */
    @Column(nullable = true, precision = 10, scale = 2)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal amount;

    /**
     * the seat this gambler would like to sit in
     */
    @NotNull
    @Column(nullable = false)
    private Integer desiredSeatNumber;

    /**
     * whether the gambler HAS to sit in this seat or not
     */
    @NotNull
    @Column(nullable = false)
    private Boolean mustHaveSeat;

    /**
     * Field description
     */
    @NotNull
    @Column(nullable = false)
    private String nickname;

    /**
     * the player
     */
    @NotNull
    @Column(nullable = false)
    private String playerId;

    /**
     * the game
     */
    @NotNull
    @Column(nullable = false)
    private String pokergameId;

    /**
     * place in the queue
     */
    @NotNull
    @Column(nullable = false)
    private Integer queueNumber;

    /** ONLY USE BUYIN OR REFILL HERE */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private GamblerAccountEntryType type;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     */
    public QueuedGambler() {}

    private QueuedGambler(String id) {
        this.id = id;
    }

    /**
     * Constructs ...
     *
     * @param accountId         accountId
     * @param desiredSeatNumber desiredSeatNumber
     * @param pokergameId       pokergameId
     * @param mustHaveSeat      mustHaveSeat
     * @param playerId          playerId
     * @param queueNumber       queueNumber
     * @param nickname          nickname
     */
    public QueuedGambler(String pokergameId, String playerId, String accountId, Integer desiredSeatNumber,
                         Boolean mustHaveSeat, Integer queueNumber, String nickname) {
        this.desiredSeatNumber = desiredSeatNumber;
        this.pokergameId       = pokergameId;
        this.mustHaveSeat      = mustHaveSeat;
        this.playerId          = playerId;
        this.queueNumber       = queueNumber;
        this.nickname          = nickname;
        this.accountId         = accountId;
    }

    /**
     * Constructs ...
     *
     * @param pokergameId       game
     * @param playerId          player
     * @param accountId         accountId
     * @param desiredSeatNumber desiredSeatNumber
     * @param mustHaveSeat      mustHaveSeat
     * @param queueNumber       queueNumber
     * @param nickname          nickname
     * @param amount             buyin
     */
    public QueuedGambler(String pokergameId, String playerId, String accountId, Integer desiredSeatNumber,
                         Boolean mustHaveSeat, Integer queueNumber, String nickname, GamblerAccountEntryType type, BigDecimal amount) {
        this.desiredSeatNumber = desiredSeatNumber;
        this.pokergameId       = pokergameId;
        this.mustHaveSeat      = mustHaveSeat;
        this.playerId          = playerId;
        this.queueNumber       = queueNumber;
        this.accountId         = accountId;
        this.amount            = amount;
        this.nickname          = nickname;
        this.type              = type;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new Gambler().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Returns a list of queued gamblers ordered by queue number
     *
     * @param pokergameId pokergameId
     * @param maxResults  maxResults
     * @return
     */
    public static List<QueuedGambler> findLatestQueuedGamblers(String pokergameId, Integer maxResults) {
        List<QueuedGambler> result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select g from QueuedGambler as g where g.pokergameId = :pokergameId order by g.queueNumber asc");

            q.setParameter("pokergameId", pokergameId);
            q.setFirstResult(0);
            q.setMaxResults(maxResults);
            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return
     */
    public static Integer findMaxQueueNumber(String pokergameId) {
        Integer result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select max(g.queueNumber) from QueuedGambler as g where g.pokergameId = :pokergameId");

            q.setParameter("pokergameId", pokergameId);
            result = (Integer) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? 0
               : result;
    }

    /**
     * Method description
     *
     * @param id id
     * @return
     */
    public static QueuedGambler findQueuedGambler(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Gambler");
        }

        return entityManager().find(QueuedGambler.class, id);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @return
     */
    public static QueuedGambler findQueuedGamblerByGameAndPlayer(String pokergameId, String playerId) {
        QueuedGambler result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }
        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }
        try {
            Query q =
                entityManager().createQuery(
                    "select g from QueuedGambler as g where g.pokergameId = :pokergameId and g.playerId = :playerId");

            q.setParameter("pokergameId", pokergameId);
            q.setParameter("playerId", playerId);
            result = (QueuedGambler) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Returns the number of queued gamblers for the specified game
     *
     * @param pokergameId pokergameId
     * @return Count
     */
    public static Long findQueuedGamblerCount(String pokergameId) {
        Long result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select count(g) from QueuedGambler as g where g.pokergameId = :pokergameId");

            q.setParameter("pokergameId", pokergameId);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
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
     * @return
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Method description
     *
     * @return
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Method description
     *
     * @return
     */
    public Integer getDesiredSeatNumber() {
        return desiredSeatNumber;
    }

    /**
     * Method description
     *
     * @return
     */
    public Boolean getMustHaveSeat() {
        return mustHaveSeat;
    }

    /**
     * Method description
     *
     * @return
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Method description
     *
     * @return
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Method description
     *
     * @return
     */
    public String getPokergameId() {
        return pokergameId;
    }

    /**
     * Method description
     *
     * @return
     */
    public Integer getQueueNumber() {
        return queueNumber;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public GamblerAccountEntryType getType() {
        return type;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        QueuedGambler merged = this.entityManager.merge(this);

        this.entityManager.flush();
        this.id = merged.getId();
    }

    /**
     * Method description
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
     */
    @Transactional
    public void remove() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Gambler attached = this.entityManager.find(Gambler.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new QueuedGambler(id).remove();
    }

    /**
     * Method description
     *
     * @param queuedGamblerId queuedGamblerId
     */
    public static void removeQueuedGambler(String queuedGamblerId) {
        if (StringUtils.isBlank(queuedGamblerId)) {
            throw new IllegalArgumentException("queuedGamblerId cannot be null");
        }

        Query q = entityManager().createQuery("delete from QueuedGambler as qg where qg.id = :id");

        q.setParameter("id", queuedGamblerId);
        q.executeUpdate();
    }

    /**
     * Bulk remove of QueuedGambler entities by playerId and pokergameId
     *
     * @param qgs gamblers
     */
    public static void removeQueuedGamblers(List<QueuedGambler> qgs) {
        if (qgs != null) {
            for (QueuedGambler qg : qgs) {
                QueuedGambler.removeQueuedGambler(qg.getId());
            }
        }
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param accountId accountId
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Method description
     *
     * @param amount buyin
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Method description
     *
     * @param desiredSeatNumber desiredSeatNumber
     */
    public void setDesiredSeatNumber(Integer desiredSeatNumber) {
        this.desiredSeatNumber = desiredSeatNumber;
    }

    /**
     * Method description
     *
     * @param mustHaveSeat mustHaveSeat
     */
    public void setMustHaveSeat(Boolean mustHaveSeat) {
        this.mustHaveSeat = mustHaveSeat;
    }

    /**
     * Method description
     *
     * @param nickname nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Method description
     *
     * @param playerId player
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /**
     * Method description
     *
     * @param pokergameId game
     */
    public void setPokergameId(String pokergameId) {
        this.pokergameId = pokergameId;
    }

    /**
     * Method description
     *
     * @param queueNumber queueNumber
     */
    public void setQueueNumber(Integer queueNumber) {
        this.queueNumber = queueNumber;
    }

    /**
     * Method description
     *
     *
     * @param type type
     */
    public void setType(GamblerAccountEntryType type) {
        this.type = type;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("QueuedGambler [");
        sb.append(super.toString());
        sb.append("accountId=").append(accountId);
        sb.append(", amount=").append(amount);
        sb.append(", desiredSeatNumber=").append(desiredSeatNumber);
        sb.append(", mustHaveSeat=").append(mustHaveSeat);
        sb.append(", playerId=").append(playerId);
        sb.append(", pokergameId=").append(pokergameId);
        sb.append(", queueNumber=").append(queueNumber);
        sb.append(", type=").append(type);
        sb.append(']');

        return sb.toString();
    }
}
