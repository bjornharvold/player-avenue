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
public final class TimeoutBroadcast implements GameBroadcast, HandBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.TIMEOUT_EVENT;

    /** Field description */
    private final String handId;

    /** Field description */
    private final String playerNickname;

    /** Field description */
    private final String pokergameId;

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
     * @param seatNumber seatNumber
     */
    public TimeoutBroadcast(String pokergameId, String handId, String playerNickname, Integer seatNumber) {
        this.pokergameId    = pokergameId;
        this.handId         = handId;
        this.playerNickname = playerNickname;
        this.seatNumber     = seatNumber;
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
    public Integer getSeatNumber() {
        return seatNumber;
    }
}
