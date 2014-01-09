/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.domain.dto.broadcast;

//~--- JDK imports ------------------------------------------------------------

import java.math.BigDecimal;

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 5/9/11
 * Time: 10:20 PM
 * Responsibility:
 */
public final class GamblerNewHand {

    /** Field description */
    private final BigDecimal amount;

    /** Field description */
    private final String avatarUrl;

    /** Field description */
    private final Boolean bigBlind;

    /** Field description */
    private final Boolean dealer;

    /** Field description */
    private final String nickname;

    /** Field description */
    private final Integer seatNumber;

    /** Field description */
    private final Boolean smallBlind;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param nickname nickname
     * @param seatNumber seatNumber
     * @param avatarUrl avatarUrl
     * @param dealer dealer
     * @param smallBlind smallBlind
     * @param bigBlind bigBlind
     * @param amount amount
     */
    public GamblerNewHand(String nickname, Integer seatNumber, String avatarUrl, Boolean dealer, Boolean smallBlind,
                          Boolean bigBlind, BigDecimal amount) {
        this.nickname   = nickname;
        this.seatNumber = seatNumber;
        this.avatarUrl  = avatarUrl;
        this.dealer     = dealer;
        this.smallBlind = smallBlind;
        this.bigBlind   = bigBlind;
        this.amount     = amount;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Boolean getBigBlind() {
        return bigBlind;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Boolean getDealer() {
        return dealer;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Integer getSeatNumber() {
        return seatNumber;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Boolean getSmallBlind() {
        return smallBlind;
    }
}
