/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service.fsm.event;

//~--- JDK imports ------------------------------------------------------------

import java.math.BigDecimal;

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 12/18/10
 * Time: 12:43 AM
 * Responsibility:
 */
public final class QueuePlayerEvent extends AbstractEvent {

    /** Field description */
    private final BigDecimal buyin;

    /** Field description */
    private final boolean mustHaveSeat;

    /** Field description */
    private final String playerId;

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
     * @param mustHaveSeat mustHaveSeat
     * @param seatNumber seatNumber
     */
    public QueuePlayerEvent(String pokergameId, String applicationUserId, String playerId, BigDecimal buyin, boolean mustHaveSeat,
                            Integer seatNumber) {
        super(pokergameId, applicationUserId);
        this.playerId     = playerId;
        this.buyin        = buyin;
        this.mustHaveSeat = mustHaveSeat;
        this.seatNumber   = seatNumber;
    }

    //~--- get methods --------------------------------------------------------

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
    public boolean getMustHaveSeat() {
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
    public Integer getSeatNumber() {
        return seatNumber;
    }
}
