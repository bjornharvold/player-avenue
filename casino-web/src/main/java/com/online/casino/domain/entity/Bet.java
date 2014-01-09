package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.BetStatus;
import com.online.casino.domain.enums.BetType;
import com.online.casino.domain.enums.HandStatus;
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
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/10/23
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class Bet extends AbstractEntity implements Serializable {

    /**
     * Field description
     */
    private static final long serialVersionUID = 1067448832308694535L;

    //~--- fields -------------------------------------------------------------

    // whether this bet has been withdrawn with winnings or not

    /**
     * Status of the bet
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetStatus betStatus = BetStatus.IN_PLAY;

    /**
     * Amount of the bet
     */
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    @NumberFormat(style = Style.CURRENCY)
    private BigDecimal amount;

    /**
     * Which hand it was for.
     */
    @NotNull
    private String handId;

    /**
     * Field description
     */
    @NotNull
    private String nickname;

    /**
     * Player who made the bet
     */
    @NotNull
    private String playerId;

    /**
     * this number follows the current round number on the hand
     */
    @NotNull
    @Column(nullable = false)
    private Integer roundNumber;

    /**
     * where in the game this bet is occurring
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HandStatus status;

    /**
     * the type of bet this is
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetType type;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     */
    public Bet() {}

    public Bet(String id) {
        this.id = id;
    }

    /**
     * Constructs ...
     *
     * @param amount      amount
     * @param type        type
     * @param status      status
     * @param playerId    playerId
     * @param handId      handId
     * @param roundNumber roundNumber
     * @param nickname    nickname
     */
    public Bet(BigDecimal amount, BetType type, HandStatus status, String playerId, String handId, Integer roundNumber,
               String nickname) {
        this.amount      = amount;
        this.type        = type;
        this.status      = status;
        this.playerId    = playerId;
        this.handId      = handId;
        this.roundNumber = roundNumber;
        this.nickname    = nickname;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return EntityManager
     */
    public static final EntityManager entityManager() {
        EntityManager em = new Bet().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     * @param id id
     * @return
     */
    public static Bet findBet(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Bet");
        }

        return entityManager().find(Bet.class, id);
    }

    /**
     * Returns the total amount this gambler has bet
     *
     *
     * @param handId handId
     * @param playerId playerId
     * @return
     */
    public static BigDecimal findBetAmount(String handId, String playerId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select sum(bet.amount) from Bet as bet where bet.playerId = :playerId and bet.handId = :handId");

            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Returns the total amount bet for this hand. Basically the value of the pot
     *
     * @param handId handId
     * @return The pot
     */
    public static BigDecimal findBetAmountByHand(String handId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select sum(bet.amount) from Bet as bet where bet.handId = :handId");

            q.setParameter("handId", handId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Retrieves the total bet amount the gambler has bet for the current round
     *
     *
     * @param handId handId
     * @param playerId    playerId
     * @param roundNumber roundNumber
     * @return Returns total bet amount by round for gambler
     */
    public static BigDecimal findBetAmountByRound(String handId, String playerId, Integer roundNumber) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        if (roundNumber == null) {
            throw new IllegalArgumentException("roundNumber cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select sum(bet.amount) from Bet as bet where bet.handId = :handId and bet.playerId = :playerId and bet.roundNumber = :roundNumber");

            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            q.setParameter("roundNumber", roundNumber);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Retrieves the total bet amount for the specified hand and round
     *
     * @param handId      handId
     * @param roundNumber roundNumber
     * @return BigDecimal
     */
    public static BigDecimal findBetAmountRound(String handId, Integer roundNumber) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (roundNumber == null) {
            throw new IllegalArgumentException("roundNumber cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select sum(bet.amount) from Bet as bet where bet.roundNumber = :roundNumber and bet.handId = :handId");

            q.setParameter("handId", handId);
            q.setParameter("roundNumber", roundNumber);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Returns a count of how many bets the gambler made for the current round, EXCLUDING
     * forced bets such as Big Blind and Small Blind
     *
     * @param handId
     * @param playerId
     * @param roundNumber
     * @return Bet count
     */
    public static Long findBetCountByRoundNumber(String handId, String playerId, Integer roundNumber) {
        Long result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        if (roundNumber == null) {
            throw new IllegalArgumentException("roundNumber cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select count(bet) from Bet as bet where bet.handId = :handId and bet.roundNumber = :roundNumber and bet.playerId = :playerId and bet.type <> :bb and bet.type <> :sb");

            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            q.setParameter("roundNumber", roundNumber);
            q.setParameter("bb", BetType.BIG_BLIND);
            q.setParameter("sb", BetType.SMALL_BLIND);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Retrieves all the bets this gambler has ever made
     *
     * @param handId
     * @param playerId playerId
     * @return List<Bet>
     */
    public static List<Bet> findBetsByGambler(String handId, String playerId) {
        List<Bet> result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select bet from Bet as bet where bet.handId = :handId and bet.playerId = :playerId order by bet.createdDate");

            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Retrieves all the bets for this hand
     *
     * @param handId handId
     * @return List<Bet>
     */
    public static List<Bet> findBetsByHand(String handId) {
        List<Bet> result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select bet from Bet as bet where bet.handId = :handId order by bet.createdDate");

            q.setParameter("handId", handId);
            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Retrieves the big blind (if any)
     *
     * @param handId handId
     * @return Bet
     */
    public static Bet findBigBlind(String handId) {
        Bet result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select o from Bet o where o.handId = :handId and o.type = :type and o.amount > 0");

            q.setParameter("handId", handId);
            q.setParameter("type", BetType.BIG_BLIND);
            result = (Bet) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Returns the number of times a specified gambler has checked during a specified round
     *
     *
     * @param handId handId
     * @param playerId    playerId
     * @param roundNumber roundNumber
     * @return Check count
     */
    public static Long findGamblerCheckCount(String handId, String playerId, Integer roundNumber) {
        return findBetByTypeCount(handId, playerId, roundNumber, BetType.CHECK);
    }

    /**
     * Returns the number of times a specified gambler has raised during a specified round
     *
     *
     * @param handId handId
     * @param playerId    playerId
     * @param roundNumber roundNumber
     * @return
     */
    public static Long findGamblerRaiseCount(String handId, String playerId, Integer roundNumber) {
        return findBetByTypeCount(handId, playerId, roundNumber, BetType.RAISE);
    }

    /**
     * Retrieves the last bet that was amde for this hand
     *
     * @param handId handId
     * @return Bet
     */
    public static Bet findLastBet(String handId) {
        Bet result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select o from Bet o where o.handId = :handId and o.createdDate = (select max(o2.lastUpdate) from Bet o2 where o2.handId = :handId)");

            q.setParameter("handId", handId);
            result = (Bet) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Retrieves the last bet that was amde for this hand
     *
     * @param handId handId
     * @return Bet
     */
    public static BigDecimal findLastBetAmount(String handId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select o.amount from Bet o where o.handId = :handId and o.createdDate = (select max(o2.lastUpdate) from Bet o2 where o2.handId = :handId)");

            q.setParameter("handId", handId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Retrieves the last Bet made on this hand
     *
     * @param handId      handId
     * @param type        type
     * @param roundNumber roundNumber
     * @return Bet
     */
    public static Bet findLastBet(String handId, BetType type, Integer roundNumber) {
        Bet result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        if (roundNumber == null) {
            throw new IllegalArgumentException("roundNumber cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select bet from Bet as bet where bet.handId = :handId and bet.roundNumber = :roundNumber and bet.type = :type and bet.lastUpdate in (select max(b2.lastUpdate) from Bet as b2 where b2.handId = :handId and b2.roundNumber = :roundNumber and b2.type = :type)");

            q.setParameter("handId", handId);
            q.setParameter("roundNumber", roundNumber);
            q.setParameter("type", type);
            result = (Bet) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Find last bet gambler made
     *
     *
     * @param handId handId
     * @param playerId playerId
     * @return Bet
     */
    public static Bet findLastBetForGambler(String handId, String playerId) {
        Bet result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                        "select bet from Bet bet where bet.handId = :handId and bet.playerId = :playerId and bet.createdDate = (select max(o2.lastUpdate) from Bet o2 where o2.playerId = :playerId)");

            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            result = (Bet) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    public static BigDecimal findLastBetAmountForGambler(String handId, String playerId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                        "select bet.amount from Bet bet where bet.handId = :handId and bet.playerId = :playerId and bet.createdDate = (select max(o2.lastUpdate) from Bet o2 where o2.playerId = :playerId)");

            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * This method returns the largest amount a single gambler has bet
     *
     * @param handId
     * @return BigDecimal
     */
    public static BigDecimal findMaxBet(String handId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q = entityManager().createNativeQuery("select max(total_amount) as total from "
                          + "(select sum(b.amount) as total_amount from bet as b "
                          + "where b.hand_id = :handId group by b.player_id) as a");

            q.setParameter("handId", handId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * This method returns the largest amount a single gambler has bet for the current round number
     *
     * @param handId
     * @param roundNumber
     * @return
     */
    public static BigDecimal findMaxBetByRoundNumber(String handId, Integer roundNumber) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (roundNumber == null) {
            throw new IllegalArgumentException("roundNumber cannot be null");
        }

        try {
            Query q = entityManager().createNativeQuery("select max(total_amount) as total from "
                          + "(select sum(b.amount) as total_amount " + "from bet as b "
                          + "where b.round_number = :roundNumber and b.hand_id = :handId "
                          + "group by b.player_id) as a");

            q.setParameter("roundNumber", roundNumber);
            q.setParameter("handId", handId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Method description
     *
     * @param handId      handId
     * @param roundNumber roundNumber
     * @param playerId    playerId
     * @return
     */
    public static BigDecimal findMaxBetByRoundNumberExcludePlayer(String handId, Integer roundNumber, String playerId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        if (roundNumber == null) {
            throw new IllegalArgumentException("roundNumber cannot be null");
        }

        try {
            Query q = entityManager().createNativeQuery("select max(total_amount) as total from "
                          + "(select sum(b.amount) as total_amount " + "from bet as b "
                          + "where b.round_number = :roundNumber and b.hand_id = :handId and b.player_id <> :playerId "
                          + "group by b.player_id) as a");

            q.setParameter("roundNumber", roundNumber);
            q.setParameter("handId", handId);
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
     * @param handId   handId
     * @param playerId playerId
     * @return
     */
    public static BigDecimal findMaxBetExcludePlayer(String handId, String playerId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            Query q = entityManager().createNativeQuery("select max(total_amount) as total from "
                    + "(select sum(b.amount) as total_amount " + "from bet as b "
                    + "where b.hand_id = :handId and b.player_id <> :playerId " + "group by b.player_id) as a");

            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            result = (BigDecimal) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Returns the number of times a gamblers have raised during a specified round
     *
     * @param handId      handId
     * @param roundNumber roundNumber
     * @return Raise count
     */
    public static Long findRaiseCount(String handId, Integer roundNumber) {
        Long result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (roundNumber == null) {
            throw new IllegalArgumentException("roundNumber cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select count(bet) from Bet as bet where bet.handId = :handId and bet.roundNumber = :roundNumber and bet.type = :type");

            q.setParameter("handId", handId);
            q.setParameter("roundNumber", roundNumber);
            q.setParameter("type", BetType.RAISE);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Retrieves the small blind (if any)
     *
     * @param handId handId
     * @return Bet
     */
    public static Bet findSmallBlind(String handId) {
        Bet result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery("select bet from Bet bet where bet.handId = :handId and bet.type = :type");

            q.setParameter("handId", handId);
            q.setParameter("type", BetType.SMALL_BLIND);
            result = (Bet) q.getSingleResult();
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
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Method description
     *
     * @return
     */
    public BetStatus getBetStatus() {
        return betStatus;
    }

    /**
     * Method description
     *
     * @return
     */
    public String getHandId() {
        return handId;
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
    public Integer getRoundNumber() {
        return roundNumber;
    }

    /**
     * Method description
     *
     * @return
     */
    public HandStatus getStatus() {
        return status;
    }

    /**
     * Method description
     *
     * @return
     */
    public BetType getType() {
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

        Bet merged = this.entityManager.merge(this);

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
     * After the winnings have been distributed, there might still be some un-accounted for side bets
     * that need to be returned to their owner. This is what this method does.
     *
     *
     * public static BigDecimal processSidebets(String playerId) {
     *   BigDecimal result = null;
     *   Query q =
     *           entityManager().createQuery(
     *                   "select sum(b.amount) from Bet as b where b.playerId = :playerId and b.betStatus <> :status");
     *
     *   q.setParameter("playerId", playerId);
     *   q.setParameter("status", BetStatusCd.WITHDRAWN);
     *
     *   try {
     *       result = (BigDecimal) q.getSingleResult();
     *   } catch (EmptyResultDataAccessException e) {
     *   }
     *
     *   Query q2 =
     *           entityManager().createQuery(
     *                   "update Bet as b set b.betStatus = :status where b.playerId = :playerId");
     *
     *   q2.setParameter("playerId", playerId);
     *   q2.setParameter("status", BetStatusCd.WITHDRAWN);
     *   q2.executeUpdate();
     *
     *   return result;
     * }
     */

    /**
     * Method description
     *
     * @param handId    handId
     * @param playerId playerId
     * @return
     * public static void processSingleWinner(String handId, String playerId) {
     *   BigDecimal result = findBetAmountByHand(handId);
     *
     *   Query q3 = entityManager().createQuery("update Bet as b set b.betStatus = :status where b.handId = :handId");
     *
     *   q3.setParameter("handId", handId);
     *   q3.setParameter("status", BetStatusCd.WITHDRAWN);
     *   q3.executeUpdate();
     *
     *   // return winnings
     *   return result;
     * }
     */

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
            Bet attached = this.entityManager.find(Bet.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new Bet(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param amount amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Method description
     *
     * @param betStatus betStatus
     */
    public void setBetStatus(BetStatus betStatus) {
        this.betStatus = betStatus;
    }

    /**
     * Method description
     *
     * @param handId handId
     */
    public void setHandId(String handId) {
        this.handId = handId;
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
     * @param playerId playerId
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /**
     * Method description
     *
     * @param roundNumber roundNumber
     */
    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    /**
     * Method description
     *
     * @param status status
     */
    public void setStatus(HandStatus status) {
        this.status = status;
    }

    /**
     * Method description
     *
     * @param type type
     */
    public void setType(BetType type) {
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
        StringBuilder builder = new StringBuilder();

        builder.append("Bet [");
        builder.append(super.toString());

        if (amount != null) {
            builder.append("amount=");
            builder.append(amount);
            builder.append(", ");
        }

        if (betStatus != null) {
            builder.append("betStatus=");
            builder.append(betStatus);
            builder.append(", ");
        }

        if (playerId != null) {
            builder.append("gambler=");
            builder.append(playerId);
            builder.append(", ");
        }

        if (handId != null) {
            builder.append("hand=");
            builder.append(handId);
            builder.append(", ");
        }

        if (roundNumber != null) {
            builder.append("roundNumber=");
            builder.append(roundNumber);
            builder.append(", ");
        }

        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }

        if (type != null) {
            builder.append("type=");
            builder.append(type);
        }

        builder.append("]");

        return builder.toString();
    }

    /**
     * Method description
     *
     * @param handId handId
     * @param status status
     */
    public static void updateBetStatus(String handId, BetStatus status) {
        Query q = entityManager().createQuery("update Bet as b set b.betStatus = :status where b.handId = :handId");

        q.setParameter("handId", handId);
        q.setParameter("status", status);
        q.executeUpdate();
    }

    /**
     * Returns the number of times a gambler has made a bet with a specific type for the specific roundNumber
     *
     * @param handId      handId
     * @param playerId    playerId
     * @param roundNumber roundNumber
     * @param type        type
     * @return Count
     */
    private static Long findBetByTypeCount(String handId, String playerId, Integer roundNumber, BetType type) {
        Long result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        if (roundNumber == null) {
            throw new IllegalArgumentException("roundNumber cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select count(bet) from Bet as bet where bet.handId = :handId and bet.playerId = :playerId and bet.roundNumber = :roundNumber and bet.type = :type");

            q.setParameter("handId", handId);
            q.setParameter("playerId", playerId);
            q.setParameter("roundNumber", roundNumber);
            q.setParameter("type", type);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }
}
