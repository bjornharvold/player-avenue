/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.game;

import com.online.casino.exception.GameException;
import com.online.casino.game.TexasHoldemPokerGame;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Bjorn Harvold
 * Date: Sep 21, 2009
 * Time: 1:08:41 AM
 * Responsibility:
 */
public class TexasHoldemGameTest extends TestCase {
    private final static Logger log = LoggerFactory.getLogger(TexasHoldemGameTest.class);

    @Test
    public void testGame() {
        try {
            List<String> playerIds = new ArrayList<String>();
            playerIds.add("1");
            playerIds.add("2");
            playerIds.add("3");
            playerIds.add("4");

            TexasHoldemPokerGame game = new TexasHoldemPokerGame();
            game.shuffle();
            TexasHoldemPokerGame game2 = new TexasHoldemPokerGame(game.getDeck());

            log.info("Testing deck equality after serialization");
            assertEquals("Decks don't match", game.getDeck(), game2.getDeck());
            log.info("Test passed");

            // move some cards
            game.dealPocketCards(playerIds);
            game.dealFlop();

            game2 = new TexasHoldemPokerGame(game.getDeck());
            log.info("Testing deck equality again after some cards have been dealt and after serialization");
            assertEquals("Decks don't match", game.getDeck(), game2.getDeck());
            log.info("Test passed");
        } catch (GameException e) {
            fail(e.getMessage());
        }
    }
}
