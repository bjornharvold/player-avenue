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
 * Time: 2:22 PM
 * Responsibility:
 */
public final class FlopBroadcast implements GameBroadcast, HandBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.DEALING_FLOP_EVENT;

    /** Field description */
    private final String card1;

    /** Field description */
    private final String card2;

    /** Field description */
    private final String card3;

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
     * @param card1 card1
     * @param card2 card2
     * @param card3 card3
     */
    public FlopBroadcast(String pokergameId, String handId, String card1, String card2, String card3) {
        this.pokergameId = pokergameId;
        this.handId      = handId;
        this.card1       = card1;
        this.card2       = card2;
        this.card3       = card3;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getCard1() {
        return card1;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getCard2() {
        return card2;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getCard3() {
        return card3;
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
