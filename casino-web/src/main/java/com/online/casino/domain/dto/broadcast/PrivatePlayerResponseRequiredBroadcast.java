/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.domain.dto.broadcast;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GameAction;
import com.online.casino.domain.enums.GameEvent;

import java.math.BigDecimal;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 2/2/11
 * Time: 2:21 PM
 * Responsibility: This object is used to broadcast privately to the player that a small blind post is required
 * from him.
 */
public final class PrivatePlayerResponseRequiredBroadcast implements GameBroadcast, HandBroadcast, UserBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.PLAYER_RESPONSE_REQUIRED_EVENT;

    /** Field description */
    private final String applicationUserId;

    /** Field description */
    private final List<GameAction> availableMoves;

    /** Field description */
    private final BigDecimal callAmount;

    /** Field description */
    private final String handId;

    /** Field description */
    private final BigDecimal maxRaiseAmount;

    /** Field description */
    private final BigDecimal minRaiseAmount;

    /** Field description */
    private final String pokergameId;

    /** Field description */
    private final Integer time;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param applicationUserId applicationUserId
     * @param availableMoves availableMoves
     * @param maxRaiseAmount
     * @param minRaiseAmount
     * @param time
     * @param callAmount
     */
    public PrivatePlayerResponseRequiredBroadcast(String pokergameId, String handId, String applicationUserId,
            List<GameAction> availableMoves, BigDecimal maxRaiseAmount, BigDecimal minRaiseAmount, Integer time,
            BigDecimal callAmount) {
        this.pokergameId       = pokergameId;
        this.handId            = handId;
        this.applicationUserId = applicationUserId;
        this.availableMoves    = availableMoves;
        this.maxRaiseAmount    = maxRaiseAmount;
        this.minRaiseAmount    = minRaiseAmount;
        this.time              = time;
        this.callAmount        = callAmount;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getApplicationUserId() {
        return applicationUserId;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public List<GameAction> getAvailableMoves() {
        return availableMoves;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public BigDecimal getCallAmount() {
        return callAmount;
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
    public BigDecimal getMaxRaiseAmount() {
        return maxRaiseAmount;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public BigDecimal getMinRaiseAmount() {
        return minRaiseAmount;
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
    public Integer getTime() {
        return time;
    }
}
