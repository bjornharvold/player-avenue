/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.dto.broadcast;

import com.online.casino.domain.enums.GameEvent;

/**
 * Created by Bjorn Harvold
 * Date: 4/9/11
 * Time: 11:42 AM
 * Responsibility:
 */
public final class PrivateCheckBroadcast implements GameBroadcast, HandBroadcast, UserBroadcast {
    private final String pokergameId;
    private final GameEvent event = GameEvent.CHECK_EVENT;
    private final String handId;
    private final String applicationUserId;

    public PrivateCheckBroadcast(String pokergameId, String handId, String applicationUserId) {
        this.pokergameId = pokergameId;
        this.handId = handId;
        this.applicationUserId = applicationUserId;
    }

    public String getPokergameId() {
        return pokergameId;
    }

    public GameEvent getEvent() {
        return event;
    }

    public String getHandId() {
        return handId;
    }

    public String getApplicationUserId() {
        return applicationUserId;
    }
}
