/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service.impl;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Account;
import com.online.casino.domain.entity.AccountEntry;
import com.online.casino.domain.entity.Bet;
import com.online.casino.domain.entity.Casino;
import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.GamblerAccountEntry;
import com.online.casino.domain.entity.GameObserver;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.entity.QueuedGambler;
import com.online.casino.domain.enums.AccountEntryType;
import com.online.casino.domain.enums.BetStatus;
import com.online.casino.domain.enums.BetType;
import com.online.casino.domain.enums.Currency;
import com.online.casino.domain.enums.GamblerAccountEntryType;
import com.online.casino.domain.enums.GamblerStatus;
import com.online.casino.domain.enums.GameAction;
import com.online.casino.domain.enums.GameType;
import com.online.casino.domain.enums.HandStatus;
import com.online.casino.exception.GameException;
import com.online.casino.game.TexasHoldemPokerGame;
import com.online.casino.service.PokerGameService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/11/16
 */
@Service("pokerGameService")
public class PokerGameServiceImpl implements PokerGameService {

    /**
     * Field description
     */
    private static final String ERROR_MISSING_PLAYERS = "error.missing.players";

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(PokerGameServiceImpl.class);

    //~--- methods ------------------------------------------------------------

    /**
     * Compares to hands
     *
     * @param hand1 Hand 1
     * @param hand2 Hand 2
     * @return Returns 1 if hand 1 is greater than hand 2. 0 if equal. And -1 if hand 2 is greater than hand 1
     */
    @Override
    public Integer compareHands(String hand1, String hand2) {
        return TexasHoldemPokerGame.compareHands(hand1, hand2);
    }

    /**
     * This method issues a call to the hand for the specified gambler
     *
     * @param gamblerId gamblerId
     * @throws GameException exception
     */
    @Override
    @Transactional
    public void doCall(String gamblerId) throws GameException {
        if (log.isDebugEnabled()) {
            log.debug("About to call for gambler with id: " + gamblerId);
        }

        if (!isCallable(gamblerId)) {
            log.error("Gambler with id: " + gamblerId + " is not allowed to CALL");

            throw new GameException("error.game.poker.call.restricted", gamblerId, "CALL");
        }

        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler with id: " + gamblerId + " doesn't exist");

            throw new GameException("error.game.inconsistent.state",
                                    "Gambler with id: " + gamblerId + " doesn't exist");
        }

