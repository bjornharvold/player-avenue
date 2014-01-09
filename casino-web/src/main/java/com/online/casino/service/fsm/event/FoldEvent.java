/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
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
public final class FoldEvent extends AbstractGamblerEvent {

    /**
     * Constructs ...
     *
     *
     *
     * @param pokergameId pokergameId
     * @param handId handId
     * @param applicationUserId applicationUserId
     * @param gamblerId gamblerId
     */
    public FoldEvent(String pokergameId, String handId, String applicationUserId, String gamblerId) {
        super(pokergameId, handId, applicationUserId, gamblerId);
    }
}
