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
 * Date: 2/2/11
 * Time: 2:21 PM
 * Responsibility: This object is used to broadcast privately to the player that a small blind post is required
 * from him.
 */
public final class PrivateSmallBlindRequiredBroadcast implements GameBroadcast, HandBroadcast, UserBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.WAITING_FOR_SMALL_BLIND_EVENT;

    /**
     * Field description
     */
    private final BigDecimal amount;

    /** Field description */
    private final String applicationUserId;

    /** Field description */
    private final String handId;

    /** Field description */
    private final String pokergameId;

    /**
     * Field description
     */
    private final Integer time;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param applicationUserId applicationUserId
     * @param time                time
     * @param amount              amount
     */
    public PrivateSmallBlindRequiredBroadcast(String pokergameId, String handId, String applicationUserId,
            Integer time, BigDecimal amount) {
        this.pokergameId       = pokergameId;
        this.handId            = handId;
        this.applicationUserId = applicationUserId;
        this.time              = time;
        this.amount            = amount;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
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
    public String getApplicationUserId() {
        return applicationUserId;
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
    public String getPokergameId() {
        return pokergameId;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Integer getTime() {
        return time;
    }
}
