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
 * Responsibility: This object is used to broadcast privately to the player that a small blind post is required
 * from him.
 */
public final class PrivateRiverBroadcast implements GameBroadcast, HandBroadcast, UserBroadcast {

    /** Field description */
    private final GameEvent event = GameEvent.DEALING_RIVER_EVENT;

    /** Field description */
    private final String applicationUserId;

    /** Field description */
    private final String card1;

    /** Field description */
    private final String card2;

    /** Field description */
    private final String card3;

    /** Field description */
    private final String card4;

    /** Field description */
    private final String card5;

    /** Field description */
    private final String card6;

    /** Field description */
    private final String card7;

    /** Field description */
    private final String handId;

    /** Field description */
    private final String pokergameId;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param applicationUserId applicationUserId
     * @param card1 card1
     * @param card2 card2
     * @param card3
     * @param card4
     * @param card5
     * @param card6
     * @param card7
     */
    public PrivateRiverBroadcast(String pokergameId, String handId, String applicationUserId, String card1,
                                 String card2, String card3, String card4, String card5, String card6, String card7) {
        this.pokergameId       = pokergameId;
        this.handId            = handId;
        this.applicationUserId = applicationUserId;
        this.card1             = card1;
        this.card2             = card2;
        this.card3             = card3;
        this.card4             = card4;
        this.card5             = card5;
        this.card6             = card6;
        this.card7             = card7;
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
    public String getCard4() {
        return card4;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getCard5() {
        return card5;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getCard6() {
        return card6;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getCard7() {
        return card7;
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
