/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service.impl;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.dto.broadcast.BigBlindRequiredBroadcast;
import com.online.casino.domain.dto.broadcast.CallBroadcast;
import com.online.casino.domain.dto.broadcast.CheckBroadcast;
import com.online.casino.domain.dto.broadcast.EndGameBroadcast;
import com.online.casino.domain.dto.broadcast.EventFailureBroadcast;
import com.online.casino.domain.dto.broadcast.FlopBroadcast;
import com.online.casino.domain.dto.broadcast.FoldBroadcast;
import com.online.casino.domain.dto.broadcast.GamblerNewHand;
import com.online.casino.domain.dto.broadcast.GamblerShowdown;
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
import com.online.casino.service.PokerGameBroadcastService;
import com.online.casino.service.cometd.GameChannelInitializer;
import com.online.casino.service.cometd.UserChannelInitializer;
import org.apache.commons.lang.StringUtils;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.online.casino.service.cometd.CometConstants.*;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 12/25/10
 * Time: 9:41 PM
 * Responsibility: This service is responsible for broadcasting game events to cometd channels
 * so players who are subscribing to a game can get updates about the game
 */
@Singleton
@Named
@Service("pokergameBroadcastService")
public class PokerGameBroadcastServiceImpl implements PokerGameBroadcastService {

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(PokerGameBroadcastServiceImpl.class);

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @Inject
    private BayeuxServer bayeuxServer;

    /**
     * Field description
     */
    @Session
    private ServerSession serverSession;

