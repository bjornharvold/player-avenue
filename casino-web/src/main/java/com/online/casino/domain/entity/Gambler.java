package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GamblerStatus;
import com.online.casino.domain.enums.HandStatus;
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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
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
 * @version ${project.version}, 10/10/22
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class Gambler extends AbstractEntity implements Serializable {

    /**
     * Field description
     */
    private static final long serialVersionUID = 4603527585739166603L;

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    private Boolean winner = Boolean.FALSE;

    /**
     * Field description
     */
    @NotNull
    private String accountId;

    /**
     * Field description
     */
    private String cards;

    /**
     * The Gambler gets associated with a hand when he starts playing a new game.
     * At the same time he loses the direct association to the game below
     */
    @ManyToOne(optional = true, targetEntity = Hand.class)
    @JoinColumn
    private Hand hand;

    /**
     * Field description
     */
    private String handName;

    /**
     * Field description
     */
    private Integer handRank;

    /**
     * Field description
     */
    @ManyToOne(targetEntity = Gambler.class, fetch = FetchType.LAZY)
    @JoinColumn
    private Gambler nextGambler;

    /**
     * Field description
     */
    @ManyToOne(optional = false, targetEntity = Player.class)
    @JoinColumn
    private Player player;

    /**
     * Field description
     */
    @ManyToOne(targetEntity = Gambler.class, fetch = FetchType.LAZY)
    @JoinColumn
    private Gambler previousGambler;

    // this is how much money the gambler wants to move from his account into the next hand

    /**
     * Field description
     */
    @Column(nullable = true, precision = 10, scale = 2)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal queuedRefillAmount;

    /**
     * Field description
     */
    private Integer seatNumber;

    /**
     * Field description
     */
    @Enumerated(EnumType.STRING)
    private GamblerStatus status;

    /**
     * This is how much the gambler won for this hand
     */
    @Column(nullable = true, precision = 10, scale = 2)
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal winAmount;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     */
    public Gambler() {}

    /**
     * Constructs ...
     *
     *
     * @param id id
     */
    private Gambler(String id) {
        this.id = id;
    }

    /**
     * Constructs ...
     *
     * @param accountId  accountId
     * @param player     player
     * @param hand       hand
     * @param status     status
     * @param seatNumber seatNumber
     */
    public Gambler(String accountId, Player player, Hand hand, GamblerStatus status, Integer seatNumber) {
        this.accountId  = accountId;
        this.player     = player;
        this.hand       = hand;
        this.status     = status;
        this.seatNumber = seatNumber;
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
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Long findActiveGamblerCount(String handId) {
        return findGamblerCountByGameAndStatus(handId, GamblerStatus.ACTIVE);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return
     */
    public static Long findActiveGamblerCountForGame(String pokergameId) {
        return findGameGamblerCountByStatuses(pokergameId, GamblerStatus.ACTIVE);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Long findActiveGamblerCountForHand(String handId) {
        return findHandGamblerCountByStatuses(handId, GamblerStatus.ACTIVE);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return
     */
    public static List<Gambler> findActiveGamblersForGame(String pokergameId) {
        return findGameGamblersByStatuses(pokergameId, GamblerStatus.ACTIVE);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static List<Gambler> findActiveGamblersForHand(String handId) {
        return findHandGamblersByStatuses(handId, GamblerStatus.ACTIVE);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Gambler findBigBlind(String handId) {
        Gambler result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select g from Gambler as g where g.hand.id = :handId and g.hand.bigBlindSeat = g.seatNumber");

            q.setParameter("handId", handId);
            result = (Gambler) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Gambler findCurrentGambler(String handId) {
        Gambler result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select g from Gambler as g where g.hand.id = :handId and g.hand.currentGamblerSeat = g.seatNumber");

            q.setParameter("handId", handId);
            result = (Gambler) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    public static String findCurrentGamblerId(String handId) {
        String result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select g.id from Gambler as g where g.hand.id = :handId and g.hand.currentGamblerSeat = g.seatNumber");

            q.setParameter("handId", handId);
            result = (String) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    public static String findUserByCurrentGambler(String handId) {
        String result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select u.id from Gambler as g " +
                            "join g.player as p " +
                            "join p.applicationUser as u " +
                            "where g.hand.id = :handId and g.hand.currentGamblerSeat = g.seatNumber");

            q.setParameter("handId", handId);
            result = (String) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Gambler findFirstGambler(String handId) {
        Gambler result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select g from Gambler as g where g.hand.id = :handId and g.hand.firstGamblerSeat = g.seatNumber");

            q.setParameter("handId", handId);
            result = (Gambler) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param id id
     * @return
     */
    public static Gambler findGambler(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Gambler");
        }

        return entityManager().find(Gambler.class, id);
    }

    /**
     * Returns a gambler entity for a player who is currently playing an active hand of said game
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @return
     */
    public static Gambler findGamblerByGameAndPlayer(String pokergameId, String playerId) {
        Gambler result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Gambler> criteria = builder.createQuery(Gambler.class);

            // from
            Root<Gambler> root = criteria.from(Gambler.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // Join
            Join<Gambler, Player> playerJoin = root.join(Gambler_.player);
            Join<Gambler, Hand>   handJoin   = root.join(Gambler_.hand);
            Join<Hand, PokerGame> gameJoin   = handJoin.join(Hand_.game);

            // conditional statement
            criteria.where(builder.equal(playerJoin.get(Player_.id), playerId),
                           builder.equal(gameJoin.get(PokerGame_.id), pokergameId),
                           builder.notEqual(handJoin.get(Hand_.status), HandStatus.COMPLETE));
            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId     handId
     * @param seatNumber seatNumber
     * @return
     */
    public static Gambler findGamblerBySeatNumber(String handId, Integer seatNumber) {
        Gambler result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (seatNumber == null) {
            throw new IllegalArgumentException("seatNumber cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select g from Gambler as g where g.seatNumber = :seatNumber and g.hand.id = :handId");

            q.setParameter("seatNumber", seatNumber);
            q.setParameter("handId", handId);
            result = (Gambler) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Long findGamblerCount(String handId) {
        Long result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select count(g) from Gambler as g where g.hand.id = :handId");

            q.setParameter("handId", handId);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId handId
     * @param status status
     * @return
     */
    public static Long findGamblerCountByGameAndStatus(String handId, GamblerStatus status) {
        Long result = 0L;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select count(g) from Gambler as g where g.hand.id = :handId and g.status = :status");

            q.setParameter("handId", handId);
            q.setParameter("status", status);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return
     */
    public static Long findGamblerCountForGameExcludeRemove(String pokergameId) {
        return findGameGamblerCountByStatuses(pokergameId, GamblerStatus.ACTIVE, GamblerStatus.AWAY,
                GamblerStatus.FOLDED);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Gambler findGamblerWithHighestSeatNumber(String handId) {
        Gambler result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select g from Gambler as g where g.hand.id = :handId and g.seatNumber = "
                          + "(select max(g2.seatNumber) from Gambler as g2 where g2.hand.id = :handId)");

            q.setParameter("handId", handId);
            result = (Gambler) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Gambler findGamblerWithLowestSeatNumber(String handId) {
        Gambler result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select g from Gambler as g where g.hand.id = :handId and g.status = :status and g.seatNumber = (select min(g2.seatNumber) from Gambler as g2 where g2.hand.id = :handId and g2.status = :status)");

            q.setParameter("handId", handId);
            q.setParameter("status", GamblerStatus.ACTIVE);
            result = (Gambler) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return
     */
    public static List<Gambler> findGamblers(String pokergameId) {
        List<Gambler> result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select g from Gambler as g where g.hand.game.id = :pokergameId order by g.seatNumber");

            q.setParameter("pokergameId", pokergameId);
            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Retrieves a List of Gamblers by handId and hand status
     *
     * @param handId handId
     * @param status status
     * @return List<Gambler>
     */
    public static List<Gambler> findGamblersByHandAndStatus(String handId, GamblerStatus status) {
        List<Gambler> result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select g from Gambler as g where g.hand.id = :handId and g.status = :status");

            q.setParameter("handId", handId);
            q.setParameter("status", status);
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
    public static List<Gambler> findGamblersForGameExcludeRemove(String pokergameId) {
        return findGameGamblersByStatuses(pokergameId, GamblerStatus.ACTIVE, GamblerStatus.AWAY, GamblerStatus.FOLDED);
    }

    /**
     * Retrieves a List of Gamblers by handId
     *
     * @param handId handId
     * @return List<Gambler>
     */
    public static List<Gambler> findGamblersForHand(String handId) {
        List<Gambler> result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select g from Gambler as g where g.hand.id = :handId");

            q.setParameter("handId", handId);
            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Long findInactiveGamblerCountForHand(String handId) {
        return findHandGamblerCountByStatuses(handId, GamblerStatus.AWAY, GamblerStatus.FOLDED);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return
     */
    public static List<Gambler> findInactiveGamblersForGame(String pokergameId) {
        return findGameGamblersByStatuses(pokergameId, GamblerStatus.AWAY, GamblerStatus.FOLDED);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static List<Gambler> findLosers(String handId) {
        return findWinnersAndLosers(handId, Boolean.FALSE);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static Gambler findSmallBlind(String handId) {
        Gambler result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select g from Gambler as g where g.hand.id = :handId and g.hand.smallBlindSeat = g.seatNumber");

            q.setParameter("handId", handId);
            result = (Gambler) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return
     */
    public static List<Gambler> findWinners(String handId) {
        return findWinnersAndLosers(handId, Boolean.TRUE);
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
    public String getCards() {
        return cards;
    }

    /**
     * Method description
     *
     * @return
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Method description
     *
     * @return
     */
    public String getHandName() {
        return handName;
    }

    /**
     * Method description
     *
     * @return
     */
    public Integer getHandRank() {
        return handRank;
    }

    /**
     * Method description
     *
     * @return
     */
    public Gambler getNextGambler() {
        return nextGambler;
    }

    /**
     * Method description
     *
     * @return
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Method description
     *
     * @return
     */
    public Gambler getPreviousGambler() {
        return previousGambler;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public BigDecimal getQueuedRefillAmount() {
        return queuedRefillAmount;
    }

    /**
     * Method description
     *
     * @return
     */
    public Integer getSeatNumber() {
        return seatNumber;
    }

    /**
     * Method description
     *
     * @return
     */
    public GamblerStatus getStatus() {
        return status;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public BigDecimal getWinAmount() {
        return winAmount;
    }

    /**
     * Method description
     *
     * @return
     */
    public Boolean getWinner() {
        return winner;
    }

    /**
     * Method description
     *
     * @param gamblerId gamblerId
     * @return
     */
    public static Boolean isBigBlind(String gamblerId) {
        Object id = null;

        if (StringUtils.isBlank(gamblerId)) {
            throw new IllegalArgumentException("gamblerId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select g.id from Gambler g where g.id = :id and g.hand.bigBlindSeat = g.seatNumber");

            q.setParameter("id", gamblerId);
            id = q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return id != null;
    }

    /**
     * Method description
     *
     * @param gamblerId gamblerId
     * @return
     */
    public static Boolean isSmallBlind(String gamblerId) {
        Object id = null;

        if (StringUtils.isBlank(gamblerId)) {
            throw new IllegalArgumentException("gamblerId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select g.id from Gambler g where g.id = :id and g.hand.smallBlindSeat = g.seatNumber");

            q.setParameter("id", gamblerId);
            id = q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return id != null;
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

        Gambler merged = this.entityManager.merge(this);

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

    /**
     * Method description
     *
     *
     * @param id id
     */
    public static void remove(String id) {
        new Gambler(id).remove();
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
     * @param cards cards
     */
    public void setCards(String cards) {
        this.cards = cards;
    }

    /**
     * Method description
     *
     * @param hand hand
     */
    public void setHand(Hand hand) {
        this.hand = hand;
    }

    /**
     * Method description
     *
     * @param handName handName
     */
    public void setHandName(String handName) {
        this.handName = handName;
    }

    /**
     * Method description
     *
     * @param handRank handRank
     */
    public void setHandRank(Integer handRank) {
        this.handRank = handRank;
    }

    /**
     * Method description
     *
     * @param nextGambler nextGambler
     */
    public void setNextGambler(Gambler nextGambler) {
        this.nextGambler = nextGambler;
    }

    /**
     * Method description
     *
     * @param player player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Method description
     *
     * @param previousGambler previousGambler
     */
    public void setPreviousGambler(Gambler previousGambler) {
        this.previousGambler = previousGambler;
    }

    /**
     * Method description
     *
     * @param queuedRefillAmount queuedRefillAmount
     */
    public void setQueuedRefillAmount(BigDecimal queuedRefillAmount) {
        this.queuedRefillAmount = queuedRefillAmount;
    }

    /**
     * Method description
     *
     * @param seatNumber seatNumber
     */
    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    /**
     * Method description
     *
     * @param status status
     */
    public void setStatus(GamblerStatus status) {
        this.status = status;
    }

    /**
     * Method description
     *
     *
     * @param winAmount winAmount
     */
    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }

    /**
     * Method description
     *
     * @param winner winner
     */
    public void setWinner(Boolean winner) {
        this.winner = winner;
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

        builder.append("Gambler [");
        builder.append(super.toString());

        if (accountId != null) {
            builder.append("accountId=");
            builder.append(accountId);
            builder.append(", ");
        }

        if (cards != null) {
            builder.append("cards=");
            builder.append(cards);
            builder.append(", ");
        }

        if (hand != null) {
            builder.append("hand=");
            builder.append(hand.getId());
            builder.append(", ");
        }

        if (handName != null) {
            builder.append("handName=");
            builder.append(handName);
            builder.append(", ");
        }

        if (handRank != null) {
            builder.append("handRank=");
            builder.append(handRank);
            builder.append(", ");
        }

        if (nextGambler != null) {
            builder.append("nextGambler=");
            builder.append(nextGambler.getId());
            builder.append(", ");
        }

        if (player != null) {
            builder.append("player=");
            builder.append(player.getId());
            builder.append(", ");
        }

        if (previousGambler != null) {
            builder.append("previousGambler=");
            builder.append(previousGambler.getId());
            builder.append(", ");
        }

        if (seatNumber != null) {
            builder.append("seatNumber=");
            builder.append(seatNumber);
            builder.append(", ");
        }

        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }

        if (winner != null) {
            builder.append("winner=");
            builder.append(winner);
        }

        builder.append("]");

        return builder.toString();
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param statuses    statuses
     * @return
     */
    private static Long findGameGamblerCountByStatuses(String pokergameId, GamblerStatus... statuses) {
        Long result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        if (statuses == null) {
            throw new IllegalArgumentException("statuses cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // from
            Root<Gambler> root = criteria.from(Gambler.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(builder.count(root));

            // join
            Join<Gambler, Hand>   handJoin = root.join(Gambler_.hand);
            Join<Hand, PokerGame> gameJoin = handJoin.join(Hand_.game);

            // conditional statement where gambler is in a certain game and contains one of the mentioned statuses
            criteria.where(builder.equal(gameJoin.get(PokerGame_.id), pokergameId),
                           root.get(Gambler_.status).in(statuses));

            // order by
            criteria.orderBy(builder.asc(root.get(Gambler_.createdDate)));
            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param statuses statuses
     *
     * @return Return value
     */
    private static List<Gambler> findGameGamblersByStatuses(String pokergameId, GamblerStatus... statuses) {
        List<Gambler> result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        if (statuses == null) {
            throw new IllegalArgumentException("statuses cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Gambler> criteria = builder.createQuery(Gambler.class);

            // from
            Root<Gambler> root = criteria.from(Gambler.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // join
            Join<Gambler, Hand>   handJoin = root.join(Gambler_.hand);
            Join<Hand, PokerGame> gameJoin = handJoin.join(Hand_.game);

            // conditional statement where gambler is in a certain game and contains one of the mentioned statuses
            criteria.where(builder.equal(gameJoin.get(PokerGame_.id), pokergameId),
                           root.get(Gambler_.status).in(statuses));

            // order by
            criteria.orderBy(builder.asc(root.get(Gambler_.createdDate)));
            result = entityManager().createQuery(criteria).getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId   handId
     * @param statuses statuses
     * @return
     */
    private static Long findHandGamblerCountByStatuses(String handId, GamblerStatus... statuses) {
        Long result = 0L;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (statuses == null) {
            throw new IllegalArgumentException("statuses cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // from
            Root<Gambler> root = criteria.from(Gambler.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(builder.count(root));

            // join
            Join<Gambler, Hand> handJoin = root.join(Gambler_.hand);

            // conditional statement where gambler is in a certain game and contains one of the mentioned statuses
            criteria.where(builder.equal(handJoin.get(Hand_.id), handId), root.get(Gambler_.status).in(statuses));
            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId   handId
     * @param statuses statuses
     * @return
     */
    private static List<Gambler> findHandGamblersByStatuses(String handId, GamblerStatus... statuses) {
        List<Gambler> result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (statuses == null) {
            throw new IllegalArgumentException("statuses cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Gambler> criteria = builder.createQuery(Gambler.class);

            // from
            Root<Gambler> root = criteria.from(Gambler.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // Join
            Join<Gambler, Hand> handJoin = root.join(Gambler_.hand);

            // conditional statement
            criteria.where(builder.equal(handJoin.get(Hand_.id), handId), root.get(Gambler_.status).in(statuses));
            criteria.orderBy(builder.asc(root.get(Gambler_.seatNumber)));
            result = entityManager().createQuery(criteria).getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     * @param handId   handId
     * @param isWinner isWinner
     * @return
     */
    private static List<Gambler> findWinnersAndLosers(String handId, Boolean isWinner) {
        List<Gambler> result = null;

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }

        if (isWinner == null) {
            throw new IllegalArgumentException("isWinner cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select g from Gambler g where g.hand.id = :handId and g.winner = :isWinner");

            q.setParameter("handId", handId);
            q.setParameter("isWinner", isWinner);
            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }
}
