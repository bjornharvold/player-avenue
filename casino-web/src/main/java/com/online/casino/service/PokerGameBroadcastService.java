package com.online.casino.service;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.dto.broadcast.BigBlindRequiredBroadcast;
import com.online.casino.domain.dto.broadcast.CallBroadcast;
import com.online.casino.domain.dto.broadcast.CheckBroadcast;
import com.online.casino.domain.dto.broadcast.EndGameBroadcast;
import com.online.casino.domain.dto.broadcast.EventFailureBroadcast;
import com.online.casino.domain.dto.broadcast.FlopBroadcast;
import com.online.casino.domain.dto.broadcast.FoldBroadcast;
import com.online.casino.domain.dto.broadcast.GameFailureBroadcast;
import com.online.casino.domain.dto.broadcast.LeaveBroadcast;
import com.online.casino.domain.dto.broadcast.LeaveQueueBroadcast;
import com.online.casino.domain.dto.broadcast.NewHandBroadcast;
import com.online.casino.domain.dto.broadcast.PlayerResponseRequiredBroadcast;
import com.online.casino.domain.dto.broadcast.PocketCardsBroadcast;
import com.online.casino.domain.dto.broadcast.PostBigBlindBroadcast;
import com.online.casino.domain.dto.broadcast.PostSmallBlindBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateBigBlindRequiredBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateCallBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateCheckBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateEventFailureBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateFlopBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateFoldBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateLeaveBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateNewHandBroadcast;
import com.online.casino.domain.dto.broadcast.PrivatePlayerResponseRequiredBroadcast;
import com.online.casino.domain.dto.broadcast.PrivatePocketCardsBroadcast;
import com.online.casino.domain.dto.broadcast.PrivatePostBigBlindBroadcast;
import com.online.casino.domain.dto.broadcast.PrivatePostSmallBlindBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateQueuePlayerBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateRaiseBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateReQueuePlayerBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateRiverBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateSmallBlindRequiredBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateTimeoutBroadcast;
import com.online.casino.domain.dto.broadcast.PrivateTurnBroadcast;
import com.online.casino.domain.dto.broadcast.QueuePlayerBroadcast;
import com.online.casino.domain.dto.broadcast.RaiseBroadcast;
import com.online.casino.domain.dto.broadcast.ReQueuePlayerBroadcast;
import com.online.casino.domain.dto.broadcast.RiverBroadcast;
import com.online.casino.domain.dto.broadcast.ShowdownBroadcast;
import com.online.casino.domain.dto.broadcast.SmallBlindRequiredBroadcast;
import com.online.casino.domain.dto.broadcast.TimeoutBroadcast;
import com.online.casino.domain.dto.broadcast.TurnBroadcast;

//~--- interfaces -------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 12/29/10
 * Time: 1:45 AM
 * Responsibility: Handles all game related broadcasts to cometd channel
 */
public interface PokerGameBroadcastService {

    /**
     * Broadcasts to the game channel that BB is required by player x
     *
     *
     * @param broadcast broadcast
     */
    void broadcastBigBlindRequired(BigBlindRequiredBroadcast broadcast);

    /**
     * Broadcasts to the game channel that the flop occurred
     *
     *
     * @param broadcast broadcast
     */
    void broadcastFlop(FlopBroadcast broadcast);

    /**
     * Broadcasts to the game channel that a new hand was dealt
     *
     *
     * @param broadcast broadcast
     */
    void broadcastNewHand(NewHandBroadcast broadcast);

    /**
     * Broadcasts to the game channel that a bet is required by player x
     *
     *
     * @param broadcast broadcast
     */
    void broadcastPlayerResponseRequired(PlayerResponseRequiredBroadcast broadcast);

    /**
     *  Broadcasts to the game channel that pocket cards were dealt
     *
     * @param broadcast broadcast
     */
    void broadcastPocketCards(PocketCardsBroadcast broadcast);

    /**
     * Broadcasts to the game channel that river card was dealt
     *
     * @param broadcast broadcast
     */
    void broadcastRiver(RiverBroadcast broadcast);

