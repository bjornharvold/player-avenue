/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.AccountTransfer;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.utils.ReferenceDataFactory;
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
@SessionAttributes(types = AccountTransfer.class)
public class AccountTransferController {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(AccountTransferController.class);

    //~--- fields -------------------------------------------------------------

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
     */
    @Autowired
    public AccountTransferController(ReferenceDataFactory refFactory, AdministrationService administrationService) {
        this.refFactory            = refFactory;
        this.administrationService = administrationService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param view view
     * @param userId userId
     * @param accountId accountId
     * @param accountTransferId accountTransferId
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/transfer/{accountTransferId}",
                    method = RequestMethod.DELETE)
    public String delete(@RequestParam(value = "view", required = false) String view,
                         @PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                         @PathVariable("accountTransferId") String accountTransferId)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        if (StringUtils.isBlank(accountTransferId)) {
            throw new IllegalArgumentException("AccountTransfer ID is required");
        }

        administrationService.deleteAccountTransfer(accountTransferId);

        if (StringUtils.isBlank(view)) {
            view = "redirect:/app/administration/user/" + userId + "/account/" + accountId;
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
     * @param accountTransfer accountTransfer
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/transfer/form",
                    method = RequestMethod.POST)
    public String insert(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                         @RequestParam(value = "view",
                                       defaultValue = "account.transfer.insert",
                                       required = false) String view, @Valid AccountTransfer accountTransfer,
                                           BindingResult result, Model map, Locale l)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        if (accountTransfer == null) {
            throw new IllegalArgumentException("An accountTransfer object is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating AccountTransfer entity before persisting: " + accountTransfer);
        }

        if (!result.hasErrors()) {
            accountTransfer.setAccountId(accountId);
            accountTransfer = administrationService.persistAccountTransfer(accountTransfer);
            view            = "redirect:/app/administration/user/" + userId + "/account/" + accountId + "/transfer/"
                              + accountTransfer.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("countries", refFactory.createCountryList(l));
            map.addAttribute("statuses", refFactory.createAccountTransferStatusList(l));
            map.addAttribute("types", refFactory.createAccountTransferTypeList(l));
            map.addAttribute("actions", refFactory.createAccountTransferActionList(l));
            map.addAttribute("userId", userId);
            map.addAttribute("accountId", accountId);
            map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
            map.addAttribute("account", administrationService.findAccount(accountId));
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
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/transfer/form",
                    method = RequestMethod.GET)
    public String insertForm(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                             @RequestParam(value = "view",
            defaultValue = "account.transfer.insert", required = false) String view, Locale l, Model map)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        // copy values from account to make it easier
        AccountTransfer accounttransfer = new AccountTransfer();

        accounttransfer.setAccountId(accountId);
        map.addAttribute("accountTransfer", accounttransfer);
        map.addAttribute("countries", refFactory.createCountryList(l));
        map.addAttribute("statuses", refFactory.createAccountTransferStatusList(l));
        map.addAttribute("types", refFactory.createAccountTransferTypeList(l));
        map.addAttribute("actions", refFactory.createAccountTransferActionList(l));
        map.addAttribute("userId", userId);
        map.addAttribute("accountId", accountId);
        map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
        map.addAttribute("account", administrationService.findAccount(accountId));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param accountId accountId
     * @param view view
     * @param name name
     * @param page page
     * @param maxResults maxResults
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/transfer/list",
                    method = RequestMethod.GET)
    public String list(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                       @RequestParam(value = "view",
                                     defaultValue = "account.transfer.list",
                                     required = false) String view, @RequestParam(value = "name",
            required = false) String name, @RequestParam(value = "page", defaultValue = "1",
            required = false) Integer page, @RequestParam(value = "maxResults",
            defaultValue = "10", required = false) Integer maxResults, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        map.addAttribute("account", administrationService.findAccount(accountId));
        map.addAttribute("accountTransfers",
                         administrationService.findAccountTransfers(accountId, page - 1, maxResults));
        map.addAttribute("count", administrationService.findAccountTransferCount(accountId));
        map.addAttribute("page", page);
        map.addAttribute("maxResults", maxResults);
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
     * @param accountTransferId accountTransferId
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/transfer/{accountTransferId}",
                    method = RequestMethod.GET)
    public String show(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                       @PathVariable("accountTransferId") String accountTransferId, @RequestParam(value = "view",
            defaultValue = "account.transfer.show", required = false) String view, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        if (StringUtils.isBlank(accountTransferId)) {
            throw new IllegalArgumentException("AccountTransfer ID is required");
        }

        map.addAttribute("accountTransfer", administrationService.findAccountTransfer(accountTransferId));
        map.addAttribute("userId", userId);
        map.addAttribute("accountId", accountId);
        map.addAttribute("accountTransferId", accountTransferId);
        map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
        map.addAttribute("account", administrationService.findAccount(accountId));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param accountId accountId
     * @param accountTransferId accountTransferId
     * @param view view
     * @param accountTransfer accountTransfer
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/transfer/{accountTransferId}/form",
                    method = RequestMethod.PUT)
    public String update(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                         @PathVariable("accountTransferId") String accountTransferId, @RequestParam(value = "view",
            defaultValue = "account.transfer.update",
            required = false) String view, @Valid AccountTransfer accountTransfer, BindingResult result, Model map,
                Locale l)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        if (StringUtils.isBlank(accountTransferId)) {
            throw new IllegalArgumentException("AccountTransfer ID is required");
        }

        if (accountTransfer == null) {
            throw new IllegalArgumentException("A accounttransfer is required");
        }

        if (!result.hasErrors()) {
            administrationService.mergeAccountTransfer(accountTransfer);
            view = "redirect:/app/administration/user/" + userId + "/account/" + accountId + "/transfer/"
                   + accountTransferId;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("countries", refFactory.createCountryList(l));
            map.addAttribute("statuses", refFactory.createAccountTransferStatusList(l));
            map.addAttribute("types", refFactory.createAccountTransferTypeList(l));
            map.addAttribute("actions", refFactory.createAccountTransferActionList(l));
            map.addAttribute("userId", userId);
            map.addAttribute("accountId", accountId);
            map.addAttribute("accountTransferId", accountTransferId);
            map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
            map.addAttribute("account", administrationService.findAccount(accountId));
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param accountId accountId
     * @param accountTransferId accountTransferId
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/account/{accountId}/transfer/{accountTransferId}/form",
                    method = RequestMethod.GET)
    public String updateForm(@PathVariable("userId") String userId, @PathVariable("accountId") String accountId,
                             @PathVariable("accountTransferId") String accountTransferId, @RequestParam(value = "view",
            defaultValue = "account.transfer.update", required = false) String view, Locale l, Model map)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Account ID is required");
        }

        if (StringUtils.isBlank(accountTransferId)) {
            throw new IllegalArgumentException("AccountTransfer ID is required");
        }

        map.addAttribute("accountTransfer", administrationService.findAccountTransfer(accountTransferId));
        map.addAttribute("countries", refFactory.createCountryList(l));
        map.addAttribute("statuses", refFactory.createAccountTransferStatusList(l));
        map.addAttribute("types", refFactory.createAccountTransferTypeList(l));
        map.addAttribute("actions", refFactory.createAccountTransferActionList(l));
        map.addAttribute("userId", userId);
        map.addAttribute("accountId", accountId);
        map.addAttribute("accountTransferId", accountTransferId);
        map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
        map.addAttribute("account", administrationService.findAccount(accountId));

        return view;
    }
}
