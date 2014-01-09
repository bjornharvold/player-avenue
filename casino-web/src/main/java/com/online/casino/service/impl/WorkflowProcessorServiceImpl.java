/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.service.impl;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.dto.HandSnapshot;
import com.online.casino.domain.dto.broadcast.BigBlindRequiredBroadcast;
import com.online.casino.domain.dto.broadcast.CallBroadcast;
import com.online.casino.domain.dto.broadcast.CheckBroadcast;
import com.online.casino.domain.dto.broadcast.EndGameBroadcast;
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
import com.online.casino.domain.entity.Bet;
import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.GamblerAccountEntry;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.QueuedGambler;
import com.online.casino.domain.enums.GamblerStatus;
import com.online.casino.domain.enums.GameAction;
import com.online.casino.exception.GameException;
import com.online.casino.service.PokerGameBroadcastService;
import com.online.casino.service.PokerGameService;
import com.online.casino.service.WorkflowProcessorService;
import com.online.casino.service.fsm.event.AbstractEvent;
import com.online.casino.service.fsm.event.CallEvent;
import com.online.casino.service.fsm.event.CheckEvent;
import com.online.casino.service.fsm.event.FoldEvent;
import com.online.casino.service.fsm.event.LeaveEvent;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import com.online.casino.service.fsm.event.RaiseEvent;
import com.online.casino.service.fsm.event.ReQueuePlayerEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 12/24/10
 * Time: 9:19 PM
 * Responsibility: Apart from the PokerGameService, this service class has a lot of responsibility. This
 * is the main interface the Finite State Machine has to the poker game. It is also the main interface for
 * the players to interact with the game. When an FSM event is triggered, either by the player or by the state model,
 * it will call one of the methods here. This service throws no exception back to the caller as the caller many
 * times is the system itself and it has no way to reach the caller who needs to see the error message. Instead, it
 * propagates the error forward to the comet channel.
 * TODO: Look into extending the workflow here as the demands grow
 * <p/>
 * These are the two most common workflows:
 * <p/>
 * 1.
 * 1a. System deals a common card
 * 1b. System broadcasts the card on the public game channel
 * 1c. System broadcasts to the first player that it's time for an action
 * <p/>
 * 2.
 * 2a. Player submits an event
 * 2b. System handles event
 * 2c. System broadcasts event on public game channel
 * 2d. System broadcasts confirmation event on private player channel
 * 2e. System broadcasts to the next player that it's time for action.
 */
@Service("workflowProcessorService")
public class WorkflowProcessorServiceImpl implements WorkflowProcessorService {

    /**
     * Field description
     */
    private static final String CARD_DELIMITER = " ";

    /**
     * Field description
     */
    private static final Logger log = LoggerFactory.getLogger(WorkflowProcessorServiceImpl.class);

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @Value("${player.response.timeout.in.seconds}")
    private Integer playerResponseTimeoutInSeconds;

    /**
     * Field description
     */
    private final PokerGameBroadcastService pokerGameBroadcastService;