    //~--- methods ------------------------------------------------------------

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void broadcastBigBlindRequired(BigBlindRequiredBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting big blind required to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createWaitingForBigBlindEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastCall(CallBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting CALL to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createCallEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastCheck(CheckBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting CHECK to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createCheckEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastEndGame(EndGameBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting showdown to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createEndGameEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastEventFailure(EventFailureBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting FAILURE to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createFailureEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void broadcastFlop(FlopBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting dealing flop to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createDealingFlopEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastFold(FoldBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting FOLD to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createFoldEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastGameFailure(GameFailureBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting GAME FAILURE to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createGameFailureEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastLeave(LeaveBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting LEAVE to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createLeaveEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void broadcastNewHand(NewHandBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting new hand status to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createDealNewHandEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void broadcastPlayerResponseRequired(PlayerResponseRequiredBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting player response required to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPlayerResponseRequiredEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void broadcastPocketCards(PocketCardsBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting dealing pocket cards to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createDealingPocketCardsEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastPostBigBlind(PostBigBlindBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting posting big blind to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPostBigBlindEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastPostSmallBlind(PostSmallBlindBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting posting small blind to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPostSmallBlindEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastQueuePlayer(QueuePlayerBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting queue player to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createQueuePlayerEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastRaise(RaiseBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting RAISE to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createRaiseEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastReQueuePlayer(ReQueuePlayerBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting queue player to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createReQueuePlayerEvent(broadcast);

        channel.publish(null, map, null);
    }

    @Override
    public void broadcastLeaveQueue(LeaveQueueBroadcast broadcast) {
        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting LEAVE to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createLeaveQueueEvent(broadcast);

        channel.publish(null, map, null);
    }

    private Map<String, Object> createLeaveQueueEvent(LeaveQueueBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(NICKNAME, broadcast.getNickname());

        return result;
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void broadcastRiver(RiverBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting dealing river to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createDealingRiverEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastShowdown(ShowdownBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting showdown to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createShowdownEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void broadcastSmallBlindRequired(SmallBlindRequiredBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting small blind required to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createWaitingForSmallBlindEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     *
     * @param broadcast broadcast
     */
    @Override
    public void broadcastTimeout(TimeoutBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting timeout to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createTimeoutEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void broadcastTurn(TurnBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createGameChannelIfAbsent(broadcast.getPokergameId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting dealing turn to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createDealingTurnEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void privateBroadcastBigBlindRequired(PrivateBigBlindRequiredBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting big blind post is required to private channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateBigBlindRequiredEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastCall(PrivateCallBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting CALL to private channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateCallEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastCheck(PrivateCheckBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting CHECK to private channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateCheckEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastEventFailure(PrivateEventFailureBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting FAILURE to private channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateFailureEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void privateBroadcastFlop(PrivateFlopBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting FLOP to private channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateFlopEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastFold(PrivateFoldBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting FOLD to private channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateFoldEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastLeave(PrivateLeaveBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting LEAVE to private channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateLeaveEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void privateBroadcastNewHand(PrivateNewHandBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player that a new hand is dealt to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateNewHandEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastPlayerResponseRequired(PrivatePlayerResponseRequiredBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player that a response is required to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivatePlayerResponseRequiredEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void privateBroadcastPocketCards(PrivatePocketCardsBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player his pocket cards to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivatePocketCardsEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastPostBigBlind(PrivatePostBigBlindBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player that he successfully posted big blind to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivatePostBigBlindEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastPostSmallBlind(PrivatePostSmallBlindBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player that he successfully posted small blind to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivatePostSmallBlindEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastQueuePlayer(PrivateQueuePlayerBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player that he successfully queued himself to game to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateQueuePlayerEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastRaise(PrivateRaiseBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting RAISE to private channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateRaiseEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastReQueuePlayer(PrivateReQueuePlayerBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player that he successfully queued himself to game to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateReQueuePlayerEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void privateBroadcastRiver(PrivateRiverBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player his river cards to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateRiverEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void privateBroadcastSmallBlindRequired(PrivateSmallBlindRequiredBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player that small blind post is required to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateSmallBlindRequiredEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     */
    @Override
    public void privateBroadcastTimeout(PrivateTimeoutBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player that he successfully posted small blind to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateTimeoutEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * @param broadcast broadcast
     * @inheritDoc
     */
    @Override
    public void privateBroadcastTurn(PrivateTurnBroadcast broadcast) {

        // in case we haven't created it yet
        String channelName = createUserChannelIfAbsent(broadcast.getApplicationUserId());

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to player his turn cards to channel: " + channelName);
        }

        // retrieve channel for pokergame
        ServerChannel       channel = bayeuxServer.getChannel(channelName);
        Map<String, Object> map     = createPrivateTurnEvent(broadcast);

        channel.publish(null, map, null);
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createCallEvent(CallBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(AMOUNT, broadcast.getAmount());
        result.put(NICKNAME, broadcast.getPlayerNickname());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(POT, broadcast.getPot());

        if (!StringUtils.isBlank(broadcast.getNextPlayerNickname())) {
            result.put(NEXT_PLAYER_NICKNAME, broadcast.getNextPlayerNickname());
            result.put(NEXT_SEAT_NUMBER, broadcast.getNextSeatNumber());
        }

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createCheckEvent(CheckBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(AMOUNT, broadcast.getAmount());
        result.put(NICKNAME, broadcast.getPlayerNickname());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(POT, broadcast.getPot());

        if (!StringUtils.isBlank(broadcast.getNextPlayerNickname())) {
            result.put(NEXT_PLAYER_NICKNAME, broadcast.getNextPlayerNickname());
            result.put(NEXT_SEAT_NUMBER, broadcast.getNextSeatNumber());
        }

        return result;
    }

    /**
     * Will create a map of data for when a new hand is dealt
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createDealNewHandEvent(NewHandBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(PLAYERS, createGamblers(broadcast.getGamblers()));

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createDealingFlopEvent(FlopBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        Map<String, Object> cards = new HashMap<String, Object>();

        cards.put(CARD_1, broadcast.getCard1());
        cards.put(CARD_2, broadcast.getCard2());
        cards.put(CARD_3, broadcast.getCard3());
        result.put(CARDS, cards);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createDealingPocketCardsEvent(PocketCardsBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createDealingRiverEvent(RiverBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        Map<String, Object> cards = new HashMap<String, Object>();

        cards.put(CARD_1, broadcast.getCard1());
        cards.put(CARD_2, broadcast.getCard2());
        cards.put(CARD_3, broadcast.getCard3());
        cards.put(CARD_4, broadcast.getCard4());
        cards.put(CARD_5, broadcast.getCard5());
        result.put(CARDS, cards);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createDealingTurnEvent(TurnBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        Map<String, Object> cards = new HashMap<String, Object>();

        cards.put(CARD_1, broadcast.getCard1());
        cards.put(CARD_2, broadcast.getCard2());
        cards.put(CARD_3, broadcast.getCard3());
        cards.put(CARD_4, broadcast.getCard4());
        result.put(CARDS, cards);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createEndGameEvent(EndGameBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(NICKNAME, broadcast.getNickname());
        result.put(AMOUNT, broadcast.getAmount());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createFailureEvent(EventFailureBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(MESSAGE, broadcast.getMessage());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createFoldEvent(FoldBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(AMOUNT, broadcast.getAmount());
        result.put(NICKNAME, broadcast.getPlayerNickname());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(POT, broadcast.getPot());

        if (!StringUtils.isBlank(broadcast.getNextPlayerNickname())) {
            result.put(NEXT_PLAYER_NICKNAME, broadcast.getNextPlayerNickname());
            result.put(NEXT_SEAT_NUMBER, broadcast.getNextSeatNumber());
        }

        return result;
    }

    /**
     * Creates a list of gambler info
     *
     * @param gambler gambler
     * @return Return value
     */
    private Map<String, Object> createGambler(GamblerNewHand gambler) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (gambler != null) {
            result.put(NICKNAME, gambler.getNickname());
            result.put(SEAT_NUMBER, gambler.getSeatNumber());
            result.put(AMOUNT, gambler.getAmount());
            result.put(AVATAR, gambler.getAvatarUrl());
            result.put(IS_DEALER, gambler.getDealer());
            result.put(IS_SMALL_BLIND, gambler.getSmallBlind());
            result.put(IS_BIG_BLIND, gambler.getBigBlind());
        }

        return result;
    }

    /**
     * Method description
     *
     * @param gambler gambler
     * @return Return value
     */
    private Map<String, Object> createGamblerShowdown(GamblerShowdown gambler) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (gambler != null) {
            result.put(HAND_NAME, gambler.getHandName());
            result.put(NICKNAME, gambler.getNickname());
            result.put(AMOUNT, gambler.getAmount());

            Map<String, Object> cards = new HashMap<String, Object>();

            cards.put(CARD_1, gambler.getCard1());
            cards.put(CARD_2, gambler.getCard2());
            cards.put(CARD_3, gambler.getCard3());
            cards.put(CARD_4, gambler.getCard4());
            cards.put(CARD_5, gambler.getCard5());
            result.put(CARDS, cards);
        }

        return result;
    }

    /**
     * Method description
     *
     * @param gamblers gamblers
     * @return Return value
     */
    private List<Object> createGamblerShowdowns(List<GamblerShowdown> gamblers) {
        List<Object> result = new ArrayList<Object>();

        if (gamblers != null) {
            for (GamblerShowdown gambler : gamblers) {
                result.add(createGamblerShowdown(gambler));
            }
        }

        return result;
    }

    /**
     * Create a map of gambler info
     *
     * @param gamblers gamblers
     * @return Return value
     */
    private List<Object> createGamblers(List<GamblerNewHand> gamblers) {
        List<Object> result = new ArrayList<Object>();

        if (gamblers != null) {
            for (GamblerNewHand gambler : gamblers) {
                result.add(createGambler(gambler));
            }
        }

        return result;
    }

    /**
     * createGameChannelIfAbsent creates a dedicated channel to a pokergame.
     * <p/>
     * E.g.:
     * - /service/game/1/foo
     * - /service/game/1/bar
     *
     * @param pokergameNaturalKey pokergameId
     * @return Return value
     */
    private String createGameChannelIfAbsent(String pokergameNaturalKey) {
        String channelName = GAME_ROOT_CHANNEL + "/" + pokergameNaturalKey;

        // only create channel if it doesn't exist yet
        boolean result = bayeuxServer.createIfAbsent(channelName, new GameChannelInitializer());

        if (result) {
            if (log.isDebugEnabled()) {
                log.debug("Created new channel:" + channelName);
            }
        }

        return channelName;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createGameFailureEvent(GameFailureBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(MESSAGE, broadcast.getMessage());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createLeaveEvent(LeaveBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(AMOUNT, broadcast.getAmount());
        result.put(NICKNAME, broadcast.getPlayerNickname());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(POT, broadcast.getPot());

        if (!StringUtils.isBlank(broadcast.getNextPlayerNickname())) {
            result.put(NEXT_PLAYER_NICKNAME, broadcast.getNextPlayerNickname());
            result.put(NEXT_SEAT_NUMBER, broadcast.getNextSeatNumber());
        }

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPlayerResponseRequiredEvent(PlayerResponseRequiredBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(NICKNAME, broadcast.getNickname());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPostBigBlindEvent(PostBigBlindBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(AMOUNT, broadcast.getAmount());
        result.put(NICKNAME, broadcast.getPlayerNickname());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(POT, broadcast.getPot());

        if (!StringUtils.isBlank(broadcast.getNextPlayerNickname())) {
            result.put(NEXT_PLAYER_NICKNAME, broadcast.getNextPlayerNickname());
            result.put(NEXT_SEAT_NUMBER, broadcast.getNextSeatNumber());
        }

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPostSmallBlindEvent(PostSmallBlindBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(AMOUNT, broadcast.getAmount());
        result.put(NICKNAME, broadcast.getPlayerNickname());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(POT, broadcast.getPot());

        if (!StringUtils.isBlank(broadcast.getNextPlayerNickname())) {
            result.put(NEXT_PLAYER_NICKNAME, broadcast.getNextPlayerNickname());
            result.put(NEXT_SEAT_NUMBER, broadcast.getNextSeatNumber());
        }

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateBigBlindRequiredEvent(PrivateBigBlindRequiredBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(TIME, broadcast.getTime());
        result.put(AMOUNT, broadcast.getAmount());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateCallEvent(PrivateCallBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateCheckEvent(PrivateCheckBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateFailureEvent(PrivateEventFailureBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent().name());
        result.put(MESSAGE, broadcast.getMessage());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateFlopEvent(PrivateFlopBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        Map<String, Object> cards = new HashMap<String, Object>();

        cards.put(CARD_1, broadcast.getCard1());
        cards.put(CARD_2, broadcast.getCard2());
        cards.put(CARD_3, broadcast.getCard3());
        cards.put(CARD_4, broadcast.getCard4());
        cards.put(CARD_5, broadcast.getCard5());
        result.put(CARDS, cards);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateFoldEvent(PrivateFoldBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateLeaveEvent(PrivateLeaveBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateNewHandEvent(PrivateNewHandBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(GAMBLER_ID, broadcast.getGamblerId());
        result.put(IS_DEALER, broadcast.getDealer());
        result.put(IS_SMALL_BLIND, broadcast.getSmallBlind());
        result.put(IS_BIG_BLIND, broadcast.getBigBlind());
        result.put(AMOUNT, broadcast.getAmount());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String,
                Object> createPrivatePlayerResponseRequiredEvent(PrivatePlayerResponseRequiredBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(TIME, broadcast.getTime());
        result.put(MAX_AMOUNT, broadcast.getMaxRaiseAmount());
        result.put(CALL_AMOUNT, broadcast.getCallAmount());
        result.put(MIN_AMOUNT, broadcast.getMinRaiseAmount());
        result.put(AVAILABLE_ACTIONS, broadcast.getAvailableMoves());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivatePocketCardsEvent(PrivatePocketCardsBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        Map<String, Object> cards = new HashMap<String, Object>();

        cards.put(CARD_1, broadcast.getCard1());
        cards.put(CARD_2, broadcast.getCard2());
        result.put(CARDS, cards);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivatePostBigBlindEvent(PrivatePostBigBlindBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivatePostSmallBlindEvent(PrivatePostSmallBlindBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateQueuePlayerEvent(PrivateQueuePlayerBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateRaiseEvent(PrivateRaiseBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateReQueuePlayerEvent(PrivateReQueuePlayerBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateRiverEvent(PrivateRiverBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        Map<String, Object> cards = new HashMap<String, Object>();

        cards.put(CARD_1, broadcast.getCard1());
        cards.put(CARD_2, broadcast.getCard2());
        cards.put(CARD_3, broadcast.getCard3());
        cards.put(CARD_4, broadcast.getCard4());
        cards.put(CARD_5, broadcast.getCard5());
        cards.put(CARD_6, broadcast.getCard6());
        cards.put(CARD_7, broadcast.getCard7());
        result.put(CARDS, cards);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateSmallBlindRequiredEvent(PrivateSmallBlindRequiredBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(TIME, broadcast.getTime());
        result.put(AMOUNT, broadcast.getAmount());

        return result;
    }

    /**
     * Method description
     *
     *
     * @param broadcast broadcast
     *
     * @return Return value
     */
    private Map<String, Object> createPrivateTimeoutEvent(PrivateTimeoutBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(STATUS, SUCCESS);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createPrivateTurnEvent(PrivateTurnBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());

        Map<String, Object> cards = new HashMap<String, Object>();

        cards.put(CARD_1, broadcast.getCard1());
        cards.put(CARD_2, broadcast.getCard2());
        cards.put(CARD_3, broadcast.getCard3());
        cards.put(CARD_4, broadcast.getCard4());
        cards.put(CARD_5, broadcast.getCard5());
        cards.put(CARD_6, broadcast.getCard6());
        result.put(CARDS, cards);

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createQueuePlayerEvent(QueuePlayerBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(PLAYER_ID, broadcast.getPlayerId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(BUYIN, broadcast.getBuyin());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(MUST_HAVE_SEAT, broadcast.getMustHaveSeat());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createRaiseEvent(RaiseBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(AMOUNT, broadcast.getAmount());
        result.put(NICKNAME, broadcast.getPlayerNickname());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(POT, broadcast.getPot());

        if (!StringUtils.isBlank(broadcast.getNextPlayerNickname())) {
            result.put(NEXT_PLAYER_NICKNAME, broadcast.getNextPlayerNickname());
            result.put(NEXT_SEAT_NUMBER, broadcast.getNextSeatNumber());
        }

        return result;
    }

    /**
     * Method description
     *
     *
     * @param broadcast broadcast
     *
     * @return Return value
     */
    private Map<String, Object> createReQueuePlayerEvent(ReQueuePlayerBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(PLAYER_ID, broadcast.getPlayerId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(BUYIN, broadcast.getBuyin());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());
        result.put(MUST_HAVE_SEAT, broadcast.getMustHaveSeat());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createShowdownEvent(ShowdownBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(WINNERS, createGamblerShowdowns(broadcast.getGamblers()));

        return result;
    }

    /**
     * Method description
     *
     *
     * @param broadcast broadcast
     *
     * @return Return value
     */
    private Map<String, Object> createTimeoutEvent(TimeoutBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(NICKNAME, broadcast.getPlayerNickname());
        result.put(SEAT_NUMBER, broadcast.getSeatNumber());

        return result;
    }

    /**
     * This method really does nothing as the channel should already exist and be configured
     *
     * @param userNaturalKey userNaturalKey
     * @return Return value
     */
    private String createUserChannelIfAbsent(final String userNaturalKey) {
        String  channelName = USER_ROOT_CHANNEL + "/event/" + userNaturalKey;
        boolean result      = bayeuxServer.createIfAbsent(channelName, new UserChannelInitializer());

        if (log.isDebugEnabled()) {
            log.debug(channelName + " already existed: " + result);
        }

        return channelName;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createWaitingForBigBlindEvent(BigBlindRequiredBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(NICKNAME, broadcast.getNickname());

        return result;
    }

    /**
     * Method description
     *
     * @param broadcast broadcast
     * @return Return value
     */
    private Map<String, Object> createWaitingForSmallBlindEvent(SmallBlindRequiredBroadcast broadcast) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(EVENT, broadcast.getEvent());
        result.put(HAND_ID, broadcast.getHandId());
        result.put(POKER_GAME_ID, broadcast.getPokergameId());
        result.put(NICKNAME, broadcast.getNickname());

        return result;
    }

    /**
     * Removes the cometd channel as the pokergame has been removed
     *
     * @param pokergameId pokergameId
     */
    private void removeChannel(Long pokergameId) {
        String channelName = "/service/game/" + pokergameId.toString();

        if (log.isDebugEnabled()) {
            log.debug("Attempting to remove cometd channel: " + channelName);
        }

        ServerChannel channel = bayeuxServer.getChannel(channelName);

        if (channel != null) {
            channel.remove();

            if (log.isDebugEnabled()) {
                log.debug("Cometd channel: " + channelName + " removed");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Cometd channel: " + channelName + " could not be removed as it doesn't exist");
            }
        }
    }
}
