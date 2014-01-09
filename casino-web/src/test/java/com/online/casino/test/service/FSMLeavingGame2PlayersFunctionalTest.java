/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.enums.HandStatus;
import com.online.casino.service.FiniteStateMachineClientService;
import com.online.casino.service.fsm.event.FoldEvent;
import com.online.casino.service.fsm.event.LeaveEvent;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility: Shows how the FSM works
 */

public class FSMLeavingGame2PlayersFunctionalTest extends AbstractPokerGameFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(FSMLeavingGame2PlayersFunctionalTest.class);
    private static final String PLAYER2A = "player2a";
    private static final String PLAYER1A = "player1a";

    @Autowired
    private FiniteStateMachineClientService finiteStateMachineClientService;

    @Test
    public void testFoldOnGame2Players() {
        log.info("Testing Finite State Machine - Folding during a game - 2 Players...");

        assertNotNull("finiteStateMachineClientService is null", finiteStateMachineClientService);

        PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();
        assertNotNull("game is null", game);

        log.info("Retrieving players for game");
        Player p1 = administrationService.findPlayerByNickname(PLAYER1A);
        Player p2 = administrationService.findPlayerByNickname(PLAYER2A);
        assertNotNull("Player 1 is null", p1);
        assertNotNull("Player 2 is null", p2);

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

        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        assertEquals(gambler.getPlayer().getNickname() + " should be small blind", gambler.getSeatNumber(), hand.getSmallBlindSeat());
        log.info("Time to bet on pocket cards...");

        log.info(gambler.getPlayer().getNickname() + " should call the pot but chooses to fold here");

        finiteStateMachineClientService.dispatchFoldEvent(
                new FoldEvent(game.getId(), hand.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getId())
        );

        log.info(PLAYER1A + " should've now won");

        assertNotNull("Hand is null", hand);
        hand = pokerGameService.findHand(hand.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand should've completed", HandStatus.COMPLETE, hand.getStatus());

        log.info("Checking scores...");

        List<Gambler> winners = pokerGameService.findWinners(hand.getId());
        assertNotNull("Winners is null", winners);
        assertEquals("There should only be one winner", 1, winners.size());
        assertEquals("Wrong winner", PLAYER1A, winners.get(0).getPlayer().getNickname());

        List<Gambler> losers = pokerGameService.findLosers(hand.getId());
        assertNotNull("Losers is null", losers);
        assertEquals("There should only be one loser", 1, losers.size());
        assertEquals("Wrong loser", gambler.getPlayer().getNickname(), losers.get(0).getPlayer().getNickname());

        log.info("A new game should be underway. Verifying...");
        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand status is incorrect", HandStatus.POCKET_CARDS, hand.getStatus());
        log.info("Retrieved current hand and verified state successfully");

        log.info("Testing Finite State Machine - Folding during a game - 2 Players COMPLETE!");
    }

    @Test
    public void testLeaveOnGame2PlayersA() {
        log.info("Testing Finite State Machine - Leaving during a game - 2 Players...");

        assertNotNull("finiteStateMachineClientService is null", finiteStateMachineClientService);

        PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();
        assertNotNull("game is null", game);

        log.info("Retrieving players for game");
        Player p1 = administrationService.findPlayerByNickname(PLAYER1A);
        Player p2 = administrationService.findPlayerByNickname(PLAYER2A);
        assertNotNull("Player 1 is null", p1);
        assertNotNull("Player 2 is null", p2);

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

        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        assertEquals(gambler.getPlayer().getNickname() + " should be small blind", gambler.getSeatNumber(), hand.getSmallBlindSeat());
        log.info("Time to bet on pocket cards...");

        log.info(gambler.getPlayer().getNickname() + " should call the pot but chooses to fold here");

        finiteStateMachineClientService.dispatchLeaveEvent(
                new LeaveEvent(game.getId(), gambler.getPlayer().getApplicationUser().getId(), gambler.getPlayer().getId(), hand.getId(), gambler.getId())
        );

        log.info(PLAYER1A + " should've now won");

        assertNotNull("Hand is null", hand);
        hand = pokerGameService.findHand(hand.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand should've completed", HandStatus.COMPLETE, hand.getStatus());

        log.info("Checking scores...");

        List<Gambler> winners = pokerGameService.findWinners(hand.getId());
        assertNotNull("Winners is null", winners);
        assertEquals("There should only be one winner", 1, winners.size());
        assertEquals("Wrong winner", PLAYER1A, winners.get(0).getPlayer().getNickname());

        List<Gambler> losers = pokerGameService.findLosers(hand.getId());
        assertNotNull("Losers is null", losers);
        assertEquals("There should only be one loser", 1, losers.size());
        assertEquals("Wrong loser", gambler.getPlayer().getNickname(), losers.get(0).getPlayer().getNickname());

        log.info("A new game should NOT be underway. Verifying...");
        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNull("Hand is null", hand);
        log.info("Retrieved current hand and verified state successfully");

        log.info("Testing Finite State Machine - Folding during a game - 2 Players COMPLETE!");
    }

    @Test
    public void testLeaveOnGame2PlayersB() {
        log.info("Testing Finite State Machine - Leaving during a game - 2 Players...");

        assertNotNull("finiteStateMachineClientService is null", finiteStateMachineClientService);

        PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();
        assertNotNull("game is null", game);

        log.info("Retrieving players for game");
        Player p1 = administrationService.findPlayerByNickname(PLAYER1A);
        Player p2 = administrationService.findPlayerByNickname(PLAYER2A);
        assertNotNull("Player 1 is null", p1);
        assertNotNull("Player 2 is null", p2);

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

        gambler = pokerGameService.findCurrentGambler(hand.getId());
        assertNotNull("Gambler is null", gambler);
        assertEquals(gambler.getPlayer().getNickname() + " should be small blind", gambler.getSeatNumber(), hand.getSmallBlindSeat());

        List<Gambler> gamblers = hand.getGamblers();
        Gambler BB = null;

        for (Gambler g : gamblers) {
            if (!StringUtils.equals(g.getId(), gambler.getId())) {
                // found the BB
                BB = g;
            }
        }

        assertNotNull("BB cannot be null here", BB);
        log.info("It's " + gambler.getPlayer().getNickname() + "'s turn. However, BB player leaves the game before he has a time to call.");
        log.info(BB.getPlayer().getNickname() + " leaves the game out of turn");

        finiteStateMachineClientService.dispatchLeaveEvent(
                new LeaveEvent(game.getId(), BB.getPlayer().getApplicationUser().getId(), BB.getPlayer().getId(), hand.getId(), BB.getId())
        );

        log.info(gambler.getPlayer().getNickname() + " should've now won because " + BB.getPlayer().getNickname() + " just left");

        assertNotNull("Hand is null", hand);
        hand = pokerGameService.findHand(hand.getId());
        assertNotNull("Hand is null", hand);
        assertEquals("The hand should've completed", HandStatus.COMPLETE, hand.getStatus());

        log.info("Checking scores...");

        List<Gambler> winners = pokerGameService.findWinners(hand.getId());
        assertNotNull("Winners is null", winners);
        assertEquals("There should only be one winner", 1, winners.size());
        assertEquals("Wrong winner", PLAYER2A, winners.get(0).getPlayer().getNickname());

        List<Gambler> losers = pokerGameService.findLosers(hand.getId());
        assertNotNull("Losers is null", losers);
        assertEquals("There should only be one loser", 1, losers.size());
        assertEquals("Wrong loser", PLAYER1A, losers.get(0).getPlayer().getNickname());

        log.info("A new game should NOT be underway. Verifying...");
        log.info("Retrieving current hand and verifying state...");
        hand = pokerGameService.findCurrentHand(game.getId());
        assertNull("Hand is null", hand);
        log.info("Retrieved current hand and verified state successfully");

        log.info("Testing Finite State Machine - Folding during a game - 2 Players COMPLETE!");
    }
}
