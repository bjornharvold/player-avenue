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
public final class LeaveEvent extends AbstractGamblerEvent {
    private final String playerId;

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     * @param applicationUserId applicationUserId
     * @param playerId playerId
     * @param handId handId optional
     * @param gamblerId gamblerId optional
     */
    public LeaveEvent(String pokergameId, String applicationUserId, String playerId, String handId, String gamblerId) {
        super(pokergameId, handId, applicationUserId, gamblerId);
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }
}
