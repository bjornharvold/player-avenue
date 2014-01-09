/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.domain.dto.broadcast;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GameEvent;

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 3/29/11
 * Time: 8:30 PM
 * Responsibility:
 */
public final class PrivatePostSmallBlindBroadcast implements GameBroadcast, HandBroadcast, UserBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.POST_SMALL_BLIND_EVENT;

    /** Field description */
    private final String applicationUserId;

    /** Field description */
    private final String handId;

    /** Field description */
    private final String pokergameId;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokergameId  pokergameId
     * @param handId       handId
     * @param applicationUserId applicationUserId
     */
    public PrivatePostSmallBlindBroadcast(String pokergameId, String handId, String applicationUserId) {
        this.pokergameId       = pokergameId;
        this.handId            = handId;
        this.applicationUserId = applicationUserId;
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
}
