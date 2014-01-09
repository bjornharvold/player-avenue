/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.game;

import com.online.casino.exception.GameException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Bjorn Harvold
 * Date: Sep 1, 2009
 * Time: 4:40:12 PM
 * Responsibility: The card distributor
 */
public class TexasHoldemPokerGame {
    private final static Logger log = LoggerFactory.getLogger(TexasHoldemPokerGame.class);
    private final static HandEvaluator evaluator = new HandEvaluator();
    private final Deck deck;

    public TexasHoldemPokerGame() {
        deck = new Deck();
    }

    public TexasHoldemPokerGame(String deck) {
        this.deck = new Deck(deck);
    }

    
    public void shuffle() {
        deck.shuffle();
    }
    
    /**
     * This method deals 3 community cards on the table
     *
     * @return
     * @throws com.online.casino.exception.GameException
     */
    
    public String dealFlop() throws GameException {
        SingleHand result = null;

        if (deck.cardsLeft() >= 3) {
            result = new SingleHand();
            result.addCard(deck.dealCard());
            result.addCard(deck.dealCard());
            result.addCard(deck.dealCard());
        } else {
            String errorStr = "There are only " + deck.cardsLeft() + " available in the deck. Cannot issue card(s)";
            throw new GameException(errorStr);
        }

        return result.toString();
    }

    /**
     * This method deals out 2 cards that should be called for each player
     *
     * @return
     * @throws GameException
     */
    
    public String dealPocketCards() throws GameException {
        SingleHand result = null;

        if (deck.cardsLeft() >= 2) {
            result = new SingleHand();
            result.addCard(deck.dealCard());
            result.addCard(deck.dealCard());
        } else {
            String errorStr = "There are only " + deck.cardsLeft() + " available in the deck. Cannot issue card(s)";
            throw new GameException(errorStr);
        }

        return result.toString();
    }

    /**
     * This method deals pocket cards for every player id
     *
     * @param gamblerIds
     * @return
     * @throws GameException
     */
    
    public Map<String, String> dealPocketCards(List<String> gamblerIds) throws GameException {
        if (gamblerIds == null) {
            throw new IllegalArgumentException("gameblerIds cannot be null");
        }

        Map<String, String> result = new HashMap<String, String>();

        for (String id : gamblerIds) {
            result.put(id, dealPocketCards());
        }

        return result;
    }

    /**
     * This method deals single card, mostly used for the river and the turn
     *
     * @return
     * @throws GameException
     */
    
    public String dealCard() throws GameException {
        Card result = null;

        if (deck.cardsLeft() >= 1) {
            result = deck.dealCard();
        } else {
            String errorStr = "There are only " + deck.cardsLeft() + " available in the deck. Cannot issue card(s)";
            throw new GameException(errorStr);
        }

        return result.toString();
    }

    public static Integer rankHand(String cards) {
        if (StringUtils.isBlank(cards)) {
            throw new IllegalArgumentException("cards cannot be null");
        }

        return HandEvaluator.rankHand(new SingleHand(cards));
    }

    public static String getBest5CardHand(String cards) {
        if (StringUtils.isBlank(cards)) {
            throw new IllegalArgumentException("cards cannot be null");
        }

        return evaluator.getBest5CardHand(new SingleHand(cards)).toString();
    }

    public static String nameHand(String cards) {
        if (StringUtils.isBlank(cards)) {
            throw new IllegalArgumentException("cards cannot be null");
        }

        return HandEvaluator.nameHand(new SingleHand(cards));
    }

    public static Integer compareHands(String h1, String h2) {
        if (StringUtils.isBlank(h1)) {
            throw new IllegalArgumentException("h1 cannot be null");
        }
        if (StringUtils.isBlank(h2)) {
            throw new IllegalArgumentException("h2 cannot be null");
        }

        return evaluator.compareHands(new SingleHand(h1), new SingleHand(h2));
    }

    
    public String getDeck() {
        return deck.toString();
    }

    public static void main(String[] args) {

        try {
            List<String> playerIds = new ArrayList<String>();
            playerIds.add("1");
            playerIds.add("2");
            playerIds.add("3");
            playerIds.add("4");

            TexasHoldemPokerGame game = new TexasHoldemPokerGame();
            game.shuffle();
            TexasHoldemPokerGame game2 = new TexasHoldemPokerGame(game.getDeck());

            System.out.println("1st deck:" + game.toString());
            System.out.println("2nd deck:" + game2.toString());

            Map<String, String> map = game.dealPocketCards(playerIds);
            String communityCards = game.dealFlop();

            HandEvaluator evaluator = new HandEvaluator();

            for (String playerId : map.keySet()) {
                String pocketCards = map.get(playerId);
                String cards = communityCards + " " + pocketCards;
                System.out.println("Player with ID: " + playerIds + " has hand: |" + TexasHoldemPokerGame.getBest5CardHand(cards) + "|");
                System.out.println("Player with ID: " + playerIds + " has hand rank: |" + TexasHoldemPokerGame.rankHand(cards) + "|");
                System.out.println("Player with ID: " + playerIds + " has hand name: |" + TexasHoldemPokerGame.nameHand(cards) + "|");
            }

            System.out.println("1st deck:" + game.toString());

            game2 = new TexasHoldemPokerGame(game.getDeck());

            System.out.println("2nd deck:" + game2.toString());
        } catch (GameException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
