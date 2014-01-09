/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service.fsm.event;

/**
 * Class description
 *
 *
 * @version        ${project.version}, 10/12/18
 * @author         Bjorn Harvold
 */
public abstract class AbstractGamblerEvent extends AbstractEvent {

    /** Field description */
    private final String gamblerId;

    /** Field description */
    private final String handId;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param pokergameId pokergameId
     * @param handId handId
     * @param applicationUserId applicationUserId
     * @param gamblerId gamblerId
     */
    public AbstractGamblerEvent(String pokergameId, String handId, String applicationUserId, String gamblerId) {
        super(pokergameId, applicationUserId);
        this.handId       = handId;
        this.gamblerId    = gamblerId;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getGamblerId() {
        return gamblerId;
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
}
