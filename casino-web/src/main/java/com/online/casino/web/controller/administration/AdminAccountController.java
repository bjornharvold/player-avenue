/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Account;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.utils.ReferenceDataFactory;
import com.online.casino.web.validator.AccountValidator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;
import java.util.Locale;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Sep 7, 2009
 * Time: 9:38:16 PM
 * Responsibility: Manages views for account / withdrawal / accounttransfer functionality
 */
@Controller
@SessionAttributes(types = Account.class)
public class AdminAccountController {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(AdminAccountController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AccountValidator accountValidator;

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final ReferenceDataFactory refFactory;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param refFactory refFactory
     * @param administrationService administrationService
     * @param accountValidator accountValidator
     */
    @Autowired
    public AdminAccountController(ReferenceDataFactory refFactory, AdministrationService administrationService,
                                  AccountValidator accountValidator) {
        this.refFactory            = refFactory;
        this.administrationService = administrationService;
        this.accountValidator      = accountValidator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param accountId accountId
     * @param view view
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                         @RequestParam(value = "view",
                                       required = false) String view) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        administrationService.deleteAccount(accountId);

        if (StringUtils.isBlank(view)) {
            view = "redirect:/app/administration/user/" + userId;
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param view view
     * @param account account
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/form", method = RequestMethod.POST)
    public String insert(@PathVariable("userId") String userId, @RequestParam(value = "view",
            defaultValue = "account.insert", required = false) String view, @Valid Account account,
            BindingResult result, Model map, Locale l)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (account == null) {
            throw new IllegalArgumentException("An account is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Account entity before persisting: " + account);
        }

        account.setApplicationUser(administrationService.findApplicationUser(userId));
        accountValidator.validate(account, result);

        if (!result.hasErrors()) {
            account = administrationService.persistAccount(account);
            view    = "redirect:/app/administration/user/" + userId + "/account/" + account.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("currencies", refFactory.createCurrencyList(l));
            map.addAttribute("statuses", refFactory.createAccountStatusList(l));
            map.addAttribute("countries", refFactory.createCountryList(l));
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/form", method = RequestMethod.GET)
    public String insertForm(@PathVariable("userId") String userId, @RequestParam(value = "view",
            defaultValue = "account.insert", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
        map.addAttribute("account", new Account());
        map.addAttribute("currencies", refFactory.createCurrencyList(l));
        map.addAttribute("statuses", refFactory.createAccountStatusList(l));
        map.addAttribute("countries", refFactory.createCountryList(l));
        map.addAttribute("userId", userId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param accountId accountId
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}", method = RequestMethod.GET)
    public String show(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                       @RequestParam(value = "view",
                                     defaultValue = "account.show",
                                     required = false) String view, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
        map.addAttribute("account", administrationService.findAccountWithBalance(accountId));
        map.addAttribute("accountTransfers", administrationService.findAccountTransfers(accountId, null, null));
        map.addAttribute("userId", userId);
        map.addAttribute("accountId", accountId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param accountId accountId
     * @param view view
     * @param account account
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/form", method = RequestMethod.PUT)
    public String update(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                         @RequestParam(value = "view",
                                       defaultValue = "account.update",
                                       required = false) String view, @Valid Account account, BindingResult result,
                                           Model map, Locale l)
            throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("Validating Account entity before persisting: " + account);
        }

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        if (account == null) {
            throw new IllegalArgumentException("An account is required");
        }

        accountValidator.validate(account, result);

        if (!result.hasErrors()) {
            administrationService.mergeAccount(account);
            view = "redirect:/app/administration/user/" + userId + "/account/" + account.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("currencies", refFactory.createCurrencyList(l));
            map.addAttribute("statuses", refFactory.createAccountStatusList(l));
            map.addAttribute("countries", refFactory.createCountryList(l));
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param accountId accountId
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                             @RequestParam(value = "view",
            defaultValue = "account.update", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
        map.addAttribute("account", administrationService.findAccount(accountId));
        map.addAttribute("currencies", refFactory.createCurrencyList(l));
        map.addAttribute("statuses", refFactory.createAccountStatusList(l));
        map.addAttribute("countries", refFactory.createCountryList(l));
        map.addAttribute("userId", userId);
        map.addAttribute("accountId", accountId);

        return view;
    }
}
