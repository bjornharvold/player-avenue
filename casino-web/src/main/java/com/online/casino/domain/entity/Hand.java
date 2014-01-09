package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.HandStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 10/11/06
 * @author         Bjorn Harvold
 */
@Entity
@Configurable
public class Hand extends AbstractEntity implements Serializable {

    /** Field description */
    private static final long serialVersionUID = -4898236479801493089L;

    //~--- fields -------------------------------------------------------------

    /** Round number for hand - Always starts with 1 */
    private Integer currentRoundNumber = 1;

    /** Field description */
    @OneToMany(targetEntity = Gambler.class, mappedBy = "hand")
    private List<Gambler> gamblers = new ArrayList<Gambler>();

    /** Hand status - Always instantiates with start */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private HandStatus status = HandStatus.START;

    /** Field description */
    private Integer bigBlindSeat;

    /** Field description */
    @Column(nullable = true)
    private Integer currentGamblerSeat;

    /** Field description */
    private Integer dealerSeat;

    /** Field description */
    private String deck;

    /** Field description */
    private Integer firstGamblerSeat;

    /** Field description */
    private String flop;

    /** Field description */
    @ManyToOne(optional = false, targetEntity = PokerGame.class)
    @JoinColumn
    private PokerGame game;

    /** Field description */
    private String river;

    /** Field description */
    private Integer smallBlindSeat;

    /** Field description */
    private String turn;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     */
    public Hand() {}

    private Hand(String id) {
        this.id = id;
    }

