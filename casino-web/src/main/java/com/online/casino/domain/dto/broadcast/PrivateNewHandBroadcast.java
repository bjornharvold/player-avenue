/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.domain.dto.broadcast;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GameEvent;

import java.math.BigDecimal;

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 2/2/11
 * Time: 2:21 PM
 * Responsibility: This object is used to broadcast privately to the player that a small blind post is required
 * from him.
 */
public final class PrivateNewHandBroadcast implements GameBroadcast, HandBroadcast, UserBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.DEALING_HAND_EVENT;

    /** Field description */
    private final String applicationUserId;

    /** Field description */
    private final String gamblerId;

    /** Field description */
    private final String handId;

    /** Field description */
    private final Boolean isBigBlind;

    /** Field description */
    private final Boolean isDealer;

    /** Field description */
    private final Boolean isSmallBlind;

    /** Field description */
    private final String pokergameId;

    /** Field description */
    private final Integer seatNumber;

    private final BigDecimal amount;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param applicationUserId applicationUserId
     * @param seatNumber seatNumber
     * @param gamblerId gamblerId
     * @param dealer dealer
     * @param bigBlind bigBlind
     * @param smallBlind smallBlind
     */
    public PrivateNewHandBroadcast(String pokergameId, String handId, String applicationUserId, Integer seatNumber,
                                   String gamblerId, Boolean dealer, Boolean bigBlind, Boolean smallBlind,
                                   BigDecimal amount) {
        this.pokergameId       = pokergameId;
        this.handId            = handId;
        this.applicationUserId = applicationUserId;
        this.seatNumber        = seatNumber;
        this.gamblerId         = gamblerId;
        this.isDealer               = dealer;
        this.isBigBlind             = bigBlind;
        this.isSmallBlind           = smallBlind;
        this.amount = amount;
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
    public Boolean getBigBlind() {
        return isBigBlind;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Boolean getDealer() {
        return isDealer;
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
    public String getGamblerId() {
        return gamblerId;
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
    public String getPokergameId() {
        return pokergameId;
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

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Boolean getSmallBlind() {
        return isSmallBlind;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
