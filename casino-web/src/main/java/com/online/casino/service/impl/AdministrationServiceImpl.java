/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.service.impl;

//~--- non-JDK imports --------------------------------------------------------

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
import com.online.casino.domain.entity.FsmProcessObject;
import com.online.casino.domain.entity.GameTemplate;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.entity.Stake;
import com.online.casino.domain.enums.AccountEntryType;
import com.online.casino.domain.enums.AccountStatus;
import com.online.casino.domain.enums.CmsType;
import com.online.casino.domain.enums.ContentType;
import com.online.casino.domain.enums.Currency;
import com.online.casino.domain.enums.DeviceType;
import com.online.casino.domain.enums.GameType;
import com.online.casino.domain.enums.LimitType;
import com.online.casino.domain.enums.SystemRightType;
import com.online.casino.domain.enums.UserStatus;
import com.online.casino.exception.CmsException;
import com.online.casino.security.PasswordEncoder;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.CmsService;
import com.online.casino.service.MailService;
import com.online.casino.service.email.MailServiceException;
import com.online.casino.utils.ImageResizer;
import com.wazeegroup.physhun.engine.ProcessContainer;
import com.wazeegroup.physhun.engine.ProcessStateModelPersistence;
import com.wazeegroup.physhun.framework.StateModel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Random;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 3/17/11
 * Time: 3:08 PM
 * Responsibility: This service is really just to put a security layer between the domain objects
 * and the ui layer. The entity objects are capable of doing the persisting etc themselves.
 */
@Service("administrationService")
public class AdministrationServiceImpl implements AdministrationService {
    /**
     * Field description
     */
    private final CmsService cmsService;

    /**
     * Field description
     */
    @Value("${avatar.image.width}")
    private Integer avatarMaxWidth;

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(AdministrationServiceImpl.class);

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    private final MailService mailService;

    /**
     * Field description
     */
    private final PasswordEncoder passwordEncoder;

    /** Field description */
    private final ProcessContainer processContainer;

    /**
     * Field description
     */
    private final ProcessStateModelPersistence processStateModelPersistence;

