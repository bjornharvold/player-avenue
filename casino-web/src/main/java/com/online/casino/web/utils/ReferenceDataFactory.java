/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.utils;

import com.online.casino.domain.enums.AccountStatus;
import com.online.casino.domain.enums.AccountTransferAction;
import com.online.casino.domain.enums.AccountTransferStatus;
import com.online.casino.domain.enums.AccountTransferType;
import com.online.casino.domain.enums.CasinoStatus;
import com.online.casino.domain.enums.Country;
import com.online.casino.domain.enums.Currency;
import com.online.casino.domain.enums.DeviceType;
import com.online.casino.domain.enums.GameStatus;
import com.online.casino.domain.enums.GameType;
import com.online.casino.domain.enums.LimitType;
import com.online.casino.domain.enums.PlayerStatus;
import com.online.casino.domain.enums.RoundType;
import com.online.casino.domain.enums.UserStatus;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * User: Bjorn Harvold
 * Date: Sep 10, 2009
 * Time: 1:36:53 PM
 * Responsibility:
 */
public class ReferenceDataFactory {
    private MessageSource messageSource;
    private List<ReferenceData> countryList = null;
    private List<ReferenceData> accountTransferStatusList = null;
    private List<ReferenceData> accountTransferTypeList = null;
    private List<ReferenceData> accountTransferActionList = null;
    private List<ReferenceData> accountStatusList = null;
    private List<ReferenceData> currencyList = null;
    private List<ReferenceData> userStatusList = null;
    private List<ReferenceData> playerStatusList = null;
    private List<ReferenceData> casinoStatusList = null;
    private List<ReferenceData> gameStatusList = null;
    private List<ReferenceData> deviceTypeList = null;
    private List<ReferenceData> limitTypeList = null;
    private List<ReferenceData> pokerTypeList = null;
    private List<ReferenceData> roundTypeList = null;

    private static final String ACCOUNT_STATUS_PREFIX = "dropdown.AccountStatusCd.";
    private static final String COUNTRY_PREFIX = "dropdown.CountryCd.";
    private static final String DEPOSIT_STATUS_PREFIX = "dropdown.AccountTransferStatusCd.";
    private static final String CURRENCY_PREFIX = "dropdown.CurrencyCd.";
    private static final String USER_STATUS_PREFIX = "dropdown.UserStatusCd.";
    private static final String DEPOSIT_TYPE_PREFIX = "dropdown.AccountTransferTypeCd.";
    private static final String ACCOUNT_TRANSFER_ACTION_PREFIX = "dropdown.AccountTransferActionCd.";
    private static final String PLAYER_STATUS_PREFIX = "dropdown.PlayerStatusCd.";
    private static final String CASINO_STATUS_PREFIX = "dropdown.CasinoStatusCd.";
    private static final String GAME_STATUS_PREFIX = "dropdown.GameStatusCd.";
    private static final String DEVICE_TYPE_PREFIX = "dropdown.DeviceTypeCd.";
    private static final String LIMIT_TYPE_PREFIX = "dropdown.LimitTypeCd.";
    private static final String POKER_TYPE_PREFIX = "dropdown.GameTypeCd.";
    private static final String ROUND_TYPE_PREFIX = "dropdown.RoundTypeCd.";

    // Spring IoC
    public void setMessageSource(MessageSource messageSource) {
    	this.messageSource = messageSource;
    }
    
    public List<ReferenceData> createCountryList(Locale l) {
        if (countryList == null) {
            countryList = new ArrayList<ReferenceData>();

            for (Country data : Country.values()) {
                countryList.add(new ReferenceData(data.name(), messageSource.getMessage(COUNTRY_PREFIX + data, null, "Could not find country for: " + data, l)));
            }
        }

        return countryList;
    }

    public List<ReferenceData> createCurrencyList(Locale l) {
        if (currencyList == null) {
            currencyList = new ArrayList<ReferenceData>();

            for (Currency data : Currency.values()) {
                currencyList.add(new ReferenceData(data.name(), messageSource.getMessage(CURRENCY_PREFIX + data, null, l)));
            }
        }

        return currencyList;
    }

    public List<ReferenceData> createAccountStatusList(Locale l) {
        if (accountStatusList == null) {
            accountStatusList = new ArrayList<ReferenceData>();

            for (AccountStatus data : AccountStatus.values()) {
                accountStatusList.add(new ReferenceData(data.name(), messageSource.getMessage(ACCOUNT_STATUS_PREFIX + data, null, "Could not find currency for: " + data, l)));
            }
        }

        return accountStatusList;
    }

    public List<ReferenceData> createUserStatusList(Locale l) {
        if (userStatusList == null) {
            userStatusList = new ArrayList<ReferenceData>();

            for (UserStatus data : UserStatus.values()) {
                userStatusList.add(new ReferenceData(data.name(), messageSource.getMessage(USER_STATUS_PREFIX + data, null, "Could not find user status for: " + data, l)));
            }
        }

        return userStatusList;
    }

