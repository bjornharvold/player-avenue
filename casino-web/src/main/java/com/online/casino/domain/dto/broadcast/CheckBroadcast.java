/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.dto.broadcast;

import com.online.casino.domain.enums.GameEvent;

import java.math.BigDecimal;

/**
 * Created by Bjorn Harvold
 * Date: 4/9/11
 * Time: 11:52 AM
 * Responsibility:
 */
public final class CheckBroadcast implements GameBroadcast, HandBroadcast {
    private final String pokergameId;
    private final String handId;
    private final String playerNickname;
    private final String nextPlayerNickname;
    private final BigDecimal amount;
    private final BigDecimal pot;
    private final Integer seatNumber;
    private final Integer nextSeatNumber;
    private final GameEvent event = GameEvent.CHECK_EVENT;

    public CheckBroadcast(String pokergameId, String handId, String playerNickname,
                          BigDecimal amount, BigDecimal pot,
                          Integer seatNumber, String nextPlayerNickname, Integer nextSeatNumber) {
        this.pokergameId = pokergameId;
        this.handId = handId;
        this.playerNickname = playerNickname;
        this.amount = amount;
        this.pot = pot;
        this.seatNumber = seatNumber;
        this.nextPlayerNickname = nextPlayerNickname;
        this.nextSeatNumber = nextSeatNumber;
    }

    public String getPokergameId() {
        return pokergameId;
    }

    public String getHandId() {
        return handId;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPot() {
        return pot;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public Integer getNextSeatNumber() {
        return nextSeatNumber;
    }

    public GameEvent getEvent() {
        return event;
    }
}
