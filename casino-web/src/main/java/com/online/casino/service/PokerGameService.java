/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Bet;
import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.GameObserver;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.entity.QueuedGambler;
import com.online.casino.domain.enums.GameAction;
import com.online.casino.domain.enums.GameType;
import com.online.casino.exception.GameException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//~--- JDK imports ------------------------------------------------------------

//~--- interfaces -------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Sep 27, 2009
 * Time: 6:07:57 PM
 * Responsibility:
 */
public interface PokerGameService {

    /**
     * Method description
     *
     *
     * @param hand1 hand1
     * @param hand2 hand2
     *
     * @return Return value
     *
     */
    Integer compareHands(String hand1, String hand2);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @throws GameException GameException
     */
    void doCall(String gamblerId) throws GameException;

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @throws GameException GameException
     */
    void doCheck(String gamblerId) throws GameException;

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @throws GameException GameException
     */
    void doDealNewHand(String pokergameId) throws GameException;

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @throws GameException GameException
     */
    void doEndGame(String handId) throws GameException;

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @throws GameException GameException
     */
    void doFold(String gamblerId) throws GameException;

    /**
     * Method description
     *
     * @param pokergameId Game identifier
     * @param playerId Player identifier
     *
     * @throws GameException GameException
     */
    void doLeaveGame(String pokergameId, String playerId) throws GameException;

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @throws GameException GameException
     */
    void doLeaveGameAndWatch(String gamblerId) throws GameException;

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @throws GameException GameException
     */
    void doPostBigBlind(String handId) throws GameException;

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @throws GameException GameException
     */
    void doPostSmallBlind(String handId) throws GameException;

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @throws GameException GameException
     */
    void doProgressHand(String handId) throws GameException;

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     * @param desiredSeatNumber desiredSeatNumber
     * @param mustHaveSeat mustHaveSeat
     * @param buyin buyin
     *
     * @throws GameException GameException
     */
    void doQueuePlayer(String pokergameId, String playerId, Integer desiredSeatNumber, boolean mustHaveSeat,
                       BigDecimal buyin)
            throws GameException;

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     * @param amount amount
     *
     * @throws GameException GameException
     */
    void doRaise(String gamblerId, BigDecimal amount) throws GameException;

    /**
     * Method description
     *
     *
     * @param handId handId
     */
    void doTimeout(String handId);