        call(gambler, true);
    }

    /**
     * Executes a check in a round for the specified gambler.
     *
     * @param gamblerId Id for gambler we wish to check for
     * @throws GameException exception
     */
    @Override
    @Transactional
    public void doCheck(String gamblerId) throws GameException {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler with id: " + gamblerId + " doesn't exist");

            throw new GameException("error.game.inconsistent.state",
                                    "Gambler with id: " + gamblerId + " doesn't exist");
        }

        check(gambler);
    }

    /**
     * This will create a new hand if and only if there are at least 2 players seated.
     * The creation of a hand of poker has the most code. Not necessarily the most complex code
     * but it takes a lot to get a hand ready for gambler to play.
     * <p/>
     * These are the steps:
     * 1. If a poker game has more than 2 gamblers, it will initiate the creation of a new hand
     * 2. It initiates a new deck of cards and shuffles it
     * 3. It will set the seat numbers for dealer, bb and sb
     * 4. It will re-issue cards to existing gamblers and add more gamblers (if there is space) from the game to the hand (the gambler is only associated with either the game or the hand at the time)
     *
     * @param pokergameId Id of game we should deal a new hand for
     * @throws GameException
     */
    @Override
    @Transactional
    public void doDealNewHand(String pokergameId) throws GameException {
        Hand result;

        // first we get the game
        PokerGame game = PokerGame.findPokerGame(pokergameId);

        if (game == null) {
            log.error("Could not find game with id: " + pokergameId + ". Cannot deal new hand.");

            throw new GameException("error.missing.game", pokergameId);
        }

        if (!startable(game)) {
            throw new GameException(ERROR_MISSING_PLAYERS);
        }

        Hand lastHand = Hand.findLastHandPlayed(pokergameId);

        // here is the deck that we want to persist
        TexasHoldemPokerGame thp = new TexasHoldemPokerGame();

        // shuffle the deck
        thp.shuffle();

        // persist a new hand regardless of the next steps
        result = new Hand(game, thp.getDeck());
        result.persist();

        // add gambler to hand
        addPlayersToHand(result);

        if (log.isDebugEnabled()) {
            log.debug("Checking to see if game with id: " + pokergameId + " has a past hand associated with it");
        }

        // determine SB, BB and dealer
        if (lastHand == null) {
            if (log.isDebugEnabled()) {
                log.debug("Creating first hand EVER for poker game with id: " + pokergameId);
            }

            // the gambler with the lowest seat number is just a way to get the ball rolling
            // he might not be available but we use him as an entry point to loop through gambler
            // until we find one who wants to play.
            Gambler lowestSeatGambler = getGamblerWithLowestSeatNumber(result.getGamblers());
            Gambler dealer            = getFirstAvailableGambler(lowestSeatGambler, lowestSeatGambler);
            Gambler smallBlind        = getFirstAvailableGambler(dealer.getNextGambler(), dealer);
            Gambler bigBlind          = getFirstAvailableGambler(smallBlind.getNextGambler(), smallBlind);

            // increment important seats based on information from last hand played
            // these seats are not final as we don't know for sure yet if the gamblers in
            // these seats are ok / validated / active etc
            result.setDealerSeat(dealer.getSeatNumber());
            result.setSmallBlindSeat(smallBlind.getSeatNumber());
            result.setBigBlindSeat(bigBlind.getSeatNumber());

            // note that this will change after the small and big blinds are posted
            // the the first gambler will be the guy left of the big blind. See: doProgressHand
            result.setFirstGamblerSeat(result.getSmallBlindSeat());
            result.setCurrentGamblerSeat(result.getSmallBlindSeat());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Creating new hand based on past hand information for poker game with id: " + pokergameId);
            }

            Integer lastHandDealerSeat = lastHand.getDealerSeat();

            // increment important seats based on information from last hand played
            // these seats are not final as we don't know for sure yet if the gamblers in
            // these seats are ok / validated / active etc
            Gambler gamblerInLastHandDealerSeat = getGamblerInSeat(result.getGamblers(), lastHandDealerSeat);
            Gambler dealer;

            if (gamblerInLastHandDealerSeat == null) {

                // this means no one is sitting in the seat where the gambler sat for the last hand
                // which means we have to find the dealer seat most closely to the left of the last dealer seat
                dealer = getFirstAvailableGamblerBySeatNumber(result.getGamblers(), game.getTemplate().getMaxPlayers(),
                        lastHandDealerSeat);
            } else {
                dealer = getFirstAvailableGambler(gamblerInLastHandDealerSeat.getNextGambler(),
                                                  gamblerInLastHandDealerSeat);
            }

            Gambler smallBlind = getFirstAvailableGambler(dealer.getNextGambler(), dealer);
            Gambler bigBlind   = getFirstAvailableGambler(smallBlind.getNextGambler(), smallBlind);

            result.setDealerSeat(dealer.getSeatNumber());
            result.setSmallBlindSeat(smallBlind.getSeatNumber());
            result.setBigBlindSeat(bigBlind.getSeatNumber());

            // note that this will change after the small and big blinds are posted
            // the the first gambler will be the guy left of the big blind
            // the the first gambler will be the guy left of the big blind. See: doProgressHand
            result.setFirstGamblerSeat(result.getSmallBlindSeat());
            result.setCurrentGamblerSeat(result.getSmallBlindSeat());
        }

        result.merge();
    }

    /**
     * Method will progress the hand by going through the hand status workflow
     *
     * @param handId id for hand for which to progress
     * @throws GameException Throws exception if handId is null
     */
    @Override
    @Transactional
    public void doEndGame(String handId) throws GameException {
        Hand hand = Hand.findHand(handId);

        if (hand == null) {
            throw new GameException("error.game.inconsistent.state", "Hand with id " + handId + " is null");
        }

        endGame(hand);
    }

    /**
     * Changes gambler status to fold
     *
     * @param gamblerId Id for gambler that wishes to fold
     * @throws GameException
     */
    @Override
    @Transactional
    public void doFold(String gamblerId) throws GameException {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler with id: " + gamblerId + " doesn't exist");

            throw new GameException("error.game.inconsistent.state",
                                    "Gambler with id: " + gamblerId + " doesn't exist");
        }

        fold(gambler);
    }

    /**
     * This method will remove the potential queued player and the potential gambler record. But before
     * it does that it has to transfer the gamblers winnings back to the user account.
     *
     * @param pokergameId Game identifier
     * @param playerId    Player identifier
     *
     * @throws GameException GameException
     */
    @Override
    @Transactional
    public void doLeaveGame(String pokergameId, String playerId) throws GameException {
        QueuedGambler qg = QueuedGambler.findQueuedGamblerByGameAndPlayer(pokergameId, playerId);

        if (qg != null) {

            // this means the player didn't reach the actual game yet
            removeQueuedGambler(qg);
        } else {

            // this means the player has reached the table and we need to go ahead and remove him from the table
            Gambler gambler = Gambler.findGamblerByGameAndPlayer(pokergameId, playerId);

            if (gambler != null) {
                leaveGame(gambler);
            } else {
                log.warn("doLeaveGame was called but there is no QueuedGambler or Gambler for pokergameId: "
                         + pokergameId + " and playerId: " + playerId);
            }
        }
    }

    /**
     * Method description
     *
     * @param gamblerId gamblerId
     * @throws GameException
     */
    @Override
    @Transactional
    public void doLeaveGameAndWatch(String gamblerId) throws GameException {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Cannot find a gambler with id: " + gamblerId);

            throw new GameException("error.game.inconsistent.state", "There is no gambler with id: " + gamblerId);
        }

        leaveGameAndObserve(gambler);
    }

    /**
     * When the designated big blind wants to post, he calls this method.
     *
     * @param handId Id of gambler that posts small blind
     * @throws GameException Throws an exception if gamblerId is null
     */
    @Override
    @Transactional
    public void doPostBigBlind(String handId) throws GameException {
        Gambler gambler = findCurrentGambler(handId);

        if (gambler == null) {
            throw new GameException("error.game.inconsistent.state",
                                    "Current gambler for hand is null. Hand ID: " + handId);
        }

        postBigBlind(gambler);
    }

    /**
     * This method is called by the small blind when posting SB
     *
     * @param handId to post small blind for
     * @throws GameException Throws an exception if gamblerId is null or if gambler has insufficient funds.
     *                       Insufficient funds is defined as 0. The small blind can go all-in when posting SB.
     */
    @Override
    @Transactional
    public void doPostSmallBlind(String handId) throws GameException {
        Gambler g = findCurrentGambler(handId);

        if (g == null) {
            throw new GameException("error.game.inconsistent.state",
                                    "Current gambler for hand is null. Hand ID: " + handId);
        }

        postSmallBlind(g);
    }

    /**
     * Method will progress the hand by going through the hand status workflow
     *
     * @param handId id for hand for which to progress
     * @throws GameException Throws exception if handId is null
     */
    @Override
    @Transactional
    public void doProgressHand(String handId) throws GameException {
        Hand hand = Hand.findHand(handId);

        if (hand == null) {
            throw new GameException("error.game.inconsistent.state", "Hand with id " + handId + " is null");
        }

        progressHand(hand);
    }

    /**
     * When a player wants to play a game he first has to be queued for the game. The player can
     * optionally choose a seat number and whether he has to or just desires that seat. This
     * information is used when a new hand is started.
     * <p/>
     * The player will need a valid account with the right currency. The player also needs to have enough cash.
     * <p/>
     * With all those requirements taken care of, the player can move right into a game when his turn is up.
     *
     * @param pokergameId       pokergameId
     * @param playerId          playerId
     * @param desiredSeatNumber desiredSeatNumber
     * @param mustHaveSeat      mustHaveSeat
     * @param buyin             buyin
     * @throws GameException GameException
     */
    @Override
    @Transactional
    public void doQueuePlayer(String pokergameId, String playerId, Integer desiredSeatNumber, boolean mustHaveSeat,
                              BigDecimal buyin)
            throws GameException {

        // all the right data points were passed. next to validate those points
        // check for valid poker game
        PokerGame game = PokerGame.findPokerGame(pokergameId);

        // throw error if game doesn't exist
        if (game == null) {
            log.error("Could not find game with id: " + pokergameId);

            throw new GameException("error.missing.game", pokergameId);
        }

        // check if player exists and if player is already in the queue
        Player player = Player.findPlayer(playerId);

        if (player == null) {
            log.error("Could not find player with id: " + playerId);

            throw new GameException("error.missing.player", playerId);
        }

        // need to check if player is already in the queue
        QueuedGambler qg = QueuedGambler.findQueuedGamblerByGameAndPlayer(pokergameId, playerId);

        if (qg != null) {
            log.error("Player entity is already queued for game, Player ID: " + playerId + ". Pokergame ID: "
                      + pokergameId);

            throw new GameException("error.player.already.queued", playerId, pokergameId);
        }

        // we also need to check if the player is already playing the game
        // throw an error if the system user has already joined with a different account
        Gambler userAlreadyInGame = Gambler.findGamblerByGameAndPlayer(pokergameId, playerId);

        if (userAlreadyInGame != null) {
            log.error("Gambler entity for this player already exists, Gambler ID: " + userAlreadyInGame.getId());

            throw new GameException("error.gambler.already.exists.with.alias",
                                    userAlreadyInGame.getPlayer().getNickname());
        }

        // verify that user has enough money and the right currency
        String   userId   = player.getApplicationUser().getId();
        Currency currency = game.getTemplate().getCasino().getCurrency();
        Account  account  = Account.findAccountByUserAndCurrency(userId, currency);

        // throw an error if the player doesn't have an account matching the currency of the game
        if (account == null) {
            log.error(
                "Player could not join the game because the User has no account registered with the game's currency: "
                + currency.name());

            throw new GameException("error.invalid.account.currency", playerId, currency.name());
        }

        BigDecimal balance = AccountEntry.findAccountBalance(account.getId());

        // throw an error if player doesn't have enough money in the account
        if (balance.compareTo(buyin) == -1) {
            log.error("Player could not join the game because User's account is lacking funds. Account balance: "
                      + balance.toString() + ". Buyin: " + buyin.toString());

            throw new GameException("error.invalid.account.balance", currency.name(), balance.toString(),
                                    buyin.toString());
        }

        // throw an error if the buyin is less than the big blind
        if (buyin.compareTo(game.getTemplate().getStake().getHigh()) == -1) {
            log.error("Player could not join the game because the buyin is less than the big blind");

            throw new GameException("error.invalid.buyin", buyin.toString(),
                                    game.getTemplate().getStake().getHigh().toString());
        }

        // it is now ok to create the QueuedGambler entity
        Integer currentQueueNumber = QueuedGambler.findMaxQueueNumber(pokergameId);

        qg = new QueuedGambler(pokergameId, playerId, account.getId(), desiredSeatNumber, mustHaveSeat,
                               currentQueueNumber + 1, player.getNickname(), GamblerAccountEntryType.BUYIN, buyin);
        qg.persist();
    }

    /**
     * This method will try to make a raise bet. First it will check to see if the gambler can raise. If it can, it
     * checks to see whether there is an outstanding debt (required call). If there is, it will first call.
     * If the call succeeds, we will go ahead and do the raise. It will make sure that the raise amount is
     * greater than the minimum raise limit (i.e. double the big blind) and that the gambler has sufficient funds to cover the raise. If
     * he doesn't, the gambler goes all in with his remaining funds and a new pot gets created for the hand.
     * <p/>
     * Method will also take care of updating the Hand of any required fields that need to be updated like the round number, pot, current gambler etc
     *
     * @param gamblerId The gambler id to do a raise for
     * @param amount    The amount (excluding the potential call amount the gambler has to make BEFORE raising)
     * @throws GameException
     */
    @Override
    @Transactional
    public void doRaise(String gamblerId, BigDecimal amount) throws GameException {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler with id: " + gamblerId + " doesn't exist");

            throw new GameException("error.game.inconsistent.state",
                                    "Gambler with id: " + gamblerId + " doesn't exist");
        }

        // then raise
        raise(gambler, amount);
    }

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     */
    @Override
    public QueuedGambler doReQueuePlayer(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        // when he requeues he get the first available seat and he cannot be certain he will be sitting in his last seat
        QueuedGambler qg = new QueuedGambler(gambler.getHand().getGame().getId(), gambler.getPlayer().getId(),
                               gambler.getAccountId(), gambler.getSeatNumber(), false, 0,
                               gambler.getPlayer().getNickname());

        qg.persist();

        return qg;
    }

    /**
     * Removes a gambler from the game queue and transfers his game balance back to his account
     *
     * @param queuedGamblerId queuedGamblerId
     * @throws GameException GameException
     */
    @Transactional
    @Override
    public void doRemoveQueuedGambler(String queuedGamblerId) throws GameException {
        QueuedGambler qg = QueuedGambler.findQueuedGambler(queuedGamblerId);

        if (qg == null) {
            String error = "QueuedGambler with id: " + queuedGamblerId + " doesn't exist";

            log.error(error);

            throw new GameException("error.game.inconsistent.state", error);
        }

        removeQueuedGambler(qg);
    }

    /**
     * When a gambler doesn't make a move during a hand, his status will be set to AWAY and move over to the next seat
     *
     * @param handId Id for which we find the current gambler to time out
     */
    @Override
    public void doTimeout(String handId) {
        Gambler gambler = findCurrentGambler(handId);

        gambler.setStatus(GamblerStatus.AWAY);
        gambler.merge();
        gotoNextGambler(gambler);
    }

    /**
     * This method gets called when the observers wishes to stop observing a particular poker game or
     * when the observer joins the game and becomes a player.
     *
     * @param gameObserverId Id of game observer to remove
     */
    @Override
    public void doUnwatchGame(String gameObserverId) {
        GameObserver.findGameObserver(gameObserverId).remove();
    }

    /**
     * A player wants to start observing a game to either watch other players play or to get ready to play
     * in that game himself.
     *
     * @param pokergameId The id for the game the player wants to observe
     * @param playerId    The id for the player who wants to observe
     */
    @Override
    public void doWatchGame(String pokergameId, String playerId) {
        GameObserver go = GameObserver.findGameObserver(pokergameId, playerId);

        if (go != null) {
            if (log.isWarnEnabled()) {
                log.warn("Player with id: " + playerId + " is ALREADY observing game with id: " + pokergameId
                         + ". GameObserver id: " + go.getId());
            }
        } else {
            go = new GameObserver(pokergameId, playerId);
            go.persist();

            if (log.isDebugEnabled()) {
                log.debug("Player with id: " + playerId + " is now observing game with id: " + pokergameId
                          + ". GameObserver id: " + go.getId());
            }
        }
    }

    /**
     * Returns the account balance for the player account that has the same currency as the game
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @return Return value
     * @throws GameException GameException
     */
    @Override
    public BigDecimal findAccountBalance(String pokergameId, String playerId) throws GameException {
        Casino casino = PokerGame.findCasinoByPokerGame(pokergameId);

        if (casino == null) {
            throw new GameException("error.game.inconsistent.state",
                                    "Casino does not have a game with id " + pokergameId + " is null");
        }

        return AccountEntry.findAccountBalanceByPlayerAndCurrency(playerId, casino.getCurrency());
    }

    /**
     * Retrieves all the actions the gambler can currently perform on the hand
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     */
    @Override
    public List<GameAction> findAvailableMoves(String gamblerId) {
        List<GameAction> result = new ArrayList<GameAction>();

        if (isCallable(gamblerId)) {
            result.add(GameAction.CALL_ACTION);
        }

        if (isCheckable(gamblerId)) {
            result.add(GameAction.CHECK_ACTION);
        }

        if (isRaiseable(gamblerId)) {
            result.add(GameAction.RAISE_ACTION);
        }

        if (isFoldable(gamblerId)) {
            result.add(GameAction.FOLD_ACTION);
        }

        if (isLeaveable(gamblerId)) {
            result.add(GameAction.LEAVE_ACTION);
        }

        return result;
    }

    /**
     * Returns a count of how many seats are available for the specified game
     *
     * @param pokergameId Id of pokergame
     * @return Returns a count of available seats
     * @throws GameException Throws exception if pokergameId is null or game entity doesn't exist
     */
    @Override
    public Integer findAvailableSeatCount(String pokergameId) throws GameException {
        PokerGame game = PokerGame.findPokerGame(pokergameId);

        if (game == null) {
            throw new GameException("error.game.inconsistent.state", "Game with id " + pokergameId + " is null");
        }

        Integer maxPlayers   = game.getTemplate().getMaxPlayers();
        Long    gamblerCount = Gambler.findGamblerCountForGameExcludeRemove(pokergameId);

        return maxPlayers - gamblerCount.intValue();
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param playerId    gamblerId
     * @return Returns the balance
     */
    @Override
    public BigDecimal findBalance(String pokergameId, String playerId) {
        return GamblerAccountEntry.findGamblerBalance(pokergameId, playerId);
    }

    /**
     * Computes the best poker game hand out of 7 cards
     *
     * @param hand Poker hand to evaluate. E.g. "2h 2c 3s 4d 7h kh jd"
     * @return Returns a string of cards that make it the best hand. E.g. "2h 2c 7h kh jd"
     */
    @Override
    public String findBestHand(String hand) {
        return TexasHoldemPokerGame.getBest5CardHand(hand);
    }

    /**
     * Returns the BigDecimal total bet amount for a specific gambler
     *
     * @param handId   handId
     * @param playerId Player id for which to retrieve the current bet amount
     * @return Returns a BigDecimal of the total current amount bets the gambler has made
     */
    @Override
    public BigDecimal findBetAmount(String handId, String playerId) {
        return Bet.findBetAmount(handId, playerId);
    }

    /**
     * Returns the BigDecimal total bet amount for a specific gambler by round number.
     *
     * @param handId      handId
     * @param playerId    Player id for which to retrieve the bet amount
     * @param roundNumber The desired round number in question
     * @return Returns a BigDecimal of the total current amount bets the gambler has made for that round
     */
    @Override
    public BigDecimal findBetAmountByRoundNumber(String handId, String playerId, Integer roundNumber) {
        return Bet.findBetAmountByRound(handId, playerId, roundNumber);
    }

    /**
     * Retrieves a list of Bet entities the gambler has made
     *
     * @param handId   handId
     * @param playerId Player id we wish to retrieve the bets for
     * @return Returns s list of bet entites for that gambler and pot
     */
    @Override
    public List<Bet> findBets(String handId, String playerId) {
        return Bet.findBetsByGambler(handId, playerId);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return Return value
     */
    @Override
    public List<Bet> findBetsByHand(String handId) {
        return Bet.findBetsByHand(handId);
    }

    /**
     * @param gamblerId The gambler id for which we want to retrieve the gambler's bets / per pot
     * @return Returns a big decimal
     */
    @Override
    public BigDecimal findCallAmount(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Inconsistent state. Gambler is null. ID: " + gamblerId);
        }

        return (gambler != null)
               ? callAmount(gambler)
               : new BigDecimal(0);
    }

    /**
     * Utility function for finding retrieving the current better off of the Hand entity.
     * The rules are:
     * - Before the pocket cards have been dealt and the Hand has status of START, we have to go around the table and ask if the blinds are in
     *
     * @param handId The desired hand to find the current gambler for
     * @return The current gambler. Null if there is no longer a gambler left to gamble
     */
    @Override
    @Transactional
    public Gambler findCurrentGambler(String handId) {
        return Gambler.findCurrentGambler(handId);
    }

    /**
     * Returns the current hand for a game.
     *
     * @param pokergameId Id of the specified game to retrieve the hand for
     * @return Hand entity
     */
    @Override
    public Hand findCurrentHand(String pokergameId) {
        return Hand.findCurrentHand(pokergameId);
    }

    /**
     * Method description
     *
     * @param gamblerId gamblerId
     * @return Return value
     */
    @Override
    public Gambler findGambler(String gamblerId) {
        return Gambler.findGambler(gamblerId);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return Returns a list of gamblers for the hand
     */
    @Override
    public List<Gambler> findGamblers(String handId) {
        return Gambler.findGamblersForHand(handId);
    }

    /**
     * Retrieves a game observer
     *
     * @param pokergameId Id of observer to retrieve
     * @param playerId    Id of observer to retrieve
     * @return Returns game observer entity
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_GAME_OBSERVER', 'RIGHT_READ_GAME_OBSERVER_AS_ADMIN')")
    @Override
    public GameObserver findGameObserver(String pokergameId, String playerId) {
        return GameObserver.findGameObserver(pokergameId, playerId);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return Returns the number of game observers for the given game
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_GAME_OBSERVER', 'RIGHT_READ_GAME_OBSERVER_AS_ADMIN')")
    @Override
    public Long findGameObserverCount(String pokergameId) {
        return GameObserver.findGameObserverCount(pokergameId);
    }

    /**
     * Retrieves a list of game observers for a specific poker game
     *
     * @param pokergameId Id of pokergame to retrieve observers for
     * @param index       index of pagination
     * @param maxResults  maxResults for pagination
     * @return Returns a list of observers for the specified game
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_GAME_OBSERVER', 'RIGHT_READ_GAME_OBSERVER_AS_ADMIN')")
    @Override
    public List<GameObserver> findGameObservers(String pokergameId, Integer index, Integer maxResults) {
        return GameObserver.findGameObservers(pokergameId, index, maxResults);
    }

    /**
     * Method description
     *
     * @param handId handId
     * @return Return value
     */
    @Override
    public Hand findHand(String handId) {
        return Hand.findHand(handId);
    }

    /**
     * Will name the incoming hand E.g. of incoming hand string "2h 2c 3s 4d 7h"
     *
     * @param hand The hand for which to name
     * @return Returns a string value of that hand. E.g. "Pair of twos"
     */
    @Override
    public String findHandName(String hand) {
        return TexasHoldemPokerGame.nameHand(hand);
    }

    /**
     * Will rank a hand. E.g. of incoming hand string "2h 2c 3s 4d 7h"
     *
     * @param hand The hand for which to rank
     * @return Returns an integer value of the hand. E.g. 34567
     */
    @Override
    public Integer findHandRank(String hand) {
        return TexasHoldemPokerGame.rankHand(hand);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return Return value
     */
    @Override
    public List<Hand> findHandsByGame(String pokergameId) {
        return Hand.findHandsByGame(pokergameId);
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param handId handId
     *
     * @return Return value
     */
    @Override
    public Gambler findLastActingGambler(String pokergameId, String handId) {
        Bet lastBet = Bet.findLastBet(handId);

        return Gambler.findGamblerByGameAndPlayer(pokergameId, lastBet.getPlayerId());
    }

    /**
     * Returns the last active gambler for the hand
     *
     * @param handId id for the hand we are working with
     * @return Returns last standing gambler
     */
    @Override
    public Gambler findLastManStanding(String handId) {
        Gambler result = null;

        if (isEndGame(handId)) {
            result = Gambler.findActiveGamblersForHand(handId).get(0);
        }

        return result;
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param maxPlayers  maxPlayers
     * @return Return value
     */
    @Override
    public List<QueuedGambler> findLatestQueuedGamblers(String pokergameId, Integer maxPlayers) {
        return QueuedGambler.findLatestQueuedGamblers(pokergameId, maxPlayers);
    }

    /**
     * Will return the amount the gambler lost. This includes the total bet for the hand minus sidebets re-drawn
     *
     * @param pokergameId pokergameId
     * @param handId      The id of the hand we wish to query
     * @param playerId    The id of the gambler we wish to query
     * @return Returns the amount the gambler has won
     */
    @Override
    public BigDecimal findLoserAmount(String pokergameId, String handId, String playerId) {
        BigDecimal result;
        BigDecimal totalBetAmount    = findBetAmount(handId, playerId);
        BigDecimal collectedSidebets = GamblerAccountEntry.findLoseAmount(pokergameId, playerId);

        result = totalBetAmount.subtract(collectedSidebets);

        return result;
    }

    /**
     * Will return a list of gambler entities for a game that has finished
     *
     * @param handId The id of the hand we wish to query
     * @return List of gamblers with a losing hand
     */
    @Override
    public List<Gambler> findLosers(String handId) {
        return Gambler.findLosers(handId);
    }

    /**
     * Grabs the stake associated with the game and depending on the hand state, returns the raise amount
     *
     * @param gamblerId The id of the gambler we wish to query for raise amount
     * @return Returns the maximum raise amount for the specified game
     */
    @Override
    public BigDecimal findMaximumRaiseAmount(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Inconsistent state. Gambler is null. ID: " + gamblerId);
        }

        return (gambler != null)
               ? getMaximumRaiseAmount(gambler)
               : new BigDecimal(0);
    }

    /**
     * Grabs the stake associated with the game and depending on the hand state, returns the raise amount
     *
     * @param gamblerId The id of the gambler we wish to query for raise amount
     * @return Returns the minimum raise amount for the specified game
     */
    @Override
    public BigDecimal findMinimumRaiseAmount(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Inconsistent state. Gambler is null. ID: " + gamblerId);
        }

        return (gambler != null)
               ? getMinimumRaiseAmount(gambler)
               : new BigDecimal(0);
    }

    /**
     * Find poker game count based on casino, type and stake
     *
     * @param casinoId Id of casino
     * @param type     Type of game
     * @param stakeId  Stake
     * @return Returns a count of how many games fit the criteria
     */
    @Override
    public Long findPokerGameCount(String casinoId, GameType type, String stakeId) {
        return PokerGame.findPokerGameCount(casinoId, type, stakeId);
    }

    /**
     * Find poker games based on casino, type and stake
     *
     * @param casinoId   Id of casino
     * @param type       Type of game
     * @param stakeId    Stake
     * @param index      index of pagination
     * @param maxResults maxResults for pagination
     * @return Returns a list of poker games that fit those criteria
     */
    @Override
    public List<PokerGame> findPokerGames(String casinoId, GameType type, String stakeId, Integer index,
            Integer maxResults) {
        return PokerGame.findPokerGames(casinoId, type, stakeId, index, maxResults);
    }

    /**
     * Based on the criteria, returns a list of pokergames and the gamblers that currently play in those games
     *
     * @param casinoId   Id of casino
     * @param type       Type of game
     * @param stakeId    Stake
     * @param index      index of pagination
     * @param maxResults maxResults for pagination
     * @return Returns a map of poker games and their gamblers
     */
    @Override
    public Map<PokerGame, List<Gambler>> findPokerGamesAndGamblers(String casinoId, GameType type, String stakeId,
            Integer index, Integer maxResults) {
        Map<PokerGame, List<Gambler>> result = new HashMap<PokerGame, List<Gambler>>();
        List<PokerGame>               list   = findPokerGames(casinoId, type, stakeId, index, maxResults);

        for (PokerGame game : list) {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving gamblers for game with id: " + game.getId());
            }

            Hand hand = findCurrentHand(game.getId());

            if ((hand != null) && (hand.getGamblers() != null)) {
                result.put(game, hand.getGamblers());
            } else {
                result.put(game, null);
            }
        }

        return result;
    }

    /**
     * Retrieves the total current pot for the specified hand
     *
     * @param handId Hand id of the hand we wish to retrieve the total bet amount for
     * @return Returns a BigDecimal with the total bet amount for this hand
     */
    @Override
    public BigDecimal findPotSize(String handId) {
        return Bet.findBetAmountByHand(handId);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return Return value
     */
    @Override
    public Long findQueuedGamblerCount(String pokergameId) {
        return QueuedGambler.findQueuedGamblerCount(pokergameId);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @return Return value
     */
    @Override
    public QueuedGambler findQueuedPlayer(String pokergameId, String playerId) {
        return QueuedGambler.findQueuedGamblerByGameAndPlayer(pokergameId, playerId);
    }

    /**
     * Returns a list of available seat numbers for the hand
     *
     * @param pokergameId Id of pokergame to retrieve seat numbers for
     * @return Returns a list of available seat numbers for the specified game
     * @throws GameException Throws an exception if pokergameId is null or game doesn't exist
     */
    @Override
    public List<Integer> findSeatNumbers(String pokergameId) throws GameException {
        PokerGame game = PokerGame.findPokerGame(pokergameId);

        if (game == null) {
            throw new GameException("error.game.inconsistent.state", "Game with id " + pokergameId + " is null");
        }

        // first we add a map of all available seat numbers (e.g. 1 - 10)
        Map<Integer, Integer> availableSeats = new HashMap<Integer, Integer>();

        for (int i = 1; i <= game.getTemplate().getMaxPlayers(); i++) {
            availableSeats.put(i, i);
        }

        // convert to list and sort
        List<Integer> result = new ArrayList<Integer>(availableSeats.values());

        Collections.sort(result);

        return result;
    }

    /**
     * Returns the application user's id for the current gambler
     *
     * @param handId handId
     * @return ApplicationUser.id
     */
    @Override
    public String findUserByCurrentGambler(String handId) {
        return Gambler.findUserByCurrentGambler(handId);
    }

    /**
     * Will return the amount the gambler won for the specified hand
     *
     * @param pokergameId pokergameId
     * @param playerId    The id of the gambler we wish to query
     * @return Returns the amount the gambler has won
     */
    @Override
    public BigDecimal findWinnerAmount(String pokergameId, String playerId) {
        return GamblerAccountEntry.findWinAmount(pokergameId, playerId);
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param handId handId
     * @param playerId playerId
     *
     * @return Return value
     */
    @Override
    public BigDecimal findWinnerAmount(String pokergameId, String handId, String playerId) {
        return GamblerAccountEntry.findWinAmount(pokergameId, handId, playerId);
    }

    /**
     * Will return a list of gambler entities for a game that has finished
     *
     * @param handId The id of the hand we wish to query
     * @return List of gamblers with a winning hand
     */
    @Override
    public List<Gambler> findWinners(String handId) {
        return Gambler.findWinners(handId);
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Based on the handId, this method will check all gamblers and see whether there are outstanding bets to be made
     * The hand needs to have a status of POCKET_CARDS, FLOP, TURN or RIVER. and the current gambler cannot be null.
     *
     * @param handId The id for the hand you wish to see if someone can still place bets
     * @return Returns true is hand is still bettable. False otherwise
     */
    @Override
    public boolean isBettable(String handId) {
        Hand hand = Hand.findHand(handId);

        if (hand == null) {
            log.error("Hand is null. ID: " + handId);
        }

        return (hand != null) && bettable(hand);
    }

    /**
     * Checks to see if the specified gambler is the big blind for his hand
     *
     * @param gambler gambler we wish to verify
     * @return Returns true if gambler is the big blind. Otherwise it returns false.
     */
    @Override
    public boolean isBigBlind(Gambler gambler) {
        return gambler.getSeatNumber().equals(gambler.getHand().getBigBlindSeat());
    }

    /**
     * Checks whether the current gambler is allowed to post the big blind
     *
     * @param handId Id for gambler we wish to query for
     * @return Returns true if gambler can post big blind. Otherwise false
     */
    @Override
    public boolean isBigBlindPostable(String handId) {
        Gambler gambler = findCurrentGambler(handId);

        if (gambler == null) {
            log.error("Current gambler is null for hand ID: " + handId);
        }

        return (gambler != null) && bigBlindPostable(gambler);
    }

    /**
     * This method checks to see whether the gambler can call for this hand.
     * A call entails that the gambler has an outstanding debt in one or several
     * pots that he has to honor before being able to continue the game
     *
     * @param gamblerId Gambler id for which to see whether can check
     * @return Returns true if gambler can call. Otherwise returns false
     */
    @Override
    public boolean isCallable(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler doesn't exist. ID: " + gamblerId);
        }

        return (gambler != null) && callable(gambler);
    }

    /**
     * This method check to see whether gambler can check for this current round.
     * A check entails no previous bets having been met in the current round
     *
     * @param gamblerId Gambler id for which to see whether can check
     * @return Returns true if gambler can check. Otherwise returns false
     */
    @Override
    @Transactional
    public boolean isCheckable(String gamblerId) {

        // first we get the gambler
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler doesn't exist. ID: " + gamblerId);
        }

        return (gambler != null) && checkable(gambler);
    }

    /**
     * Checks whether the current gambler seat number on the hand is gambler's seat number and if the gambler is active
     *
     * @param gamblerId Id for gambler we wish to query if he is current
     * @return Returns true if the gambler is the currently playing gambler. False otherwise.
     */
    @Override
    public boolean isCurrentGambler(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler doesn't exist. ID: " + gamblerId);
        }

        return (gambler != null) && currentGambler(gambler);
    }

    /**
     * Checks to see if all but one gambler has folded or are away or other statuses that prevent them from playing
     *
     * @param handId Id for the hand the check is being made
     * @return Returns true if there in only one man left standing
     */
    @Override
    public boolean isEndGame(String handId) {
        boolean result      = false;
        Long    activeCount = Gambler.findActiveGamblerCountForHand(handId);

        // if there is only 1 active gambler
        if (activeCount < 2) {
            if (log.isDebugEnabled()) {
                log.debug("There are less than 2 players in the game: " + activeCount + ". A winner is available.");
            }

            result = true;
        }

        return result;
    }

    /**
     * Method description
     *
     * @param gamblerId gamblerId
     * @return Return value
     */
    @Override
    public boolean isFoldable(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler doesn't exist. ID: " + gamblerId);
        }

        return (gambler != null) && currentGambler(gambler);
    }

    /**
     * Returns the gambler status
     *
     * @param gamblerId Id of gambler to verify
     * @return Returns true if the status is active. Otherwise returns false;
     */
    @Override
    public boolean isGamblerActive(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler doesn't exist. ID: " + gamblerId);
        }

        return (gambler != null) && gambler.getStatus().equals(GamblerStatus.ACTIVE);
    }

    /**
     * This should always return true.
     *
     * @param gamblerId gamblerId
     * @return Return value
     */
    @Override
    public boolean isLeaveable(String gamblerId) {
        return true;
    }

    /**
     * The game can only progress (progress is defined as moving cards, not betting) if there are more than
     * 1 active player and that the last cards hasn't been dealt
     *
     * @param handId Id for hand we wish to check whether it's progressable
     * @return Returns true if hand is progressable. Otherwise returns false.
     */
    @Override
    public boolean isProgressable(String handId) {
        Hand hand = Hand.findHand(handId);

        if (hand == null) {
            log.error("Hand doesn't exist. ID: " + handId);
        }

        return (hand != null) && progressable(hand);
    }

    /**
     * Checks whether the player can queue up for a specific poker game
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @param buyin       buyin
     * @return Whether player can queue up for the game or not
     */
    @Override
    public boolean isQueueable(String pokergameId, String playerId, BigDecimal buyin) {

        // all the right data points were passed. next to validate those points
        // check for valid poker game
        PokerGame game = PokerGame.findPokerGame(pokergameId);

        // check if player exists and if player is already in the queue
        Player player = Player.findPlayer(playerId);

        return (game != null) && (player != null) && (buyin != null) && queueable(game, player, buyin);
    }

    /**
     * This method checks for whether the gambler can raise for the current round.
     * The rules dictating whether the user can raise are:
     * 1. Raise max limit for gambler has not been reached for the current round
     * 2. Gambler wasn't the last to raise when subsequently every one else called
     *
     * @param gamblerId Gambler id to check whether he can raise
     * @return Returns true if gambler can raise. Otherwise returns false
     */
    @Override
    @Transactional
    public boolean isRaiseable(String gamblerId) {
        Gambler gambler = Gambler.findGambler(gamblerId);

        if (gambler == null) {
            log.error("Gambler doesn't exist. ID: " + gamblerId);
        }

        return (gambler != null) && raiseable(gambler);
    }

    /**
     * Checks to see if specified gambler is the current small blind
     *
     *
     * @param gambler gambler
     * @return Returns true if gambler is the current small blind. Otherwise returns false.
     */
    @Override
    public boolean isSmallBlind(Gambler gambler) {
        return gambler.getSeatNumber().equals(gambler.getHand().getSmallBlindSeat());
    }

    /**
     * Checks whether the current gambler is allowed to post the small blind
     *
     * @param handId Id for gambler we wish to query for
     * @return Returns true if gambler can post small blind. Otherwise false
     */
    @Override
    public boolean isSmallBlindPostable(String handId) {
        Gambler gambler = findCurrentGambler(handId);

        if (gambler == null) {
            log.error("Current gambler is null for hand ID: " + handId);
        }

        return (gambler != null) && smallBlindPostable(gambler);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return Return value
     */
    @Override
    public boolean isStartable(String pokergameId) {

        // first we get the game
        PokerGame game = PokerGame.findPokerGame(pokergameId);

        if (game == null) {
            log.error("Poker game doesn't exist. ID: " + pokergameId);
        }

        return (game != null) && startable(game);
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Before a new hand can get created, the players need to take their seat.
     * This method will retrieve players from the QueuedGambler entity and add gamblers to the hand.
     * It will also move funds from the old gambler to the new if an existing gambler is joining the hand.
     *
     * @param hand Hand to add players to
     * @throws GameException exception
     */
    private void addPlayersToHand(Hand hand) throws GameException {
        Gambler gambler;

        if (hand == null) {
            throw new GameException("error.missing.data", "hand");
        }

        PokerGame           game           = hand.getGame();
        List<QueuedGambler> queuedGamblers = QueuedGambler.findLatestQueuedGamblers(game.getId(),
                                                 hand.getGame().getTemplate().getMaxPlayers());
        List<QueuedGambler> deletable = new ArrayList<QueuedGambler>();

        for (QueuedGambler qg : queuedGamblers) {
            Integer       seatNumber     = null;
            List<Integer> availableSeats = availableSeatNumbers(hand);

            if (availableSeats.contains(qg.getDesiredSeatNumber())) {
                if (log.isDebugEnabled()) {
                    log.debug("Queued gambler id: " + qg.getId() + " in queue. Desired seat: "
                              + qg.getDesiredSeatNumber() + " is available.");
                }

                // gambler gets the seat he desires
                seatNumber = qg.getDesiredSeatNumber();
            } else if (!availableSeats.contains(qg.getDesiredSeatNumber()) && !qg.getMustHaveSeat()) {

                // assigning first available seat
                seatNumber = availableSeats.get(0);

                if (log.isDebugEnabled()) {
                    log.debug("Queued gambler id: " + qg.getId() + " in queue. Desired seat: "
                              + qg.getDesiredSeatNumber() + " is not available but settling on seat: " + seatNumber);
                }
            } else if (!availableSeats.contains(qg.getDesiredSeatNumber()) && qg.getMustHaveSeat()) {
                if (log.isDebugEnabled()) {
                    log.debug("Leaving queued gambler id: " + qg.getId() + " in queue. Desired seat: "
                              + qg.getDesiredSeatNumber() + " is not available. Player will wait until next hand.");
                }
            }

            if (seatNumber != null) {

                // retrieve gamblers
                List<Gambler> gamblers = hand.getGamblers();

                // find the player
                Player player = Player.findPlayer(qg.getPlayerId());

                // instantiate the gambler entity
                gambler = new Gambler(qg.getAccountId(), player, hand, GamblerStatus.ACTIVE, seatNumber);

                // save gambler
                gambler.persist();

                // now we link the gambler with the gamblers already on the hand
                linkGambler(game, gambler, gamblers);

                // update game
                hand.getGamblers().add(gambler);

                if ((qg.getType() != null) && (qg.getAmount() != null)) {

                    // here is where we will move funds around / add funds / or not
                    // when the gambler first joins the hand the type will automatically be a buyin
                    // when he wants to add funds after a hand is complete he can queue that amount here
                    // if the gambler does not want to do anything for the next hand because
                    // he has sufficient cash, the type and amount in the condition will be null and
                    // he does nothing and this is the default from moving from hand to hand
                    transferPlayerFundsFromAccountToGame(game.getId(), gambler.getPlayer().getId(),
                            gambler.getAccountId(), qg.getAmount(), qg.getType());
                }

                deletable.add(qg);
            }
        }

        // remove queued gambler that are now real gamblers
        QueuedGambler.removeQueuedGamblers(deletable);
    }

    /**
     * Returns a list of available seat numbers for the hand
     *
     * @param hand Hand to retrieve seat numbers for
     * @return Returns a list of available seat numbers for the specified game
     */
    private List<Integer> availableSeatNumbers(Hand hand) {

        // first we add a map of all available seat numbers (e.g. 1 - 10)
        Map<Integer, Integer> availableSeats = new HashMap<Integer, Integer>();

        for (int i = 1; i <= hand.getGame().getTemplate().getMaxPlayers(); i++) {
            availableSeats.put(i, i);
        }

        for (Gambler g : hand.getGamblers()) {
            availableSeats.remove(g.getSeatNumber());
        }

        // convert to list and sort
        List<Integer> result = new ArrayList<Integer>(availableSeats.values());

        Collections.sort(result);

        return result;
    }

    /**
     * Controls whether gambler can bet on a given hand
     *
     * @param hand hand
     * @return Return value
     */
    private boolean bettable(Hand hand) {
        boolean result;
        boolean isEndGame = isEndGame(hand.getId());

        // the hand is still bettable IF it's either not end game or if we are in POCKET_CARDS, FLOP, TURN or RIVER mode
        if (isEndGame) {
            result = false;
        } else {
            HandStatus currentHandStatus = hand.getStatus();
            Gambler    currentGambler    = findCurrentGambler(hand.getId());

            result = ((currentHandStatus.equals(HandStatus.POCKET_CARDS) || currentHandStatus.equals(HandStatus.FLOP)
                       || currentHandStatus.equals(HandStatus.TURN)
                       || currentHandStatus.equals(HandStatus.RIVER)) && (currentGambler != null));
        }

        return result;
    }

    /**
     * Method description
     *
     * @param gambler gambler
     * @return Return value
     */
    private boolean bigBlindPostable(Gambler gambler) {
        boolean result = false;

        if (gambler.getHand().getStatus().equals(HandStatus.POST_BIG_BLIND) && isBigBlind(gambler)) {
            result = true;
        }

        return result;
    }

    /**
     * This method will first verify that gambler can call for this hand.
     * Then it will figure out what the gambler owes in order to make a successful call. Once a successful call
     * is made to the correct pot, the hand will get updated with required info such as the next better and maybe a round number increment.
     *
     * @param gambler                  gambler for which to call
     * @param gotoNextGamblerAfterCall Whether to go to the next gambler after this call is made. Sometimes you do a call before you do another action and you don't want to change the current gambler before all actions are complete. E.g. Raise.
     * @throws GameException exception
     */
    private void call(Gambler gambler, boolean gotoNextGamblerAfterCall) throws GameException {
        BigDecimal gamblerBalance = GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                                        gambler.getPlayer().getId());

        // get a list of all bets made by gambler for each pot
        // we'll get back a 0 pot number for a total that we can tally against
        BigDecimal outstandingAmount = callAmount(gambler);

        if (log.isDebugEnabled()) {
            log.debug("Gambler with id: " + gambler.getId() + " owes: " + outstandingAmount.toString());
        }

        if (outstandingAmount.compareTo(gamblerBalance) == 1) {
            if (log.isDebugEnabled()) {
                log.debug("Gambler with id: " + gambler.getId()
                          + " does not have enough funds to call and, instead, has to go all-in in the amount of: "
                          + gamblerBalance.toString());
            }

            // this gambler has to go all in on this pot
            // now we can place a bet to go all-in for the current pot for the current gambler
            placeBet(gambler, gamblerBalance, BetType.ALL_IN, gambler.getHand().getStatus());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Gambler with id: " + gambler.getId() + " has enough funds and is calling");
            }

            // gambler can now place bet regardless of whether he is calling or going all-in. placeBet will handle that
            placeBet(gambler, outstandingAmount, BetType.CALL, gambler.getHand().getStatus());
        }

        // we want this to be conditional because we will be calling this method before doing a raise,
        // and we don't want this method to change the current player until after the raise is complete
        if (gotoNextGamblerAfterCall) {

            // call has been made successfully - time to go to the next gambler
            gotoNextGambler(gambler);
        }
    }

    /**
     * Determines the outstanding call amount for gambler in question.
     * Retrieves gambler's current bet amount for the current round and the max bet amount for that same round.
     * If the gambler's current bet amount is less than the max bet, the gambler owes the pot the delta of the two
     *
     * @param gambler gambler
     * @return Returns the call amount the gambler has for the current round
     */
    private BigDecimal callAmount(Gambler gambler) {
        Integer roundNumber = gambler.getHand().getCurrentRoundNumber();

        // there is a case we have to test for. if there are only 2 players (sb and bb), they will progress
        // to the next round before evening out the pot. if we don't check for total bet amount and instead
        // only check for total bet amount by round number, small blind will not owe anything in that new round
        // and will consequently not be able to call but will be able to check
        Long       gamblerCount = Gambler.findActiveGamblerCountForHand(gambler.getHand().getId());
        BigDecimal currentBetAmount;
        BigDecimal currentMaxBet;

        if ((gamblerCount == 2L) && (roundNumber == 2)) {
            currentBetAmount = Bet.findBetAmount(gambler.getHand().getId(), gambler.getPlayer().getId());
            currentMaxBet    = Bet.findMaxBetExcludePlayer(gambler.getHand().getId(), gambler.getPlayer().getId());
        } else {
            currentBetAmount = Bet.findBetAmountByRound(gambler.getHand().getId(), gambler.getPlayer().getId(),
                    roundNumber);
            currentMaxBet = Bet.findMaxBetByRoundNumberExcludePlayer(gambler.getHand().getId(), roundNumber,
                    gambler.getPlayer().getId());
        }

        return (currentMaxBet.compareTo(currentBetAmount) == 1)
               ? currentMaxBet.subtract(currentBetAmount)
               : new BigDecimal(0);
    }

    /**
     * Checks whether gambler can call on the current hand.
     * The rules are:
     * 1. If gambler has an outstanding amount to the pot, he can call
     * 2. If the gambler has an oustanding amount to the pot BUT he went all-in on his last bet, he cannot call
     *
     * @param gambler The gambler specified
     * @return Returns true if the gambler can call. False otherwise.
     */
    private boolean callable(Gambler gambler) {
        boolean result = false;

        if (isValidHandAndGamblerState(gambler)) {
            boolean    allIn      = wasLastBetAllIn(gambler);
            BigDecimal callAmount = callAmount(gambler);

            if ((callAmount.compareTo(new BigDecimal(0)) == 1) && !allIn) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Executes a check in a round for the specified gambler.
     *
     * @param gambler Entity for gambler we wish to check for
     * @throws GameException exception
     */
    private void check(Gambler gambler) throws GameException {

        // first check if we can check
        if (!checkable(gambler)) {
            log.error("Gambler with id: " + gambler.getId() + " is not allowed to CHECK");

            throw new GameException("error.game.poker.call.restricted", gambler.getId(), "CHECK");
        }

        Hand h = gambler.getHand();

        placeBet(gambler, new BigDecimal(0), BetType.CHECK, h.getStatus());

        if (log.isDebugEnabled()) {
            log.debug("Checked successfully for gambler with id: " + gambler.getId());
        }

        // go to next better
        gotoNextGambler(gambler);
    }

    /**
     * This method check to see whether gambler can check for this current round.
     * A check entails no previous bets having been met in the current round
     *
     * @param gambler gambler
     * @return Returns true if the gambler can check
     */
    private boolean checkable(Gambler gambler) {
        boolean result = false;

        if (isValidHandAndGamblerState(gambler)) {

            // then we get the current round number
            Integer    roundNumber = gambler.getHand().getCurrentRoundNumber();
            BigDecimal maxBet;

            // in order to be able to check you cannot have an outstanding amount
            // but if that was the only condition the round would never end.
            // the gambler also cannot have made a bet for that round except for the big blind in round 1.
            // so the rules are:
            // 1. no outstanding debt
            // 2. no bets have been made in current round
            // 3. except for big blind in round 1 but he can only do it once
            // 4. no gamblers have raised during the round
            Long betCount = Bet.findBetCountByRoundNumber(gambler.getHand().getId(), gambler.getPlayer().getId(),
                                roundNumber);
            boolean isBigBlind       = isBigBlind(gambler);
            boolean bigBlindCanCheck = false;

            // there is a case we have to test for. if there are only 2 players (sb and bb), they will progress
            // to the next round before evening out the pot. if we don't check for total bet amount and instead
            // only check for total bet amount by round number, small blind will not owe anything in that new round
            // and will consequently not be able to call but will be able to check
            Long       gamblerCount = Gambler.findActiveGamblerCountForHand(gambler.getHand().getId());
            BigDecimal gamblerBet;

            if ((gamblerCount == 2L) && (roundNumber == 2)) {

                // gambler bet is small blind
                gamblerBet = findBetAmount(gambler.getHand().getId(), gambler.getPlayer().getId());

                // max bet is big blind.
                maxBet = Bet.findMaxBet(gambler.getHand().getId());
            } else {

                // now we find out how much the gambler has bet to date
                gamblerBet = findBetAmountByRoundNumber(gambler.getHand().getId(), gambler.getPlayer().getId(),
                        roundNumber);

                // then we check to see if anyone placed any bets in that round
                maxBet = Bet.findMaxBetByRoundNumber(gambler.getHand().getId(), roundNumber);
            }

            // big blind check says if gambler hasn't checked before in the 1st round
            // and no one has raised during that round and the pot is the equal to the big blind
            // and gambler is the big blind
            if (isBigBlind && (roundNumber == 1)) {
                Long checkCount = Bet.findGamblerCheckCount(gambler.getHand().getId(), gambler.getPlayer().getId(),
                                      roundNumber);
                Long raiseCount = Bet.findRaiseCount(gambler.getHand().getId(), roundNumber);

                if ((raiseCount == 0L) && (checkCount == 0L) && (maxBet.compareTo(gamblerBet) == 0)) {
                    bigBlindCanCheck = true;
                }
            }

            // there are 2 checks here. 1. if gambler is not big blind they can't have an outstanding debt to the pot and they cannot have made a bet for the current round number.
            // 2. if the gambler is the big blind there cannot be an outstanding debt to the pot, he cannot have checked before and the round number has to be one
            boolean isEqual = (maxBet.compareTo(gamblerBet) == 0) && (betCount == 0);

            result = isEqual || bigBlindCanCheck;
        }

        return result;
    }

    /**
     * Method description
     *
     * @param gambler gambler
     * @return Returns true if the gambler is the current gambler
     */
    private boolean currentGambler(Gambler gambler) {
        return gambler.getSeatNumber().equals(gambler.getHand().getCurrentGamblerSeat())
               && gambler.getStatus().equals(GamblerStatus.ACTIVE);
    }

    /**
     * Common method used to issue community cards
     * There will be changes made to the hand here. The actual merge of the hand will happen
     * in the top calling method doProgressHand()
     *
     * @param hand The hand to deal the card for
     * @return Returns the string of cards that were dealt
     * @throws GameException Throws exception if hand or status is null
     */
    private String dealCommunityCard(Hand hand) throws GameException {
        String result = null;

        // initialize the game with an existing stack of cards from the db
        TexasHoldemPokerGame thp = new TexasHoldemPokerGame(hand.getDeck());

        if (log.isTraceEnabled()) {
            log.trace("Deck with hand status =  " + hand.getStatus() + " : " + thp);
        }

        switch (hand.getStatus()) {
            case FLOP :
                hand.setFlop(thp.dealFlop());
                result = hand.getFlop();

                break;

            case TURN :
                hand.setTurn(thp.dealCard());
                result = hand.getTurn();

                break;

            case RIVER :
                hand.setRiver(thp.dealCard());
                result = hand.getRiver();
        }

        hand.setCurrentRoundNumber(hand.getCurrentRoundNumber() + 1);

        // updated the deck and persist it again
        hand.setDeck(thp.getDeck());

        // reset current better back to first better
        // but the first gambler can also have folded, so we need to iterate through until we find one that is active
        hand.setCurrentGamblerSeat(getFirstAvailableGamblerSeatNumber(hand));

        if (log.isTraceEnabled()) {
            log.trace("Deck after " + hand.getStatus() + ": " + thp);
        }

        return result;
    }

    /**
     * After the small and big blind have been posted, we can go ahead and deal out the pocket cards
     *
     * @param hand Hand we wish to deal pocket cards for
     * @throws GameException Throws an exception if handId is null or if there aren't any active gamblers
     */
    private void dealPocketCards(Hand hand) throws GameException {
        List<Gambler> gamblers = Gambler.findActiveGamblersForHand(hand.getId());

        if ((gamblers == null) || gamblers.isEmpty()) {
            throw new GameException("error.game.inconsistent.state", "No gamblers for game");
        }

        if (StringUtils.isBlank(hand.getDeck())) {
            throw new GameException("error.game.inconsistent.state", "No gamblers for game");
        }

        // re-initialize the deck
        TexasHoldemPokerGame thp = new TexasHoldemPokerGame(hand.getDeck());

        for (Gambler g : gamblers) {
            g.setCards(thp.dealPocketCards());
            g.merge();
        }

        // update the deck on the hand
        hand.setDeck(thp.getDeck());
    }

    /**
     * When the game has stopped before the game is finished. e.g. everyone folds except one etc. This method is called
     * <p/>
     * This method does a whole bunch of stuff.
     * 1. It will return a list of gamblers who have completed it to the end (this MUST only be one)
     * 2. Will post the wins/losses to gamblers accounts
     * 3. Will save the stats
     *
     * @param hand Hand we are doing the showdown for
     * @throws GameException exception
     */
    private void endGame(Hand hand) throws GameException {
        List<Gambler> gamblers;

        if (isEndGame(hand.getId())) {
            if (log.isDebugEnabled()) {
                log.debug("Hand with id: " + hand.getId() + " is ready for end game");
            }

            // retrieve all gamblers who are still playing
            gamblers = findGamblers(hand.getId());

            if ((gamblers != null) && (gamblers.size() > 0)) {

                // update hand status to complete
                hand.setStatus(HandStatus.COMPLETE);
                hand.merge();

                // the winner is the only guy that is active
                for (Gambler gambler : gamblers) {
                    if (gambler.getStatus().equals(GamblerStatus.ACTIVE)) {
                        gambler.setWinner(true);
                    } else {
                        gambler.setWinner(false);
                    }
                }

                // give winner(s) his rightful pot
                processGamblersAfterHandIsFinished(hand, gamblers);
            } else {
                log.error(
                    "There are no gamblers left for this hand to be able to process a winner. There should always be at least one gambler left on the table.");

                throw new GameException("error.game.showdown.gamblers");
            }

            // put losers back on the bench
            requeueGamblers(hand);
        }
    }

    /**
     * Convenience method for folding a gambler. This method is meant to be called internally where
     * the gambler already exists and there is no need to call the persistence layer again to retrieve the gambler
     *
     * @param gambler Gambler entity
     */
    private void fold(Gambler gambler) {
        gambler.setStatus(GamblerStatus.FOLDED);
        gambler.merge();

        if (log.isDebugEnabled()) {
            log.debug("Gambler with id: " + gambler.getId() + " has folded");
        }

        // goto next gambler
        gotoNextGambler(gambler);
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method is used when dealing a new hand. Since the gamblers are linked, we can easily go to one gambler at the time
     * and return the one that matches the conditions.
     *
     * @param gambler  First gambler to start checking whether he fullfills the criteria as an available gambler
     * @param stopLoop This is the last gambler that should be checked. This avoid an infinite loop problem if we keep on calling next on gambler.
     * @return Returns an active gambler
     */
    private Gambler getFirstAvailableGambler(Gambler gambler, Gambler stopLoop) {
        Gambler result = null;

        do {
            boolean allInBet = wasLastBetAllIn(gambler);

            // first available gambler is active and hasn't just gone all in
            if (gambler.getStatus().equals(GamblerStatus.ACTIVE) && !allInBet) {
                result = gambler;
            } else {
                gambler = gambler.getNextGambler();
            }
        } while ((gambler != null) && !gambler.getId().equals(stopLoop.getId()) && (result == null));

        if (result == null) {
            if (log.isDebugEnabled()) {
                log.debug("There are no more gamblers for hand");
            }
        }

        return result;
    }

    /**
     * Returns the gambler that most closely matches the specified seat number
     *
     * @param gamblers   gamblers
     * @param maxPlayers maxPlayers
     * @param seatNumber seatNumber
     * @return Gambler
     */
    private Gambler getFirstAvailableGamblerBySeatNumber(List<Gambler> gamblers, Integer maxPlayers,
            Integer seatNumber) {
        Gambler result = null;

        if (gamblers != null) {
            for (Gambler gambler : gamblers) {
                if (gambler.getSeatNumber().equals(seatNumber)) {
                    return gambler;
                }
            }

            // that means no one is sitting in that chair - so we get the gambler who sits the closest to it
            // TODO this might be inefficient
            for (Gambler gambler : gamblers) {
                Integer nextSeat = seatNumber + 1;

                if (nextSeat > maxPlayers) {
                    nextSeat = nextSeat - maxPlayers;
                }

                result = getFirstAvailableGamblerBySeatNumber(gamblers, maxPlayers, nextSeat);
            }
        }

        return result;
    }

    /**
     * Loops through gamblers in seats and returns the seat number of the first active gambler it can find.
     * We're not checking for anything but gambler status. The gambler might have no funds but be all-in in a game
     * in which case the status is the only indication we have.
     *
     * @param hand The hand for which to retrieve the first available seat number
     * @return Returns the first seat number for an available gambler
     */
    private Integer getFirstAvailableGamblerSeatNumber(Hand hand) {
        if (hand == null) {
            throw new IllegalArgumentException("hand cannot be null");
        }

        Integer result = null;
        Gambler g      = Gambler.findGamblerBySeatNumber(hand.getId(), hand.getFirstGamblerSeat());

        g = getFirstAvailableGambler(g, g);

        if (g != null) {
            result = g.getSeatNumber();
        }

        if (log.isDebugEnabled()) {
            if (result == null) {
                log.debug(
                    "There are not active seats in the game any longer. Setting currentBetterSeatNumber to null. Handling end-of-game somewhere else.");
            } else {
                log.debug("The first seat number to play after the new card is dealt is: " + result);
            }
        }

        return result;
    }

    /**
     * Finds the gambler sitting in the specified seat number
     *
     * @param gamblers   gamblers
     * @param dealerSeat dealerSeat
     * @return Returns the gambler entity for that seat
     */
    private Gambler getGamblerInSeat(List<Gambler> gamblers, Integer dealerSeat) {
        if (gamblers != null) {
            for (Gambler gambler : gamblers) {
                if (gambler.getSeatNumber().equals(dealerSeat)) {
                    return gambler;
                }
            }
        }

        return null;
    }

    /**
     * Finds the gambler with the lowest seat number
     *
     * @param gamblers gamblers
     * @return Gambler with lowest seat number
     */
    private Gambler getGamblerWithLowestSeatNumber(List<Gambler> gamblers) {
        Gambler result = null;

        if (gamblers != null) {
            result = gamblers.get(0);

            for (Gambler gambler : gamblers) {
                if (gambler.getSeatNumber() < result.getSeatNumber()) {
                    result = gambler;
                }
            }
        }

        return result;
    }

    /**
     * Method description
     *
     * @param gambler gambler
     * @return Returns the maximum amount the gambler can raise
     */
    private BigDecimal getMaximumRaiseAmount(Gambler gambler) {
        BigDecimal result = null;
        BigDecimal zero   = new BigDecimal(0);
        BigDecimal two    = new BigDecimal(2);

        if (!raiseable(gambler)) {
            result = zero;
        } else {

            // first we need to see what limit type this game is
            switch (gambler.getHand().getGame().getTemplate().getLimitType()) {
                case FIXED_LIMIT :

                    // fixed limit is BB before the Turn and BB*2 after the Turn
                    switch (gambler.getHand().getStatus()) {
                        case POCKET_CARDS :
                        case FLOP :
                            result = gambler.getHand().getGame().getTemplate().getStake().getHigh();

                            break;

                        case TURN :
                        case RIVER :
                            result = gambler.getHand().getGame().getTemplate().getStake().getHigh().multiply(two);

                            break;

                        default :
                            log.error("There is no maximum raise amount when the hand has status: "
                                      + gambler.getHand().getStatus());
                            result = zero;
                    }

                    break;

                case POT_LIMIT :
                    switch (gambler.getHand().getStatus()) {
                        case POCKET_CARDS :
                        case FLOP :
                        case TURN :
                        case RIVER :

                            // call amounts returns us a map of all the different outstanding debts gambler has to specific pots
                            // the 0th spot in that is the total outstanding bets gambler has
                            result = callAmount(gambler);

                            break;

                        default :
                            log.error("There is no maximum raise amount when the hand has status: "
                                      + gambler.getHand().getStatus());
                            result = zero;
                    }

                    break;

                case NO_LIMIT :
                    switch (gambler.getHand().getStatus()) {
                        case POCKET_CARDS :
                        case FLOP :
                        case TURN :
                        case RIVER :

                            // the maximum raise amount for a no limit game the gambler's total stake MINUS any outstanding call amount
                            BigDecimal callAmount = callAmount(gambler);
                            BigDecimal stake      =
                                GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                                    gambler.getPlayer().getId());

                            // if call amount is greater or equal to the stake, the maximum raise amount is 0
                            if (stake.compareTo(callAmount) < 1) {
                                result = zero;
                            } else {

                                // else, just subtract any outstanding call amount from the stake to get the maximum raise amount
                                result = stake.subtract(callAmount);
                            }

                            break;

                        default :
                            log.error("There is no maximum raise amount when the hand has status: "
                                      + gambler.getHand().getStatus());
                            result = zero;
                    }

                    break;

                default :
                    log.error("System doesn't support limit type: "
                              + gambler.getHand().getGame().getTemplate().getLimitType());
            }
        }

        return result;
    }

    /**
     * Returns the minimum raise amount for a gambler.
     * If the gambler cannot raise, we return a 0. Otherwise we return the minimum raise amount.
     * The minimum raise amount rules are outlined here: http://harvold.com:8090/display/PKR/Gameplay
     * Note that regardless whether the gambler can raise or not, we return the minimum raise amount
     *
     * @param gambler The gambler we wish to query
     * @return Returns the minimum raise amount
     */
    private BigDecimal getMinimumRaiseAmount(Gambler gambler) {
        BigDecimal result = null;
        BigDecimal zero   = new BigDecimal(0);
        BigDecimal two    = new BigDecimal(2);

        // first we need to see what limit type this game is
        switch (gambler.getHand().getGame().getTemplate().getLimitType()) {
            case FIXED_LIMIT :

                // fixed limit is BB before the Turn and BB*2 after the Turn
                switch (gambler.getHand().getStatus()) {
                    case POCKET_CARDS :
                    case FLOP :
                        result = gambler.getHand().getGame().getTemplate().getStake().getHigh();

                        break;

                    case TURN :
                    case RIVER :
                        result = gambler.getHand().getGame().getTemplate().getStake().getHigh().multiply(two);

                        break;

                    default :
                        log.error("There is no minimum raise amount when the hand has status: "
                                  + gambler.getHand().getStatus());
                        result = zero;
                }

                break;

            case POT_LIMIT :
            case NO_LIMIT :
                switch (gambler.getHand().getStatus()) {
                    case POCKET_CARDS :
                    case FLOP :
                    case TURN :
                    case RIVER :

                        // call amounts returns us a map of all the different outstanding debts gambler has to specific pots
                        // the 0th spot in that is the total outstanding bets gambler has
                        BigDecimal largestBet = callAmount(gambler);

                        // if the largest bet made to date is greater than the big blind, minimum raise amount is largestBet
                        if (largestBet.compareTo(gambler.getHand().getGame().getTemplate().getStake().getHigh()) == 1) {
                            result = largestBet;
                        } else {

                            // else, it's the big blind
                            result = gambler.getHand().getGame().getTemplate().getStake().getHigh();
                        }

                        break;

                    default :
                        log.error("There is no minimum raise amount when the hand has status: "
                                  + gambler.getHand().getStatus());
                        result = zero;
                }

                break;

            default :
                log.error("System doesn't support limit type: "
                          + gambler.getHand().getGame().getTemplate().getLimitType());
        }

        return result;
    }

    /**
     * Method to find the gambler with the lower seat number than the one specified
     *
     * @param gamblers   Gamblers to check for lowest seat number
     * @param maxPlayers maxPlayers
     * @param seatNumber The seat number in question
     * @return Returns the gambler entity with the lowest seat number
     */
    private Gambler getPreviousGamblerBySeat(List<Gambler> gamblers, Integer maxPlayers, Integer seatNumber) {

        // if there is only one existing gambler, we can safely return that one
        if (gamblers.size() == 1) {
            return gamblers.get(0);
        }

        // loop down from seatNumber to 0 until we find a gambler with a seat number lower
        for (int i = seatNumber - 1; i > 0; i--) {
            for (Gambler gambler : gamblers) {
                if (log.isTraceEnabled()) {
                    log.trace("Gambler seat number: " + gambler.getSeatNumber() + ". Looking for seat number: " + i);
                }

                if (gambler.getSeatNumber() == i) {
                    return gambler;
                }
            }
        }

        // this means we didn't find the gambler on the way down -
        // now we need to start the loop from on high and go down again
        for (int i = maxPlayers; i > seatNumber; i--) {
            for (Gambler gambler : gamblers) {
                if (log.isTraceEnabled()) {
                    log.trace("Gambler seat number: " + gambler.getSeatNumber() + ". Looking for seat number: " + i);
                }

                if (gambler.getSeatNumber() == i) {
                    return gambler;
                }
            }
        }

        return null;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * This method will go to the next gambler and let the next gambler start betting.
     * The way we determine whether there is a next gambler are these rules:
     * 1. There is another gambler who has not yet folded that has an outstanding debt to the pot
     * 2. There is another gambler who had the big blind who still needs to check
     *
     * @param currentGambler The current gambler to iterate from
     */
    private void gotoNextGambler(Gambler currentGambler) {
        Gambler nextGambler = null;
        Hand    h           = currentGambler.getHand();
        Gambler g           = currentGambler.getNextGambler();

        // loop around until we find the next valid seat
        // if there are none then this round is done
        while ((g != null) && !g.getId().equals(currentGambler.getId()) && (g.getHand() != null)
                && (nextGambler == null)) {

            // the big differences with going to the next gambler based on status are the blinds
            switch (h.getStatus()) {
                case START :
                    break;

                case POST_SMALL_BLIND :
                case POST_SMALL_BLIND_COMPLETE :
                case POST_BIG_BLIND :
                case POST_BIG_BLIND_COMPLETE :
                    if (g.getStatus().equals(GamblerStatus.ACTIVE)) {
                        nextGambler = g;
                    }

                    break;

                case POCKET_CARDS :
                case FLOP :
                case TURN :
                case RIVER :
                    boolean isCallable  = callable(g);
                    boolean isCheckable = checkable(g);
                    boolean isRaisable  = raiseable(g);

                    if (log.isTraceEnabled()) {
                        log.trace("Next player: " + g.getPlayer() + ". \nRaise: " + isRaisable + ", Call: "
                                  + isCallable + ", Check: " + isCheckable);
                    }

                    if (g.getStatus().equals(GamblerStatus.ACTIVE) && (isCallable || isCheckable || isRaisable)) {
                        nextGambler = g;
                    }

                    break;

                default :
            }

            g = g.getNextGambler();
        }

        if (nextGambler != null) {
            if (log.isDebugEnabled()) {
                log.debug("The next gambler for hand with id: " + nextGambler.getHand().getId() + " is: "
                          + nextGambler.getId());
            }

            h.setCurrentGamblerSeat(nextGambler.getSeatNumber());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("There is no next gambler for hand. This round is over!");
            }

            h.setCurrentGamblerSeat(null);
        }

        // update hand
        h.merge();
    }

    //~--- get methods --------------------------------------------------------

    /**
     *
     * @param gambler gambler
     * @return Returns a boolean stating whether or not this user is already playing this game
     *         with one of his player accounts
     *
     * private Gambler isUserAlreadyInGame(List<Gambler> gamblers, Player player) {
     *   Gambler result = null;
     *
     *   if (gamblers != null) {
     *       for (Gambler gambler : gamblers) {
     *           if (gambler.getPlayer().getApplicationUser().getId().equals(player.getApplicationUser().getId())) {
     *               result = gambler;
     *
     *               break;
     *           }
     *       }
     *   }
     *
     *   return result;
     * }
     */

    /**
     * Called by callable, checkable and raisable. This method makes sure the hand and gambler
     * have the correct statuses before the callers go int their respective business rules
     *
     * @param gambler The gambler specified
     * @return Returns true if both the hand and the gambler are in a valida state. False otherwise.
     */
    private boolean isValidHandAndGamblerState(Gambler gambler) {
        boolean result = false;

        if (gambler.getHand().getStatus().equals(HandStatus.POCKET_CARDS)
                || gambler.getHand().getStatus().equals(HandStatus.FLOP)
                || gambler.getHand().getStatus().equals(HandStatus.TURN)
                || gambler.getHand().getStatus().equals(HandStatus.RIVER)) {
            if (gambler.getStatus().equals(GamblerStatus.ACTIVE)) {
                result = true;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Gambler: " + gambler.getId()
                              + " cannot do anything any more as he is no longer in the game");
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Gambler with id: " + gambler.getId()
                          + " cannot do anything any more because hand status is: " + gambler.getHand().getStatus());
            }
        }

        return result;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * A gambler that wishes to leave a game or that has no money left on the table will eventually cll this method.
     * The method will fold any open hand, transfer the remained of the balance back to gambler's account
     * and remove the gambler from the game thereby unlinking him from the other gamblers
     *
     * @param gambler Gambler entity that wishes to leave the game
     * @throws GameException Throws an exception if gambler doesn't exist
     */
    private void leaveGame(Gambler gambler) throws GameException {

        // we only fold the gambler if the hand is active
        if ((gambler.getHand() != null) && !gambler.getHand().getStatus().equals(HandStatus.COMPLETE)) {

            // we only fold when we are in a game
            fold(gambler);
        }

        BigDecimal balance = GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                                 gambler.getPlayer().getId());

        // if there is an outstanding balance we need to move it back to the player's account
        if (balance.compareTo(new BigDecimal(0)) == 1) {

            // transfer money back to account
            transferPlayerFundsFromGameToAccount(gambler.getHand().getGame().getId(), gambler.getPlayer().getId(),
                    gambler.getAccountId(), balance);
        }

        if (log.isDebugEnabled()) {
            log.debug("Removing gambler: " + gambler.getId() + " from game with id: "
                      + gambler.getHand().getGame().getId());
        }

        removeGamblerFromGame(gambler);

        if (log.isDebugEnabled()) {
            log.debug("Gambler with id: " + gambler.getId() + " has left the game with id: "
                      + gambler.getHand().getGame().getId());
        }
    }

    /**
     * This method will remove the gambler entity and free up the seat. But before
     * it does that it has to transfer the gamblers winnings back to the user account.
     *
     * @param gambler Gambler entity who wishes to stop playing the game and go back to observing the game
     * @throws GameException exception
     */
    private void leaveGameAndObserve(Gambler gambler) throws GameException {
        if (log.isDebugEnabled()) {
            log.debug("Removing gambler: " + gambler.getId() + " from game: " + gambler.getHand().getGame().getId());
        }

        leaveGame(gambler);

        if (log.isDebugEnabled()) {
            log.debug("Turning gambler into observer...");
        }

        doWatchGame(gambler.getHand().getGame().getId(), gambler.getPlayer().getId());

        if (log.isDebugEnabled()) {
            log.debug("Gambler with id: " + gambler.getId() + " is observing the game again");
        }
    }

    /**
     * Links a new or existing gambler into the eco-system of gamblers. It will also un-link a gambler who wishes
     * to move from one seat to another.
     *
     * @param game     PokerGame for which we are linking the gambler for
     * @param g        The gambler we are linking
     * @param gamblers Gamblers already seated by this game
     * @throws GameException Throws exception if next or previous gamblers could not be found to link to
     */
    private void linkGambler(PokerGame game, Gambler g, List<Gambler> gamblers) throws GameException {
        if (gamblers.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("This gambler is the first one to join this game. No linking with other gamblers necessary.");
            }

            // the first gambler will just link with himself first - that is ok - the links will change as more gamblers join
            g.setNextGambler(g);
            g.setPreviousGambler(g);
        } else {

            // if this is an existing gambler who was a connection to other gamblers, we need to un-link this first
            if ((g.getNextGambler() != null) && (g.getPreviousGambler() != null)) {

                // we need to join previous with next to un-link this gambler
                unlinkGambler(g);
            }

            // first we find the lowest seat gambler after this seat number
            Gambler previousGambler = getPreviousGamblerBySeat(gamblers, game.getTemplate().getMaxPlayers(),
                                          g.getSeatNumber());

            // with the previous gambler we know the next gambler
            Gambler nextGambler = previousGambler.getNextGambler();

            if (previousGambler == null) {
                throw new GameException("error.game.inconsistent.state", "Previous gambler is null");
            }

            if (nextGambler == null) {
                throw new GameException("error.game.inconsistent.state", "Next gambler is null");
            }

            if (log.isDebugEnabled()) {
                log.debug("Linked gambler with next and previous gamblers:\n" + "Previous gambler id: "
                          + previousGambler + "\n" + "Next gambler id: " + nextGambler);
            }

            // now we inject the new gambler between the previously linked gamblers
            g.setNextGambler(nextGambler);
            g.setPreviousGambler(previousGambler);

            // then we update the next and previous with the new guy as well
            nextGambler.setPreviousGambler(g);
            previousGambler.setNextGambler(g);

            // update
            nextGambler.merge();
            previousGambler.merge();
        }

        g.merge();
    }

    /**
     * This will place a bet on a hand for the gambler specified. It will withdraw the funds from the buyin and add the amount
     * to the specified pot.
     *
     * @param gambler Gambler to place bet for
     * @param amount  Amount to bet
     * @param type    Type of bet
     * @param status  Status the hand is currently in
     * @return returns a completed Bet entity
     * @throws GameException Throws exception if any of the parameters are missing and if the gambler has insufficient funds to bet with
     */
    private Bet placeBet(Gambler gambler, BigDecimal amount, BetType type, HandStatus status) throws GameException {
        if (gambler == null) {
            throw new IllegalArgumentException("gambler cannot be null");
        }

        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }

        Bet        result;
        BigDecimal gamblerBalance = GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                                        gambler.getPlayer().getId());

        if (gamblerBalance.compareTo(amount) == -1) {
            throw new GameException("error.insufficient.funds", amount.toString(), gamblerBalance.toString());
        }

        // the money needs to go from the gambler's balance to the bet
        GamblerAccountEntry ge = new GamblerAccountEntry(gambler.getHand().getGame().getId(),
                                     gambler.getHand().getId(), gambler.getPlayer().getId(), gambler.getAccountId(),
                                     GamblerAccountEntryType.BET, amount.negate(), null);

        ge.persist();

        // with that money we can now place a bet
        result = new Bet(amount, type, status, gambler.getPlayer().getId(), gambler.getHand().getId(),
                         gambler.getHand().getCurrentRoundNumber(), gambler.getPlayer().getNickname());
        result.persist();

        return result;
    }

    /**
     * Method description
     *
     * @param gambler gambler
     * @throws GameException exception
     */
    private void postBigBlind(Gambler gambler) throws GameException {
        if (!bigBlindPostable(gambler)) {
            throw new GameException("error.game.blind.big.restricted", gambler.getId());
        }

        BigDecimal bigBlind = gambler.getHand().getGame().getTemplate().getStake().getHigh();

        placeBet(gambler, bigBlind, BetType.BIG_BLIND, HandStatus.POST_BIG_BLIND);

        // change hand status to small blind complete
        Hand hand = gambler.getHand();

        hand.setStatus(HandStatus.POST_BIG_BLIND_COMPLETE);
        hand.merge();
        gotoNextGambler(gambler);
    }

    /**
     * Method description
     *
     * @param gambler gambler
     * @throws GameException exception
     */
    private void postSmallBlind(Gambler gambler) throws GameException {
        if (!smallBlindPostable(gambler)) {
            throw new GameException("error.game.blind.small.restricted", gambler.getId());
        }

        BigDecimal smallBlind = gambler.getHand().getGame().getTemplate().getStake().getLow();
        BigDecimal balance    = GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                                    gambler.getPlayer().getId());

        if (balance.compareTo(new BigDecimal(0)) == 0) {
            throw new GameException("error.insufficient.funds.small.blind");
        }

        // the seat with the small blind might have insufficient funds (but not 0) to cover it. Gambler can still go all in to play the game
        if (balance.compareTo(smallBlind) == -1) {
            placeBet(gambler, balance, BetType.SMALL_BLIND, gambler.getHand().getStatus());
        } else {
            placeBet(gambler, smallBlind, BetType.SMALL_BLIND, gambler.getHand().getStatus());
        }

        // change hand status to small blind complete
        Hand hand = gambler.getHand();

        hand.setStatus(HandStatus.POST_SMALL_BLIND_COMPLETE);
        hand.merge();
        gotoNextGambler(gambler);
    }

    /**
     * Splits the pot amount between the winners
     *
     * @param hand     Hand for which to process winners
     * @param gamblers Winning gamblers
     * @throws GameException exception
     */
    private void processGamblersAfterHandIsFinished(Hand hand, List<Gambler> gamblers) throws GameException {
        if (log.isDebugEnabled()) {
            log.debug("Processing winnings for hand with id: " + hand.getId() + "...");
        }

        if (!hand.getStatus().equals(HandStatus.COMPLETE)) {
            if (log.isErrorEnabled()) {
                log.error("Hand with id: " + hand.getId() + " is not complete yet. Cannot process gamblers.");
            }

            throw new GameException("error.game.inconsistent.hand.status", hand.getId());
        }

        List<Gambler> winners = new ArrayList<Gambler>();
        List<Gambler> losers  = new ArrayList<Gambler>();
        BigDecimal    maxBet  = new BigDecimal(0);

        if (gamblers != null) {
            for (Gambler gambler : gamblers) {
                if (gambler.getWinner()) {
                    winners.add(gambler);
                } else {
                    losers.add(gambler);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("There are: " + winners.size() + " winner(s)");
            }

            if (winners.size() > 0) {

                // now we want to loop through the winners and give each their due from the total pot
                for (Gambler winner : winners) {
                    BigDecimal winnerTotalBetAmount = Bet.findBetAmount(winner.getHand().getId(),
                                                          winner.getPlayer().getId());

                    // this is just so that we can keep track of who made the highest bet
                    // then we can check to see if any of the losers made a higher bet
                    // and that would mean there is an unclaimed side bet waiting to be returned
                    if (winnerTotalBetAmount.compareTo(maxBet) == 1) {
                        maxBet = winnerTotalBetAmount;
                    }

                    // this winner cannot take more from the pot than what gambler bet * number of gamblers or less
                    // so we have to loop through every gambler (including gambler himself) to see what he bet and then take a chunk of that money from the pot
                    BigDecimal winAmount = new BigDecimal(0);

                    for (Gambler gambler : gamblers) {
                        BigDecimal gamblerTotalBetAmount = Bet.findBetAmount(gambler.getHand().getId(),
                                                               gambler.getPlayer().getId());
                        Integer winnerCount = winners.size();

                        if (winnerTotalBetAmount.compareTo(gamblerTotalBetAmount) > -1) {

                            // in this case, the winner has bet more than the current gambler
                            // winner can only take as big a chunk from the pot that the loser put in divided by the amount of winners
                            winAmount = winAmount.add(gamblerTotalBetAmount.divide(new BigDecimal(winnerCount), 2));
                        } else {

                            // in this case, the winner has bet less than the current gambler
                            // winner can take out his own bet divided by the amount of winners
                            winAmount = winAmount.add(winnerTotalBetAmount.divide(new BigDecimal(winnerCount), 2));
                        }
                    }

                    // persist entry only if amount is greater than 0
                    if (winAmount.compareTo(new BigDecimal(0)) == 1) {
                        GamblerAccountEntry ge = new GamblerAccountEntry(winner.getHand().getGame().getId(),
                                                     winner.getHand().getId(), winner.getPlayer().getId(),
                                                     winner.getAccountId(), GamblerAccountEntryType.WIN, winAmount,
                                                     null);

                        ge.persist();

                        // also set the winning amount so we have it available on the gambler for display purposes
                        winner.setWinAmount(winAmount);
                        winner.merge();
                    }
                }

                // we have to loop through losers so we can return any unclaimed side bets
                for (Gambler loser : losers) {
                    BigDecimal loserTotalBetAmount = Bet.findBetAmount(loser.getHand().getId(),
                                                         loser.getPlayer().getId());

                    // if the amount the gambler bet is larger than the total amount of bets for the winner
                    // we need to return that money
                    if (loserTotalBetAmount.compareTo(maxBet) == 1) {

                        // yes - there is an unaccounted for side bet here that we need to take care of
                        BigDecimal          sidebet = loserTotalBetAmount.subtract(maxBet);
                        GamblerAccountEntry ge      = new GamblerAccountEntry(loser.getHand().getGame().getId(),
                                                          loser.getPlayer().getId(), loser.getAccountId(),
                                                          GamblerAccountEntryType.COLLECTED_UNACCOUNTED_SIDEBET,
                                                          sidebet, null);

                        ge.persist();
                    }
                }

                // all the gamblers in this pot got their stake
                // now we set all the bets this accounted for as withdrawn so they won't be used again
                Bet.updateBetStatus(hand.getId(), BetStatus.WITHDRAWN);
            } else {
                log.error("There were no winners for the hand. There should be at least one.");

                throw new GameException("error.game.inconsistent.state", "No winners for hand could be found");
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Processing winnings for gambler(s) for hand with id: " + hand.getId() + " complete");
        }
    }

    /**
     * Loops through gamblers and compares cards. Also updates the gambler entity with hand name and rank info
     *
     * @param hand     Hand entity for which to process winners
     * @param gamblers Gamblers that are still in the game
     * @return Returns the list of gamblers with their cards interpreted and their winner status
     */
    private List<Gambler> processGamblersAndComputeWinners(Hand hand, List<Gambler> gamblers) {
        Integer topHandRank = 0;

        if (log.isDebugEnabled()) {
            log.debug("Determining winner for hand with id: " + hand.getId() + "...");
        }

        StringBuilder communityCards = new StringBuilder();

        communityCards.append(hand.getFlop());
        communityCards.append(" ");
        communityCards.append(hand.getTurn());
        communityCards.append(" ");
        communityCards.append(hand.getRiver());
        communityCards.append(" ");

        // loop through gamblers and compute rank and hand name
        for (Gambler gambler : gamblers) {
            StringBuilder cards = new StringBuilder(communityCards);

            cards.append(gambler.getCards());

            Integer handRank = findHandRank(cards.toString());
            String  handName = findHandName(cards.toString());
            String  bestHand = findBestHand(cards.toString());

            gambler.setHandName(handName);
            gambler.setHandRank(handRank);
            gambler.setCards(bestHand);

            if (log.isTraceEnabled()) {
                log.trace("Determining whether gambler is the winner: " + gambler);
            }

            // this will take care of separating the winners from the losers
            // notice that it accommodates for
            if (handRank > topHandRank) {
                topHandRank = handRank;
            }
        }

        // now we find the gamblers who have that highest rank - might be several
        for (Gambler gambler : gamblers) {
            if (gambler.getHandRank().compareTo(topHandRank) == 0) {
                gambler.setWinner(true);
            }

            gambler.merge();
        }

        if (log.isDebugEnabled()) {
            log.debug("Winner for hand with id: " + hand.getId() + " determined");
        }

        if (log.isTraceEnabled()) {
            for (Gambler gambler : gamblers) {
                if (gambler.getWinner()) {
                    log.trace("Winner for hand with id: " + hand.getId() + " is: " + gambler.getId());
                }
            }
        }

        return gamblers;
    }

    /**
     * Method description
     *
     * @param hand hand
     * @throws GameException exception
     */
    private void progressHand(Hand hand) throws GameException {
        if (progressable(hand)) {
            if (log.isDebugEnabled()) {
                log.debug("Hand status before progress is: " + hand.getStatus());
            }

            // what is the current status of hand is what this switch statement is about and we want to move over to the next stage
            switch (hand.getStatus()) {
                case START :

                    // at the creation of the hand, the hand will be in this state.
                    // Calling doProgressHand only changes the hand status and kicks of the game
                    hand.setStatus(HandStatus.POST_SMALL_BLIND);

                    break;

                case POST_SMALL_BLIND_COMPLETE :

                    // once the small blind has either posted SB or chosen to sit out, we can move over to the big blind post
                    // there is nothing to this change either but to change the status
                    hand.setStatus(HandStatus.POST_BIG_BLIND);

                    break;

                case POST_BIG_BLIND_COMPLETE :

                    // once the BB is complete, we can deal the pocket cards
                    hand.setStatus(HandStatus.POCKET_CARDS);

                    // at this point we also have to change the first gambler seat on the hand as it is no longer the small blind
                    // now it's going to be the guy left of the big blind
                    Gambler bigBlind = Gambler.findGamblerBySeatNumber(hand.getId(), hand.getBigBlindSeat());

                    hand.setFirstGamblerSeat(getFirstAvailableGambler(bigBlind.getNextGambler(),
                            bigBlind).getSeatNumber());
                    dealPocketCards(hand);

                    break;

                case POCKET_CARDS :

                    // pocket cards have been dealt and betting is finished - dealing flop card
                    hand.setStatus(HandStatus.FLOP);
                    dealCommunityCard(hand);

                    break;

                case FLOP :

                    // flop has been dealt and betting is finished - dealing turn card
                    hand.setStatus(HandStatus.TURN);
                    dealCommunityCard(hand);

                    break;

                case TURN :

                    // turn has been dealt and betting is finished - dealing river card
                    hand.setStatus(HandStatus.RIVER);
                    dealCommunityCard(hand);

                    break;

                case RIVER :

                    // river has been dealt and betting is finished - do showdown
                    hand.setStatus(HandStatus.COMPLETE);
                    showdown(hand);

                    break;
            }

            hand.merge();

            if (log.isDebugEnabled()) {
                log.debug("Hand status after progress is: " + hand.getStatus());
            }
        }
    }

    /**
     * The game can only progress (progress is defined as moving cards, not betting) if there are more than
     * 1 active player and that the last cards hasn't been dealt
     *
     * @param hand Hand we wish to check whether it's progressable
     * @return Returns true if hand is progressable. Otherwise returns false.
     */
    private boolean progressable(Hand hand) {
        boolean result = true;

        if (isEndGame(hand.getId())) {
            if (log.isDebugEnabled()) {
                log.debug("There are less than 2 players in the game. Can't progress game further.");
            }

            result = false;
        } else {
            switch (hand.getStatus()) {
                case POST_SMALL_BLIND :
                case POST_BIG_BLIND :
                    result = false;

                    break;

                // these are the cases below that can cause progressable to be false
                case POST_SMALL_BLIND_COMPLETE :

                    // if the hand status is small blind - we expect there to be a bet for that hand that has a type of small blind
                    if (Bet.findSmallBlind(hand.getId()) == null) {
                        if (log.isDebugEnabled()) {
                            log.debug(
                                "The hand has the status of SMALL_BLIND but no small blind can be found. Cannot continue further before small blind has been paid.");
                        }

                        result = false;
                    }

                    break;

                case POST_BIG_BLIND_COMPLETE :

                    // if the hand status is big blind - we expect there to be a bet for that hand that has a type of big blind and the amount needs to be greater than 0
                    if (Bet.findBigBlind(hand.getId()) == null) {
                        if (log.isDebugEnabled()) {
                            log.debug(
                                "The hand has the status of BIG_BLIND but no big blind can be found. Cannot continue further before big blind has been paid.");
                        }

                        result = false;
                    }

                    break;

                case POCKET_CARDS :
                case FLOP :
                case TURN :
                case RIVER :
                    if (hand.getCurrentGamblerSeat() != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("There is still a current gambler assigned. Can't progress game further.");
                        }

                        result = false;
                    }

                    break;

                case COMPLETE :

                    // and if we have reached the complete status there is nothing left to do but deal a new hand
                    if (log.isDebugEnabled()) {
                        log.debug("The hand has the status of COMPLETE. Can't progress game further.");
                    }

                    result = false;
            }
        }

        return result;
    }

    /**
     * Method description
     *
     * @param game   game
     * @param player player
     * @param buyin  buyin
     * @return Return value
     */
    private boolean queueable(PokerGame game, Player player, BigDecimal buyin) {
        boolean result = true;

        // need to check if player is already in the queue
        QueuedGambler qg = QueuedGambler.findQueuedGamblerByGameAndPlayer(game.getId(), player.getId());

        if (qg != null) {
            result = false;
        }

        // we also need to check if the player is already playing the game
        // throw an error if the system user has already joined with a different account
        Gambler userAlreadyInGame = Gambler.findGamblerByGameAndPlayer(game.getId(), player.getId());

        if (userAlreadyInGame != null) {
            result = false;
        }

        // verify that user has enough money and the right currency
        String   userId   = player.getApplicationUser().getId();
        Currency currency = game.getTemplate().getCasino().getCurrency();
        Account  account  = Account.findAccountByUserAndCurrency(userId, currency);

        // throw an error if the player doesn't have an account matching the currency of the game
        if (account == null) {
            result = false;
        }

        BigDecimal balance = AccountEntry.findAccountBalance(account.getId());

        // throw an error if player doesn't have enough money in the account
        if (balance.compareTo(buyin) == -1) {
            result = false;
        }

        // throw an error if the buyin is less than the big blind
        if (buyin.compareTo(game.getTemplate().getStake().getHigh()) == -1) {
            result = false;
        }

        return result;
    }

    /**
     * This method will raise the pot for gambler with specified amount. If
     * the amount is the same as gambler balance, the raise will turn into an ALL-IN
     *
     * @param gambler Gambler who wants to raise
     * @param amount  Amount the gambler wants to raise by
     * @throws GameException exception
     */
    private void raise(Gambler gambler, BigDecimal amount) throws GameException {
        if (!raiseable(gambler) && !callable(gambler)) {
            log.error("Gambler with id: " + gambler.getId() + " is not allowed to RAISE");

            throw new GameException("error.game.poker.call.restricted", gambler.getId(), "RAISE");
        }

        // first call
        call(gambler, false);

        // now the gambler should not be able to call any more
        if (callable(gambler)) {
            log.error("Gambler with id: " + gambler.getId() + " needs to call first");

            throw new GameException("error.game.poker.call.expected", gambler.getId());
        }

        // but he should be able to raise now - othewise throw an exception
        if (!raiseable(gambler)) {
            log.error("Gambler with id: " + gambler.getId() + " is not allowed to RAISE");

            throw new GameException("error.game.poker.call.restricted", gambler.getId(), "RAISE");
        }

        Hand h = gambler.getHand();

        // grab the maximum and minimum raise amount
        BigDecimal minimumRaiseAmount = getMinimumRaiseAmount(gambler);
        BigDecimal maximumRaiseAmount = getMaximumRaiseAmount(gambler);

        // the amount needs to be greater or equal than the minimum and less or equal to the maximum
        // e.g. mini: 10, maxi: 20. amount = 15 is OK
        if ((amount.compareTo(minimumRaiseAmount) > -1) && (amount.compareTo(maximumRaiseAmount) < 1)) {
            BigDecimal gamblerBalance = GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                                            gambler.getPlayer().getId());

            // place the bet
            if (gamblerBalance.compareTo(amount) == 0) {
                placeBet(gambler, amount, BetType.ALL_IN, h.getStatus());
            } else {
                placeBet(gambler, amount, BetType.RAISE, h.getStatus());
            }

            if (log.isDebugEnabled()) {
                log.debug("Gambler with id: " + gambler.getId() + " raised by " + amount);
            }

            // move to next gambler
            gotoNextGambler(gambler);
        } else {
            log.error("Gambler with id: " + gambler.getId() + " cannot RAISE with amount: " + amount
                      + ". Minimum raise amount is: " + minimumRaiseAmount);

            throw new GameException("error.game.poker.raise.restricted", gambler.getId(), amount.toString(),
                                    minimumRaiseAmount.toString());
        }
    }

    /**
     * Method description
     *
     * @param gambler gambler
     * @return Returns true if the gambler can raise
     */
    private boolean raiseable(Gambler gambler) {
        boolean result = false;

        if (!isValidHandAndGamblerState(gambler)) {
            if (log.isDebugEnabled()) {
                log.debug("The hand has a status where raising is not allowed: " + gambler.getHand().getStatus());
            }
        } else {
            Bet     lastBet       = Bet.findLastBet(gambler.getHand().getId());
            boolean calledToRaise = false;

            if (lastBet != null) {
                calledToRaise = lastBet.getPlayerId().equals(gambler.getPlayer().getId())
                                && lastBet.getType().equals(BetType.CALL);
            }

            // a gambler can raise if he is able to check or to call OR since he might have to call
            // before raising we check if the last bet made was by him and it was a call
            if (!calledToRaise && !callable(gambler) && !checkable(gambler)) {
                if (log.isDebugEnabled()) {
                    log.debug("Gambler with id: " + gambler.getId()
                              + " can neither call nor check and can therefore not raise either");
                }
            } else {

                // first let's check if raise limit has been reached
                Integer raiseLimit   = gambler.getHand().getGame().getTemplate().getRaiseLimit();
                Integer currentRound = gambler.getHand().getCurrentRoundNumber();
                Long    raiseCount   = Bet.findRaiseCount(gambler.getHand().getId(), currentRound);

                if (raiseCount >= raiseLimit) {
                    if (log.isDebugEnabled()) {
                        log.debug("Gambler with id: " + gambler.getId() + " has already raised " + raiseLimit
                                  + " times, which is the maximum allotted per round");
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Hand with id: " + gambler.getHand().getId()
                                  + " hasn't exceeded the raise limit for the current round");
                    }

                    // ok - gambler can raise because he didn't pass the limit
                    // but gambler can't raise if everyone just called his last raise
                    // so we have to make sure that that hasn't happened
                    // Gambler also needs to have enough funds to raise
                    Bet        lastRaise          = Bet.findLastBet(gambler.getHand().getId(), BetType.RAISE,
                                                        currentRound);
                    BigDecimal minimumRaiseAmount = getMinimumRaiseAmount(gambler);
                    BigDecimal gamblerBalance     =
                        GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                            gambler.getPlayer().getId());
                    BigDecimal callAmount = callAmount(gambler);
                    boolean    hasFunds   = gamblerBalance.subtract(callAmount).compareTo(minimumRaiseAmount) > -1;

                    // if the last raise is null or it didn't belong to gambler, it is ok for this gambler to raise
                    if (hasFunds && (lastRaise != null)
                            && !lastRaise.getPlayerId().equals(gambler.getPlayer().getId())) {
                        if (log.isDebugEnabled()) {
                            log.debug("A gambler with id: " + lastRaise.getPlayerId()
                                      + " has raised in this round which means gambler with id: " + gambler.getId()
                                      + " can raise");
                        }

                        result = true;
                    } else if (hasFunds && (lastRaise == null)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Gambler with id: " + gambler.getId()
                                      + " can raise as there have been no raises in this round");
                        }

                        result = true;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Removes the gambler from the chain of gamblers and deletes the entity
     *
     * @param gambler Gambler entity that should be remove from game
     * @throws GameException exception
     */
    private void removeGamblerFromGame(Gambler gambler) throws GameException {
        unlinkGambler(gambler);

        // and NOW we can finally delete the gambler
        gambler.setStatus(GamblerStatus.REMOVED);
        gambler.merge();
    }

    /**
     * Method description
     *
     *
     * @param qg qg
     *
     * @throws GameException GameException
     */
    private void removeQueuedGambler(QueuedGambler qg) throws GameException {
        String     queuedGamblerId = qg.getId();
        BigDecimal gameBalance     = GamblerAccountEntry.findGamblerBalance(qg.getPokergameId(), qg.getPlayerId());

        // if there is a balance there we need to transfer it back to the player's account
        if (gameBalance.compareTo(new BigDecimal(0)) == 1) {
            transferPlayerFundsFromGameToAccount(qg.getPokergameId(), qg.getPlayerId(), qg.getAccountId(), gameBalance);
        }

        // and now we can remove the gambler
        QueuedGambler.removeQueuedGambler(queuedGamblerId);
    }

    /**
     * This method is called at the very end of a hand. It does two things.
     * For those gamblers who still have funds it moves the gambler back in
     * the queue (but in the front of everyone else so they will automatically get added for the next hand).
     * <p/>
     * For the [unfortunate] ones who lost all their hard earned cash and are bust are converted to observers.
     * <p/>
     * Any gamblers with statuses like AWAY, REMOVED, SIT_OUT are converted to observers.
     *
     * @param hand hand
     */
    private void requeueGamblers(Hand hand) {

        // now we process all the gamblers for this hand and the ones who don't have enough cash
        // get's put back into an observer state
        List<Gambler> gamblers = Gambler.findGamblersForHand(hand.getId());

        if (gamblers != null) {
            for (Gambler gambler : gamblers) {
                BigDecimal balance = GamblerAccountEntry.findGamblerBalance(gambler.getHand().getGame().getId(),
                                         gambler.getPlayer().getId());

                // if gambler has enough money AND he is still around to play the next game - we'll requeue him
                if ((balance.compareTo(new BigDecimal(0)) > 0)
                        && (gambler.getStatus().equals(GamblerStatus.ACTIVE)
                            || gambler.getStatus().equals(GamblerStatus.FOLDED))) {

                    // requeue
                    QueuedGambler qg;

                    if (gambler.getQueuedRefillAmount() != null) {
                        qg = new QueuedGambler(hand.getGame().getId(), gambler.getPlayer().getId(),
                                               gambler.getAccountId(), gambler.getSeatNumber(), true, 0,
                                               gambler.getPlayer().getNickname(), GamblerAccountEntryType.REFILL,
                                               gambler.getQueuedRefillAmount());
                    } else {
                        qg = new QueuedGambler(hand.getGame().getId(), gambler.getPlayer().getId(),
                                               gambler.getAccountId(), gambler.getSeatNumber(), true, 0,
                                               gambler.getPlayer().getNickname());
                    }

                    qg.persist();
                }
            }
        }
    }

    /**
     * Time to determine the winner
     * <p/>
     * This method does a whole bunch of stuff.
     * 1. It will return a list of gamblers who have completed it to the end
     * 2. Will return gamblers and their cards
     * 3. Will post the wins/losses to gamblers accounts
     * 4. Will save the stats
     * 5. Saves hand stats
     *
     * @param hand Hand we are doing the showdown for
     * @throws GameException exception
     */
    @Transactional
    private void showdown(Hand hand) throws GameException {
        List<Gambler> gamblers;

        if (showdownable(hand)) {
            if (log.isDebugEnabled()) {
                log.debug("Hand with id: " + hand.getId() + " is ready for a showdown");
            }

            // retrieve gamblers who are still playing
            gamblers = Gambler.findActiveGamblersForHand(hand.getId());

            if ((gamblers != null) && (gamblers.size() > 0)) {
                if (log.isDebugEnabled()) {
                    log.debug("There are " + gamblers.size() + " gamblers in the showdown");
                }

                // time to show the hands and divide the pot
                // first we determine the winner
                processGamblersAndComputeWinners(hand, gamblers);

                // give winner(s) his rightful pot
                processGamblersAfterHandIsFinished(hand, gamblers);
            } else {
                log.error(
                    "There are no gamblers left for this hand to be able to process a winner. There should always be at least one gambler left on the table.");

                throw new GameException("error.game.showdown.gamblers");
            }

            // put losers back on the bench
            requeueGamblers(hand);
        }
    }

    /**
     * Returns true if it is safe to run the showdown workflow
     *
     * @param hand hand
     * @return Return value
     */
    private boolean showdownable(Hand hand) {
        boolean    result;
        HandStatus currentHandStatus = hand.getStatus();

        result = (currentHandStatus.equals(HandStatus.COMPLETE) && !progressable(hand) && !bettable(hand));

        return result;
    }

    /**
     * Method description
     *
     * @param gambler gambler
     * @return Returns true if gambler can post small blind
     */
    private boolean smallBlindPostable(Gambler gambler) {
        boolean result = false;

        if (gambler.getHand().getStatus().equals(HandStatus.POST_SMALL_BLIND) && isSmallBlind(gambler)) {
            result = true;
        }

        return result;
    }

    /**
     * Method description
     *
     * @param game game
     * @return Return value
     */
    private boolean startable(PokerGame game) {
        boolean result             = false;
        Long    queuedGamblerCount = QueuedGambler.findQueuedGamblerCount(game.getId());

        // verify that there are at least 2 gamblers to play this game
        if (queuedGamblerCount >= 2) {
            result = true;
        }

        return result;
    }

    /**
     * Transfers funds from the account associated with the gambler. Throws an exception if the amount is not available in the account.
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @param accountId   accountId
     * @param amount      Amount of funds to transfer
     * @param type        entry type
     * @throws GameException Throws an exception if gamblerId is null or amount is missing or if there are inadequate funds
     */
    private void transferPlayerFundsFromAccountToGame(String pokergameId, String playerId, String accountId,
            BigDecimal amount, GamblerAccountEntryType type)
            throws GameException {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        if (accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }

        if (amount.compareTo(new BigDecimal(0)) <= 0) {
            log.error("Missing amount to transfer");

            throw new IllegalArgumentException("amount cannot be 0");
        }

        // first retrieve account balance
        BigDecimal accountBalance = AccountEntry.findAccountBalance(accountId);

        // make sure he can cover the amount on his account
        if (accountBalance.compareTo(amount) == -1) {
            throw new GameException("error.account.transfer", accountBalance.toString(), amount.toString());
        }

        if (log.isDebugEnabled()) {
            log.debug("Transferring amount: " + amount.toString() + " from account id: " + accountId
                      + " to game with id: " + pokergameId + " and player with id: " + playerId);
        }

        // load the account
        Account account = Account.findAccount(accountId);

        // amount needs to be removed from the user's account that has the same currency as the game
        AccountEntry entry = new AccountEntry(account, amount.negate(), null, AccountEntryType.TRANSFER, pokergameId,
                                 playerId);

        // create an equal deposit
        GamblerAccountEntry ge = new GamblerAccountEntry(pokergameId, playerId, accountId, type, amount, null);

        // save account withdrawal
        entry.persist();
        ge.persist();
    }

    /**
     * Transfers funds from the gambler to his specified account. Throws an exception if the amount is not available in the account.
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @param accountId   playerId
     * @param amount      Amount to transfer
     * @throws GameException Throws an exception if gambler or amount is null or if the amount is wrong
     */
    private void transferPlayerFundsFromGameToAccount(String pokergameId, String playerId, String accountId,
            BigDecimal amount)
            throws GameException {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        if (accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }

        if (amount.compareTo(new BigDecimal(0)) <= 0) {
            log.error("Missing amount to transfer");

            throw new IllegalArgumentException("amount cannot be 0");
        }

        BigDecimal gamblerBalance = GamblerAccountEntry.findGamblerBalance(pokergameId, playerId);

        if (gamblerBalance.compareTo(amount) == -1) {
            throw new GameException("error.account.transfer", gamblerBalance.toString(), amount.toString());
        }

        // the same amount needs to be added to the GamblerAccountEntry entity so we can track
        // what happens to the money during the course of the game
        if (log.isDebugEnabled()) {
            log.debug("Transferring amount: " + amount.toString() + " from game with id: " + pokergameId
                      + " with player with id: " + playerId + " to account id: " + accountId);
        }

        GamblerAccountEntry ge = new GamblerAccountEntry(pokergameId, playerId, accountId,
                                     GamblerAccountEntryType.WITHDRAWAL, amount.negate(), null);

        // amount needs to be removed from the user's account that has the same currency as the game
        Account      account = Account.findAccount(accountId);
        AccountEntry entry   = new AccountEntry(account, amount, null, AccountEntryType.TRANSFER, pokergameId,
                                   playerId);

        // and save both
        ge.persist();
        entry.persist();
    }

    /**
     * Unlinks a gambler from its chain and joins it's left and right links together
     *
     * @param gambler Gambler entity that should be unlinked
     * @throws GameException exception
     */
    private void unlinkGambler(Gambler gambler) throws GameException {
        if (gambler == null) {
            throw new IllegalArgumentException("gambler cannot be null");
        }

        Gambler previousGambler = gambler.getPreviousGambler();
        Gambler nextGambler     = gambler.getNextGambler();

        if (previousGambler == null) {
            throw new GameException("error.game.inconsistent.state", "Previous gambler is null");
        }

        if (nextGambler == null) {
            throw new GameException("error.game.inconsistent.state", "Next gambler is null");
        }

        previousGambler.setNextGambler(nextGambler);
        nextGambler.setPreviousGambler(previousGambler);
        nextGambler.merge();
        previousGambler.merge();
        gambler.setPreviousGambler(null);
        gambler.setNextGambler(null);
    }

    /**
     * Checks to see if gambler's last bet was all-in
     *
     * @param gambler gambler
     * @return Returns true if last gambler went all in
     */
    private boolean wasLastBetAllIn(Gambler gambler) {
        boolean allIn = false;

        if ((gambler != null) && (gambler.getHand() != null)) {
            Bet lastBet = Bet.findLastBetForGambler(gambler.getHand().getId(), gambler.getPlayer().getId());

            if ((lastBet != null) && lastBet.getType().equals(BetType.ALL_IN)) {
                allIn = true;
            }
        }

        return allIn;
    }
}