    /**
     * Field description
     */
    private final PokerGameService pokerGameService;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokerGameService          pokerGameService
     * @param pokerGameBroadcastService pokerGameBroadcastService
     */
    @Autowired
    public WorkflowProcessorServiceImpl(PokerGameService pokerGameService,
                                        PokerGameBroadcastService pokerGameBroadcastService) {
        this.pokerGameService = pokerGameService;
        this.pokerGameBroadcastService = pokerGameBroadcastService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * If the call is successful we will broadcast to the game channel and the caller
     * If the call is not successful, we will an an error message to the game channel and the caller.
     * <p/>
     * This method also has to do some extra work. Instead of relying on the FSM to take care
     * of method broadcasting AFTER someone has executed an action like CALL, we do double duty work
     * here and broadcast to the next player that a response is required.
     *
     * @param event event
     * @inheritDoc
     */
    @Override
    public void doCall(CallEvent event) {
        try {
            pokerGameService.doCall(event.getGamblerId());

            // broadcast success messages to channels
            handleBroadcastCall(event);
        } catch (GameException e) {
            handleBroadcastEventFailure(event, e);
        }
    }

    /**
     * If the check is successful we will broadcast to the game channel and the caller
     * If the check is not successful, we will an an error message to the game channel and the caller.
     * <p/>
     * This method also has to do some extra work. Instead of relying on the FSM to take care
     * of method broadcasting AFTER someone has executed an action like CALL, we do double duty work
     * here and broadcast to the next player that a response is required.
     *
     * @param event event
     */
    @Override
    public void doCheck(CheckEvent event) {
        try {
            pokerGameService.doCheck(event.getGamblerId());

            // broadcast success messages to channels
            handleBroadcastCheck(event);
        } catch (GameException e) {
            handleBroadcastEventFailure(event, e);
        }
    }

    /**
     * @param pokergameId pokergameId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public Hand doDealNewHand(String pokergameId) {
        Hand hand = null;

        try {
            pokerGameService.doDealNewHand(pokergameId);

            // broadcast this
            hand = pokerGameService.findCurrentHand(pokergameId);

            if (hand != null) {
                handleBroadcastNewHand(hand);
            } else {
                log.error("Hand should not be null at this point. Pokergame ID: " + pokergameId);
            }
        } catch (GameException e) {
            handleBroadcastGameFailure(pokergameId, e);
        }

        return hand;
    }

    /**
     * @param pokergameId pokergameId
     * @param handId      handId
     * @inheritDoc
     */
    @Override
    public void doEndGame(String pokergameId, String handId) {
        try {
            pokerGameService.doEndGame(handId);
            handleBroadcastEndGame(pokergameId, handId);
        } catch (GameException e) {
            handleBroadcastGameFailure(pokergameId, e);
        }
    }

    /**
     * If the fold is successful we will broadcast to the game channel and the caller
     * If the fold is not successful, we will an an error message to the game channel and the caller.
     *
     * @param event event
     */
    @Override
    public void doFold(FoldEvent event) {
        try {
            pokerGameService.doFold(event.getGamblerId());

            // broadcast success messages to channels
            handleBroadcastFold(event);
        } catch (GameException e) {
            handleBroadcastEventFailure(event, e);
        }
    }

    /**
     * If the leave is successful we will broadcast to the game channel and the caller
     * If the leave is not successful, we will an an error message to the game channel and the caller.
     *
     * @param event event
     */
    @Override
    public void doLeaveGame(LeaveEvent event) {
        try {
            pokerGameService.doLeaveGame(event.getPokergameId(), event.getPlayerId());

            // broadcast success messages to channels
            handleBroadcastLeave(event);
        } catch (GameException e) {
            handleBroadcastEventFailure(event, e);
        }
    }

    /**
     * Sends a private broadcast to the current gambler that it's time to make a move
     * Sends a public broadcast to the game channel that the table is waiting on a specific gambler
     *
     * @param handId handId
     */
    @Override
    public void doPlayerResponseRequired(String handId) {
        Gambler gambler = pokerGameService.findCurrentGambler(handId);

        // first we broadcast
        handleBroadcastPlayerResponseRequired(gambler);
    }

    /**
     * @param pokergameId pokergameId
     * @param handId      handId
     * @inheritDoc
     */
    @Override
    public void doPostBigBlind(String pokergameId, String handId) {
        try {
            String applicationUserId = pokerGameService.findUserByCurrentGambler(handId);

            pokerGameService.doPostBigBlind(handId);
            handleBroadcastPostBigBlind(pokergameId, handId, applicationUserId);
        } catch (GameException e) {
            handleBroadcastGameFailure(pokergameId, e);
        }
    }

    /**
     * @param pokergameId pokergameId
     * @param handId      handId
     * @inheritDoc
     */
    @Override
    public void doPostSmallBlind(String pokergameId, String handId) {
        try {
            String applicationUserId = pokerGameService.findUserByCurrentGambler(handId);

            pokerGameService.doPostSmallBlind(handId);
            handleBroadcastPostSmallBlind(pokergameId, handId, applicationUserId);
        } catch (GameException e) {
            handleBroadcastGameFailure(pokergameId, e);
        }
    }

    /**
     * @param pokergameId pokergameId
     * @param handId      handId
     * @inheritDoc
     */
    @Override
    public void doProgressHand(String pokergameId, String handId) {
        try {

            // first we just progress the hand
            pokerGameService.doProgressHand(handId);

            // here we need to find the state of the hand and broadcast accordingly
            Hand hand = pokerGameService.findHand(handId);
            Gambler currentGambler = pokerGameService.findCurrentGambler(handId);

            if (hand != null) {

                // based on the status we broadcast different messages
                switch (hand.getStatus()) {
                    case POST_SMALL_BLIND:
                        if (log.isDebugEnabled()) {
                            log.debug("Broadcasting SMALL BLIND to public and private channels");
                        }

                        handleBroadcastSmallBlind(hand, currentGambler);

                        break;

                    case POST_BIG_BLIND:
                        if (log.isDebugEnabled()) {
                            log.debug("Broadcasting BIG BLIND to public and private channels");
                        }

                        handleBroadcastBigBlind(hand, currentGambler);

                        break;

                    case POCKET_CARDS:
                        if (log.isDebugEnabled()) {
                            log.debug("Broadcasting POCKET CARDS to public and private channels");
                        }

                        handleBroadcastPocketCards(hand);

                        break;

                    case FLOP:
                        if (log.isDebugEnabled()) {
                            log.debug("Broadcasting FLOP to public and private channels");
                        }

                        handleBroadcastFlop(hand);

                        break;

                    case TURN:
                        if (log.isDebugEnabled()) {
                            log.debug("Broadcasting TURN to public and private channels");
                        }

                        handleBroadcastTurn(hand);

                        break;

                    case RIVER:
                        if (log.isDebugEnabled()) {
                            log.debug("Broadcasting RIVER to public and private channels");
                        }

                        handleBroadcastRiver(hand);

                        break;

                    case COMPLETE:
                        if (log.isDebugEnabled()) {
                            log.debug("Broadcasting COMPLETE to public and private channels");
                        }

                        handleBroadcastShowdown(hand);

                        break;

                    default:
                        log.error("Unsupported hand status: " + hand.getStatus());
                }
            } else {
                log.error("Hand should not be null at this point. Hand ID: " + handId);
            }
        } catch (GameException e) {
            handleBroadcastGameFailure(handId, e);
        }
    }

    /**
     * @param event event
     * @inheritDoc
     */
    @Override
    public void doQueuePlayer(QueuePlayerEvent event) {
        try {
            pokerGameService.doQueuePlayer(event.getPokergameId(), event.getPlayerId(), event.getSeatNumber(),
                    event.getMustHaveSeat(), event.getBuyin());
            handleBroadcastQueuePlayer(event);
        } catch (GameException e) {
            handleBroadcastEventFailure(event, e);
        }
    }

    /**
     * If the raise is successful we will broadcast to the game channel and the caller
     * If the raise is not successful, we will an an error message to the game channel and the caller.
     *
     * @param event event
     */
    @Override
    public void doRaise(RaiseEvent event) {
        try {
            pokerGameService.doRaise(event.getGamblerId(), event.getAmount());

            // broadcast success messages to channels
            handleBroadcastRaise(event);
        } catch (GameException e) {
            handleBroadcastEventFailure(event, e);
        }
    }

    /**
     * Method description
     *
     * @param event event
     */
    @Override
    public void doReQueuePlayer(ReQueuePlayerEvent event) {
        QueuedGambler qg = pokerGameService.doReQueuePlayer(event.getGamblerId());

        // broadcast message to channels
        handleBroadcastReQueue(event, qg);
    }

    /**
     * @param pokergameId pokergameId
     * @param handId      handId
     * @inheritDoc
     */
    @Override
    public void doTimeout(String pokergameId, String handId) {
        Gambler gamblerToBeTimedOut = pokerGameService.findCurrentGambler(handId);

        // run timeout action on game
        pokerGameService.doTimeout(handId);

        // broadcast timeout action
        handleBroadcastTimeout(pokergameId, handId, gamblerToBeTimedOut);
    }

    //~--- get methods --------------------------------------------------------

    /**
     * @param pokergameId pokergameId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public Hand getCurrentHand(String pokergameId) {
        return pokerGameService.findCurrentHand(pokergameId);
    }

    /**
     * @param handId handId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isBettable(String handId) {
        return pokerGameService.isBettable(handId);
    }

    /**
     * @param handId gamblerId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isBigBlindPostable(String handId) {
        return pokerGameService.isBigBlindPostable(handId);
    }

    /**
     * @param gamblerId gamblerId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isCallable(String gamblerId) {
        return pokerGameService.isCallable(gamblerId);
    }

    /**
     * @param gamblerId gamblerId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isCheckable(String gamblerId) {
        return pokerGameService.isCheckable(gamblerId);
    }

    /**
     * @param gamblerId gamblerId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isCurrentGambler(String gamblerId) {
        return pokerGameService.isCurrentGambler(gamblerId);
    }

    /**
     * @param handId handId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isEndGame(String handId) {
        return pokerGameService.isEndGame(handId);
    }

    /**
     * @param gamblerId gamblerId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isFoldable(String gamblerId) {
        return pokerGameService.isFoldable(gamblerId);
    }

    /**
     * @param gamblerId gamblerId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isGamblerActive(String gamblerId) {
        return pokerGameService.isGamblerActive(gamblerId);
    }

    /**
     * @param gamblerId gamblerId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isLeaveable(String gamblerId) {
        return pokerGameService.isLeaveable(gamblerId);
    }

    /**
     * @param handId handId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isProgressable(String handId) {
        return pokerGameService.isProgressable(handId);
    }

    /**
     * @param gamblerId gamblerId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isRaiseable(String gamblerId) {
        return pokerGameService.isRaiseable(gamblerId);
    }

    /**
     * @param handId handId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isSmallBlindPostable(String handId) {
        return pokerGameService.isSmallBlindPostable(handId);
    }

    /**
     * @param pokergameId pokergameId
     * @return Return value
     * @inheritDoc
     */
    @Override
    public boolean isStartable(String pokergameId) {
        return pokerGameService.isStartable(pokergameId);
    }

    /**
     * Retrieves last bet gambler made. This method should always be called after a game action
     * has been performed. It collects information about what just happened.
     *
     * @param pokergameId pokergameId
     * @param handId      handId
     * @return snapshot
     */
    private HandSnapshot getHandSnapshot(String pokergameId, String handId) {
        Gambler lastActingGambler = pokerGameService.findLastActingGambler(pokergameId, handId);

        return getHandSnapshot(pokergameId, handId, lastActingGambler);
    }

    /**
     * Method description
     *
     * @param pokergameId       pokergameId
     * @param handId            handId
     * @param lastActingGambler lastActingGambler
     * @return Return value
     */
    private HandSnapshot getHandSnapshot(String pokergameId, String handId, Gambler lastActingGambler) {
        return getHandSnapshot(pokergameId, handId, lastActingGambler, false);
    }

    /**
     * Method description
     *
     * @param pokergameId       pokergameId
     * @param handId            handId
     * @param lastActingGambler lastActingGambler
     * @param wasTimeout        wasTimeout
     * @return Return value
     */
    private HandSnapshot getHandSnapshot(String pokergameId, String handId, Gambler lastActingGambler,
                                         boolean wasTimeout) {
        HandSnapshot result;
        BigDecimal lastBet = null;

        if (!wasTimeout) {
            lastBet = Bet.findLastBetAmount(handId);
        }

        BigDecimal potSize = pokerGameService.findPotSize(handId);
        Gambler currentGambler = pokerGameService.findCurrentGambler(handId);

        if (currentGambler != null) {
            result = new HandSnapshot(pokergameId, handId, lastBet, potSize, lastActingGambler.getSeatNumber(),
                    lastActingGambler.getPlayer().getNickname(),
                    currentGambler.getPlayer().getNickname(), currentGambler.getSeatNumber());
        } else {
            result = new HandSnapshot(lastActingGambler.getHand().getGame().getId(),
                    lastActingGambler.getHand().getId(), lastBet, potSize,
                    lastActingGambler.getSeatNumber(), lastActingGambler.getPlayer().getNickname(),
                    null, null);
        }

        return result;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param hand           hand
     * @param currentGambler currentGambler
     */
    private void handleBroadcastBigBlind(Hand hand, Gambler currentGambler) {

        // the system is looking for the big blind to make a move
        // we need to broadcast to the BB to make a move
        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to private channel: Small Blind post required.");
        }

        PrivateBigBlindRequiredBroadcast privateBroadcastBB =
                new PrivateBigBlindRequiredBroadcast(hand.getGame().getId(), hand.getId(),
                        currentGambler.getPlayer().getApplicationUser().getId(), playerResponseTimeoutInSeconds,
                        hand.getGame().getTemplate().getStake().getHigh());

        pokerGameBroadcastService.privateBroadcastBigBlindRequired(privateBroadcastBB);

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to game channel: Waiting for Small Blind post.");
        }

        // and we need to broadcast to game subscribers that we are waiting for BB to make a move
        BigBlindRequiredBroadcast broadcastBB = new BigBlindRequiredBroadcast(hand.getGame().getId(), hand.getId(),
                currentGambler.getPlayer().getNickname());

        pokerGameBroadcastService.broadcastBigBlindRequired(broadcastBB);
    }

