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
public final class TimeoutEvent {

    private final String pokergameId;
    private final String handId;

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     * @param handId handId
     */
    public TimeoutEvent(String pokergameId, String handId) {
        this.pokergameId = pokergameId;
        this.handId = handId;
    }

    public String getPokergameId() {
        return pokergameId;
    }

    public String getHandId() {
        return handId;
    }
}
