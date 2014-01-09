/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.player;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Account;
import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.domain.enums.Currency;
import com.online.casino.security.SpringSecurityHelper;
import com.online.casino.service.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Oct 11, 2009
 * Time: 11:52:47 AM
 * Responsibility:
 */
@Controller
public class DashboardController {

    /** Field description */
    private final AdministrationService administrationService;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param administrationService administrationService
     */
    @Autowired
    public DashboardController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/dashboard", method = RequestMethod.GET)
    public String dashboard(Model map) throws Exception {
        ApplicationUser loggedInUser = SpringSecurityHelper.getSecurityContextPrincipal();

        // TODO verify to see if it is necessary to load up system user from db just to show accounts
        ApplicationUser u          = administrationService.findApplicationUser(loggedInUser.getId());
        List<Currency> currencies = null;

        if (u.getAccounts() != null) {
            currencies = new ArrayList<Currency>(u.getAccounts().size());

            for (Account acct : u.getAccounts()) {
                currencies.add(acct.getCurrency());
            }

            map.addAttribute("casinos", administrationService.findCasinoByCurrencies(currencies));
        }

        String result = "player.dashboard";

        return result;
    }
}
