package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GamblerAccountEntryType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
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

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 10/10/23
 * @author         Bjorn Harvold
 */
@Entity
@Configurable
public class GamblerAccountEntry extends AbstractEntity implements Serializable {

    /** Field description */
    private static final long serialVersionUID = -1377954661957397329L;

    //~--- fields -------------------------------------------------------------

    /** amount of entry */
    @Column(nullable = false, precision = 10, scale = 2)
    @NumberFormat(style = Style.CURRENCY)
    private BigDecimal amount;

    /** Description of amount */
    private String description;

    /** Gambler whose entry this belongs to */
    @NotNull
    @Column(nullable = false)
    private String playerId;

    /** Field description */
    @NotNull
    @Column(nullable = false)
    private String pokergameId;

    @NotNull
    @Column(nullable = false)
    private String accountId;
    
    /** Type of entry */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GamblerAccountEntryType type;

    @Column(nullable = true)
    private String handId;
    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     */
    public GamblerAccountEntry() {}

    private GamblerAccountEntry(String id) {
        this.id = id;
    }

    /**
     * Constructs ...
     *
     *
     * @param playerId gamblerId
     * @param type type
     * @param amount amount
     * @param description description
     */
    public GamblerAccountEntry(String pokergameId, String playerId, String accountId, GamblerAccountEntryType type, BigDecimal amount, String description) {
        this.pokergameId    = pokergameId;
        this.playerId    = playerId;
        this.accountId   = accountId;
        this.type        = type;
        this.amount      = amount;
        this.description = description;
    }

    public GamblerAccountEntry(String pokergameId, String handId, String playerId, String accountId, GamblerAccountEntryType type, BigDecimal amount, String description) {
        this.pokergameId    = pokergameId;
        this.handId    = handId;
        this.playerId    = playerId;
        this.accountId   = accountId;
        this.type        = type;
        this.amount      = amount;
        this.description = description;
    }



    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new GamblerAccountEntry().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Returns the amount gambler bet for the given hand
     * @param playerId gamblerId
     * @return BigDecimal
     */
    public static BigDecimal findBetAmount(String pokergameId, String playerId) {
        return findSumByType(pokergameId, playerId, GamblerAccountEntryType.BET);
    }

    /**
     * Returns the amount gambler deposited for the given hand
     * @param playerId gamblerId
     * @return BigDecimal
     */
    public static BigDecimal findDepositAmount(String pokergameId, String playerId) {
        return findSumByType(pokergameId, playerId, GamblerAccountEntryType.BUYIN);
    }

    /**
     * Method description
     *
     *
     * @param playerId playerId
     *
     * @return
     */
    public static BigDecimal findGamblerBalance(String pokergameId, String playerId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }
        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select sum(g.amount) from GamblerAccountEntry as g where g.pokergameId = :pokergameId and g.playerId = :playerId");

            q.setParameter("pokergameId", pokergameId);
            q.setParameter("playerId", playerId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Method description
     *
     *
     * @param id id
     *
     * @return
     */
    public static GamblerAccountEntry findGamblerEntry(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of GamblerEntry");
        }

        return entityManager().find(GamblerAccountEntry.class, id);
    }

    /**
     * Returns the amount the gambler lost for the hand minus the amount he collected in side bets
     * @param playerId gamblerId
     * @return BigDecimal
     */
    public static BigDecimal findLoseAmount(String pokergameId, String playerId) {
        return findSumByType(pokergameId, playerId, GamblerAccountEntryType.COLLECTED_UNACCOUNTED_SIDEBET);
    }

    /**
     * Returns the amount gambler won for the given game
     * @param playerId gamblerId
     * @return BigDecimal
     */
    public static BigDecimal findWinAmount(String pokergameId, String playerId) {
        return findSumByType(pokergameId, playerId, GamblerAccountEntryType.WIN);
    }

    /**
     * Returns the amount gambler won for the given hand
     * @param playerId gamblerId
     * @return BigDecimal
     */
    public static BigDecimal findWinAmount(String pokergameId, String handId, String playerId) {
        return findSumByType(pokergameId, handId, playerId, GamblerAccountEntryType.WIN);
    }

    /**
     * Returns the amount gambler withdrew for the given hand. Not sure how important this method is
     * @param playerId gamblerId
     * @return BigDecimal
     */
    public static BigDecimal findWithdrawAmount(String pokergameId, String playerId) {
        return findSumByType(pokergameId, playerId, GamblerAccountEntryType.WITHDRAWAL);
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
     * @return
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getPokergameId() {
        return pokergameId;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public GamblerAccountEntryType getType() {
        return type;
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

        GamblerAccountEntry merged = this.entityManager.merge(this);

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
            GamblerAccountEntry attached = this.entityManager.find(GamblerAccountEntry.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new GamblerAccountEntry(id).remove();
    }
    
    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param amount amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

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
     * @param playerId gamblerId
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     */
    public void setPokergameId(String pokergameId) {
        this.pokergameId = pokergameId;
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
     *
     * @return
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("GamblerEntry [");
        sb.append(super.toString());
        sb.append(", gamblerId=").append(playerId);
        sb.append(", type=").append(type);
        sb.append(", amount=").append(amount);
        sb.append(", description='").append(description).append('\'');
        sb.append(']');

        return sb.toString();
    }

    /**
     * Method description
     *
     *
     * @param playerId gamblerId
     * @param type type
     *
     * @return
     */
    private static BigDecimal findSumByType(String pokergameId, String playerId, GamblerAccountEntryType type) {
        BigDecimal result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }
        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select sum(g.amount) from GamblerAccountEntry as g where g.pokergameId = :pokergameId and g.playerId = :playerId and g.type = :type");

            q.setParameter("pokergameId", pokergameId);
            q.setParameter("playerId", playerId);
            q.setParameter("type", type);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    private static BigDecimal findSumByType(String pokergameId, String handId, String playerId, GamblerAccountEntryType type) {
        BigDecimal result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }
        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }
        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select sum(g.amount) from GamblerAccountEntry as g where g.pokergameId = :pokergameId and g.handId = :handId and g.playerId = :playerId and g.type = :type");

            q.setParameter("pokergameId", pokergameId);
            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            q.setParameter("type", type);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }
}
