/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.domain.dto.broadcast;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GameEvent;

import java.math.BigDecimal;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 3/29/11
 * Time: 8:04 PM
 * Responsibility:
 */
public final class PostBigBlindBroadcast implements GameBroadcast, HandBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.POST_BIG_BLIND_EVENT;

    /** Field description */
    private final BigDecimal amount;

    /** Field description */
    private final String handId;

    /** Field description */
    private final String nextPlayerNickname;

    /** Field description */
    private final Integer nextSeatNumber;

    /** Field description */
    private final String playerNickname;

    /** Field description */
    private final String pokergameId;

    /** Field description */
    private final BigDecimal pot;

    /** Field description */
    private final Integer seatNumber;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param pokergameId pokergameId
     * @param handId handId
     * @param playerNickname player
     * @param amount amount
     * @param pot pot
     * @param seatNumber seatNumber
     * @param nextPlayerNickname nextPlayer
     * @param nextSeatNumber nextSeatNumber
     */
    public PostBigBlindBroadcast(String pokergameId, String handId, String playerNickname, BigDecimal amount, BigDecimal pot,
                                 Integer seatNumber, String nextPlayerNickname, Integer nextSeatNumber) {
        this.pokergameId    = pokergameId;
        this.handId         = handId;
        this.playerNickname = playerNickname;
        this.amount         = amount;
        this.pot            = pot;
        this.seatNumber     = seatNumber;
        this.nextPlayerNickname = nextPlayerNickname;
        this.nextSeatNumber = nextSeatNumber;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public GameEvent getEvent() {
        return event;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getHandId() {
        return handId;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Integer getNextSeatNumber() {
        return nextSeatNumber;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getPlayerNickname() {
        return playerNickname;
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
     * @return Return value
     */
    public BigDecimal getPot() {
        return pot;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Integer getSeatNumber() {
        return seatNumber;
    }
}
