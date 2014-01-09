/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.enums;

/**
 * Created by Bjorn Harvold
 * Date: 12/28/10
 * Time: 5:43 PM
 * Responsibility:
 */
public enum GameEvent {
    WAITING_FOR_BIG_BLIND_EVENT,
    DEALING_FLOP_EVENT,
    DEALING_POCKET_CARDS_EVENT,
    WAITING_FOR_SMALL_BLIND_EVENT,
    DEALING_RIVER_EVENT,
    DEALING_TURN_EVENT,
    PLAYER_RESPONSE_REQUIRED_EVENT,
    FAILURE_EVENT,
    DEALING_HAND_EVENT,
    SHOWDOWN_EVENT,
    END_GAME_EVENT,
    CALL_EVENT,
    CHECK_EVENT,
    RAISE_EVENT,
    FOLD_EVENT,
    LEAVE_EVENT,
    QUEUE_PLAYER_EVENT,
    REQUEUE_PLAYER_EVENT,
    POST_SMALL_BLIND_EVENT,
    POST_BIG_BLIND_EVENT,
    LEAVE_QUEUE_EVENT, TIMEOUT_EVENT
}