    /**
     * Constructs ...
     *
     *
     * @param game game
     * @param deck deck
     */
    public Hand(PokerGame game, String deck) {
        this.game = game;
        this.deck = deck;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new Hand().entityManager;

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
     * @param pokergameId pokergameId
     *
     * @return
     */
    public static Hand findCurrentHand(String pokergameId) {
        Hand result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select hand from Hand as hand "
                          + "where hand.game.id = :pokergameId and hand.status <> :status and hand.createdDate = "
                          + "(select max(h2.createdDate) from Hand as h2 "
                          + "where hand.game.id = :pokergameId and h2.status <> :status)");

            q.setParameter("pokergameId", pokergameId);
            q.setParameter("status", HandStatus.COMPLETE);

            // this creates a sql query like so:
            // select * from Hand where createdDate = (select Max(createdDate) from Hand where game and status = blah) where game and status = blah
            result = (Hand) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param id id
     *
     * @return
     */
    public static Hand findHand(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Hand");
        }

        return entityManager().find(Hand.class, id);
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return
     */
    public static List<Hand> findHandsByGame(String pokergameId) {
        List<Hand> result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Hand> criteria = builder.createQuery(Hand.class);

            // from
            Root<Hand> root = criteria.from(Hand.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // Join
            Join<Hand, PokerGame> gameJoin = root.join(Hand_.game);

            // conditional statement
            criteria.where(builder.equal(gameJoin.get(PokerGame_.id), pokergameId));

            // order by last first
            criteria.orderBy(builder.asc(root.get(Hand_.createdDate)));
            result = entityManager().createQuery(criteria).getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return
     */
    public static Hand findLastHandPlayed(String pokergameId) {
        return findLastHandPlayedByStatus(pokergameId, HandStatus.COMPLETE);
    }

    /**
     * Method description
     *
     *
     * @param handId handId
     * @param gamblerId gamblerId
     *
     * @return
     */
    public static List<Player> findOpponents(String handId, String gamblerId) {
        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("handId cannot be null");
        }
        if (StringUtils.isBlank(gamblerId)) {
            throw new IllegalArgumentException("gamblerId cannot be null");
        }

        return entityManager()
            .createQuery(
                "select gamblers.player from Hand h join h.gamblers as gamblers where h.id = :handId and gamblers.id <> :gamblerId")
                    .setParameter("handId", handId).setParameter("gamblerId", gamblerId).getResultList();
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
    public Integer getBigBlindSeat() {
        return bigBlindSeat;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Integer getCurrentGamblerSeat() {
        return currentGamblerSeat;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Integer getCurrentRoundNumber() {
        return currentRoundNumber;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Integer getDealerSeat() {
        return dealerSeat;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getDeck() {
        return deck;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Integer getFirstGamblerSeat() {
        return firstGamblerSeat;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getFlop() {
        return flop;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public List<Gambler> getGamblers() {
        return gamblers;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public PokerGame getGame() {
        return game;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getRiver() {
        return river;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public Integer getSmallBlindSeat() {
        return smallBlindSeat;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public HandStatus getStatus() {
        return status;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getTurn() {
        return turn;
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

        Hand merged = this.entityManager.merge(this);

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
            Hand attached = this.entityManager.find(Hand.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new Hand(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param bigBlindSeat bigBlindSeat
     */
    public void setBigBlindSeat(Integer bigBlindSeat) {
        this.bigBlindSeat = bigBlindSeat;
    }

    /**
     * Method description
     *
     *
     * @param currentGamblerSeat currentGamblerSeat
     */
    public void setCurrentGamblerSeat(Integer currentGamblerSeat) {
        this.currentGamblerSeat = currentGamblerSeat;
    }

    /**
     * Method description
     *
     *
     * @param currentRoundNumber currentRoundNumber
     */
    public void setCurrentRoundNumber(Integer currentRoundNumber) {
        this.currentRoundNumber = currentRoundNumber;
    }

    /**
     * Method description
     *
     *
     * @param dealerSeat dealerSeat
     */
    public void setDealerSeat(Integer dealerSeat) {
        this.dealerSeat = dealerSeat;
    }

    /**
     * Method description
     *
     *
     * @param deck deck
     */
    public void setDeck(String deck) {
        this.deck = deck;
    }

    /**
     * Method description
     *
     *
     * @param firstGamblerSeat firstGamblerSeat
     */
    public void setFirstGamblerSeat(Integer firstGamblerSeat) {
        this.firstGamblerSeat = firstGamblerSeat;
    }

    /**
     * Method description
     *
     *
     * @param flop flop
     */
    public void setFlop(String flop) {
        this.flop = flop;
    }

    /**
     * Method description
     *
     *
     * @param gamblers gamblers
     */
    public void setGamblers(List<Gambler> gamblers) {
        this.gamblers = gamblers;
    }

    /**
     * Method description
     *
     *
     * @param game game
     */
    public void setGame(PokerGame game) {
        this.game = game;
    }

    /**
     * Method description
     *
     *
     * @param river river
     */
    public void setRiver(String river) {
        this.river = river;
    }

    /**
     * Method description
     *
     *
     * @param smallBlindSeat smallBlindSeat
     */
    public void setSmallBlindSeat(Integer smallBlindSeat) {
        this.smallBlindSeat = smallBlindSeat;
    }

    /**
     * Method description
     *
     *
     * @param status status
     */
    public void setStatus(HandStatus status) {
        this.status = status;
    }

    /**
     * Method description
     *
     *
     * @param turn turn
     */
    public void setTurn(String turn) {
        this.turn = turn;
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
        StringBuilder builder = new StringBuilder();

        builder.append("Hand [");
        builder.append(super.toString());

        if (game != null) {
            builder.append("game=");
            builder.append(game.getId());
            builder.append(", ");
        }

        if (bigBlindSeat != null) {
            builder.append("bigBlindSeat=");
            builder.append(bigBlindSeat);
            builder.append(", ");
        }

        if (currentGamblerSeat != null) {
            builder.append("currentGamblerSeat=");
            builder.append(currentGamblerSeat);
            builder.append(", ");
        }

        if (currentRoundNumber != null) {
            builder.append("currentRoundNumber=");
            builder.append(currentRoundNumber);
            builder.append(", ");
        }

        if (dealerSeat != null) {
            builder.append("dealerSeat=");
            builder.append(dealerSeat);
            builder.append(", ");
        }

        if (deck != null) {
            builder.append("deck=");
            builder.append(deck);
            builder.append(", ");
        }

        if (firstGamblerSeat != null) {
            builder.append("firstGamblerSeat=");
            builder.append(firstGamblerSeat);
            builder.append(", ");
        }

        if (flop != null) {
            builder.append("flop=");
            builder.append(flop);
            builder.append(", ");
        }

        if (river != null) {
            builder.append("river=");
            builder.append(river);
            builder.append(", ");
        }

        if (smallBlindSeat != null) {
            builder.append("smallBlindSeat=");
            builder.append(smallBlindSeat);
            builder.append(", ");
        }

        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }

        if (turn != null) {
            builder.append("turn=");
            builder.append(turn);
        }

        builder.append("]");

        return builder.toString();
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param status status
     *
     * @return
     */
    private static Hand findLastHandPlayedByStatus(String pokergameId, HandStatus status) {
        Hand result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select h from Hand as h "
                          + "where h.game.id = :pokergameId and h.status = :status and h.createdDate = "
                          + "(select max(h2.createdDate) from Hand as h2 "
                          + "where h.game.id = :pokergameId and h2.status = :status)");

            q.setParameter("pokergameId", pokergameId);
            q.setParameter("status", status);

            // this creates a sql query like so:
            // select * from Hand where createdDate = (select Max(createdDate) from Hand where game and status = blah) where game and status = blah
            result = (Hand) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }
}
