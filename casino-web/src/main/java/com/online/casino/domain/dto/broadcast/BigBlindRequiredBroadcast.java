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
 * Date: 2/2/11
 * Time: 2:21 PM
 * Responsibility:
 */
public final class BigBlindRequiredBroadcast implements GameBroadcast, HandBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.WAITING_FOR_BIG_BLIND_EVENT;

    /** Field description */
    private final String handId;

    /** Field description */
    private final String nickname;

    /** Field description */
    private final String pokergameId;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param pokergameId pokergameId
     * @param handId handId
     * @param nickname nickname
     */
    public BigBlindRequiredBroadcast(String pokergameId, String handId, String nickname) {
        this.pokergameId = pokergameId;
        this.handId      = handId;
        this.nickname    = nickname;
    }

    //~--- get methods --------------------------------------------------------

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
    public String getNickname() {
        return nickname;
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
