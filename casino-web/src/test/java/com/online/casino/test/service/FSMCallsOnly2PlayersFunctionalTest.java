/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.*;
import com.online.casino.domain.enums.HandStatus;
import com.online.casino.service.FiniteStateMachineClientService;
import com.online.casino.service.fsm.event.CallEvent;
import com.online.casino.service.fsm.event.CheckEvent;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility: Shows how the FSM works
 */

public class FSMCallsOnly2PlayersFunctionalTest extends AbstractPokerGameFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(FSMCallsOnly2PlayersFunctionalTest.class);

    @Autowired
    private FiniteStateMachineClientService finiteStateMachineClientService;

    @Test
    public void testCallsOnly2Players() {
        log.info("Testing Finite State Machine - Calls Only - 2 Players...");

        assertNotNull("finiteStateMachineClientService is null", finiteStateMachineClientService);

        PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();
        assertNotNull("game is null", game);

        log.info("Retrieving players for game");
        Player p1 = administrationService.findPlayerByNickname("player1a");
        Player p2 = administrationService.findPlayerByNickname("player2a");
        Player p3 = administrationService.findPlayerByNickname("player3a");
        Player p4 = administrationService.findPlayerByNickname("player4a");
        Player p5 = administrationService.findPlayerByNickname("player5a");
        Player p6 = administrationService.findPlayerByNickname("player6a");
        Player p7 = administrationService.findPlayerByNickname("player7a");
        Player p8 = administrationService.findPlayerByNickname("player8a");
        Player p9 = administrationService.findPlayerByNickname("player9a");
        Player p10 = administrationService.findPlayerByNickname("player10a");
        assertNotNull("Player 1 is null", p1);
        assertNotNull("Player 2 is null", p2);
        assertNotNull("Player 3 is null", p3);
        assertNotNull("Player 4 is null", p4);
        assertNotNull("Player 5 is null", p5);
        assertNotNull("Player 6 is null", p6);
        assertNotNull("Player 7 is null", p7);
        assertNotNull("Player 8 is null", p8);
        assertNotNull("Player 9 is null", p9);
        assertNotNull("Player 10 is null", p10);

        Gambler gambler;
        Hand hand = null;

        log.info("Player 1 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p1.getApplicationUser().getId(), p1.getId(), new BigDecimal(100), false, 1));


        log.info("Player 2 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p2.getApplicationUser().getId(), p2.getId(), new BigDecimal(100), false, 2));

        log.info("Game should start new as the condition for having 2 players in a game has been met");
        log.info("SB posted automatically");
        log.info("BB posted automatically");

        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.POCKET_CARDS, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("Queuing another gambler at POCKET_CARDS so we can do a tally at the end of game one.");
        log.info("Player 3 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p3.getApplicationUser().getId(), p3.getId(), new BigDecimal(100), false, 3));

        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);

        log.info("Time to bet on pocket cards...");

        log.info("Player 1 is small blind and calls the big blind");

        finiteStateMachineClientService.dispatchCallEvent(
                new CallEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        log.info("Player 2 is big blind and checks only");

        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);

        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);

        finiteStateMachineClientService.dispatchCheckEvent(
                new CheckEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.FLOP, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("Queuing another gambler at FLOP so we can do a tally at the end of game one.");
        log.info("Player 4 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p4.getApplicationUser().getId(), p4.getId(), new BigDecimal(100), false, 4));

        log.info("Time to bet on flop...");

        log.info("Player 1 checks on Flop");
        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        finiteStateMachineClientService.dispatchCheckEvent(
                new CheckEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.FLOP, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("Queuing another gambler at FLOP so we can do a tally at the end of game one.");
        log.info("Player 5 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p5.getApplicationUser().getId(), p5.getId(), new BigDecimal(100), false, 5));

        log.info("Player 2 checks on Flop");
        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        finiteStateMachineClientService.dispatchCheckEvent(
                new CheckEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.TURN, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("Queuing another gambler at TURN so we can do a tally at the end of game one.");
        log.info("Player 6 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p6.getApplicationUser().getId(), p6.getId(), new BigDecimal(100), false, 6));

        log.info("Time to bet on turn...");

        log.info("Player 1 checks on turn");
        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        finiteStateMachineClientService.dispatchCheckEvent(
                new CheckEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.TURN, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("Queuing another gambler at TURN so we can do a tally at the end of game one.");
        log.info("Player 7 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p7.getApplicationUser().getId(), p7.getId(), new BigDecimal(100), false, 7));

        log.info("Player 2 checks on turn");
        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        finiteStateMachineClientService.dispatchCheckEvent(
                new CheckEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.RIVER, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("Queuing another gambler at RIVER so we can do a tally at the end of game one.");
        log.info("Player 8 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p8.getApplicationUser().getId(), p8.getId(), new BigDecimal(100), false, 8));

        log.info("Time to bet on river...");

        log.info("Player 1 checks");
        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        finiteStateMachineClientService.dispatchCheckEvent(
                new CheckEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.RIVER, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("Queuing another gambler at RIVER so we can do a tally at the end of game one.");
        log.info("Player 9 queues up for the game");
        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(game.getId(), p9.getApplicationUser().getId(), p9.getId(), new BigDecimal(100), false, 9));

        log.info("Player 2 checks");
        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        finiteStateMachineClientService.dispatchCheckEvent(
                new CheckEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        assertNotNull("Hand is null", hand);
        hand = pokerGameService.findHand(hand.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand should've completed", HandStatus.COMPLETE, hand.getStatus());

        log.info("Checking scores...");

        List<Gambler> winners = pokerGameService.findWinners(hand.getId());
        assertNotNull("Winners is null", winners);

        List<Gambler> losers = pokerGameService.findLosers(hand.getId());
        assertNotNull("Losers is null", losers);

        for (Gambler winner : winners) {
            log.info("Winner: " + winner.getPlayer().getNickname() + " : " + winner.getHandName());
        }

        for (Gambler loser : losers) {
            log.info("Loser: " + loser.getPlayer().getNickname() + " : " + loser.getHandName());
        }

        log.info("A new game should be underway. Verifying...");
        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.POCKET_CARDS, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("There should be 9 players in the game now");
        assertEquals("Incorrect number of gamblers for hand", 9, hand.getGamblers().size());
        log.info("9 players for new game verified successfully");

        log.info("Testing Finite State Machine - Calls Only - 2 Players COMPLETE!");
    }
}