    /**
     * Method description
     *
     * @param event event
     */
    private void handleBroadcastCall(CallEvent event) {
        HandSnapshot snapshot = getHandSnapshot(event.getPokergameId(), event.getHandId(),
                Gambler.findGambler(event.getGamblerId()));

        pokerGameBroadcastService.privateBroadcastCall(new PrivateCallBroadcast(event.getPokergameId(),
                event.getHandId(), event.getApplicationUserId()));
        pokerGameBroadcastService.broadcastCall(new CallBroadcast(snapshot.getPokergameId(), snapshot.getHandId(),
                snapshot.getPlayerNickname(), snapshot.getLastBetAmount(), snapshot.getPot(), snapshot.getSeatNumber(),
                snapshot.getNextPlayerNickname(), snapshot.getNextSeatNumber()));
    }

    /**
     * Method description
     *
     * @param event event
     */
    private void handleBroadcastCheck(CheckEvent event) {
        HandSnapshot snapshot = getHandSnapshot(event.getPokergameId(), event.getHandId(),
                Gambler.findGambler(event.getGamblerId()));

        pokerGameBroadcastService.privateBroadcastCheck(new PrivateCheckBroadcast(event.getPokergameId(),
                event.getHandId(), event.getApplicationUserId()));
        pokerGameBroadcastService.broadcastCheck(new CheckBroadcast(snapshot.getPokergameId(), snapshot.getHandId(),
                snapshot.getPlayerNickname(), snapshot.getLastBetAmount(), snapshot.getPot(), snapshot.getSeatNumber(),
                snapshot.getNextPlayerNickname(), snapshot.getNextSeatNumber()));
    }

