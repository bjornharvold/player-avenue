/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service;

import com.online.casino.UserException;
import com.online.casino.domain.dto.AutoComplete;
import com.online.casino.domain.entity.Account;
import com.online.casino.domain.entity.AccountEntry;
import com.online.casino.domain.entity.AccountTransfer;
import com.online.casino.domain.entity.ApplicationRight;
import com.online.casino.domain.entity.ApplicationRole;
import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.domain.entity.ApplicationUserRole;
import com.online.casino.domain.entity.Casino;
import com.online.casino.domain.entity.GameTemplate;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.entity.Stake;
import com.online.casino.domain.enums.Currency;
import com.online.casino.domain.enums.DeviceType;
import com.online.casino.domain.enums.GameType;
import com.online.casino.domain.enums.LimitType;
import com.online.casino.domain.enums.SystemRightType;
import com.online.casino.exception.CmsException;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 3/17/11
 * Time: 3:03 PM
 * Responsibility:
 */
public interface AdministrationService {
    List<Casino> findCasinos(String name, Integer index, Integer maxResults);
    Long findCasinoCount(String name);
    Casino persistCasino(Casino entity);
    void mergeCasino(Casino entity);
    Casino findCasino(String id);
    void deleteCasino(String id);

    List<Account> findAccounts(String userId);
    Account persistAccount(Account entity);
    void mergeAccount(Account entity);
    Account findAccount(String id);
    void deleteAccount(String id);
    Account findAccountWithBalance(String accountId);
    
    List<AccountTransfer> findAccountTransfers(String accountId, Integer index, Integer maxResults);
    Long findAccountTransferCount(String name);
    AccountTransfer persistAccountTransfer(AccountTransfer entity);
    void mergeAccountTransfer(AccountTransfer entity);
    AccountTransfer findAccountTransfer(String id);
    void deleteAccountTransfer(String id);
    
    List<GameTemplate> findGameTemplates(String casinoId, Integer index, Integer maxResults);
    Long findGameTemplateCount(String name);
    GameTemplate persistGameTemplate(GameTemplate entity);
    void mergeGameTemplate(GameTemplate entity);
    GameTemplate findGameTemplate(String id);
    void deleteGameTemplate(String id);
    
    List<Player> findPlayers(String userId, String name, Integer index, Integer maxResults);
    Long findPlayerCount(String userId, String name);
    Player persistPlayer(Player entity);
    void mergePlayer(Player entity);
    Player findPlayer(String id);
    void deletePlayer(Player player);

    Long findPokerGameCount(String casinoId, String name);
    PokerGame persistPokerGame(PokerGame entity);
    void mergePokerGame(PokerGame entity);
    PokerGame findPokerGame(String id);
    
    List<Stake> findStakes(String casinoId, Integer index, Integer maxResults);
    List<Stake> findStakes(String casinoId, BigDecimal hilo, Integer index, Integer maxResults);
    Long findStakeCount(String casinoId);
    Long findStakeCount(String casinoId, BigDecimal hilo);
    Stake persistStake(Stake entity);
    void mergeStake(Stake entity);
    Stake findStake(String id);
    void deleteStake(String id);
    
    List<ApplicationUser> findApplicationUsers(String name, Integer index, Integer maxResults);
    Long findApplicationUserCount(String name);
    ApplicationUser persistApplicationUser(ApplicationUser entity);
    void mergeApplicationUser(ApplicationUser entity);
    ApplicationUser findApplicationUser(String id);
    void deleteApplicationUser(String id);

    List<Casino> findAllCasinos();
    Long findPlayersByNicknameCount(String nickname);
    List<Player> findPlayersByNickname(String nickname, Integer index, Integer maxResults);
    List<AutoComplete> autoCompletePlayersByNickname(String term, Integer index, Integer maxResults);

    void changePassword(ApplicationUser user);

    Casino findCasinoByName(String name);

    void reinitializeFiniteStateProcesses();

    Stake findStakeByHighLow(String casinoId, BigDecimal high, BigDecimal low);

    ApplicationRight findSystemRightByStatusCode(SystemRightType statusCode);

    ApplicationRole findSystemRoleByName(String name);

    ApplicationRight persistSystemRight(ApplicationRight right);

    ApplicationRole persistSystemRole(ApplicationRole role);

    ApplicationUser findApplicationUserByUsername(String username);

    ApplicationUserRole persistApplicationUserRole(ApplicationUserRole ur);

    AccountEntry persistAccountEntry(AccountEntry entry);

    Boolean isDuplicateAccount(Account account);

    GameTemplate findGameTemplateByValues(String casinoId, DeviceType deviceType, LimitType limitType, Integer maxPlayers, String stakeId, GameType type);

    Player findPlayerByNickname(String nickname);

    ApplicationUser isApplicationUserUniqueByUsername(String userId, String username);

    ApplicationUser findApplicationUserByEmail(String email);

    ApplicationUser isApplicationUserUniqueByEmail(String userId, String email);

    ApplicationUser activateUser(String userId) throws UserException;

    List<PokerGame> findLatestEmptyPokerGamesByGameTemplate(String templateId, Integer gamesToDelete);

    Long findPokerGameCountForGameTemplate(String templateId);

    Long findEmptyPokerGameCountForGameTemplate(String templateId);

    List<PokerGame> findPokerGames(String casinoId, Integer index, Integer maxResults);

    List<Casino> findCasinoByCurrencies(List<Currency> currencies);

    ApplicationUser findApplicationUserByUsernameAndPassword(String username, String password);

    void sendPasswordReminder(ApplicationUser user) throws UserException;

    ApplicationUser registerUser(ApplicationUser user) throws UserException;

    Boolean isPasswordMatch(String password, String currentPassword);

    void uploadAvatarImage(String playerId, String fileName, InputStream originalFile, long fileSize) throws CmsException;
}
