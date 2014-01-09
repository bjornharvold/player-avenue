/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.test.service;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.enums.HandStatus;
import com.online.casino.exception.GameException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.Assert.*;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility:
 */
public class FoldingAndLeavingFunctionalTest extends AbstractPokerGameFunctionalTest {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(FoldingAndLeavingFunctionalTest.class);

    //~--- methods ------------------------------------------------------------

    /**
     * This method will only test the essentials of the game engine but put emphasis on what it really wants to test.
     * More detailed tests have already been run in other integration tests.
     */
    @Test
    public void testFoldingDuringAGame() {
        try {
            log.info("Testing folding during a game...");

            // do game creation with test
            PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();

            // do users joining game with tests
            handle3PeopleJoiningGame(game, DEFAULT_BUYIN, DEFAULT_BUYIN, DEFAULT_BUYIN);

            // hand creation with tests
            Hand hand = handleNewHand(game);

            // progress the hand to small blind post
            assertTrue("Hand should be progressable", pokerGameService.isProgressable(hand.getId()));
            pokerGameService.doProgressHand(hand.getId());

            // get the latest hand
            hand = pokerGameService.findCurrentHand(game.getId());

            // get the small blind
            log.info("Retrieving current gambler (SB)");
            Gambler currentGambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("Posting small blind...");
            pokerGameService.doPostSmallBlind(hand.getId());
            log.info("We posted the small blind.");

            log.info("Retrieving new hand instance");
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("The hand state should be POST_SMALL_BLIND_COMPLETE");
            assertEquals("Hand state is not correct", HandStatus.POST_SMALL_BLIND_COMPLETE, hand.getStatus());
            log.info("Hand status is correct: " + hand.getStatus());

            assertTrue("Hand should be progressable", pokerGameService.isProgressable(hand.getId()));
            log.info("Now we want to progress the hand");
            pokerGameService.doProgressHand(hand.getId());
            log.info("Progressed hand successfully");
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("The hand state should be POST_BIG_BLIND");
            assertEquals("Hand state is not correct", HandStatus.POST_BIG_BLIND, hand.getStatus());
            log.info("Hand status is correct: " + hand.getStatus());

            log.info("Retrieving current gambler (BB)");
            currentGambler = pokerGameService.findCurrentGambler(hand.getId());

            log.info("Posting big blind...");
            pokerGameService.doPostBigBlind(hand.getId());
            log.info("We posted the big blind.");

            log.info("Retrieving new hand instance");
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("The hand state should be POST_BIG_BLIND_COMPLETE");
            assertEquals("Hand state is not correct", HandStatus.POST_BIG_BLIND_COMPLETE, hand.getStatus());
            log.info("Hand status is correct: " + hand.getStatus());

            assertTrue("Hand should be progressable", pokerGameService.isProgressable(hand.getId()));
            log.info("Now we want to progress the hand");
            pokerGameService.doProgressHand(hand.getId());
            log.info("Progressed hand successfully");
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("The hand state should be POCKET_CARDS");
            assertEquals("Hand state is not correct", HandStatus.POCKET_CARDS, hand.getStatus());
            log.info("Hand status is correct: " + hand.getStatus());

            log.info("Retrieving current gambler (Dealer)");
            currentGambler = pokerGameService.findCurrentGambler(hand.getId());
            pokerGameService.doCall(currentGambler.getId());
            log.info("Dealer called.");

            log.info("Retrieving current gambler (Small Blind)");
            currentGambler = pokerGameService.findCurrentGambler(hand.getId());
            pokerGameService.doCall(currentGambler.getId());
            log.info("SB called.");

            log.info("Retrieving current gambler (Big Blind)");
            currentGambler = pokerGameService.findCurrentGambler(hand.getId());
            pokerGameService.doCheck(currentGambler.getId());
            log.info("BB checked.");

            assertTrue("Hand should be progressable", pokerGameService.isProgressable(hand.getId()));
            log.info("Now we want to progress the hand");
            pokerGameService.doProgressHand(hand.getId());
            log.info("Progressed hand successfully");
            hand = pokerGameService.findCurrentHand(game.getId());
            log.info("The hand state should be FLOP");
            assertEquals("Hand state is not correct", HandStatus.FLOP, hand.getStatus());
            log.info("Hand status is correct: " + hand.getStatus());

            log.info("Retrieving current gambler (Small Blind)");
            currentGambler = pokerGameService.findCurrentGambler(hand.getId());
            pokerGameService.doRaise(currentGambler.getId(), new BigDecimal(20));
            log.info("SB raised.");

            log.info("Retrieving current gambler (Big Blind)");
            currentGambler = pokerGameService.findCurrentGambler(hand.getId());
            pokerGameService.doFold(currentGambler.getId());
            log.info("BB folder.");

            log.info("Retrieving current gambler (Dealer)");
            currentGambler = pokerGameService.findCurrentGambler(hand.getId());
            pokerGameService.doFold(currentGambler.getId());
            log.info("Dealer folder.");

            log.info("At this time the game should not be progressable but it should be end game");
            assertFalse("Hand should not be progressable", pokerGameService.isProgressable(hand.getId()));
            assertTrue("Hand should be end game", pokerGameService.isEndGame(hand.getId()));

            log.info("Running end game process");
            pokerGameService.doEndGame(hand.getId());

            String handId = hand.getId();
            hand = pokerGameService.findCurrentHand(game.getId());
            assertNull("Current hand is not null", hand);

            hand = pokerGameService.findHand(handId);
            log.info("The hand state should be COMPLETE");
            assertEquals("Hand state is not correct", HandStatus.COMPLETE, hand.getStatus());
            log.info("Hand status is correct: " + hand.getStatus());
            assertFalse("Hand should not be progressable", pokerGameService.isProgressable(hand.getId()));
            assertFalse("Hand should not be bettable", pokerGameService.isBettable(hand.getId()));

            log.info("Tested folding during a game successfully");
        } catch (GameException e) {
            log.error(e.getTranslatedMessage(), e);
            fail(e.getTranslatedMessage());
        }
    }
}
