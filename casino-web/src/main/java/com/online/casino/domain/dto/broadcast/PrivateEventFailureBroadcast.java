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
 * Date: 3/30/11
 * Time: 4:29 PM
 * Responsibility:
 */
public final class PrivateEventFailureBroadcast implements UserBroadcast, GameBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.FAILURE_EVENT;

    /** Field description */
    private final String applicationUserId;

    /** Field description */
    private final String message;

    /** Field description */
    private final String pokergameId;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokergameId       pokergameId
     * @param applicationUserId applicationUserId
     * @param message           message
     */
    public PrivateEventFailureBroadcast(String pokergameId, String applicationUserId, String message) {
        this.pokergameId       = pokergameId;
        this.applicationUserId = applicationUserId;
        this.message           = message;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    @Override
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
    public String getMessage() {
        return message;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    @Override
    public String getPokergameId() {
        return pokergameId;
    }
}