    /**
     * Field description
     */
    private final StateModel stateModel;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param processStateModelPersistence processStateModelPersistence
     * @param stateModel                   stateModel
     * @param mailService                  mailService
     * @param passwordEncoder              passwordEncoder
     * @param cmsService cmsService
     * @param processContainer processContainer
     */
    @Autowired
    public AdministrationServiceImpl(ProcessStateModelPersistence processStateModelPersistence, StateModel stateModel,
                                     MailService mailService, PasswordEncoder passwordEncoder, CmsService cmsService, ProcessContainer processContainer) {
        this.processStateModelPersistence = processStateModelPersistence;
        this.stateModel = stateModel;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.cmsService = cmsService;
        this.processContainer = processContainer;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param userId userId
     * @return Return value
     * @throws UserException UserException
     */
    @Override
    public ApplicationUser activateUser(String userId) throws UserException {
        return ApplicationUser.activateUser(userId);
    }

    /**
     * Method description
     *
     * @param term       term
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_PLAYER', 'RIGHT_READ_PLAYER_AS_ADMIN')")
    @Override
    public List<AutoComplete> autoCompletePlayersByNickname(String term, Integer index, Integer maxResults) {
        return Player.autoCompletePlayersByNickname(term, index, maxResults);
    }

    /*
     * Convenience method for saving a password. Expecting currentPassword,
     * newPassword and confirmNewPassword to be filled in. As the values have
     * already been validated, we will go ahead and replace the newPassword with
     * password on ApplicationUser entity
     */

    /**
     * Method description
     *
     * @param user user
     */
    @PreAuthorize("hasRole('RIGHT_UPDATE_PASSWORD')")
    @Override
    public void changePassword(ApplicationUser user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        user.setPassword(passwordEncoder.encodePassword(user.getNewPassword(), null));
        user.merge();
    }

    /**
     * Method description
     *
     * @param id id
     */
    @PreAuthorize("hasRole('RIGHT_DELETE_ACCOUNT')")
    @Override
    public void deleteAccount(String id) {
        Account.remove(id);
    }

    /**
     * Method description
     *
     * @param id id
     */
    @PreAuthorize("hasRole('RIGHT_DELETE_ACCOUNT_TRANSFER')")
    @Override
    public void deleteAccountTransfer(String id) {
        AccountTransfer.remove(id);
    }

    /**
     * Method description
     *
     * @param id id
     */
    @PreAuthorize("hasRole('RIGHT_DELETE_CASINO_AS_ADMIN')")
    @Override
    public void deleteCasino(String id) {
        Casino.remove(id);
    }

    /**
     * Method description
     *
     * @param id id
     */
    @PreAuthorize("hasRole('RIGHT_DELETE_GAME_TEMPLATE_AS_ADMIN')")
    @Override
    public void deleteGameTemplate(String id) {
        GameTemplate.remove(id);
    }

    /**
     * Method description
     *
     * @param player player
     */
    @PreAuthorize("hasPermission(#player, 'delete')")
    @Override
    public void deletePlayer(Player player) {
        player.remove();
    }

    /**
     * Method description
     *
     * @param id id
     */
    @PreAuthorize("hasRole('RIGHT_DELETE_STAKE_AS_ADMIN')")
    @Override
    public void deleteStake(String id) {
        Stake.remove(id);
    }

    /**
     * Method description
     *
     * @param id id
     */
    @PreAuthorize("hasRole('RIGHT_DELETE_SYSTEM_USER')")
    @Override
    public void deleteApplicationUser(String id) {
        ApplicationUser.remove(id);
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_ACCOUNT', 'RIGHT_READ_ACCOUNT_AS_ADMIN')")
    @Override
    public Account findAccount(String id) {
        return Account.findAccount(id);
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_ACCOUNT_TRANSFER', 'RIGHT_READ_ACCOUNT_TRANSFER_AS_ADMIN')")
    @Override
    public AccountTransfer findAccountTransfer(String id) {
        return AccountTransfer.findAccountTransfer(id);
    }

    /**
     * Method description
     *
     * @param accountId accountId
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_ACCOUNT_TRANSFER', 'RIGHT_READ_ACCOUNT_TRANSFER_AS_ADMIN')")
    @Override
    public Long findAccountTransferCount(String accountId) {
        return AccountTransfer.findAccountTransferCount(accountId);
    }

    /**
     * Method description
     *
     * @param accountId  accountId
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_ACCOUNT_TRANSFER', 'RIGHT_READ_ACCOUNT_TRANSFER_AS_ADMIN')")
    @Override
    public List<AccountTransfer> findAccountTransfers(String accountId, Integer index, Integer maxResults) {
        return AccountTransfer.findAccountTransfers(accountId, index, maxResults);
    }

    /**
     * Method description
     *
     * @param accountId accountId
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_ACCOUNT', 'RIGHT_READ_ACCOUNT_AS_ADMIN')")
    @Override
    public Account findAccountWithBalance(String accountId) {
        return Account.findAccountWithBalance(accountId);
    }

    /**
     * Method description
     *
     * @param userId userId
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_ACCOUNT', 'RIGHT_READ_ACCOUNT_AS_ADMIN')")
    @PostFilter("hasPermission(filterObject, 'list')")
    @Override
    public List<Account> findAccounts(String userId) {
        return Account.findAccounts(userId, null);
    }

    /**
     * Method description
     *
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_CASINO', 'RIGHT_READ_CASINO_AS_ADMIN')")
    @Override
    public List<Casino> findAllCasinos() {
        return Casino.findAllCasinos();
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_CASINO', 'RIGHT_READ_CASINO_AS_ADMIN')")
    @Override
    public Casino findCasino(String id) {
        return Casino.findCasino(id);
    }

    /**
     * Method description
     *
     * @param currencies currencies
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_CASINO', 'RIGHT_READ_CASINO_AS_ADMIN')")
    @Override
    public List<Casino> findCasinoByCurrencies(List<Currency> currencies) {
        return Casino.findCasinoByCurrencies(currencies);
    }

    /**
     * Method description
     *
     * @param name name
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_CASINO', 'RIGHT_READ_CASINO_AS_ADMIN')")
    @Override
    public Casino findCasinoByName(String name) {
        return Casino.findCasinoByName(name);
    }

    /**
     * Method description
     *
     * @param name name
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_CASINO', 'RIGHT_READ_CASINO_AS_ADMIN')")
    @Override
    public Long findCasinoCount(String name) {
        return Casino.findCasinoCount(name);
    }

    /**
     * Method description
     *
     * @param name       name
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_CASINO', 'RIGHT_READ_CASINO_AS_ADMIN')")
    @Override
    public List<Casino> findCasinos(String name, Integer index, Integer maxResults) {
        return Casino.findCasinos(name, index, maxResults);
    }

    /**
     * Method description
     *
     * @param templateId templateId
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_POKER_GAME_AS_ADMIN_AS_TASK')")
    @Override
    public Long findEmptyPokerGameCountForGameTemplate(String templateId) {
        return PokerGame.findEmptyPokerGameCountForGameTemplate(templateId);
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_GAME_TEMPLATE_AS_ADMIN')")
    @Override
    public GameTemplate findGameTemplate(String id) {
        return GameTemplate.findGameTemplate(id);
    }

    /**
     * Method description
     *
     * @param casinoId   casinoId
     * @param deviceType deviceType
     * @param limitType  limitType
     * @param maxPlayers maxPlayers
     * @param stakeId    stakeId
     * @param type       type
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_GAME_TEMPLATE_AS_ADMIN')")
    @Override
    public GameTemplate findGameTemplateByValues(String casinoId, DeviceType deviceType, LimitType limitType,
                                                 Integer maxPlayers, String stakeId, GameType type) {
        return GameTemplate.findGameTemplateByValues(casinoId, deviceType, limitType, maxPlayers, stakeId, type);
    }

    /**
     * Method description
     *
     * @param casinoId casinoId
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_GAME_TEMPLATE_AS_ADMIN')")
    @Override
    public Long findGameTemplateCount(String casinoId) {
        return GameTemplate.findGameTemplateCount(casinoId);
    }

    /**
     * Method description
     *
     * @param casinoId   casinoId
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_GAME_TEMPLATE_AS_ADMIN')")
    @Override
    public List<GameTemplate> findGameTemplates(String casinoId, Integer index, Integer maxResults) {
        return GameTemplate.findGameTemplates(casinoId, index, maxResults);
    }

    /**
     * Method description
     *
     * @param templateId    templateId
     * @param gamesToDelete gamesToDelete
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_POKER_GAME_AS_ADMIN')")
    @Override
    public List<PokerGame> findLatestEmptyPokerGamesByGameTemplate(String templateId, Integer gamesToDelete) {
        return PokerGame.findLatestEmptyPokerGamesByGameTemplate(templateId, gamesToDelete);
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_PLAYER', 'RIGHT_READ_PLAYER_AS_ADMIN')")
    @Override
    public Player findPlayer(String id) {
        return Player.findPlayer(id);
    }

    /**
     * Method description
     *
     * @param nickname nickname
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_PLAYER', 'RIGHT_READ_PLAYER_AS_ADMIN')")
    @Override
    public Player findPlayerByNickname(String nickname) {
        return Player.findPlayerByNickname(nickname);
    }

    /**
     * Method description
     *
     * @param userId userId
     * @param name   name
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_PLAYER', 'RIGHT_READ_PLAYER_AS_ADMIN')")
    @Override
    public Long findPlayerCount(String userId, String name) {
        return Player.findPlayerCount(userId, name);
    }

    /**
     * Method description
     *
     * @param userId     userId
     * @param name       name
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_PLAYER', 'RIGHT_READ_PLAYER_AS_ADMIN')")
    @PostFilter("hasPermission(filterObject, 'list')")
    @Override
    public List<Player> findPlayers(String userId, String name, Integer index, Integer maxResults) {
        return Player.findPlayers(userId, name, index, maxResults);
    }

    /**
     * Method description
     *
     * @param nickname   nickname
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_PLAYER', 'RIGHT_READ_PLAYER_AS_ADMIN')")
    @PostFilter("hasPermission(filterObject, 'list')")
    @Override
    public List<Player> findPlayersByNickname(String nickname, Integer index, Integer maxResults) {
        return Player.findPlayersByNickname(nickname, index, maxResults);
    }

    /**
     * Method description
     *
     * @param nickname nickname
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_PLAYER', 'RIGHT_READ_PLAYER_AS_ADMIN')")
    @Override
    public Long findPlayersByNicknameCount(String nickname) {
        return Player.findPlayersByNicknameCount(nickname);
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_POKER_GAME_AS_ADMIN')")
    @Override
    public PokerGame findPokerGame(String id) {
        return PokerGame.findPokerGame(id);
    }

    /**
     * Method description
     *
     * @param casinoId casinoId
     * @param name     name
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_POKER_GAME_AS_ADMIN')")
    @Override
    public Long findPokerGameCount(String casinoId, String name) {
        return PokerGame.findPokerGameCount(casinoId, name);
    }

    /**
     * Method description
     *
     * @param templateId templateId
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_POKER_GAME_AS_ADMIN')")
    @Override
    public Long findPokerGameCountForGameTemplate(String templateId) {
        return PokerGame.findPokerGameCountForGameTemplate(templateId);
    }

    /**
     * Method description
     *
     * @param casinoId   casinoId
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_POKER_GAME_AS_ADMIN')")
    @Override
    public List<PokerGame> findPokerGames(String casinoId, Integer index, Integer maxResults) {
        return PokerGame.findPokerGames(casinoId, index, maxResults);
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_STAKE_AS_ADMIN')")
    @Override
    public Stake findStake(String id) {
        return Stake.findStake(id);
    }

    /**
     * Method description
     *
     * @param casinoId casinoId
     * @param high     high
     * @param low      low
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_STAKE_AS_ADMIN')")
    @Override
    public Stake findStakeByHighLow(String casinoId, BigDecimal high, BigDecimal low) {
        return Stake.findStakeByHighLow(casinoId, high, low);
    }

    /**
     * Method description
     *
     * @param casinoId casinoId
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_STAKE_AS_ADMIN')")
    @Override
    public Long findStakeCount(String casinoId) {
        return Stake.findStakeCount(casinoId);
    }

    /**
     * Method description
     *
     * @param casinoId casinoId
     * @param hilo     hilo
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_STAKE_AS_ADMIN')")
    @Override
    public Long findStakeCount(String casinoId, BigDecimal hilo) {
        return Stake.findStakeCount(casinoId, hilo);
    }

    /**
     * Method description
     *
     * @param casinoId   casinoId
     * @param hilo       hilo
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_STAKE_AS_ADMIN')")
    @Override
    public List<Stake> findStakes(String casinoId, BigDecimal hilo, Integer index, Integer maxResults) {
        return Stake.findStakes(casinoId, hilo, index, maxResults);
    }

    /**
     * Method description
     *
     * @param casinoId   casinoId
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_STAKE', 'RIGHT_READ_STAKE_AS_ADMIN')")
    @Override
    public List<Stake> findStakes(String casinoId, Integer index, Integer maxResults) {
        return Stake.findStakes(casinoId, index, maxResults);
    }

    /**
     * Method description
     *
     * @param statusCode statusCode
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_SYSTEM_RIGHT_AS_ADMIN')")
    @Override
    public ApplicationRight findSystemRightByStatusCode(SystemRightType statusCode) {
        return ApplicationRight.findSystemRightByStatusCode(statusCode);
    }

    /**
     * Method description
     *
     * @param name statusCode
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_SYSTEM_ROLE_AS_ADMIN')")
    @Override
    public ApplicationRole findSystemRoleByName(String name) {
        return ApplicationRole.findSystemRoleByName(name);
    }

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_SYSTEM_USER', 'RIGHT_READ_SYSTEM_USER_AS_ADMIN')")
    @Override
    public ApplicationUser findApplicationUser(String id) {
        return ApplicationUser.findApplicationUser(id);
    }

    /**
     * Method description
     *
     * @param name name
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_SYSTEM_USER_AS_ADMIN')")
    @Override
    public Long findApplicationUserCount(String name) {
        return ApplicationUser.findApplicationUserCount(name);
    }

    /**
     * Method description
     *
     * @param name       name
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_SYSTEM_USER_AS_ADMIN')")
    @Override
    public List<ApplicationUser> findApplicationUsers(String name, Integer index, Integer maxResults) {
        return ApplicationUser.findApplicationUsers(name, index, maxResults);
    }

    /**
     * Method description
     *
     * @param email email
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_SYSTEM_USER', 'RIGHT_READ_SYSTEM_USER_AS_ADMIN')")
    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        return ApplicationUser.findApplicationUserByEmail(email);
    }

    /**
     * Method description
     *
     * @param username username
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_SYSTEM_USER', 'RIGHT_READ_SYSTEM_USER_AS_ADMIN')")
    @Override
    public ApplicationUser findApplicationUserByUsername(String username) {
        return ApplicationUser.findApplicationUserByUsername(username);
    }

    /**
     * Method description
     *
     * @param username username
     * @param password password
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_SYSTEM_USER', 'RIGHT_READ_SYSTEM_USER_AS_ADMIN')")
    @Override
    public ApplicationUser findApplicationUserByUsernameAndPassword(String username, String password) {
        ApplicationUser result = ApplicationUser.findApplicationUserByUsername(username);
        boolean passwordOk = false;

        if (result != null) {
            passwordOk = passwordEncoder.isPasswordValid(result.getPassword(), password, null);
        }

        return passwordOk
                ? result
                : null;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param account account
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_ACCOUNT', 'RIGHT_READ_ACCOUNT_AS_ADMIN')")
    @Override
    public Boolean isDuplicateAccount(Account account) {
        return Account.isDuplicateAccount(account);
    }

    /**
     * Method description
     *
     * @param encryptedPassword encryptedPassword
     * @param rawPassword       rawPassword
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_READ_SYSTEM_USER', 'RIGHT_READ_SYSTEM_USER_AS_ADMIN')")
    public Boolean isPasswordMatch(String encryptedPassword, String rawPassword) {
        return passwordEncoder.isPasswordValid(encryptedPassword, rawPassword, null);
    }

    /**
     * Method description
     *
     * @param userId userId
     * @param email  email
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_SYSTEM_USER')")
    @Override
    public ApplicationUser isApplicationUserUniqueByEmail(String userId, String email) {
        return ApplicationUser.isApplicationUserUniqueByEmail(userId, email);
    }

    /**
     * Method description
     *
     * @param userId   userId
     * @param username username
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_SYSTEM_USER')")
    @Override
    public ApplicationUser isApplicationUserUniqueByUsername(String userId, String username) {
        return ApplicationUser.isApplicationUserUniqueByUsername(userId, username);
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_UPDATE_ACCOUNT_AS_ADMIN') or hasPermission(#entity, 'merge')")
    @Override
    public void mergeAccount(Account entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @Transactional
    @PreAuthorize("hasRole('RIGHT_UPDATE_ACCOUNT_TRANSFER_AS_ADMIN')")
    @Override
    public void mergeAccountTransfer(AccountTransfer entity) {
        doAccountTransferStatusChange(entity);
        entity.merge();
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_UPDATE_CASINO_AS_ADMIN')")
    @Override
    public void mergeCasino(Casino entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_UPDATE_GAME_TEMPLATE_AS_ADMIN')")
    @Override
    public void mergeGameTemplate(GameTemplate entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_UPDATE_PLAYER_AS_ADMIN') or hasPermission(#entity, 'merge')")
    @Override
    public void mergePlayer(Player entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_UPDATE_POKER_GAME_AS_ADMIN')")
    @Override
    public void mergePokerGame(PokerGame entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_UPDATE_STAKE_AS_ADMIN')")
    @Override
    public void mergeStake(Stake entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_UPDATE_SYSTEM_USER_AS_ADMIN') or hasPermission(#entity, 'merge')")
    @Override
    public void mergeApplicationUser(ApplicationUser entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_ACCOUNT_AS_ADMIN') or hasPermission(#entity, 'persist')")
    @Override
    public Account persistAccount(Account entity) {

        if (entity.getStatus() == null) {
            entity.setStatus(AccountStatus.ACTIVE);
        }

        entity.persist();

        return entity;
    }

    /**
     * Method description
     *
     * @param entry entry
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_ACCOUNT_ENTRY')")
    @Override
    public AccountEntry persistAccountEntry(AccountEntry entry) {
        entry.persist();

        return entry;
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @Transactional
    @PreAuthorize("hasRole('RIGHT_UPDATE_ACCOUNT_TRANSFER_AS_ADMIN')")
    @Override
    public AccountTransfer persistAccountTransfer(AccountTransfer entity) {
        doAccountTransferStatusChange(entity);
        entity.persist();

        return entity;
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_CASINO_AS_ADMIN')")
    @Override
    public Casino persistCasino(Casino entity) {
        entity.persist();

        return entity;
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_GAME_TEMPLATE_AS_ADMIN')")
    @Override
    public GameTemplate persistGameTemplate(GameTemplate entity) {
        entity.persist();

        return entity;
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_PLAYER_AS_ADMIN') or hasPermission(#entity, 'persist')")
    @Override
    public Player persistPlayer(Player entity) {
        entity.persist();

        return entity;
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_POKER_GAME_AS_ADMIN')")
    @Override
    public PokerGame persistPokerGame(PokerGame entity) {
        entity.persist();

        if (StringUtils.isBlank(entity.getId())) {
            throw new IllegalStateException("Newly created poker game entity does not have an ID");
        }
        
        // at this time we should create an FSM process for this game
        initiateFSMProcessForPokerGame(entity.getId());

        return entity;
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_STAKE_AS_ADMIN')")
    @Override
    public Stake persistStake(Stake entity) {
        entity.persist();

        return entity;
    }

    /**
     * Method description
     *
     * @param right right
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_SYSTEM_RIGHT_AS_ADMIN')")
    @Override
    public ApplicationRight persistSystemRight(ApplicationRight right) {
        right.persist();

        return right;
    }

    /**
     * Method description
     *
     * @param role role
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_SYSTEM_ROLE_AS_ADMIN')")
    @Override
    public ApplicationRole persistSystemRole(ApplicationRole role) {
        role.persist();

        return role;
    }

    /**
     * Method description
     *
     * @param entity entity
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_SYSTEM_USER_AS_ADMIN') or hasPermission(#entity, 'persist')")
    @Override
    public ApplicationUser persistApplicationUser(ApplicationUser entity) {
        entity.setPassword(passwordEncoder.encodePassword(entity.getNewPassword(), null));
        entity.persist();

        return entity;
    }

    /**
     * Method description
     *
     * @param ur ur
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_SYSTEM_USER_ROLE_AS_ADMIN')")
    @Override
    public ApplicationUserRole persistApplicationUserRole(ApplicationUserRole ur) {
        ur.persist();

        return ur;
    }

    /**
     * Method description
     *
     * @param user applicationUser
     * @return Return value
     * @throws UserException UserException
     */
    public ApplicationUser registerUser(ApplicationUser user) throws UserException {
        ApplicationUser result = null;

        if (user == null) {
            throw new IllegalArgumentException("applicationUser cannot be null");
        }

        try {
            user.setStatus(UserStatus.REGISTERED);
            user.setPassword(passwordEncoder.encodePassword(user.getNewPassword(), null));

            // send activation email
            if (log.isDebugEnabled()) {
                log.debug("Sending activation email...");
            }

            user.persist();

            mailService.sendActivationEmail(user.getEmail(), user.getId(), new Locale(user.getLocale()));

            if (log.isDebugEnabled()) {
                log.debug("Sent activation email");
            }
        } catch (MailServiceException e) {
            log.error(e.getMessage(), e);

            throw new UserException(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Re-initialize FSM processes from DB
     */
    @Override
    public void reinitializeFiniteStateProcesses() {
        List<FsmProcessObject> processes = FsmProcessObject.findFsmProcessObjects();

        for (FsmProcessObject fsm : processes) {
            processStateModelPersistence.persistProcess(fsm.getId(), stateModel);
        }
    }

    /**
     * Method description
     *
     * @param user user
     * @throws UserException UserException
     */
    public void sendPasswordReminder(ApplicationUser user) throws UserException {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        try {

            // reset password
            String newPassword = Long.toString(Math.abs(new Random().nextLong()), 36);

            user.setPassword(passwordEncoder.encodePassword(newPassword, null));

            // update user
            mergeApplicationUser(user);

            if (log.isDebugEnabled()) {
                log.debug("Sending password reset email...");
            }

            mailService.sendPasswordReminderEmail(user.getEmail(), newPassword, new Locale(user.getLocale()));

            if (log.isDebugEnabled()) {
                log.debug("Sent password reset email...");
            }
        } catch (MailServiceException e) {
            log.error(e.getMessage(), e);

            throw new UserException(e.getMessage(), e);
        }
    }

    /**
     * Method description
     *
     * @param transfer transfer
     */
    private static void doAccountTransferStatusChange(AccountTransfer transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("transfer cannot be null");
        }

        switch (transfer.getStatus()) {
            case COMPLETE:
                Account account = Account.findAccount(transfer.getAccountId());

                switch (transfer.getAction()) {
                    case DEPOSIT:
                        new AccountEntry(account, transfer.getAmount(), "Deposit from type: " + transfer.getType().name(), AccountEntryType.DEPOSIT).persist();

                        break;

                    case WITHDRAWAL:
                        new AccountEntry(account, transfer.getAmount().negate(), "Withdrawal from type: " + transfer.getType().name(), AccountEntryType.WITHDRAWAL).persist();

                        break;

                    default:
                        log.error("Shouldn't be here.");
                }

                break;

            default:
                log.error("Shouldn't be here.");
        }
    }

    @PreAuthorize("hasRole('RIGHT_UPDATE_PLAYER')")
    @Override
    public void uploadAvatarImage(String playerId, String fileName, InputStream originalFile, long originalFileSize) throws CmsException {
        int beg = fileName.lastIndexOf(".");
        String extension = fileName.substring(beg + 1);

        try {
            new ImageResizer();
            final InputStream is = ImageResizer.resize(originalFile, avatarMaxWidth, extension);
            StringBuilder fileNameSB = new StringBuilder(playerId);

            fileNameSB.append(".");
            fileNameSB.append(extension);

            ContentType type = ContentType.valueOf(extension.toUpperCase());

            String url = cmsService.upload(CmsType.AVATAR, type, fileNameSB.toString(), IOUtils.toByteArray(is));

            Player player = Player.findPlayer(playerId);

            // update persona with avatar image url
            player.setAvatarUrl(url);
            player.merge();
        } catch (IOException e) {
            throw new CmsException(e.getMessage(), e);
        }
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @return FSMProcessObject
     */
    private FsmProcessObject initiateFSMProcessForPokerGame(String pokergameId) {
        // NOTE: the pokergameId is the same as the FPO identifier
        FsmProcessObject fpo = new FsmProcessObject(pokergameId);
        fpo.persist();

        processContainer.startProcess(fpo, stateModel);

        return fpo;
    }
}
