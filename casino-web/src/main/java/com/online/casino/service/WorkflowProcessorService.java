/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Hand;
import com.online.casino.service.fsm.event.CallEvent;
import com.online.casino.service.fsm.event.CheckEvent;
import com.online.casino.service.fsm.event.FoldEvent;
import com.online.casino.service.fsm.event.LeaveEvent;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import com.online.casino.service.fsm.event.RaiseEvent;
import com.online.casino.service.fsm.event.ReQueuePlayerEvent;

//~--- interfaces -------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 12/24/10
 * Time: 9:26 PM
 * Responsibility: This service is mostly a wrapper around game service calls that the FSN
 * model makes. It is also responsible for interacting with the BayeuxServer and create and delete channels
 * for the game as it sees necessary.
 */
public interface WorkflowProcessorService {

    /**
     * Method description
     *
     * @param event event
     *
     *
     */
    void doCall(CallEvent event);

    /**
     * Method description
     *
     * @param event event
     *
     *
     */
    void doCheck(CheckEvent event);

    /**
     * A hand is created with the game engine. Then this service sets up
     * cometd channels for the game and for the players.
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     *
     */
    Hand doDealNewHand(String pokergameId);

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param handId handId
     *
     *
     */
    void doEndGame(String pokergameId, String handId);

    /**
     * Method description
     *
     * @param event event
     *
     *
     */
    void doFold(FoldEvent event);

    /**
     * Method description
     *
     * @param event event
     *
     *
     */
    void doLeaveGame(LeaveEvent event);

    /**
     * Method description
     *
     *
     * @param handId handId
     */
    void doPlayerResponseRequired(String handId);

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param handId handId
     */
    void doPostBigBlind(String pokergameId, String handId);

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param handId handId
     */
    void doPostSmallBlind(String pokergameId, String handId);

    /**
     * Method description
     *
     *
     *
     * @param pokergameId pokergameId
     * @param handId handId
     *
     *
     */
    void doProgressHand(String pokergameId, String handId);

    /**
     * Method description
     *
     * @param event event
     *
     *
     */
    void doQueuePlayer(QueuePlayerEvent event);

    /**
     * Method description
     *
     * @param event event
     *
     *
     */
    void doRaise(RaiseEvent event);

    /**
     * Method description
     *
     *
     * @param event event
     */
    void doReQueuePlayer(ReQueuePlayerEvent event);

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param handId handId
     *
     */
    void doTimeout(String pokergameId, String handId);

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     *
     *
     */
    Hand getCurrentHand(String pokergameId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     *
     *
     */
    boolean isBettable(String handId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     *
     *
     */
    boolean isBigBlindPostable(String handId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     *
     */
    boolean isCallable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     *
     */
    boolean isCheckable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     *
     */
    boolean isCurrentGambler(String gamblerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     *
     *
     */
    boolean isEndGame(String handId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     *
     */
    boolean isFoldable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     *
     */
    boolean isGamblerActive(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     *
     */
    boolean isLeaveable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     *
     *
     */
    boolean isProgressable(String handId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     *
     */
    boolean isRaiseable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     *
     *
     */
    boolean isSmallBlindPostable(String handId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     *
     *
     */
    boolean isStartable(String pokergameId);
}
