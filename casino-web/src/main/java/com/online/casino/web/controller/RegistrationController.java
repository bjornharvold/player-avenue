/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller;

//~--- non-JDK imports --------------------------------------------------------

import com.google.code.kaptcha.Constants;
import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.validator.ForgotPasswordValidator;
import com.online.casino.web.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Locale;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 11/03/22
 * @author         Bjorn Harvold    
 */
@Controller
@SessionAttributes(types = ApplicationUser.class)
public class RegistrationController {

    /** Field description */
    private final static String REGISTRATION_FORM = "user.register.form";

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final ForgotPasswordValidator forgotPasswordValidator;

    /** Field description */
    private final UserValidator userValidator;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param administrationService administrationService
     * @param userValidator userValidator
     * @param forgotPasswordValidator forgotPasswordValidator
     */
    @Autowired
    public RegistrationController(AdministrationService administrationService, UserValidator userValidator,
                                  ForgotPasswordValidator forgotPasswordValidator) {
        this.userValidator           = userValidator;
        this.administrationService   = administrationService;
        this.forgotPasswordValidator = forgotPasswordValidator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param id id
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/registration/activate/{userId}", method = RequestMethod.GET)
    public String activate(@PathVariable("userId") String id, Model map) throws Exception {
        String view = "user.activation";

        if (id == null) {
            throw new IllegalArgumentException("A user ID is required");
        }

        // try to activate user here
        ApplicationUser u = administrationService.activateUser(id);

        if (u != null) {
            map.addAttribute("user", u);
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param session session
     * @param user user
     * @param result result
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/registration/signup", method = RequestMethod.POST)
    public String create(HttpSession session, @Valid ApplicationUser user, BindingResult result, Locale l) throws Exception {

        // pull it off the session form Kaptcha
        String requiredKaptcha = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);

        user.setRequiredKaptcha(requiredKaptcha);
        userValidator.validate(user, result);

        String view = null;

        if (result.hasErrors()) {
            view = REGISTRATION_FORM;
        } else {
            user = administrationService.registerUser(user);
            view = "redirect:/app/";
        }

        return view;
    }

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
    @RequestMapping(value = "/registration/forgotpassword", method = RequestMethod.GET)
    public String forgotPassword(Model map) throws Exception {
        map.addAttribute("user", new ApplicationUser());

        String view = "user.forgotpassword";

        return view;
    }

    /**
     * Method description
     *
     *
     * @param session session
     * @param user user
     * @param errors errors
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/registration/forgotpassword", method = RequestMethod.POST)
    public String forgotPassword(HttpSession session, @Valid ApplicationUser user, BindingResult errors, Locale l)
            throws Exception {
        String view = null;

        // pull it off the session form Kaptcha
        String requiredKaptcha = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);

        user.setRequiredKaptcha(requiredKaptcha);

        if (log.isTraceEnabled()) {
            log.trace("Validating User entity before sending password reminder: " + user);
        }

        forgotPasswordValidator.validate(user, errors);

        if (errors.hasErrors()) {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + errors);
            }

            view = "user.forgotpassword";
        } else {
            administrationService.sendPasswordReminder(user);
            view = "user.forgotpassword.confirmation";
        }

        return view;
    }

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
    @RequestMapping(value = "/registration/signup", method = RequestMethod.GET)
    public String register(Model map) throws Exception {
        map.addAttribute("user", new ApplicationUser());

        return REGISTRATION_FORM;
    }
}
