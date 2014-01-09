package com.online.casino.test.service;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Casino;
import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.GameTemplate;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.entity.QueuedGambler;
import com.online.casino.domain.entity.Stake;
import com.online.casino.domain.enums.CasinoStatus;
import com.online.casino.domain.enums.Currency;
import com.online.casino.domain.enums.DeviceType;
import com.online.casino.domain.enums.GameStatus;
import com.online.casino.domain.enums.GameType;
import com.online.casino.domain.enums.HandStatus;
import com.online.casino.domain.enums.LimitType;
import com.online.casino.domain.enums.RoundType;
import com.online.casino.exception.GameException;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.PokerGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 10/10/30
 * @author         Bjorn Harvold    
 */
public abstract class AbstractPokerGameFunctionalTest extends AbstractFunctionalTest {

    /** Field description */
    private final static Integer SEAT_BJORN = 3;

    /** Field description */
    private final static Integer SEAT_CELINE = 1;

    /** Field description */
    private final static Integer SEAT_DAVE = 2;

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(AbstractPokerGameFunctionalTest.class);

    /** Field description */
    private final static BigDecimal TOO_LARGE_BUYIN = new BigDecimal(1000000);

    /** Field description */
    protected final static BigDecimal DEFAULT_BUYIN = new BigDecimal(100);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    protected QueuedGambler qgB = null;

    /** Field description */
    protected QueuedGambler qgC = null;

    /** Field description */
    protected QueuedGambler qgD = null;

    /** Field description */
    @Autowired
    protected PokerGameService pokerGameService;

    //~--- methods ------------------------------------------------------------

    /**
     * This method will check that player entities do exist and then add the players to the game queue. Unlike
     * handleBasicJoiningGame, it will not force errors.
     *
     *
     * @param game game
     * @param queuedGamblers queuedGamblers
     *
     * @throws com.online.casino.exception.GameException GameException
     */
    protected void handleAdvancedJoiningGame(PokerGame game, List<QueuedGambler> queuedGamblers) throws GameException {
        log.info("Adding " + queuedGamblers.size() + " players to game id: " + game.getId());

        for (QueuedGambler qg : queuedGamblers) {
            Player player = administrationService.findPlayerByNickname(qg.getNickname());

            assertNotNull("Player is null", player);
            assertNotNull("Player is transient", player.getId());
            qg.setPlayerId(player.getId());
        }

        log.info("Let's try to start a new hand before there are any players. This should throw an exception.");

        try {
            pokerGameService.doDealNewHand(game.getId());
            fail("Did not throw expected exception");
        } catch (GameException ex) {
            log.info("An exception was thrown as expected");
        }

        for (QueuedGambler qg : queuedGamblers) {
            log.info("Now player: " + qg.getNickname() + " wants to join the game.");
            pokerGameService.doQueuePlayer(game.getId(), qg.getPlayerId(), qg.getDesiredSeatNumber(), 
                    qg.getMustHaveSeat(), qg.getAmount());
        }

        log.info("Now let's check the queue count for game id: " + game.getId());
        Long count = pokerGameService.findQueuedGamblerCount(game.getId());
        assertNotNull("Count is null", count);
        assertEquals("QueuedGambler count is incorrect", queuedGamblers.size(), count.intValue());
        
        
        log.info("Added all players to game queue successfully");
    }

