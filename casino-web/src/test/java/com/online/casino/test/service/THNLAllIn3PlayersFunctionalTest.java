/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.enums.HandStatus;
import com.online.casino.exception.GameException;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
public class THNLAllIn3PlayersFunctionalTest extends AbstractPokerGameFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(THNLAllIn3PlayersFunctionalTest.class);
    private static final String DAVID = "David";
    private static final String CELINE = "Celine";
    private static final String BJORN = "Bjorn";

    @Test
    public void testAllIn() {
        try {
            log.info("Testing going all-in in the game");

            // do game creation with test
            PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();

            // do users joining game with tests
            handle3PeopleJoiningGame(game, new BigDecimal(100), new BigDecimal(150), new BigDecimal(200));

            // hand creation with tests
            Hand hand = handleNewHand(game);

            // posting of successful blinds with tests
            hand = handleSmallAndBigBlinds(hand);

            BigDecimal currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(15).floatValue(), currentPot.floatValue(), 0);

            log.info("Now it is time to bet. The guy to the left of the dealer goes first. In this scenario, everyone is going to call");
            Boolean isProgressable = pokerGameService.isProgressable(hand.getId());
            assertTrue("Hand is not progressable although it should be", isProgressable);

            log.info("Progressing hand once more");
            pokerGameService.doProgressHand(hand.getId());
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("New hand status: " + hand.getStatus());
            assertEquals("Incorrect hand status", HandStatus.POCKET_CARDS, hand.getStatus());

            Boolean isBettable = pokerGameService.isBettable(hand.getId());
            assertTrue("Betting should not be over yet", isBettable);

            log.info("In round number: " + hand.getCurrentRoundNumber());
            assertEquals("Round number is incorrect", 1, hand.getCurrentRoundNumber(), 0);

            log.info("Retrieving the next gambler to bet");
            Gambler gambler = pokerGameService.findCurrentGambler(hand.getId());
            assertNotNull("Gambler is null", gambler);
            log.info("Gambler retrieved successfully: " + gambler);

            log.info("This gambler should in our case be the dealer as we are only 3 players");
            assertEquals("Gambler should be dealer", gambler.getSeatNumber(), hand.getDealerSeat());
            assertEquals("Player should be Celine", CELINE, gambler.getPlayer().getNickname());
            Boolean canCheck = pokerGameService.isCheckable(gambler.getId());
            Boolean canCall = pokerGameService.isCallable(gambler.getId());
            Boolean canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Celine should not be able to check", canCheck);
            assertTrue("Celine should be able to raise", canRaise);
            assertTrue("Celine should be able to call", canCall);

            log.info("Statuses for Celine are good");

            log.info("Celine wants to raise for this test case");
            BigDecimal minimumRaiseAmount = pokerGameService.findMinimumRaiseAmount(gambler.getId());
            BigDecimal maximumRaiseAmount = pokerGameService.findMaximumRaiseAmount(gambler.getId());
            log.info("Verifying that the minimum raise amount is 10");
            assertEquals("Minimum raise amount is incorrect", new BigDecimal(10).floatValue(), minimumRaiseAmount.floatValue(), 0);
            log.info("Verifying that the maximum raise amount is 190");
            assertEquals("Maximum raise amount is incorrect", new BigDecimal(190).floatValue(), maximumRaiseAmount.floatValue(), 0);

            log.info("Now we raise");
            pokerGameService.doRaise(gambler.getId(), maximumRaiseAmount);
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Celine's raise we should verify that the pot for this hand includes the SB, the BB and this latest raise");
            log.info("Note, the raise implementation currently auto calls any outstanding amount before raising so the pot should be 45 now");
            currentPot = pokerGameService.findPotSize(hand.getId());
            log.info("Verifying pot size is correct after raise");
            assertEquals("The pot size is incorrect", new BigDecimal(215).floatValue(), currentPot.floatValue(), 0);

            log.info("Celine raised successfully. The next gambler should be the small blind and Dave");
            assertNotNull("Gambler should be David but is null", gambler);
            assertEquals("Gambler should be small blind", gambler.getSeatNumber(), hand.getSmallBlindSeat());
            assertEquals("Player should be David", DAVID, gambler.getPlayer().getNickname());

            log.info("David is the current small blind for this hand. Verifying call, check and raise status");
            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("David with the small blind should not be able to check", canCheck);
            assertFalse("David with the small blind should not be able to raise", canRaise);
            assertTrue("David with the small blind should be able to call", canCall);

            log.info("Statuses for David are good");

            log.info("David just wants to call but needs to go all in");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After David's call the pot size should be 360");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(360).floatValue(), currentPot.floatValue(), 0);

            log.info("David called successfully. The next gambler should be the big blind and Bjorn");
            assertNotNull("Gambler should be Bjorn", gambler);
            assertEquals("Gambler should be big blind", gambler.getSeatNumber(), hand.getBigBlindSeat());
            assertEquals("Player should be Bjorn", BJORN, gambler.getPlayer().getNickname());

            log.info("Bjorn is the current big blind for this hand. Verifying call, check and raise status");
            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Bjorn with the big blind should not be able to check", canCheck);
            assertFalse("Bjorn with the big blind should not be able to raise", canRaise);
            assertTrue("Bjorn with the big blind should be able to call", canCall);

            log.info("Statuses for Bjorn are good");

            log.info("Bjorn just wants to call but also needs to go all in");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Bjorn's call the pot size should be 450");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(450).floatValue(), currentPot.floatValue(), 0);

            log.info("The gambler should now return null as there are no more gamblers there to bet");
            assertNull("Gambler isn't null", gambler);

            List<Gambler> gamblers = pokerGameService.findGamblers(hand.getId());
            assertNotNull("Gamblers is null for hand id: " + hand.getId());

            log.info("Let's make sure that all the gamblers have the right account balance after the initial bet");
            for (Gambler g : gamblers) {
                BigDecimal balance = pokerGameService.findBalance(game.getId(), g.getPlayer().getId());
                assertEquals("Incorrect account balance for " + g.getPlayer().getNickname(), new BigDecimal(0).floatValue(), balance.floatValue(), 0);
            }

            log.info("This should mark the end of this round (the pocket cards). Let's verify.");
            isBettable = pokerGameService.isBettable(hand.getId());
            isProgressable = pokerGameService.isProgressable(hand.getId());
            assertFalse("Gamblers can still bet on hand", isBettable);
            assertTrue("Hand is not progressable although it should be", isProgressable);

            log.info("Let's progress to the next stage");
            pokerGameService.doProgressHand(hand.getId());
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("New hand status: " + hand.getStatus());
            assertEquals("Incorrect hand status", HandStatus.FLOP, hand.getStatus());

            log.info("We are in the FLOP stage. We shouldn't be able to bet and we should just progress the game.");
            isBettable = pokerGameService.isBettable(hand.getId());
            assertFalse("Betting should be over for everyone went all-in", isBettable);
            isProgressable = pokerGameService.isProgressable(hand.getId());
            assertTrue("Hand is not progressable although it should be", isProgressable);

            log.info("Let's progress to the next stage");
            pokerGameService.doProgressHand(hand.getId());
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("New hand status: " + hand.getStatus());
            assertEquals("Incorrect hand status", HandStatus.TURN, hand.getStatus());

            log.info("We are in the TURN stage. We shouldn't be able to bet and we should just progress the game.");
            isBettable = pokerGameService.isBettable(hand.getId());
            assertFalse("Betting should be over for everyone went all-in", isBettable);
            isProgressable = pokerGameService.isProgressable(hand.getId());
            assertTrue("Hand is not progressable although it should be", isProgressable);

            log.info("Let's progress to the next stage");
            pokerGameService.doProgressHand(hand.getId());
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("New hand status: " + hand.getStatus());
            assertEquals("Incorrect hand status", HandStatus.RIVER, hand.getStatus());

            log.info("We are in the RIVER stage. We shouldn't be able to bet and we should just progress the game.");
            isBettable = pokerGameService.isBettable(hand.getId());
            assertFalse("Betting should be over for everyone went all-in", isBettable);
            isProgressable = pokerGameService.isProgressable(hand.getId());
            assertTrue("Hand is not progressable although it should be", isProgressable);

            log.info("Let's progress to the next stage");
            String handId = hand.getId();
            pokerGameService.doProgressHand(handId);
            hand = pokerGameService.findCurrentHand(game.getId());

            log.info("Hand should be null now thatt he game is over");
            assertNull("Hand is not null", hand);
            handleShowdown(handId);
        } catch (GameException e) {
            log.error(e.getTranslatedMessage(), e);
            fail(e.getTranslatedMessage());
        }
    }

    private void handleShowdown(String handId) throws GameException {
        Hand hand = pokerGameService.findHand(handId);
        log.info("Retieving the hand we just completed");
        assertNotNull("Hand is null", hand);
        
        log.info("New hand status: " + hand.getStatus());
        assertEquals("Status should be complete", HandStatus.COMPLETE, hand.getStatus());

        log.info("Retrieving winners and losers");
        List<Gambler> winners = pokerGameService.findWinners(hand.getId());
        List<Gambler> losers = pokerGameService.findLosers(hand.getId());
        BigDecimal potAmount = pokerGameService.findPotSize(hand.getId());
        log.info("Pot amount for hand is: " + potAmount);

        log.warn("The below asserts WILL fail if there were more than one winner!! FYI.");

        assertNotNull("There are no winners", winners);

        if (winners.size() == 1) {
            assertEquals("More than one winner and the below tests will fail", 1, winners.size(), 0);
            Gambler winner = winners.get(0);
            assertNotNull("There are no losers", losers);
            assertNotNull("There is no pot for the hand", potAmount);
            assertEquals("Pot amount is not as expected", new BigDecimal(450.00).floatValue(), potAmount.floatValue(), 0);

            log.info("Printing out winners with hand information");

            log.info("Winner: " + winner.getPlayer().getNickname() + ". Cards: " + winner.getHandName() + ". Card rank: " + winner.getHandRank());
            BigDecimal win = pokerGameService.findWinnerAmount(hand.getGame().getId(), winner.getPlayer().getId());

            if (StringUtils.equals(winner.getPlayer().getNickname(), CELINE)) {
                log.info(winner.getPlayer().getNickname() + " is the winner. Player should've won: " + potAmount.toString());
                assertTrue("Winner didn't win anything", win.compareTo(new BigDecimal(450.00)) == 0);

                log.info("Printing out losers with hand information");
                for (Gambler loser : losers) {
                    log.info("Loser: " + loser.getPlayer().getNickname() + ". Cards: " + loser.getHandName() + ". Card rank: " + loser.getHandRank());
                    BigDecimal loss = pokerGameService.findLoserAmount(hand.getGame().getId(), hand.getId(), loser.getPlayer().getId());

                    if (StringUtils.equals(loser.getPlayer().getNickname(), DAVID)) {
                        log.info(loser.getPlayer().getNickname() + " is a loser");
                        BigDecimal expectedLoss = new BigDecimal(150.00);
                        assertTrue("Loser: " + loser.getPlayer().getNickname() + " should've lost: " + expectedLoss.toString() + ", but was: " + loss.toString(), loss.compareTo(expectedLoss) == 0);
                    } else if (StringUtils.equals(loser.getPlayer().getNickname(), BJORN)) {
                        log.info(loser.getPlayer().getNickname() + " is a loser");
                        BigDecimal expectedLoss = new BigDecimal(100.00);
                        assertTrue("Loser: " + loser.getPlayer().getNickname() + " should've lost: " + expectedLoss.toString() + ", but was: " + loss.toString(), loss.compareTo(expectedLoss) == 0);
                    }
                }
            } else if (StringUtils.equals(winner.getPlayer().getNickname(), DAVID)) {
                log.info(winner.getPlayer().getNickname() + " is the winner. Player should've won: " + potAmount.toString());
                assertTrue("Winner didn't win anything", win.compareTo(new BigDecimal(400.00)) == 0);

                log.info("Printing out losers with hand information");
                for (Gambler loser : losers) {
                    log.info("Loser: " + loser.getPlayer().getNickname() + ". Cards: " + loser.getHandName() + ". Card rank: " + loser.getHandRank());
                    BigDecimal loss = pokerGameService.findLoserAmount(hand.getGame().getId(), hand.getId(), loser.getPlayer().getId());

                    if (StringUtils.equals(loser.getPlayer().getNickname(), BJORN)) {
                        log.info(loser.getPlayer().getNickname() + " is a loser");
                        BigDecimal expectedLoss = new BigDecimal(100.00);
                        assertTrue("Loser: " + loser.getPlayer().getNickname() + " should've lost: " + expectedLoss.toString() + ", but was: " + loss.toString(), loss.compareTo(expectedLoss) == 0);
                    } else if (StringUtils.equals(loser.getPlayer().getNickname(), CELINE)) {
                        log.info(loser.getPlayer().getNickname() + " is a loser");
                        BigDecimal expectedLoss = new BigDecimal(150.00);
                        assertTrue("Loser: " + loser.getPlayer().getNickname() + " should've lost: " + expectedLoss.toString() + ", but was: " + loss.toString(), loss.compareTo(expectedLoss) == 0);
                    }
                }
            } else if (StringUtils.equals(winner.getPlayer().getNickname(), BJORN)) {
                log.info(winner.getPlayer().getNickname() + " is the winner. Player should've won: " + potAmount.toString());
                assertTrue("Winner didn't win anything", win.compareTo(new BigDecimal(300.00)) == 0);

                log.info("Printing out losers with hand information");
                for (Gambler loser : losers) {
                    log.info("Loser: " + loser.getPlayer().getNickname() + ". Cards: " + loser.getHandName() + ". Card rank: " + loser.getHandRank());
                    BigDecimal loss = pokerGameService.findLoserAmount(hand.getGame().getId(), hand.getId(), loser.getPlayer().getId());

                    if (StringUtils.equals(loser.getPlayer().getNickname(), CELINE)) {
                        log.info(loser.getPlayer().getNickname() + " is a loser");
                        BigDecimal expectedLoss = new BigDecimal(100.00);
                        assertTrue("Loser: " + loser.getPlayer().getNickname() + " should've lost: " + expectedLoss.toString() + ", but was: " + loss.toString(), loss.compareTo(expectedLoss) == 0);
                    } else if (StringUtils.equals(loser.getPlayer().getNickname(), DAVID)) {
                        log.info(loser.getPlayer().getNickname() + " is a loser");
                        BigDecimal expectedLoss = new BigDecimal(100.00);
                        assertTrue("Loser: " + loser.getPlayer().getNickname() + " should've lost: " + expectedLoss.toString() + ", but was: " + loss.toString(), loss.compareTo(expectedLoss) == 0);
                    }
                }
            }
        }
    }
}
