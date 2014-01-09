/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.domain.dto.broadcast;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.enums.GameEvent;

import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 2/2/11
 * Time: 2:22 PM
 * Responsibility:
 */
public final class NewHandBroadcast implements GameBroadcast, HandBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.DEALING_HAND_EVENT;

    /** Field description */
    private final List<GamblerNewHand> gamblers;

    /** Field description */
    private final String handId;

    /** Field description */
    private final String pokergameId;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param pokergameId pokergameId
     * @param handId handId
     * @param gamblers gamblers
     */
    public NewHandBroadcast(String pokergameId, String handId, List<GamblerNewHand> gamblers) {
        this.pokergameId = pokergameId;
        this.handId      = handId;
        this.gamblers    = gamblers;
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
    public List<GamblerNewHand> getGamblers() {
        return gamblers;
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
