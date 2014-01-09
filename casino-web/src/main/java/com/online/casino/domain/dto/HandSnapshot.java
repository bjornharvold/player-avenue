/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.dto;

import java.math.BigDecimal;

/**
 * Created by Bjorn Harvold
 * Date: 3/29/11
 * Time: 5:05 PM
 * Responsibility:
 */
public final class HandSnapshot {
    private final BigDecimal lastBetAmount;
    private final BigDecimal pot;
    private final Integer seatNumber;
    private final String playerNickname;
    private final String nextPlayerNickname;
    private final Integer nextSeatNumber;
    private final String pokergameId;
    private final String handId;

    public HandSnapshot(String pokergameId, String handId, BigDecimal lastBetAmount, BigDecimal pot, Integer seatNumber,
                        String playerNickname, String nextPlayerNickname, Integer nextSeatNumber) {
        this.pokergameId = pokergameId;
        this.handId = handId;
        this.lastBetAmount = lastBetAmount;
        this.pot = pot;
        this.seatNumber = seatNumber;
        this.playerNickname = playerNickname;
        this.nextPlayerNickname = nextPlayerNickname;
        this.nextSeatNumber = nextSeatNumber;
    }

    public String getPokergameId() {
        return pokergameId;
    }

    public String getHandId() {
        return handId;
    }

    public BigDecimal getLastBetAmount() {
        return lastBetAmount;
    }

    public BigDecimal getPot() {
        return pot;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }

    public Integer getNextSeatNumber() {
        return nextSeatNumber;
    }
}
