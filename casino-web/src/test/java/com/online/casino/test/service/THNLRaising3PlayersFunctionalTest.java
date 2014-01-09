/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.enums.HandStatus;
import com.online.casino.exception.GameException;
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
public class THNLRaising3PlayersFunctionalTest extends AbstractPokerGameFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(THNLRaising3PlayersFunctionalTest.class);

    @Test
    public void testRaising() {
        try {
            log.info("Testing raising in the game");

            // do game creation with test
            PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();

            // do users joining game with tests
            handle3PeopleJoiningGame(game, null, null, null);

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

            assertEquals("Player should be Celine", "Celine", gambler.getPlayer().getNickname());
            Gambler celine = gambler;
            Boolean canCheck = pokerGameService.isCheckable(gambler.getId());
            Boolean canCall = pokerGameService.isCallable(gambler.getId());
            Boolean canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Celine should not be able to check", canCheck);
            assertTrue("Celine should be able to raise", canRaise);
            assertTrue("Celine should be able to call", canCall);

            log.info("Statuses for Celine are good");

            log.info("Celine wants to raise for this test case");
            BigDecimal minimumRaiseAmount = pokerGameService.findMinimumRaiseAmount(celine.getId());
            BigDecimal maximumRaiseAmount = pokerGameService.findMaximumRaiseAmount(celine.getId());
            log.info("Verifying that the minimum raise amount is 10");
            assertEquals("Minimum raise amount is incorrect", new BigDecimal(10).floatValue(), minimumRaiseAmount.floatValue(), 0);
            log.info("Verifying that the maximum raise amount is 90");
            assertEquals("Maximum raise amount is incorrect", new BigDecimal(90).floatValue(), maximumRaiseAmount.floatValue(), 0);

            log.info("Now we can raise");
            pokerGameService.doRaise(gambler.getId(), minimumRaiseAmount);
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Celine's raise we should verify that the pot for this hand includes the SB, the BB and this latest raise");
            log.info("Note, the raise implementation currently auto calls any outstanding amount before raising so the pot should be 35 now");
            currentPot = pokerGameService.findPotSize(hand.getId());
            log.info("Verifying pot size is correct after raise");
            assertEquals("The pot size is incorrect", new BigDecimal(35).floatValue(), currentPot.floatValue(), 0);

            log.info("Celine raised successfully. The next gambler should be the small blind and Dave");
            assertNotNull("Gambler should be David but is null", gambler);
            assertEquals("Gambler should be small blind", gambler.getSeatNumber(), hand.getSmallBlindSeat());
            assertEquals("Player should be David", "David", gambler.getPlayer().getNickname());
            Gambler david = gambler;

            log.info("David is the current small blind for this hand. Verifying call, check and raise status");
            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("David with the small blind should not be able to check", canCheck);
            assertTrue("David with the small blind should be able to raise", canRaise);
            assertTrue("David with the small blind should be able to call", canCall);

            log.info("Statuses for David are good");

            log.info("David just wants to call");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After David's call the pot size should be 50");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(50).floatValue(), currentPot.floatValue(), 0);

            log.info("David called successfully. The next gambler should be the big blind and Bjorn");
            assertNotNull("Gambler should be Bjorn", gambler);
            assertEquals("Gambler should be big blind", gambler.getSeatNumber(), hand.getBigBlindSeat());
            assertEquals("Player should be Bjorn", "Bjorn", gambler.getPlayer().getNickname());
            Gambler bjorn = gambler;

            log.info("Bjorn is the current big blind for this hand. Verifying call, check and raise status");
            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Bjorn with the big blind should not be able to check", canCheck);
            assertTrue("Bjorn with the big blind should be able to raise", canRaise);
            assertTrue("Bjorn with the big blind should be able to call", canCall);

            log.info("Statuses for Bjorn are good");

            log.info("Bjorn just wants to call");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Bjorn's call the pot size should be 60");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(60).floatValue(), currentPot.floatValue(), 0);

            log.info("The gambler should now return null as there are no more gamblers there to bet");
            assertNull("Gambler isn't null", gambler);

            log.info("Let's make sure that all the gamblers have the right account balance after the initial bet");
            BigDecimal bjornBalance = pokerGameService.findBalance(game.getId(), bjorn.getPlayer().getId());
            BigDecimal davidBalance = pokerGameService.findBalance(game.getId(), david.getPlayer().getId());
            BigDecimal celineBalance = pokerGameService.findBalance(game.getId(), celine.getPlayer().getId());

            assertEquals("Incorrect account balance for Bjorn", new BigDecimal(80).floatValue(), bjornBalance.floatValue(), 0);
            assertEquals("Incorrect account balance for David", new BigDecimal(80).floatValue(), davidBalance.floatValue(), 0);
            assertEquals("Incorrect account balance for Celine", new BigDecimal(80).floatValue(), celineBalance.floatValue(), 0);

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

            isBettable = pokerGameService.isBettable(hand.getId());
            assertTrue("Betting should not be over yet", isBettable);

            log.info("In round number: " + hand.getCurrentRoundNumber());
            assertEquals("Round number is incorrect", 2, hand.getCurrentRoundNumber(), 0);

            log.info("Retrieving the next gambler to bet");
            gambler = pokerGameService.findCurrentGambler(hand.getId());
            assertNotNull("Gambler is null", gambler);
            log.info("Gambler retrieved successfully: " + gambler);

            log.info("Gambler should be Celine");
            assertEquals("Player should be Celine", "Celine", gambler.getPlayer().getNickname());
            log.info("This gambler should in our case be the dealer as we are only 3 players");
            assertEquals("Gambler should be in dealer seat number", gambler.getSeatNumber(), hand.getDealerSeat());
            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertTrue("Celine should be able to check", canCheck);
            assertTrue("Celine should be able to raise", canRaise);
            assertFalse("Celine should not be able to call", canCall);

            log.info("Statuses for Celine are good");
            log.info("Celine is feeling confident and raises again");

            minimumRaiseAmount = pokerGameService.findMinimumRaiseAmount(gambler.getId());
            log.info("Verifying that the minimum raise amount is 10");
            assertEquals("Minimum raise amount is incorrect", new BigDecimal(10).floatValue(), minimumRaiseAmount.floatValue(), 0);

            log.info("Celine raises again by 10");
            pokerGameService.doRaise(gambler.getId(), minimumRaiseAmount);
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            currentPot = pokerGameService.findPotSize(hand.getId());
            log.info("Verifying pot size is correct after raise");
            assertEquals("The pot size is incorrect", new BigDecimal(70).floatValue(), currentPot.floatValue(), 0);

            log.info("Celine raised successfully. The next gambler should be the small blind and Dave");
            assertNotNull("Gambler should be Dave", gambler);
            assertEquals("Player should be David", "David", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("David should not be able to check", canCheck);
            assertTrue("David should be able to raise", canRaise);
            assertTrue("David should be able to call", canCall);

            log.info("Statuses for David are good");

            log.info("David raises again by 10");
            pokerGameService.doRaise(gambler.getId(), minimumRaiseAmount);
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After David's call the pot size should be 90");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(90).floatValue(), currentPot.floatValue(), 0);

            log.info("David raised successfully. The next gambler should be Bjorn");
            assertNotNull("Gambler should be Bjorn", gambler);
            assertEquals("Player should be Bjorn", "Bjorn", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Bjorn with the big blind should not be able to check", canCheck);
            assertTrue("Bjorn with the big blind should be able to raise", canRaise);
            assertTrue("Bjorn with the big blind should be able to call", canCall);

            log.info("Statuses for Bjorn are good");

            log.info("Bjorn just wants to call");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Bjorn's call the pot size should be 110");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(110).floatValue(), currentPot.floatValue(), 0);

            log.info("Bjorn raised successfully. The next gambler should be Celine again");
            assertNotNull("Gambler should be Celine", gambler);
            assertEquals("Player should be Celine", "Celine", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Celine should not be able to check", canCheck);
            assertTrue("Celine should be able to raise", canRaise);
            assertTrue("Celine should be able to call", canCall);

            log.info("Statuses for Celine are good");

            log.info("Celine just wants to call this time around");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Celines's call the pot size should be 120");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(120).floatValue(), currentPot.floatValue(), 0);

            log.info("The gambler should now return null as there are no more gamblers there to bet");
            assertNull("Gambler isn't null", gambler);

            log.info("Let's make sure that all the gamblers have the right account balance after the initial bet");
            bjornBalance = pokerGameService.findBalance(game.getId(), bjorn.getPlayer().getId());
            davidBalance = pokerGameService.findBalance(game.getId(), david.getPlayer().getId());
            celineBalance = pokerGameService.findBalance(game.getId(), celine.getPlayer().getId());

            assertEquals("Incorrect account balance for Bjorn", new BigDecimal(60).floatValue(), bjornBalance.floatValue(), 0);
            assertEquals("Incorrect account balance for David", new BigDecimal(60).floatValue(), davidBalance.floatValue(), 0);
            assertEquals("Incorrect account balance for Celine", new BigDecimal(60).floatValue(), celineBalance.floatValue(), 0);

            log.info("This should mark the end of this round (the flop). Let's verify.");
            isBettable = pokerGameService.isBettable(hand.getId());
            isProgressable = pokerGameService.isProgressable(hand.getId());
            assertFalse("Gamblers can still bet on hand", isBettable);
            assertTrue("Hand is not progressable although it should be", isProgressable);

            log.info("Let's progress to the next stage");
            pokerGameService.doProgressHand(hand.getId());
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("New hand status: " + hand.getStatus());
            assertEquals("Incorrect hand status", HandStatus.TURN, hand.getStatus());

            isBettable = pokerGameService.isBettable(hand.getId());
            assertTrue("Betting should not be over yet", isBettable);

            log.info("In round number: " + hand.getCurrentRoundNumber());
            assertEquals("Round number is incorrect", 3, hand.getCurrentRoundNumber(), 0);

            log.info("Retrieving the next gambler to bet");
            gambler = pokerGameService.findCurrentGambler(hand.getId());
            assertNotNull("Gambler is null", gambler);
            log.info("Gambler retrieved successfully: " + gambler);

            log.info("Gambler should be Celine");
            assertEquals("Player should be Celine", "Celine", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertTrue("Celine should be able to check", canCheck);
            assertTrue("Celine should be able to raise", canRaise);
            assertFalse("Celine should not be able to call", canCall);

            log.info("Statuses for Celine are good");
            log.info("Celine wants to just check this time around");
            pokerGameService.doCheck(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());
            log.info("After Celine's check, the pot size should still be 120");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(120).floatValue(), currentPot.floatValue(), 0);

            log.info("Celine checked successfully. The next gambler should be Dave");
            assertNotNull("Gambler should be David", gambler);
            assertEquals("Player should be David", "David", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertTrue("David should be able to check", canCheck);
            assertTrue("David should be able to raise", canRaise);
            assertFalse("David should not be able to call", canCall);

            log.info("Statuses for David are good");

            log.info("David wants to check as well");
            pokerGameService.doCheck(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After David's check the pot size should still be 120");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(120).floatValue(), currentPot.floatValue(), 0);

            log.info("David checked successfully. The next gambler should be Bjorn");
            assertNotNull("Gambler should be Bjorn", gambler);
            assertEquals("Player should be Bjorn", "Bjorn", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertTrue("Bjorn should be able to check", canCheck);
            assertTrue("Bjorn should be able to raise", canRaise);
            assertFalse("Bjorn should not be able to call", canCall);

            log.info("Statuses for Bjorn are good");

            log.info("Bjorn wants to raise and she doesn't need to call first");
            maximumRaiseAmount = pokerGameService.findMaximumRaiseAmount(gambler.getId());
            log.info("Verifying that the maximum raise amount is 60");
            assertEquals("Maximum raise amount is incorrect", new BigDecimal(60).floatValue(), maximumRaiseAmount.floatValue(), 0);

            pokerGameService.doRaise(gambler.getId(), maximumRaiseAmount);
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Bjorn's raise the pot size should be 180");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(180).floatValue(), currentPot.floatValue(), 0);

            log.info("Bjorn raised successfully. The next gambler should be Celine");
            assertNotNull("Gambler should be Celine", gambler);
            assertEquals("Player should be Celine", "Celine", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("Celine should not be able to check", canCheck);
            assertFalse("Celine should not be able to raise", canRaise);
            assertTrue("Celine should be able to call", canCall);

            log.info("Statuses for Celine are good");

            log.info("Celine wants to call");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Celine's call the pot size should be 240");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(240).floatValue(), currentPot.floatValue(), 0);

            log.info("Celine called successfully. The next gambler should be David");
            assertNotNull("Gambler should be David", gambler);
            assertEquals("Player should be David", "David", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertFalse("David should not be able to check", canCheck);
            assertFalse("David should not be able to raise", canRaise);
            assertTrue("David should be able to call", canCall);

            log.info("Statuses for David are good");

            log.info("David wants to call");
            pokerGameService.doCall(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After David's call the pot size should be 300");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(300).floatValue(), currentPot.floatValue(), 0);

            log.info("Let's make sure that all the gamblers have the right account balance after the initial bet");
            bjornBalance = pokerGameService.findBalance(game.getId(), bjorn.getPlayer().getId());
            davidBalance = pokerGameService.findBalance(game.getId(), david.getPlayer().getId());
            celineBalance = pokerGameService.findBalance(game.getId(), celine.getPlayer().getId());

            assertEquals("Incorrect account balance for Bjorn", new BigDecimal(0).floatValue(), bjornBalance.floatValue(), 0);
            assertEquals("Incorrect account balance for David", new BigDecimal(0).floatValue(), davidBalance.floatValue(), 0);
            assertEquals("Incorrect account balance for Celine", new BigDecimal(0).floatValue(), celineBalance.floatValue(), 0);

            log.info("This should mark the end of this round (the turn). Let's verify.");
            isBettable = pokerGameService.isBettable(hand.getId());
            isProgressable = pokerGameService.isProgressable(hand.getId());
            assertFalse("Gamblers can still bet on hand", isBettable);
            assertTrue("Hand is not progressable although it should be", isProgressable);

            log.info("Let's progress to the next stage");
            pokerGameService.doProgressHand(hand.getId());
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("New hand status: " + hand.getStatus());
            assertEquals("Incorrect hand status", HandStatus.RIVER, hand.getStatus());

            isBettable = pokerGameService.isBettable(hand.getId());
            assertTrue("Betting should not be over yet", isBettable);

            log.info("In round number: " + hand.getCurrentRoundNumber());
            assertEquals("Round number is incorrect", 4, hand.getCurrentRoundNumber(), 0);

            log.info("Retrieving the next gambler to bet");
            gambler = pokerGameService.findCurrentGambler(hand.getId());
            assertNotNull("Gambler is null", gambler);
            log.info("Gambler retrieved successfully: " + gambler);

            log.info("Gambler should be Celine");
            assertEquals("Player should be Celine", "Celine", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertTrue("Celine should be able to check", canCheck);
            assertFalse("Celine should not be able to raise", canRaise);
            assertFalse("Celine should not be able to call", canCall);

            log.info("Statuses for Celine are good");

            log.info("Celine can only check at this point because the pot is full");
            pokerGameService.doCheck(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Celine's check the pot size should still be 300");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(300).floatValue(), currentPot.floatValue(), 0);

            log.info("Celine checked successfully. The next gambler should be David");
            assertNotNull("Gambler should be David", gambler);
            assertEquals("Player should be David", "David", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertTrue("David should be able to check", canCheck);
            assertFalse("David should not be able to raise", canRaise);
            assertFalse("David should not be able to call", canCall);

            log.info("Statuses for David are good");

            log.info("David wants to check as well because he doesn't have any other option either");
            pokerGameService.doCheck(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After David's check the pot size should still be 300");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(300).floatValue(), currentPot.floatValue(), 0);

            log.info("David checked successfully. The next gambler should be Bjorn");
            assertNotNull("Gambler should be Bjorn", gambler);
            assertEquals("Player should be Bjorn", "Bjorn", gambler.getPlayer().getNickname());

            canCheck = pokerGameService.isCheckable(gambler.getId());
            canCall = pokerGameService.isCallable(gambler.getId());
            canRaise = pokerGameService.isRaiseable(gambler.getId());
            assertTrue("Bjorn should be able to check", canCheck);
            assertFalse("Bjorn should be able to raise", canRaise);
            assertFalse("Bjorn should not be able to call", canCall);

            log.info("Statuses for Bjorn are good");
            pokerGameService.doCheck(gambler.getId());
            gambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("After Bjorn's check the pot size should still be 300");
            currentPot = pokerGameService.findPotSize(hand.getId());
            assertEquals("The pot size is incorrect", new BigDecimal(300).floatValue(), currentPot.floatValue(), 0);

            log.info("Let's make sure that all the gamblers have the right account balance after the initial bet");
            bjornBalance = pokerGameService.findBalance(game.getId(), bjorn.getPlayer().getId());
            davidBalance = pokerGameService.findBalance(game.getId(), david.getPlayer().getId());
            celineBalance = pokerGameService.findBalance(game.getId(), celine.getPlayer().getId());

            assertEquals("Incorrect account balance for Bjorn", new BigDecimal(0).floatValue(), bjornBalance.floatValue(), 0);
            assertEquals("Incorrect account balance for David", new BigDecimal(0).floatValue(), davidBalance.floatValue(), 0);
            assertEquals("Incorrect account balance for Celine", new BigDecimal(0).floatValue(), celineBalance.floatValue(), 0);

            log.info("This should mark the end of this round (the river). Let's verify.");
            isBettable = pokerGameService.isBettable(hand.getId());
            isProgressable = pokerGameService.isProgressable(hand.getId());
            assertFalse("Gamblers can still bet on hand", isBettable);
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

        if (winners.size() > 1) {
            log.warn("There are more than one winner for this game. Not continuing with more tests.");
        } else {
            assertNotNull("There are no winners", winners);
            assertNotNull("There are no losers", losers);
            assertNotNull("There is no pot for the hand", potAmount);
            assertEquals("Pot amount is not as expected", new BigDecimal(300.00).floatValue(), potAmount.floatValue(), 0);

            log.info("Printing out winners with hand information");
            for (Gambler winner : winners) {
                log.info("Winner: " + winner.getPlayer() + ". Cards: " + winner.getHandName() + ". Card rank: " + winner.getHandRank());
                BigDecimal win = pokerGameService.findWinnerAmount(hand.getGame().getId(), winner.getPlayer().getId());
                log.info("Winner should've won :" + potAmount.toString());
                assertTrue("Winner didn't win anything", win.compareTo(new BigDecimal(300.00)) == 0);
            }

            log.info("Printing out losers with hand information");
            for (Gambler loser : losers) {
                log.info("Loser: " + loser.getPlayer() + ". Cards: " + loser.getHandName() + ". Card rank: " + loser.getHandRank());
                BigDecimal loss = pokerGameService.findBetAmount(hand.getId(), loser.getPlayer().getId());
                log.info("Loser should've lost some money: " + loss.toString());
                assertTrue("Winner didn't win anything", loss.compareTo(new BigDecimal(100.00)) == 0);
            }
        }
    }
}