    /**
     * Method description
     *
     *
     * @param game game
     * @param bjornBuyin bjornBuyin
     * @param daveBuyin daveBuyin
     * @param celineBuyin celineBuyin
     *
     * @throws GameException GameException
     */
    protected void handle3PeopleJoiningGame(PokerGame game, BigDecimal bjornBuyin, BigDecimal daveBuyin,
            BigDecimal celineBuyin)
            throws GameException {
        log.info(
            "Then we add some players to the game. In this case we'll use 3 pre-created users and insert som players/personas for them");

        Player bjornP  = administrationService.findPlayerByNickname("Bjorn");
        Player davidP  = administrationService.findPlayerByNickname("David");
        Player celineP = administrationService.findPlayerByNickname("Celine");

        assertNotNull("User: Bjorn could not be found", bjornP);
        assertNotNull("User: David could not be found", davidP);
        assertNotNull("User: Celine could not be found", celineP);
        log.info("Personas saved successfully");
        log.info("Let's try to start a new hand before there are any players. This should throw an exception.");

        try {
            pokerGameService.doDealNewHand(game.getId());
            fail("Did not throw expected exception");
        } catch (GameException ex) {
            log.info("An exception was thrown as expected");
            assertEquals("Error message is not what was expected", "error.missing.players", ex.getMessage());
        }

        BigDecimal buyin = bjornBuyin;

        if (buyin == null) {
            buyin = DEFAULT_BUYIN;
        }

        log.info("Player Bjorn queuing up for the game with buyin: " + buyin.toString());
        pokerGameService.doQueuePlayer(game.getId(), bjornP.getId(), SEAT_BJORN, false, buyin);
        qgB = pokerGameService.findQueuedPlayer(game.getId(), bjornP.getId());
        assertNotNull("Gambler Bjorn is null", qgB);
        log.info("Let's verify some values on the QueuedGambler for Bjorn...");
        assertNotNull("Identifier is null", qgB.getId());
        assertNotNull("Account id is null", qgB.getAccountId());
        assertNotNull("QueueNumber is null", qgB.getQueueNumber());
        assertTrue("Buyin is not equal", buyin.compareTo(qgB.getAmount()) == 0);
        assertEquals("Player is not equal", bjornP.getId(), qgB.getPlayerId());
        assertEquals("Desired seat is not equal", SEAT_BJORN, qgB.getDesiredSeatNumber());
        assertEquals("Poker game is not equal", game.getId(), qgB.getPokergameId());
        assertFalse("Must have seat is not equal", qgB.getMustHaveSeat());
        buyin = daveBuyin;

        if (buyin == null) {
            buyin = DEFAULT_BUYIN;
        }

        log.info("Player David queuing up for the game with buyin: " + buyin.toString());
        pokerGameService.doQueuePlayer(game.getId(), davidP.getId(), SEAT_DAVE, false, buyin);
        qgD = pokerGameService.findQueuedPlayer(game.getId(), davidP.getId());
        assertNotNull("Gambler David is null", qgD);
        log.info("Let's verify some values on the QueuedGambler for David...");
        assertNotNull("Identifier is null", qgD.getId());
        assertNotNull("Account id is null", qgD.getAccountId());
        assertNotNull("QueueNumber is null", qgD.getQueueNumber());
        assertTrue("Buyin is not equal", buyin.compareTo(qgD.getAmount()) == 0);
        assertEquals("Player is not equal", davidP.getId(), qgD.getPlayerId());
        assertEquals("Desired seat is not equal", SEAT_DAVE, qgD.getDesiredSeatNumber());
        assertEquals("Poker game is not equal", game.getId(), qgD.getPokergameId());
        assertFalse("Must have seat is not equal", qgD.getMustHaveSeat());

        try {
            log.info("Let's force an error now. Dave wants to queue up a second time fo the same game");
            pokerGameService.doQueuePlayer(game.getId(), davidP.getId(), SEAT_DAVE, false, buyin);
            fail("Should throw an error because Dave can't queue up twice");
        } catch (GameException e) {
            assertEquals("Expecting a certain exception to be thrown", "error.player.already.queued", e.getMessage());
        }

        try {
            log.info(
                "Let's force another error. Celine wants to join but her buyin is larger than her account balance.");
            pokerGameService.doQueuePlayer(game.getId(), celineP.getId(), SEAT_CELINE, false, TOO_LARGE_BUYIN);
            fail("Should throw an error because Celine can't join a game with money she doesn't have");
        } catch (GameException e) {
            assertEquals("Expecting a certain exception to be thrown", "error.invalid.account.balance", e.getMessage());
        }

        try {
            log.info("Let's force yet another error. Celine wants to join but the poker game id is wrong");
            pokerGameService.doQueuePlayer("987654321", celineP.getId(), SEAT_CELINE, false, TOO_LARGE_BUYIN);
            fail("Should throw an error because Celine can't join a game that doesn't exist");
        } catch (GameException e) {
            assertEquals("Expecting a certain exception to be thrown", "error.missing.game", e.getMessage());
        }

        try {
            log.info("Let's force yet another error. Celine wants to join but her player id is wrong");
            pokerGameService.doQueuePlayer(game.getId(), "987654321", SEAT_CELINE, false, TOO_LARGE_BUYIN);
            fail("Should throw an error because Celine can't join a game with a wrong player id");
        } catch (GameException e) {
            assertEquals("Expecting a certain exception to be thrown", "error.missing.player", e.getMessage());
        }

        buyin = celineBuyin;

        if (buyin == null) {
            buyin = DEFAULT_BUYIN;
        }

        log.info("Player Celine queuing up for the game with buyin: " + buyin.toString());
        pokerGameService.doQueuePlayer(game.getId(), celineP.getId(), SEAT_CELINE, false, buyin);
        qgC = pokerGameService.findQueuedPlayer(game.getId(), celineP.getId());
        assertNotNull("Gambler Celine is null", qgC);
        log.info("Let's verify some values on the QueuedGambler for Celine...");
        assertNotNull("Identifier is null", qgC.getId());
        assertNotNull("Account id is null", qgC.getAccountId());
        assertNotNull("QueueNumber is null", qgC.getQueueNumber());
        assertTrue("Buyin is not equal", buyin.compareTo(qgC.getAmount()) == 0);
        assertEquals("Player is not equal", celineP.getId(), qgC.getPlayerId());
        assertEquals("Desired seat is not equal", SEAT_CELINE, qgC.getDesiredSeatNumber());
        assertEquals("Poker game is not equal", game.getId(), qgC.getPokergameId());
        assertFalse("Must have seat is not equal", qgC.getMustHaveSeat());
        log.info("3 of 3 players queued up for the game. ");

        Long queuedGamblers = pokerGameService.findQueuedGamblerCount(game.getId());

        assertEquals("Queued Gambler count is not correct", 3L, queuedGamblers, 0);
    }