    public List<ReferenceData> createAccountTransferTypeList(Locale l) {
        if (accountTransferTypeList == null) {
            accountTransferTypeList = new ArrayList<ReferenceData>();

            for (AccountTransferType data : AccountTransferType.values()) {
                accountTransferTypeList.add(new ReferenceData(data.name(), messageSource.getMessage(DEPOSIT_TYPE_PREFIX + data, null, "Could not find accounttransfer status for: " + data, l)));
            }
        }

        return accountTransferTypeList;
    }

    public List<ReferenceData> createAccountTransferStatusList(Locale l) {
        if (accountTransferStatusList == null) {
            accountTransferStatusList = new ArrayList<ReferenceData>();

            for (AccountTransferStatus data : AccountTransferStatus.values()) {
                accountTransferStatusList.add(new ReferenceData(data.name(), messageSource.getMessage(DEPOSIT_STATUS_PREFIX + data, null, "Could not find accounttransfer status for: " + data, l)));
            }
        }

        return accountTransferStatusList;
    }

    public List<ReferenceData> createAccountTransferActionList(Locale l) {
        if (accountTransferActionList == null) {
            accountTransferActionList = new ArrayList<ReferenceData>();

            for (AccountTransferAction data : AccountTransferAction.values()) {
                accountTransferActionList.add(new ReferenceData(data.name(), messageSource.getMessage(ACCOUNT_TRANSFER_ACTION_PREFIX + data, null, "Could not find accounttransfer status for: " + data, l)));
            }
        }

        return accountTransferActionList;
    }

    public List<ReferenceData> createPlayerStatusList(Locale l) {
        if (playerStatusList == null) {
            playerStatusList = new ArrayList<ReferenceData>();

            for (PlayerStatus data : PlayerStatus.values()) {
                playerStatusList.add(new ReferenceData(data.name(), messageSource.getMessage(PLAYER_STATUS_PREFIX + data, null, "Could not find player status for: " + data, l)));
            }
        }

        return playerStatusList;
    }

    public List<ReferenceData> createCasinoStatusList(Locale l) {
        if (casinoStatusList == null) {
            casinoStatusList = new ArrayList<ReferenceData>();

            for (CasinoStatus data : CasinoStatus.values()) {
                casinoStatusList.add(new ReferenceData(data.name(), messageSource.getMessage(CASINO_STATUS_PREFIX + data, null, "Could not find casino status for: " + data, l)));
            }
        }

        return casinoStatusList;
    }

    public List<ReferenceData> createGameStatusList(Locale l) {
        if (gameStatusList == null) {
            gameStatusList = new ArrayList<ReferenceData>();

            for (GameStatus data : GameStatus.values()) {
                gameStatusList.add(new ReferenceData(data.name(), messageSource.getMessage(GAME_STATUS_PREFIX + data, null, "Could not find game status for: " + data, l)));
            }
        }

        return gameStatusList;
    }

    public List<ReferenceData> createDeviceTypeList(Locale l) {
        if (deviceTypeList == null) {
            deviceTypeList = new ArrayList<ReferenceData>();

            for (DeviceType data : DeviceType.values()) {
                deviceTypeList.add(new ReferenceData(data.name(), messageSource.getMessage(DEVICE_TYPE_PREFIX + data, null, "Could not find device type for: " + data, l)));
            }
        }

        return deviceTypeList;
    }

    public List<ReferenceData> createLimitTypeList(Locale l) {
        if (limitTypeList == null) {
            limitTypeList = new ArrayList<ReferenceData>();

            for (LimitType data : LimitType.values()) {
                limitTypeList.add(new ReferenceData(data.name(), messageSource.getMessage(LIMIT_TYPE_PREFIX + data, null, "Could not find limit type for: " + data, l)));
            }
        }

        return limitTypeList;
    }

    public List<ReferenceData> createGameTypeList(Locale l) {
        if (pokerTypeList == null) {
            pokerTypeList = new ArrayList<ReferenceData>();

            for (GameType data : GameType.values()) {
                pokerTypeList.add(new ReferenceData(data.name(), messageSource.getMessage(POKER_TYPE_PREFIX + data, null, "Could not find poker type for: " + data, l)));
            }
        }

        return pokerTypeList;
    }

    public List<ReferenceData> createRoundTypeList(Locale l) {
        if (roundTypeList == null) {
            roundTypeList = new ArrayList<ReferenceData>();

            for (RoundType data : RoundType.values()) {
                roundTypeList.add(new ReferenceData(data.name(), messageSource.getMessage(ROUND_TYPE_PREFIX + data, null, "Could not find round type for: " + data, l)));
            }
        }

        return roundTypeList;
    }
}
