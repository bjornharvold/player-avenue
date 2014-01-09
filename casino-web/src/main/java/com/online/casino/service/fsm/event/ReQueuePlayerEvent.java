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
public final class ReQueuePlayerEvent extends AbstractEvent {

    private final String gamblerId;

    /**
     *
     * @param pokergameId pokergameId
     * @param applicationUserId applicationUserId
     * @param gamblerId gamblerId
     */
    public ReQueuePlayerEvent(String pokergameId, String applicationUserId, String gamblerId) {
        super(pokergameId, applicationUserId);
        this.gamblerId = gamblerId;
    }

    public String getGamblerId() {
        return gamblerId;
    }
}
