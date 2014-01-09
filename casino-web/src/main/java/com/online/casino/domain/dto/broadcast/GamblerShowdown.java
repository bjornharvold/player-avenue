/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.dto.broadcast;

import java.math.BigDecimal;

/**
 * Created by Bjorn Harvold
 * Date: 4/27/11
 * Time: 9:29 PM
 * Responsibility:
 */
public final class GamblerShowdown {
    private final String nickname;
    private final String card1;
    private final String card2;
    private final String card3;
    private final String card4;
    private final String card5;
    private final String handName;
    private final BigDecimal amount;

    public GamblerShowdown(String nickname, String handName, BigDecimal amount, String[] cards) {
        this.nickname = nickname;
        this.handName = handName;
        this.amount = amount;
        this.card1 = cards[0];
        this.card2 = cards[1];
        this.card3 = cards[2];
        this.card4 = cards[3];
        this.card5 = cards[4];
    }

    public String getNickname() {
        return nickname;
    }

    public String getCard1() {
        return card1;
    }

    public String getCard2() {
        return card2;
    }

    public String getCard3() {
        return card3;
    }

    public String getCard4() {
        return card4;
    }

    public String getCard5() {
        return card5;
    }

    public String getHandName() {
        return handName;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
