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
 * Date: 4/9/11
 * Time: 11:48 PM
 * Responsibility:
 */
public class QueuePlayerBroadcast implements GameBroadcast, UserBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.QUEUE_PLAYER_EVENT;

    /** Field description */
    private final String applicationUserId;

    /** Field description */
    private final BigDecimal buyin;

    /** Field description */
    private final Boolean mustHaveSeat;

    /** Field description */
    private final String playerId;

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
     * @param applicationUserId applicationUserId
     * @param playerId playerId
     * @param buyin buyin
     * @param seatNumber seatNumber
     * @param mustHaveSeat mustHaveSeat
     */
    public QueuePlayerBroadcast(String pokergameId, String applicationUserId, String playerId, BigDecimal buyin,
                                Integer seatNumber, Boolean mustHaveSeat) {
        this.pokergameId       = pokergameId;
        this.applicationUserId = applicationUserId;
        this.playerId          = playerId;
        this.buyin             = buyin;
        this.seatNumber        = seatNumber;
        this.mustHaveSeat      = mustHaveSeat;
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
    public BigDecimal getBuyin() {
        return buyin;
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
    public Boolean getMustHaveSeat() {
        return mustHaveSeat;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getPlayerId() {
        return playerId;
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