    /**
     * This is broadcast when the hand ended before showdown
     *
     * @param pokergameId pokergameId
     * @param handId      handId
     */
    private void handleBroadcastEndGame(String pokergameId, String handId) {
        List<Gambler> winners = pokerGameService.findWinners(handId);

        if ((winners != null) && (winners.size() == 1)) {
            pokerGameBroadcastService.broadcastEndGame(new EndGameBroadcast(pokergameId, handId,
                    winners.get(0).getPlayer().getNickname(), winners.get(0).getWinAmount()));
        } else if ((winners != null) && (winners.size() > 1)) {
            log.error(String.format("There should only be one winner in an end game state. Hand ID: %s has %d", handId,
                    winners.size()));
        }

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to public channel: End game !");
        }
    }

    /**
     * This is a generic error message broadcaster
     *
     * @param event event
     * @param e     e
     */
    private void handleBroadcastEventFailure(AbstractEvent event, GameException e) {
        pokerGameBroadcastService.privateBroadcastEventFailure(new PrivateEventFailureBroadcast(event.getPokergameId(),
                event.getApplicationUserId(), e.getTranslatedMessage()));
    }

    /**
     * Broadcasts Flop
     *
     * @param hand hand
     */
    private void handleBroadcastFlop(Hand hand) {

        // the system just dealt the pocket cards
        // we need to broadcast to all players what their cards are
        List<Gambler> gamblers = hand.getGamblers();

        if (gamblers != null) {

            // community cards
            String[] flop = hand.getFlop().split(CARD_DELIMITER);

            // loop through all gamblers and broadcast them their cards
            for (Gambler gambler : gamblers) {
                if (gambler.getStatus().equals(GamblerStatus.ACTIVE)) {

                    // players pocket cards
                    String[] pocketCards = gambler.getCards().split(CARD_DELIMITER);
                    PrivateFlopBroadcast privateBroadcast = new PrivateFlopBroadcast(hand.getGame().getId(),
                            hand.getId(),
                            gambler.getPlayer().getApplicationUser().getId(),
                            pocketCards[0], pocketCards[1], flop[0], flop[1],
                            flop[2]);

                    if (log.isDebugEnabled()) {
                        log.debug("Broadcasting to private channel: Flop.");
                    }

                    pokerGameBroadcastService.privateBroadcastFlop(privateBroadcast);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Broadcasting to public channel: Flop dealt.");
            }

            // broadcast to the game channel that pocket cards were dealt
            FlopBroadcast broadcast = new FlopBroadcast(hand.getGame().getId(), hand.getId(), flop[0], flop[1],
                    flop[2]);

            pokerGameBroadcastService.broadcastFlop(broadcast);
        } else {
            if (log.isErrorEnabled()) {
                log.error("There are no gamblers to broadcast flop to. This game should be over. Hand ID: "
                        + hand.getId());
            }
        }
    }

    /**
     * Method description
     *
     * @param event event
     */
    private void handleBroadcastFold(FoldEvent event) {
        HandSnapshot snapshot = getHandSnapshot(event.getPokergameId(), event.getHandId(),
                Gambler.findGambler(event.getGamblerId()));

        pokerGameBroadcastService.privateBroadcastFold(new PrivateFoldBroadcast(event.getPokergameId(),
                event.getHandId(), event.getApplicationUserId()));
        pokerGameBroadcastService.broadcastFold(new FoldBroadcast(snapshot.getPokergameId(), snapshot.getHandId(),
                snapshot.getPlayerNickname(), snapshot.getLastBetAmount(), snapshot.getPot(), snapshot.getSeatNumber(),
                snapshot.getNextPlayerNickname(), snapshot.getNextSeatNumber()));
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param e           e
     */
    private void handleBroadcastGameFailure(String pokergameId, GameException e) {
        log.error(e.getMessage(), e);
        pokerGameBroadcastService.broadcastGameFailure(new GameFailureBroadcast(pokergameId, e.getTranslatedMessage()));
    }

    /**
     * Method description
     *
     * @param event event
     */
    private void handleBroadcastLeave(LeaveEvent event) {

        // we broadcast the same private message regardless what state the player is currently in
        pokerGameBroadcastService.privateBroadcastLeave(new PrivateLeaveBroadcast(event.getPokergameId(),
                    event.getHandId(), event.getApplicationUserId()));

        if (StringUtils.isNotBlank(event.getHandId()) && StringUtils.isNotBlank(event.getGamblerId())) {
            // this condition is for when the player has already started playing a game
            HandSnapshot snapshot = getHandSnapshot(event.getPokergameId(), event.getHandId(),
                    Gambler.findGambler(event.getGamblerId()));

            pokerGameBroadcastService.broadcastLeave(new LeaveBroadcast(snapshot.getPokergameId(), snapshot.getHandId(),
                    snapshot.getPlayerNickname(), snapshot.getLastBetAmount(), snapshot.getPot(), snapshot.getSeatNumber(),
                    snapshot.getNextPlayerNickname(), snapshot.getNextSeatNumber()));
        } else {
            // this condition is for when the player is queued up for a game but not yet in the game
            Player player = Player.findPlayer(event.getPlayerId());
            pokerGameBroadcastService.broadcastLeaveQueue(new LeaveQueueBroadcast(event.getPokergameId(), player.getNickname()));
        }
    }

    /**
     * Broadcasts New Hand
     *
     * @param hand hand
     */
    private void handleBroadcastNewHand(Hand hand) {

        // the system just dealt the pocket cards
        // we need to broadcast to all players what their cards are
        List<Gambler> gamblers = hand.getGamblers();
        List<GamblerNewHand> newHandGamblers = new ArrayList<GamblerNewHand>();

        if (gamblers != null) {

            // loop through all gamblers and broadcast them new hand info
            for (Gambler gambler : gamblers) {
                if (gambler.getStatus().equals(GamblerStatus.ACTIVE)) {
                    Boolean isDealer = hand.getDealerSeat().equals(gambler.getSeatNumber());
                    Boolean isSmallBlind = hand.getSmallBlindSeat().equals(gambler.getSeatNumber());
                    Boolean isBigBlind = hand.getBigBlindSeat().equals(gambler.getSeatNumber());
                    BigDecimal amount =
                            GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                                    gambler.getPlayer().getId());
                    PrivateNewHandBroadcast privateBroadcast = new PrivateNewHandBroadcast(hand.getGame().getId(),
                            hand.getId(),
                            gambler.getPlayer().getApplicationUser().getId(),
                            gambler.getSeatNumber(), gambler.getId(), isDealer,
                            isBigBlind, isSmallBlind, amount);

                    if (log.isDebugEnabled()) {
                        log.debug("Broadcasting to private channel: New Hand.");
                    }

                    pokerGameBroadcastService.privateBroadcastNewHand(privateBroadcast);
                    newHandGamblers.add(new GamblerNewHand(gambler.getPlayer().getNickname(), gambler.getSeatNumber(),
                            gambler.getPlayer().getAvatarUrl(), isDealer, isSmallBlind, isBigBlind, amount));
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Broadcasting to public channel: New Hand dealt.");
            }

            // broadcast to the game channel that pocket cards were dealt
            pokerGameBroadcastService.broadcastNewHand(new NewHandBroadcast(hand.getGame().getId(), hand.getId(),
                    newHandGamblers));
        } else {
            if (log.isErrorEnabled()) {
                log.error("There are no gamblers to broadcast New Hand to. This game should be over. Hand ID: "
                        + hand.getId());
            }
        }
    }

    /**
     * Method description
     *
     * @param gambler gambler
     */
    private void handleBroadcastPlayerResponseRequired(Gambler gambler) {

        // first we tell the public channel we are waiting on a player
        pokerGameBroadcastService.broadcastPlayerResponseRequired(
                new PlayerResponseRequiredBroadcast(
                        gambler.getHand().getGame().getId(), gambler.getHand().getId(), gambler.getPlayer().getNickname()));

        BigDecimal callAmount = new BigDecimal(0);
        BigDecimal maximumRaiseAmount = new BigDecimal(0);
        BigDecimal minimumRaiseAmount = new BigDecimal(0);
        List<GameAction> availableMoves = new ArrayList<GameAction>();

        // retrieve available moves for the player
        availableMoves = pokerGameService.findAvailableMoves(gambler.getId());

        if ((availableMoves != null) && !availableMoves.isEmpty()) {
            for (GameAction availableMove : availableMoves) {
                switch (availableMove) {
                    case CALL_ACTION:
                        callAmount = pokerGameService.findCallAmount(gambler.getId());

                        break;

                    case RAISE_ACTION:
                        maximumRaiseAmount = pokerGameService.findMaximumRaiseAmount(gambler.getId());
                        minimumRaiseAmount = pokerGameService.findMinimumRaiseAmount(gambler.getId());

                        break;
                }
            }
        }

        pokerGameBroadcastService.privateBroadcastPlayerResponseRequired(
                new PrivatePlayerResponseRequiredBroadcast(
                        gambler.getHand().getGame().getId(), gambler.getHand().getId(),
                        gambler.getPlayer().getApplicationUser().getId(), availableMoves, maximumRaiseAmount,
                        minimumRaiseAmount, playerResponseTimeoutInSeconds, callAmount));
    }

    /**
     * Broadcasts pocket cards
     *
     * @param hand hand
     */
    private void handleBroadcastPocketCards(Hand hand) {

        // the system just dealt the pocket cards
        // we need to broadcast to all players what their cards are
        List<Gambler> gamblers = hand.getGamblers();

        if (gamblers != null) {

            // loop through all gamblers and broadcast them their cards
            for (Gambler gambler : gamblers) {
                if (gambler.getStatus().equals(GamblerStatus.ACTIVE)) {
                    String[] pocketCards = gambler.getCards().split(CARD_DELIMITER);
                    PrivatePocketCardsBroadcast privateBroadcast =
                            new PrivatePocketCardsBroadcast(hand.getGame().getId(), hand.getId(),
                                    gambler.getPlayer().getApplicationUser().getId(), pocketCards[0], pocketCards[1]);

                    if (log.isDebugEnabled()) {
                        log.debug("Broadcasting to private channel: Pocket cards.");
                    }

                    pokerGameBroadcastService.privateBroadcastPocketCards(privateBroadcast);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Broadcasting to public channel: Pocket cards dealt.");
            }

            // broadcast to the game channel that pocket cards were dealt
            PocketCardsBroadcast broadcast = new PocketCardsBroadcast(hand.getGame().getId(), hand.getId());

            pokerGameBroadcastService.broadcastPocketCards(broadcast);
        } else {
            if (log.isErrorEnabled()) {
                log.error("There are no gamblers to broadcast pocket cards to. This game should be over. Hand ID: "
                        + hand.getId());
            }
        }
    }

    /**
     * Method description
     *
     * @param pokergameId       pokergameId
     * @param handId            handId
     * @param applicationUserId applicationUserId
     */
    private void handleBroadcastPostBigBlind(String pokergameId, String handId, String applicationUserId) {
        HandSnapshot snapshot = getHandSnapshot(pokergameId, handId);

        pokerGameBroadcastService.privateBroadcastPostBigBlind(new PrivatePostBigBlindBroadcast(pokergameId, handId,
                applicationUserId));
        pokerGameBroadcastService.broadcastPostBigBlind(new PostBigBlindBroadcast(snapshot.getPokergameId(),
                snapshot.getHandId(), snapshot.getPlayerNickname(), snapshot.getLastBetAmount(), snapshot.getPot(),
                snapshot.getSeatNumber(), snapshot.getNextPlayerNickname(), snapshot.getNextSeatNumber()));
    }

    /**
     * Method description
     *
     * @param pokergameId       pokergameId
     * @param handId            handId
     * @param applicationUserId applicationUserId
     */
    private void handleBroadcastPostSmallBlind(String pokergameId, String handId, String applicationUserId) {
        HandSnapshot snapshot = getHandSnapshot(pokergameId, handId);

        pokerGameBroadcastService.privateBroadcastPostSmallBlind(new PrivatePostSmallBlindBroadcast(pokergameId,
                handId, applicationUserId));
        pokerGameBroadcastService.broadcastPostSmallBlind(new PostSmallBlindBroadcast(snapshot.getPokergameId(),
                snapshot.getHandId(), snapshot.getPlayerNickname(), snapshot.getLastBetAmount(), snapshot.getPot(),
                snapshot.getSeatNumber(), snapshot.getNextPlayerNickname(), snapshot.getNextSeatNumber()));
    }

    /**
     * Method description
     *
     * @param event event
     */
    private void handleBroadcastQueuePlayer(QueuePlayerEvent event) {
        pokerGameBroadcastService.privateBroadcastQueuePlayer(new PrivateQueuePlayerBroadcast(event.getPokergameId(),
                event.getApplicationUserId()));
        pokerGameBroadcastService.broadcastQueuePlayer(new QueuePlayerBroadcast(event.getPokergameId(),
                event.getApplicationUserId(), event.getPlayerId(), event.getBuyin(), event.getSeatNumber(),
                event.getMustHaveSeat()));
    }

    /**
     * Method description
     *
     * @param event event
     */
    private void handleBroadcastRaise(RaiseEvent event) {
        HandSnapshot snapshot = getHandSnapshot(event.getPokergameId(), event.getHandId(),
                Gambler.findGambler(event.getGamblerId()));

        pokerGameBroadcastService.privateBroadcastRaise(new PrivateRaiseBroadcast(event.getPokergameId(),
                event.getHandId(), event.getApplicationUserId()));
        pokerGameBroadcastService.broadcastRaise(new RaiseBroadcast(snapshot.getPokergameId(), snapshot.getHandId(),
                snapshot.getPlayerNickname(), snapshot.getLastBetAmount(), snapshot.getPot(), snapshot.getSeatNumber(),
                snapshot.getNextPlayerNickname(), snapshot.getNextSeatNumber()));
    }

    /**
     * Method description
     *
     * @param event event
     * @param qg    qg
     */
    private void handleBroadcastReQueue(ReQueuePlayerEvent event, QueuedGambler qg) {
        pokerGameBroadcastService.privateBroadcastReQueuePlayer(new PrivateReQueuePlayerBroadcast(event.getPokergameId(),
                event.getApplicationUserId()));
        pokerGameBroadcastService.broadcastReQueuePlayer(new ReQueuePlayerBroadcast(event.getPokergameId(),
                event.getApplicationUserId(), qg.getPlayerId(), new BigDecimal(0), qg.getDesiredSeatNumber(),
                qg.getMustHaveSeat()));
    }

    /**
     * Broadcasts River
     *
     * @param hand hand
     */
    private void handleBroadcastRiver(Hand hand) {

        // the system just dealt the pocket cards
        // we need to broadcast to all players what their cards are
        List<Gambler> gamblers = hand.getGamblers();

        if (gamblers != null) {

            // community cards
            String[] flop = hand.getFlop().split(CARD_DELIMITER);
            String turn = hand.getTurn();
            String river = hand.getRiver();

            // loop through all gamblers and broadcast them their cards
            for (Gambler gambler : gamblers) {
                if (gambler.getStatus().equals(GamblerStatus.ACTIVE)) {

                    // players pocket cards
                    String[] pocketCards = gambler.getCards().split(CARD_DELIMITER);
                    PrivateRiverBroadcast privateBroadcast = new PrivateRiverBroadcast(hand.getGame().getId(),
                            hand.getId(),
                            gambler.getPlayer().getApplicationUser().getId(),
                            pocketCards[0], pocketCards[1], flop[0], flop[1],
                            flop[2], turn, river);

                    if (log.isDebugEnabled()) {
                        log.debug("Broadcasting to private channel: River.");
                    }

                    pokerGameBroadcastService.privateBroadcastRiver(privateBroadcast);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Broadcasting to public channel: River dealt.");
            }

            // broadcast to the game channel that pocket cards were dealt
            RiverBroadcast broadcast = new RiverBroadcast(hand.getGame().getId(), hand.getId(), flop[0], flop[1],
                    flop[2], turn, river);

            pokerGameBroadcastService.broadcastRiver(broadcast);
        } else {
            if (log.isErrorEnabled()) {
                log.error("There are no gamblers to broadcast River to. This game should be over. Hand ID: "
                        + hand.getId());
            }
        }
    }

    /**
     * When the game is complete, this method will finish off the hand and broadcast out the results
     *
     * @param hand hand
     */
    private void handleBroadcastShowdown(Hand hand) {
        List<GamblerShowdown> list = null;

        if (hand.getGamblers() != null) {
            list = new ArrayList<GamblerShowdown>();

            for (Gambler gambler : hand.getGamblers()) {
                if (gambler.getWinner()) {
                    String[] cards = gambler.getCards().split(" ");
                    GamblerShowdown gs = new GamblerShowdown(gambler.getPlayer().getNickname(),
                            gambler.getHandName(), gambler.getWinAmount(), cards);

                    list.add(gs);
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to public channel: Showdown!");
        }

        pokerGameBroadcastService.broadcastShowdown(new ShowdownBroadcast(hand.getGame().getId(), hand.getId(), list));
    }

    /**
     * Method description
     *
     * @param hand           hand
     * @param currentGambler currentGambler
     */
    private void handleBroadcastSmallBlind(Hand hand, Gambler currentGambler) {

        // the system is looking for the small blind to make a move
        // we need to broadcast to the SB to make a move
        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to private channel: Small Blind post required.");
        }

        PrivateSmallBlindRequiredBroadcast privateBroadcastSB =
                new PrivateSmallBlindRequiredBroadcast(hand.getGame().getId(), hand.getId(),
                        currentGambler.getPlayer().getApplicationUser().getId(), playerResponseTimeoutInSeconds,
                        hand.getGame().getTemplate().getStake().getLow());

        pokerGameBroadcastService.privateBroadcastSmallBlindRequired(privateBroadcastSB);

        if (log.isDebugEnabled()) {
            log.debug("Broadcasting to game channel: Waiting for Small Blind post.");
        }

        // and we need to broadcast to game subscribers that we are waiting for SB to make a move
        SmallBlindRequiredBroadcast broadcastSB = new SmallBlindRequiredBroadcast(hand.getGame().getId(), hand.getId(),
                currentGambler.getPlayer().getNickname());

        pokerGameBroadcastService.broadcastSmallBlindRequired(broadcastSB);
    }

    /**
     * Method description
     *
     * @param pokergameId         pokergameId
     * @param handId              handId
     * @param gamblerToBeTimedOut gamblerToBeTimedOut
     */
    private void handleBroadcastTimeout(String pokergameId, String handId, Gambler gamblerToBeTimedOut) {
        HandSnapshot snapshot = getHandSnapshot(pokergameId, handId, gamblerToBeTimedOut, true);
        TimeoutBroadcast broadcast = new TimeoutBroadcast(pokergameId, handId, snapshot.getPlayerNickname(),
                snapshot.getSeatNumber());

        pokerGameBroadcastService.broadcastTimeout(broadcast);

        PrivateTimeoutBroadcast privateBroadcast = new PrivateTimeoutBroadcast(pokergameId, handId,
                gamblerToBeTimedOut.getPlayer().getApplicationUser().getId());

        pokerGameBroadcastService.privateBroadcastTimeout(privateBroadcast);
    }

    /**
     * Broadcasts Turn
     *
     * @param hand hand
     */
    private void handleBroadcastTurn(Hand hand) {

        // the system just dealt the pocket cards
        // we need to broadcast to all players what their cards are
        List<Gambler> gamblers = hand.getGamblers();

        if (gamblers != null) {

            // community cards
            String[] flop = hand.getFlop().split(CARD_DELIMITER);
            String turn = hand.getTurn();

            // loop through all gamblers and broadcast them their cards
            for (Gambler gambler : gamblers) {
                if (gambler.getStatus().equals(GamblerStatus.ACTIVE)) {

                    // players pocket cards
                    String[] pocketCards = gambler.getCards().split(CARD_DELIMITER);
                    PrivateTurnBroadcast privateBroadcast = new PrivateTurnBroadcast(hand.getGame().getId(),
                            hand.getId(),
                            gambler.getPlayer().getApplicationUser().getId(),
                            pocketCards[0], pocketCards[1], flop[0], flop[1],
                            flop[2], turn);

                    if (log.isDebugEnabled()) {
                        log.debug("Broadcasting to private channel: Turn.");
                    }

                    pokerGameBroadcastService.privateBroadcastTurn(privateBroadcast);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Broadcasting to public channel: Turn dealt.");
            }

            // broadcast to the game channel that pocket cards were dealt
            TurnBroadcast broadcast = new TurnBroadcast(hand.getGame().getId(), hand.getId(), flop[0], flop[1],
                    flop[2], turn);

            pokerGameBroadcastService.broadcastTurn(broadcast);
        } else {
            if (log.isErrorEnabled()) {
                log.error("There are no gamblers to broadcast Turn to. This game should be over. Hand ID: "
                        + hand.getId());
            }
        }
    }
}