    /**
     * Broadcasts to the game channel that SB is required
     *
     *
     * @param broadcast broadcast
     */
    void broadcastSmallBlindRequired(SmallBlindRequiredBroadcast broadcast);

    /**
     * Broadcasts to the game channel that turn card was dealt
     *
     * @param broadcast broadcast
     */
    void broadcastTurn(TurnBroadcast broadcast);

    /**
     * Sends a private broadcast to the BB player that it is time for action
     *
     * @param broadcast broadcast
     */
    void privateBroadcastBigBlindRequired(PrivateBigBlindRequiredBroadcast broadcast);

    /**
     * Sends a private broadcast to all gamblers with additional information
     *
     * @param broadcast broadcast
     */
    void privateBroadcastFlop(PrivateFlopBroadcast broadcast);

    /**
     * Sends a private broadcast to all gamblers with their new hand
     *
     *
     * @param broadcast broadcast
     */
    void privateBroadcastNewHand(PrivateNewHandBroadcast broadcast);

    /**
     * Sends a private broadcast to player informing him what his pocket cards are
     *
     * @param broadcast broadcast
     */
    void privateBroadcastPocketCards(PrivatePocketCardsBroadcast broadcast);

    /**
     * Sends a private broadcast to all gamblers with additional information
     *
     *
     * @param broadcast broadcast
     */
    void privateBroadcastRiver(PrivateRiverBroadcast broadcast);

    /**
     * Sends a private broadcast to the SB player that it is time for action
     *
     * @param broadcast broadcast
     */
    void privateBroadcastSmallBlindRequired(PrivateSmallBlindRequiredBroadcast broadcast);

    /**
     * Sends a private broadcast to all gamblers with additional information
     *
     * @param broadcast broadcast
     */
    void privateBroadcastTurn(PrivateTurnBroadcast broadcast);

    /**
     * Broadcasts to the game channel that a call was made
     * @param broadcast broadcast
     */
    void broadcastCall(CallBroadcast broadcast);

    /**
     * Sends private broadcast to caller
     * @param broadcast broadcast
     */
    void privateBroadcastCall(PrivateCallBroadcast broadcast);

    void privateBroadcastEventFailure(PrivateEventFailureBroadcast broadcast);

    void broadcastEventFailure(EventFailureBroadcast broadcast);

    void privateBroadcastCheck(PrivateCheckBroadcast broadcast);

    void broadcastCheck(CheckBroadcast broadcast);

    void privateBroadcastRaise(PrivateRaiseBroadcast broadcast);

    void broadcastRaise(RaiseBroadcast broadcast);

    void privateBroadcastFold(PrivateFoldBroadcast broadcast);

    void broadcastFold(FoldBroadcast broadcast);

    void broadcastGameFailure(GameFailureBroadcast broadcast);

    void privateBroadcastLeave(PrivateLeaveBroadcast broadcast);

    void broadcastLeave(LeaveBroadcast broadcast);

    void privateBroadcastPostBigBlind(PrivatePostBigBlindBroadcast broadcast);

    void broadcastPostBigBlind(PostBigBlindBroadcast broadcast);

    void privateBroadcastPostSmallBlind(PrivatePostSmallBlindBroadcast broadcast);

    void broadcastPostSmallBlind(PostSmallBlindBroadcast broadcast);

    void privateBroadcastQueuePlayer(PrivateQueuePlayerBroadcast broadcast);

    void broadcastQueuePlayer(QueuePlayerBroadcast broadcast);

    void privateBroadcastPlayerResponseRequired(PrivatePlayerResponseRequiredBroadcast broadcast);

    void broadcastShowdown(ShowdownBroadcast broadcast);

    void broadcastEndGame(EndGameBroadcast broadcast);

    void broadcastTimeout(TimeoutBroadcast broadcast);

    void privateBroadcastTimeout(PrivateTimeoutBroadcast broadcast);

    void privateBroadcastReQueuePlayer(PrivateReQueuePlayerBroadcast broadcast);

    void broadcastReQueuePlayer(ReQueuePlayerBroadcast broadcast);

    void broadcastLeaveQueue(LeaveQueueBroadcast broadcast);
}
