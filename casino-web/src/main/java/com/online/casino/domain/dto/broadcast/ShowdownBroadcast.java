/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.dto.broadcast;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GameEvent;

import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 2/2/11
 * Time: 2:25 PM
 * Responsibility:
 */
public final class ShowdownBroadcast implements GameBroadcast, HandBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.SHOWDOWN_EVENT;

    /** Field description */
    private final List<GamblerShowdown> gamblers;

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
     * @param gamblers
     */
    public ShowdownBroadcast(String pokergameId, String handId, List<GamblerShowdown> gamblers) {
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
    public List<GamblerShowdown> getGamblers() {
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