    /**
     * Method description
     *
     *
     * @param game game
     *
     * @return
     *
     * @throws GameException GameException
     */
    protected Hand handleNewHand(PokerGame game) throws GameException {
        log.info("Now we start a new hand within the new round");

        pokerGameService.doDealNewHand(game.getId());
        
        Hand hand = pokerGameService.findCurrentHand(game.getId());

        assertNotNull("Hand is null", hand);
        log.info("A hand object was created successfully");
        log.info("Verifying some hand details before continuing");
        assertNotNull("Deck is empty", hand.getDeck());
        log.info("Hand has a new deck ready to go");
        log.info("Verifying pot");

        BigDecimal potAmount = pokerGameService.findPotSize(hand.getId());

        log.info("Total bet amount for hand is: " + potAmount.toString());

        return hand;
    }

    /**
     * Method description
     *
     *
     * @param hand hand
     *
     * @return
     *
     * @throws GameException GameException
     */
    protected Hand handleSmallAndBigBlinds(Hand hand) throws GameException {
        log.info("We now want to progress the game from hand status START to POST_SMALL_BLIND");
        assertTrue("Hand should be progressable", pokerGameService.isProgressable(hand.getId()));
        assertEquals("Hand status should be in START position", HandStatus.START, hand.getStatus());
        log.info("Progressing game to next stage...");
        pokerGameService.doProgressHand(hand.getId());
        hand = pokerGameService.findCurrentHand(hand.getGame().getId());
        assertEquals("Hand status should be in POST_SMALL_BLIND position", HandStatus.POST_SMALL_BLIND,
                     hand.getStatus());
        log.info("It's time to have small blind bet but first let's throw some errors");
        log.info(
            "First of all. The method isProgressable should be false. We need to have the small blind make a move first");
        assertFalse("We should not be able to progress the game at this point",
                    pokerGameService.isProgressable(hand.getId()));
        log.info("Retrieving current gambler");

        Gambler gambler = pokerGameService.findCurrentGambler(hand.getId());

        assertNotNull("Current gambler should be small blind and not null", gambler);
        log.info("Current gambler should not be able to call, check or raise at this point");
        assertFalse("Should not be able to call", pokerGameService.isCallable(gambler.getId()));
        assertFalse("Should not be able to check", pokerGameService.isCheckable(gambler.getId()));
        assertFalse("Should not be able to raise", pokerGameService.isRaiseable(gambler.getId()));
        log.info("The current gambler should be the small blind");

        Boolean isSmallBlind = pokerGameService.isSmallBlind(gambler);
        Boolean isBigBlind   = pokerGameService.isBigBlind(gambler);

        log.info("Making sure isSmallBlind is not null");
        assertNotNull("isSmallBlind method should not return null", isSmallBlind);
        log.info("Making sure isBigBlind is not null");
        assertNotNull("isBigBlind method should not return null", isBigBlind);
        log.info("The current gambler should be the small blind");
        assertTrue("Gambler should be the small blind", isSmallBlind);
        log.info("The current gambler should not be the big blind");
        assertFalse("Gambler should not be the big blind", isBigBlind);
        log.info("Let's try to call some methods that will throw some exceptions");

        try {
            pokerGameService.doCall(gambler.getId());
            fail("doCall method should've thrown error");
        } catch (GameException e) {
            assertEquals("Exception should be of a specific kind", "error.game.poker.call.restricted", e.getMessage());
        }

        try {
            pokerGameService.doCheck(gambler.getId());
            fail("doCheck method should've thrown error");
        } catch (GameException e) {
            assertEquals("Exception should be of a specific kind", "error.game.poker.call.restricted", e.getMessage());
        }

        try {
            pokerGameService.doRaise(gambler.getId(), new BigDecimal(5));
            fail("doRaise method should've thrown error");
        } catch (GameException e) {
            assertEquals("Exception should be of a specific kind", "error.game.poker.call.restricted", e.getMessage());
        }

        try {
            pokerGameService.doPostBigBlind(hand.getId());
            fail("doPostBigBlind method should've thrown error");
        } catch (GameException e) {
            assertEquals("Exception should be of a specific kind", "error.game.blind.big.restricted", e.getMessage());
        }

        log.info("All exceptions were thrown as expected. Let's go ahead and post the small blind");
        log.info("Posting small blind...");
        pokerGameService.doPostSmallBlind(hand.getId());
        log.info("We posted the small blind. Let's make sure it has been registered.");
        BigDecimal smallBlindBet = pokerGameService.findBetAmountByRoundNumber(gambler.getHand().getId(), gambler.getPlayer().getId(), gambler.getHand().getCurrentRoundNumber());
        assertNotNull("Small blind we just posted is null", smallBlindBet);
        assertTrue("Small blind bet is not correct amount", gambler.getHand().getGame().getTemplate().getStake().getLow().compareTo(smallBlindBet) == 0);
        
        log.info("Small blind has been registered and accounted for");
        gambler = pokerGameService.findCurrentGambler(hand.getId());
        log.info("Posting small blind method should have given us the new potential big blind gambler");
        assertNotNull("Potential big blind gambler should not be null", gambler);
        log.info("Hand should be progressable again");
        assertTrue("Hand should be progressable", pokerGameService.isProgressable(hand.getId()));
        pokerGameService.doProgressHand(hand.getId());
        hand = pokerGameService.findCurrentHand(hand.getGame().getId());
        assertEquals("Hand status should be in POST_BIG_BLIND position", HandStatus.POST_BIG_BLIND, hand.getStatus());
        log.info("The gambler we got back from calling the big blind should also be the current gambler");
        assertEquals("Big blind gambler should equal current gambler", gambler.getId(),
                     pokerGameService.findCurrentGambler(hand.getId()).getId());
        log.info(
            "This new gambler that we received from doPostSmallBlind should also not be able to call, check or raise at this point");
        assertFalse("Should not be able to call", pokerGameService.isCallable(gambler.getId()));
        assertFalse("Should not be able to check", pokerGameService.isCheckable(gambler.getId()));
        assertFalse("Should not be able to raise", pokerGameService.isRaiseable(gambler.getId()));
        log.info("The gambler we got back from posting the small blind should be the big blind");
        assertFalse("Gambler should not be the small blind", pokerGameService.isSmallBlind(gambler));
        assertTrue("Gambler should be the big blind", pokerGameService.isBigBlind(gambler));
        log.info(
            "Let's try to call some methods that will throw some exceptions again and this time with the big blind gambler");

        try {
            pokerGameService.doCall(gambler.getId());
            fail("doCall method should've thrown error");
        } catch (GameException e) {
            assertEquals("Exception should be of a specific kind", "error.game.poker.call.restricted", e.getMessage());
        }

        try {
            pokerGameService.doCheck(gambler.getId());
            fail("doCheck method should've thrown error");
        } catch (GameException e) {
            assertEquals("Exception should be of a specific kind", "error.game.poker.call.restricted", e.getMessage());
        }

        try {
            pokerGameService.doRaise(gambler.getId(), new BigDecimal(5));
            fail("doRaise method should've thrown error");
        } catch (GameException e) {
            assertEquals("Exception should be of a specific kind", "error.game.poker.call.restricted", e.getMessage());
        }

        try {
            pokerGameService.doPostSmallBlind(hand.getId());
            fail("doPostSmallBlind method should've thrown error");
        } catch (GameException e) {
            assertEquals("Exception should be of a specific kind", "error.game.blind.small.restricted", e.getMessage());
        }

        log.info("Gambler should be able to post the big blind");
        assertTrue("Gambler is big blind but is not allowed to post big blind",
                   pokerGameService.isBigBlindPostable(hand.getId()));
        log.info("All exceptions were thrown as expected. Let's go ahead and post the big blind");
        pokerGameService.doPostBigBlind(hand.getId());
        BigDecimal bigBlindBet = pokerGameService.findBetAmountByRoundNumber(gambler.getHand().getId(), gambler.getPlayer().getId(), gambler.getHand().getCurrentRoundNumber());
        assertNotNull("Big blind we just posted is null", bigBlindBet);
        assertTrue("Big blind bet is not correct amount", gambler.getHand().getGame().getTemplate().getStake().getHigh().compareTo(bigBlindBet) == 0);

        return hand;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    protected PokerGame handleTexasHoldemNoLimitPokerGameCreation() {
        log.info("Creating everything needed for a poker game");
        log.info("First we create a casino");

        Casino casino = new Casino();

        casino.setCurrency(Currency.DOLLAR);
        casino.setDeleteGamesOnCheck(1);
        casino.setEmptyGameMaximumLimit(10);
        casino.setEmptyGameMinimumLimit(5);
        casino.setGameBuffer(3);
        casino.setName("Functional Test Casino");
        casino.setStatus(CasinoStatus.ACTIVE);
        administrationService.persistCasino(casino);
        log.info("Verifying that casino was created");
        assertNotNull("Casino is null", casino);
        assertNotNull("Casino id is null", casino.getId());
        log.info("Casino is ready to go");
        log.info("Then we create the stake we need");

        Stake stake = new Stake();

        stake.setHigh(new BigDecimal(10));
        stake.setLow(new BigDecimal(5));
        stake.setCasino(casino);
        administrationService.persistStake(stake);
        log.info("Verifying that stake was created");
        assertNotNull("Stake is null", stake);
        assertNotNull("Stake id is null", stake.getId());
        log.info("Stake is ready to go");
        log.info("Then we create the game template");

        GameTemplate template = new GameTemplate();

        template.setAutoGenerated(false);
        template.setCasino(casino);
        template.setDeviceType(DeviceType.WEB);
        template.setLimitType(LimitType.NO_LIMIT);
        template.setMaxPlayers(10);
        template.setRaiseLimit(3);
        template.setRoundType(RoundType.NO_ROUND);
        template.setStake(stake);
        template.setType(GameType.TEXAS_HOLDEM);
        administrationService.persistGameTemplate(template);
        log.info("Verifying that template was created");
        assertNotNull("Template is null", template);
        assertNotNull("Template id is null", template.getId());
        log.info("Template is ready to go");
        log.info("Creating a custom poker game for this test");

        PokerGame game = new PokerGame();
        game.setTemplate(template);
        game.setStatus(GameStatus.ACTIVE);
        game.setAutoGenerated(Boolean.TRUE);
        game.setGameName("Functional Test Poker Game");
        administrationService.persistPokerGame(game);
        log.info("Verifying that game was created");
        assertNotNull("PokerGame is null", game);
        assertNotNull("PokerGame id is null", game.getId());
        log.info("PokerGame is ready to go");

        return game;
    }
}
