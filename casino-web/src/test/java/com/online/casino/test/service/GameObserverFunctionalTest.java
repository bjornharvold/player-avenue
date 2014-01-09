/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold. 
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.GameObserver;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.exception.GameException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility:
 */
public class GameObserverFunctionalTest extends AbstractPokerGameFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(GameObserverFunctionalTest.class);
    private static final String[] players = {"David", "Celine", "Bjorn"};

    @Test
    public void testGameObservers() {
        log.info("Testing game observers");

        // do game creation with test
        PokerGame game = handleTexasHoldemNoLimitPokerGameCreation();

        log.info("Adding " + players.length + " as observers to game id: " + game.getId());

        List<Player> list = new ArrayList<Player>();

        for (String player : players) {
            list.add(administrationService.findPlayerByNickname(player));
        }

        for (Player player : list) {
            assertNotNull("Player is null", player);
            assertNotNull("Player is transient", player.getId());
            log.info("Now player: " + player.getNickname() + " wants to observe the game.");

            pokerGameService.doWatchGame(game.getId(), player.getId());
            GameObserver go = pokerGameService.findGameObserver(game.getId(), player.getId());

            assertNotNull("Missing game observer for player: " + player.getNickname(), go);
            assertNotNull("GameObserver is transient: " + player.getNickname(), go.getId());
        }

        log.info("All players are observing the game. Let's do a quick count of game observers");

        Long count = pokerGameService.findGameObserverCount(game.getId());

        assertEquals("GameObserver count doesn't match", players.length, count.intValue());
        log.info("Now remove those same game observers");

        List<GameObserver> gsList = pokerGameService.findGameObservers(game.getId(), null, null);

        assertNotNull("GameObserver list is null", gsList);

        for (GameObserver go : gsList) {
            pokerGameService.doUnwatchGame(go.getId());
        }

        log.info("All observers were removed. This time the game observer count should be 0");
        count = pokerGameService.findGameObserverCount(game.getId());
        assertEquals("GameObserver count doesn't match", 0, count.intValue());
        log.info("All Game Observer tests completed successfully");
    }
}
