/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold. 
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.entity.QueuedGambler;
import com.online.casino.domain.enums.HandStatus;
import com.online.casino.exception.GameException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility:
 */
public class THNLCallsOnly10PlayersFunctionalTest extends AbstractPokerGameFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(THNLCallsOnly10PlayersFunctionalTest.class);

    @Test
    public void test10PersonCallsOnly() {
        try {
            log.info("Testing a calls-only game");

            // do game creation with test
            PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();

            // do users joining game with tests
            List<QueuedGambler> list = new ArrayList<QueuedGambler>();
            for (int i = 1; i <= 10; i++) {
                QueuedGambler qg = new QueuedGambler();
                qg.setNickname("player" + i + "a");
                qg.setDesiredSeatNumber(i);
                qg.setMustHaveSeat(true);
                qg.setAmount(new BigDecimal(100));
                qg.setQueueNumber(i);

                list.add(qg);
            }

            handleAdvancedJoiningGame(game, list);

            // hand creation with tests
            Hand hand = handleNewHand(game);

            // posting of successful blinds with tests
            hand = handleSmallAndBigBlinds(hand);

            log.info("Now it is time to bet. The guy to the left of the dealer goes first. In this scenario, everyone is going to call");
            assertTrue("Hand is not progressable although it should be", pokerGameService.isProgressable(hand.getId()));

            List<Gambler> gamblers = pokerGameService.findGamblers(hand.getId());
            assertNotNull("Gamblers for hand are null", gamblers);
            assertEquals("Gambler list size is not correct", 10, gamblers.size());

            while (hand != null && pokerGameService.isProgressable(hand.getId())) {
                log.info("Progressing hand...");
                pokerGameService.doProgressHand(hand.getId());
                hand = pokerGameService.findCurrentHand(game.getId());

                log.info("The hand can be null here if there is no active game. That will happen at the end");
                if (hand != null) {
                    log.info("New hand status: " + hand.getStatus());

                    Boolean isBettable = pokerGameService.isBettable(hand.getId());
                    switch (hand.getStatus()) {
                        case POCKET_CARDS:
                        case FLOP:
                        case TURN:
                        case RIVER:
                            assertTrue("Betting should not be over yet", isBettable);
                            while (pokerGameService.isBettable(hand.getId())) {
                                log.info("In round number: " + hand.getCurrentRoundNumber());

                                log.info("Retrieving the next gambler to bet");
                                Gambler gambler = pokerGameService.findCurrentGambler(hand.getId());

                                assertNotNull("Gambler is null", gambler);
                                log.info("Gambler retrieved successfully: " + gambler);

                                switch (hand.getCurrentRoundNumber()) {
                                    case 1:
                                        handlePocketCards(hand, gambler);
                                        break;
                                    case 2:
                                        handleFlop(hand, gambler);
                                        break;
                                    case 3:
                                        handleTurn(hand, gambler);
                                        break;
                                    case 4:
                                        handleRiver(hand, gambler);
                                        break;
                                }
                            }
                            break;
                        case COMPLETE:
                            assertFalse("Betting should be over", isBettable);
                            handleShowdown(hand);
                    }

                    log.info("Betting for round " + hand.getCurrentRoundNumber() + " is over. Time to progress the game.");
                }
            }

        } catch (GameException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    private void handlePocketCards(Hand hand, Gambler gambler) throws GameException {
        log.info("Pocket cards were dealt...");

        log.info("All other gamblers except the big blind should be able to call and raise. The big blind should be able to check and raise");

        log.info("We are in round one of this hand and there will have been already 2 forced bets. Therefore we have to validate a little bit differently than an ordinary round");
        if (gambler.getSeatNumber().equals(hand.getBigBlindSeat())) {
            log.info("This gambler is the current big blind for this hand. Verifying call, check and raise status");
            Boolean canCheck = pokerGameService.isCheckable(gambler.getId());
            Boolean canCall = pokerGameService.isCallable(gambler.getId());
            Boolean canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertTrue("Gambler with big blind should be able to check", canCheck);
            assertTrue("Gambler with big blind should be able to raise", canRaise);
            assertFalse("Gambler with big blind should not be able to call", canCall);

            log.info("Statuses are good");

            log.info("Now we just check for big blind gambler");
            pokerGameService.doCheck(gambler.getId());
            Gambler doCheck = pokerGameService.findCurrentGambler(hand.getId());
            assertNull("There should be no more next gambler in this scenario", doCheck);
            log.info("Check successful. This should be the last action before the Turn");

        } else if (gambler.getSeatNumber().equals(hand.getSmallBlindSeat())) {
            log.info("This gambler is the current small blind for this hand. Verifying call, check and raise status");
            Boolean canCheck = pokerGameService.isCheckable(gambler.getId());
            Boolean canCall = pokerGameService.isCallable(gambler.getId());
            Boolean canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Gambler with small blind should not be able to check", canCheck);
            assertTrue("Gambler with small blind should be able to raise", canRaise);
            assertTrue("Gambler with small blind should be able to call", canCall);

            log.info("Statuses are good");

            log.info("Now we just call for the gambler");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

        } else {
            log.info("This gambler is currently not the big blind for this hand. Verifying call, check and raise status");
            Boolean canCheck = pokerGameService.isCheckable(gambler.getId());
            Boolean canCall = pokerGameService.isCallable(gambler.getId());
            Boolean canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Gambler should not be able to check", canCheck);
            assertTrue("Gambler should be able to raise", canRaise);
            assertTrue("Gambler should be able to call", canCall);

            log.info("Now we just call for the gambler");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());
        }
    }

    private void handleFlop(Hand hand, Gambler gambler) throws GameException {
        log.info("Flop was dealt...");

        log.info("Verifying that current gambler can check, call and raise...");
        Boolean canCheck = pokerGameService.isCheckable(gambler.getId());
        Boolean canCall = pokerGameService.isCallable(gambler.getId());
        Boolean canRaise = pokerGameService.isRaiseable(gambler.getId());
        assertTrue("Gambler should be able to check", canCheck);
        assertTrue("Gambler should be able to raise", canRaise);
        assertFalse("Gambler should not be able to call", canCall);

        log.info("Now we just check for the gambler");
        pokerGameService.doCheck(gambler.getId());
        gambler = pokerGameService.findCurrentGambler(hand.getId());
    }

    private void handleTurn(Hand hand, Gambler gambler) throws GameException {
        log.info("Turn was dealt...");

        log.info("Verifying that current gambler can check, call and raise...");
        Boolean canCheck = pokerGameService.isCheckable(gambler.getId());
        Boolean canCall = pokerGameService.isCallable(gambler.getId());
        Boolean canRaise = pokerGameService.isRaiseable(gambler.getId());
        assertTrue("Gambler should be able to check", canCheck);
        assertTrue("Gambler should be able to raise", canRaise);
        assertFalse("Gambler should not be able to call", canCall);

        log.info("Now we just check for the gambler");
        pokerGameService.doCheck(gambler.getId());
        gambler = pokerGameService.findCurrentGambler(hand.getId());
    }

    private void handleRiver(Hand hand, Gambler gambler) throws GameException {
        log.info("River was dealt...");

        log.info("Verifying that current gambler can check, call and raise...");
        Boolean canCheck = pokerGameService.isCheckable(gambler.getId());
        Boolean canCall = pokerGameService.isCallable(gambler.getId());
        Boolean canRaise = pokerGameService.isRaiseable(gambler.getId());
        assertTrue("Gambler should be able to check", canCheck);
        assertTrue("Gambler should be able to raise", canRaise);
        assertFalse("Gambler should not be able to call", canCall);

        log.info("Now we just check for the gambler");
        pokerGameService.doCheck(gambler.getId());
        gambler = pokerGameService.findCurrentGambler(hand.getId());
    }

    private void handleShowdown(Hand hand) throws GameException {
        log.info("Hand status should be set to complete");
        assertEquals("Status should be complete", HandStatus.COMPLETE, hand.getStatus());

        log.info("Retrieving winners and losers");
        List<Gambler> winners = pokerGameService.findWinners(hand.getId());
        List<Gambler> losers = pokerGameService.findLosers(hand.getId());
        BigDecimal potAmount = pokerGameService.findPotSize(hand.getId());
        log.info("Pot amount for hand is: " + potAmount);

        log.warn("The below asserts WILL fail if there were more than one winner!! FYI.");

        assertNotNull("There are no winners", winners);
        assertNotNull("There are no losers", losers);
        assertNotNull("There is no pot for the hand", potAmount);
        assertEquals("Pot amount is not as expected", new BigDecimal(100.00).floatValue(), potAmount.floatValue(), 0);

        if (winners.size() == 1) {
            log.info("Printing out winners with hand information");
            for (Gambler winner : winners) {
                log.info("Winner: " + winner.getPlayer() + ". Cards: " + winner.getHandName() + ". Card rank: " + winner.getHandRank());
                BigDecimal win = pokerGameService.findWinnerAmount(hand.getGame().getId(), winner.getPlayer().getId());
                log.info("Winner should've won :" + potAmount.toString());
                assertTrue("Winner didn't win anything", win.compareTo(new BigDecimal(100.00)) == 0);
            }

            log.info("Printing out losers with hand information");
            for (Gambler loser : losers) {
                log.info("Loser: " + loser.getPlayer() + ". Cards: " + loser.getHandName() + ". Card rank: " + loser.getHandRank());
                BigDecimal loss = pokerGameService.findBetAmount(hand.getId(), loser.getPlayer().getId());
                log.info("Loser should've lost some money: " + loss.toString());
                assertTrue("Loser didn't lose " + loss.toString(), loss.compareTo(new BigDecimal(10.00)) == 0);
            }
        } else {
            log.warn("There were multiple winners and this integration test doesn't support this");
        }
    }
}
