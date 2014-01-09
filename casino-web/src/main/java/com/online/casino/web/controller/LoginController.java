/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.domain.enums.SystemRightType;
import com.online.casino.security.SpringSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Aug 26, 2009
 * Time: 5:32:36 PM
 * Responsibility:
 */
@Controller
public class LoginController {

    /** Field description */
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    //~--- methods ------------------------------------------------------------

    /**
     * Displays the login box on the index page.
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() throws Exception {
        return "login.form";
    }

    /**
     * Method determines whether to forward to the admin page or the player page.
     * If the user ended up here by mistake she won't have the right credentials and there is nothing to worry about
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login/redirect", method = RequestMethod.GET)
    public String redirect() throws Exception {
        ApplicationUser principal = SpringSecurityHelper.getSecurityContextPrincipal();
        String     view      = "redirect:/app/login?success=false";
        boolean    isAdmin   = false;
        boolean    isUser    = false;

        if (principal == null) {
            log.error("This is not possible. The user cannot log in and get to this place without having a principal. " +
                    "Redirecting to log in page.");
        } else if (principal.getAuthorities() != null) {

            for (GrantedAuthority authority : principal.getAuthorities()) {
                if (authority.getAuthority().equals(SystemRightType.RIGHT_LOGIN_ADMIN.name())) {
                    isAdmin = true;
                } else if (authority.getAuthority().equals(SystemRightType.RIGHT_LOGIN_PLAYER.name())) {
                    isUser = true;
                }
            }

            if (isAdmin) {
                view = "redirect:/app/administration/dashboard";
            } else if (isUser) {
                view = "redirect:/app/player/dashboard";
            }
        }

        return view;
    }
}