    /**
     * Method description
     *
     *
     * @param gameObserverId gameObserverId
     */
    void doUnwatchGame(String gameObserverId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     */
    void doWatchGame(String pokergameId, String playerId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     *
     * @return Return value
     *
     * @throws GameException GameException
     */
    BigDecimal findAccountBalance(String pokergameId, String playerId) throws GameException;

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     *
     * @throws GameException GameException
     */
    Integer findAvailableSeatCount(String pokergameId) throws GameException;

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     *
     * @return Return value
     */
    BigDecimal findBalance(String pokergameId, String playerId);

    /**
     * Method description
     *
     *
     * @param hand hand
     *
     * @return Return value
     */
    String findBestHand(String hand);

    /**
     * Method description
     *
     *
     * @param handId handId
     * @param playerId playerId
     *
     * @return Return value
     */
    BigDecimal findBetAmount(String handId, String playerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     * @param playerId playerId
     * @param roundNumber roundNumber
     *
     * @return Return value
     */
    BigDecimal findBetAmountByRoundNumber(String handId, String playerId, Integer roundNumber);

    /**
     * Method description
     *
     *
     * @param handId handId
     * @param playerId playerId
     *
     * @return Return value
     */
    List<Bet> findBets(String handId, String playerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    List<Bet> findBetsByHand(String handId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     */
    BigDecimal findCallAmount(String gamblerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    Gambler findCurrentGambler(String handId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     */
    Hand findCurrentHand(String pokergameId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     */
    Gambler findGambler(String gamblerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    List<Gambler> findGamblers(String handId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     *
     * @return Return value
     */
    GameObserver findGameObserver(String pokergameId, String playerId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     */
    Long findGameObserverCount(String pokergameId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param index index
     * @param maxResults maxResults
     *
     * @return Return value
     */
    List<GameObserver> findGameObservers(String pokergameId, Integer index, Integer maxResults);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    Hand findHand(String handId);

    /**
     * Method description
     *
     *
     * @param hand hand
     *
     * @return Return value
     */
    String findHandName(String hand);

    /**
     * Method description
     *
     *
     * @param hand hand
     *
     * @return Return value
     */
    Integer findHandRank(String hand);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     */
    List<Hand> findHandsByGame(String pokergameId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    Gambler findLastManStanding(String handId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param maxPlayers maxPlayers
     *
     * @return Return value
     */
    List<QueuedGambler> findLatestQueuedGamblers(String pokergameId, Integer maxPlayers);

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
    BigDecimal findLoserAmount(String pokergameId, String handId, String playerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    List<Gambler> findLosers(String handId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     */
    BigDecimal findMaximumRaiseAmount(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     */
    BigDecimal findMinimumRaiseAmount(String gamblerId);

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param type type
     * @param stakeId stakeId
     *
     * @return Return value
     */
    Long findPokerGameCount(String casinoId, GameType type, String stakeId);

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param type type
     * @param stakeId stakeId
     * @param index index
     * @param maxResults maxResults
     *
     * @return Return value
     */
    List<PokerGame> findPokerGames(String casinoId, GameType type, String stakeId, Integer index, Integer maxResults);

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param type type
     * @param stakeId stakeId
     * @param index index
     * @param maxResults maxResults
     *
     * @return Return value
     */
    Map<PokerGame, List<Gambler>> findPokerGamesAndGamblers(String casinoId, GameType type, String stakeId,
            Integer index, Integer maxResults);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    BigDecimal findPotSize(String handId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     */
    Long findQueuedGamblerCount(String pokergameId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     *
     * @return Return value
     */
    QueuedGambler findQueuedPlayer(String pokergameId, String playerId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     *
     * @throws GameException GameException
     */
    List<Integer> findSeatNumbers(String pokergameId) throws GameException;

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     *
     * @return Return value
     */
    BigDecimal findWinnerAmount(String pokergameId, String playerId);

    BigDecimal findWinnerAmount(String pokergameId, String handId, String playerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    List<Gambler> findWinners(String handId);

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     *
     */
    boolean isBettable(String handId);

    /**
     * Method description
     *
     *
     * @param gambler gambler
     *
     * @return Return value
     */
    boolean isBigBlind(Gambler gambler);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     *
     */
    boolean isBigBlindPostable(String handId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     */
    boolean isCallable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     */
    boolean isCheckable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     */
    boolean isCurrentGambler(String gamblerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    boolean isEndGame(String handId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     */
    boolean isFoldable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     */
    boolean isGamblerActive(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     */
    boolean isLeaveable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     *
     */
    boolean isProgressable(String handId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     *
     * @return Return value
     */
    boolean isQueueable(String pokergameId, String playerId, BigDecimal buyin);

    /**
     * Method description
     *
     *
     * @param gamblerId gamblerId
     *
     * @return Return value
     *
     */
    boolean isRaiseable(String gamblerId);

    /**
     * Method description
     *
     *
     * @param gambler gambler
     *
     * @return Return value
     */
    boolean isSmallBlind(Gambler gambler);

    /**
     * Method description
     *
     *
     * @param handId handId
     *
     * @return Return value
     */
    boolean isSmallBlindPostable(String handId);

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Return value
     */
    boolean isStartable(String pokergameId);

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param queuedGamblerId queuedGamblerId
     *
     * @throws GameException GameException
     */
    void doRemoveQueuedGambler(String queuedGamblerId) throws GameException;

    List<GameAction> findAvailableMoves(String gamblerId);

    /**
     * Returns the gambler who made the last bet for the specified hand
     * @param pokergameId pokergameId
     * @param handId handId
     * @return Gambler
     */
    Gambler findLastActingGambler(String pokergameId, String handId);

    /**
     * Returns the application user's id for the current gambler
     * @param handId handId
     * @return ApplicationUser.id
     */
    String findUserByCurrentGambler(String handId);

    /**
     * Requeues a gambler who has previously timed out
     * @param gamblerId gamblerId
     */
    QueuedGambler doReQueuePlayer(String gamblerId);
}
