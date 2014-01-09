/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.player;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.security.SpringSecurityHelper;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Oct 11, 2009
 * Time: 11:52:47 AM
 * Responsibility:
 */
@Controller
@SessionAttributes(types = ApplicationUser.class)
public class PasswordController {

    /** Field description */
    private static final Logger log = LoggerFactory.getLogger(PasswordController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final PasswordValidator passwordValidator;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param administrationService administrationService
     * @param passwordValidator passwordValidator
     */
    @Autowired
    public PasswordController(AdministrationService administrationService, PasswordValidator passwordValidator) {
        this.administrationService = administrationService;
        this.passwordValidator     = passwordValidator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param view view
     * @param user user
     * @param result result
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/password/form", method = RequestMethod.PUT)
    public String update(@RequestParam(value = "view", defaultValue = "player.password.update",
                                       required = false) String view, @Valid ApplicationUser user, BindingResult result)
            throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("A user is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating User entity before persisting: " + user);
        }

        passwordValidator.validate(user, result);

        if (!result.hasErrors()) {
            view = "redirect:/app/player/settings";
            administrationService.changePassword(user);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/password/form", method = RequestMethod.GET)
    public String updateForm(@RequestParam(value = "view", defaultValue = "player.password.update",
            required = false) String view, Model map)
            throws Exception {
        ApplicationUser user = SpringSecurityHelper.getSecurityContextPrincipal();

        map.addAttribute("applicationUser", administrationService.findApplicationUser(user.getId()));

        return view;
    }
}
