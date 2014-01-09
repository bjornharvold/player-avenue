/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold. 
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.cometd;

/**
 * Created by Bjorn Harvold
 * Date: 3/12/11
 * Time: 10:06 AM
 * Responsibility:
 */
public class CometConstants {
    public static final String GAME_ROOT_CHANNEL = "/services/game";
    public static final String SERVICE_USER_ACTION_CHANNEL = "/user/action/**";
    public static final String SERVICE_USER_CHANNEL = "/user/**";
    public static final String USER_ROOT_CHANNEL = "/user";
    public static final String POKER_GAME_ID = "pokergameId";
    public static final String GAMBLER_ID = "gamblerId";
    public static final String PLAYER_ID = "playerId";
    public static final String AMOUNT = "amount";
    public static final String BUYIN = "buyin";
    public static final String MUST_HAVE_SEAT = "mustHaveSeat";
    public static final String SEAT_NUMBER = "seatNumber";
    public static final String STATUS = "status";
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";
    public static final String HAND_ID = "handId";
    public static final String AVATAR = "avatar";
    public static final String CARDS = "cards";
    public static final String CARD_1 = "card1";
    public static final String CARD_2 = "card2";
    public static final String CARD_3 = "card3";
    public static final String CARD_4 = "card4";
    public static final String CARD_5 = "card5";
    public static final String CARD_6 = "card6";
    public static final String CARD_7 = "card7";
    public static final String EVENT = "event";
    public static final String WINNERS = "winners";
    public static final String ACTION = "action";
    public static final String AVAILABLE_ACTIONS = "availableActions";
    public static final String MESSAGE = "message";
    public static final String IS_BIG_BLIND = "isBigBlind";
    public static final String IS_DEALER = "isDealer";
    public static final String IS_SMALL_BLIND = "isSmallBlind";
    public static final String MAX_AMOUNT = "maxAmount";
    public static final String CALL_AMOUNT = "callAmount";
    public static final String MIN_AMOUNT = "minAmount";
    public static final String NICKNAME = "nickname";
    public static final String HAND_NAME = "handName";
    public static final String NEXT_PLAYER_NICKNAME = "nextPlayerNickname";
    public static final String POT = "pot";
    public static final String NEXT_SEAT_NUMBER = "nextSeatNumber";
    public static final String ONLY_THE_SYSTEM_CAN_CREATE_A_CHANNEL =
        "Bayeux security verification failed. Only the system can create a channel.";
    public static final String PLAYERS = "players";
    public static final String SYSTEM_DENY_ERROR_MESSAGE =
        "Bayeux security verification failed. User is trying to access a public channel " + "which is not his.";
    public static final String TIME = "time";
    public static final String USER_DENY_MESSAGE =
        "You are not allowed to access this channel. Your IP is being sent to the security "
        + "department for investigation.";
}
