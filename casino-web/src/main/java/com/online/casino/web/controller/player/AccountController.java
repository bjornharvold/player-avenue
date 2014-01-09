/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.player;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Account;
import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.domain.enums.AccountStatus;
import com.online.casino.security.SpringSecurityHelper;
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
 * Responsibility: Manages views for account
 */
@Controller
@SessionAttributes(types = Account.class)
public class AccountController {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(AccountController.class);

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
    public AccountController(ReferenceDataFactory refFactory, AdministrationService administrationService) {
        this.refFactory            = refFactory;
        this.administrationService = administrationService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param accountId accountId
     * @param view view
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/account/{accountId}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("accountId") String accountId,
                         @RequestParam(value = "view", required = false) String view)
            throws Exception {
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        Account account = administrationService.findAccount(accountId);

        if (account != null) {
            account.setStatus(AccountStatus.CLOSED);
            account.merge();
        } else {
            log.error("Account does not exist");
        }

        if (StringUtils.isBlank(view)) {
            view = "redirect:/app/app/player/account/list";
        }

        return view;
    }

    /**
     * Method description
     *
     *
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
    @RequestMapping(value = "/player/account/form", method = RequestMethod.POST)
    public String insert(@RequestParam(value = "view", defaultValue = "player.account.insert",
                                       required = false) String view, @Valid Account account, BindingResult result,
                                           Model map, Locale l)
            throws Exception {
        if (account == null) {
            throw new IllegalArgumentException("An account is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Account entity before persisting: " + account);
        }

        account.setStatus(AccountStatus.ACTIVE);

        ApplicationUser user = SpringSecurityHelper.getSecurityContextPrincipal();

        account.setApplicationUser(user);
        new AccountValidator(administrationService).validate(account, result);

        if (!result.hasErrors()) {
            account = administrationService.persistAccount(account);
            view = "redirect:/app/app/player/account/" + account.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("currencies", refFactory.createCurrencyList(l));
            map.addAttribute("countries", refFactory.createCountryList(l));
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/account/form", method = RequestMethod.GET)
    public String insertForm(@RequestParam(value = "view", defaultValue = "player.account.insert",
            required = false) String view, Locale l, Model map)
            throws Exception {
        map.addAttribute("account", new Account());
        map.addAttribute("currencies", refFactory.createCurrencyList(l));
        map.addAttribute("countries", refFactory.createCountryList(l));

        return view;
    }

    /**
     * Method description
     *
     *
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
    @RequestMapping(value = "/player/account/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "view", defaultValue = "player.account.list",
                                     required = false) String view, @RequestParam(value = "name",
            required = false) String name, @RequestParam(value = "page", defaultValue = "1",
            required = false) Integer page, @RequestParam(value = "maxResults",
            defaultValue = "10", required = false) Integer maxResults, Model map) throws Exception {
        ApplicationUser user   = SpringSecurityHelper.getSecurityContextPrincipal();
        String     userId = user.getId();

        map.addAttribute("accounts", administrationService.findAccounts(userId));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param accountId accountId
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/account/{accountId}", method = RequestMethod.GET)
    public String show(@PathVariable("accountId") String accountId, @RequestParam(value = "view",
            defaultValue = "player.account.show", required = false) String view, Model map) throws Exception {
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        map.addAttribute("accountId", accountId);
        map.addAttribute("account", administrationService.findAccountWithBalance(accountId));

        return view;
    }

    /**
     * Method description
     *
     *
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
    @RequestMapping(value = "/player/account/{accountId}/form", method = RequestMethod.PUT)
    public String update(@PathVariable("accountId") String accountId, @RequestParam(value = "view",
            defaultValue = "player.account.update", required = false) String view, @Valid Account account,
            BindingResult result, Model map, Locale l)
            throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("Validating Account entity before persisting: " + account);
        }

        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        if (account == null) {
            throw new IllegalArgumentException("An account is required");
        }

        new AccountValidator(administrationService).validate(account, result);

        if (!result.hasErrors()) {
            administrationService.mergeAccount(account);
            view = "redirect:/app/app/player/account/" + accountId;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("accountId", accountId);
            map.addAttribute("currencies", refFactory.createCurrencyList(l));
            map.addAttribute("countries", refFactory.createCountryList(l));
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param accountId accountId
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/account/{accountId}/form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("accountId") String accountId, @RequestParam(value = "view",
            defaultValue = "player.account.update", required = false) String view, Locale l, Model map)
            throws Exception {
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        map.addAttribute("accountId", accountId);
        map.addAttribute("account", administrationService.findAccount(accountId));
        map.addAttribute("currencies", refFactory.createCurrencyList(l));
        map.addAttribute("countries", refFactory.createCountryList(l));

        return view;
    }
}
