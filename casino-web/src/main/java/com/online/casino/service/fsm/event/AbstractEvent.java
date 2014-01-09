/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.fsm.event;

/**
 * Created by Bjorn Harvold
 * Date: 4/9/11
 * Time: 12:36 AM
 * Responsibility:
 */
public abstract class AbstractEvent {
    /** Field description */
    private final String pokergameId;

    /** Field description */
    private final String applicationUserId;

    protected AbstractEvent(String pokergameId, String applicationUserId) {
        this.pokergameId = pokergameId;
        this.applicationUserId = applicationUserId;
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

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getApplicationUserId() {
        return applicationUserId;
    }
}
