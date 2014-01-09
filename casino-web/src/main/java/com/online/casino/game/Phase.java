/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.game;

/**
 * User: Bjorn Harvold
 * Date: Jan 4, 2010
 * Time: 4:38:32 PM
 * Responsibility:
 */
public enum Phase {
    GET_PLAYERS,
    GET_PLAYERS_COMPLETE,
    FIND_A_GAME,
    FIND_A_GAME_COMPLETE,
    OBSERVE_GAME,
    OBSERVE_GAME_COMPLETE,
    JOIN_GAME,
    JOIN_GAME_COMPLETE,
    DEAL_NEW_HAND,
    DEAL_NEW_HAND_COMPLETE,
    PLACE_BET_FOR_POCKET_CARDS,
    PLACE_BET_FOR_POCKET_CARDS_COMPLETE,
    DEAL_FLOP_CARD,
    DEAL_FLOP_CARD_COMPLETE,
    PLACE_BET_FOR_FLOP_CARD,
    PLACE_BET_FOR_FLOP_CARD_COMPLETE,
    DEAL_TURN_CARD,
    DEAL_TURN_CARD_COMPLETE,
    PLACE_BET_FOR_TURN_CARD,
    PLACE_BET_FOR_TURN_CARD_COMPLETE,
    DEAL_RIVER_CARD,
    DEAL_RIVER_CARD_COMPLETE,
    PLACE_BET_FOR_RIVER_CARD,
    PLACE_BET_FOR_RIVER_CARD_COMPLETE,
    SHOWDOWN,
    SHOWDOWN_COMPLETE,
    LEAVE_GAME, LEAVE_GAME_COMPLETE, COMPLETE
}
