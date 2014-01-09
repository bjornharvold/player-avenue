/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.utils.ReferenceDataFactory;
import com.online.casino.web.validator.UserValidator;
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
import java.util.List;
import java.util.Locale;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Sep 5, 2009
 * Time: 6:48:40 PM
 * Responsibility:
 */
@Controller
@SessionAttributes(types = ApplicationUser.class)
public class UserController {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final ReferenceDataFactory refFactory;

    /** Field description */
    private final UserValidator userValidator;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param refFactory refFactory
     * @param administrationService administrationService
     * @param userValidator userValidator
     */
    @Autowired
    public UserController(ReferenceDataFactory refFactory, AdministrationService administrationService,
                          UserValidator userValidator) {
        this.refFactory            = refFactory;
        this.administrationService = administrationService;
        this.userValidator         = userValidator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param userId userId
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("userId") String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        administrationService.deleteApplicationUser(userId);

        return "redirect:/app/administration/user/list";
    }

    /**
     * Method description
     *
     *
     * @param view view
     * @param user user
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/form", method = RequestMethod.POST)
    public String insert(@RequestParam(value = "view", defaultValue = "user.insert", required = false) String view,
                         @Valid ApplicationUser user, BindingResult result, Model map, Locale l)
            throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("A user is required");
        }

        // admins shouldn't have to add captchas so we disable it before validating
        user.setKaptchaEnabled(false);

        if (log.isTraceEnabled()) {
            log.trace("Validating User entity before persisting: " + user);
        }

        userValidator.validate(user, result);

        if (!result.hasErrors()) {
            user = administrationService.persistApplicationUser(user);
            view = "redirect:/app/administration/user/" + user.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("statuses", refFactory.createUserStatusList(l));
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
    @RequestMapping(value = "/administration/user/form", method = RequestMethod.GET)
    public String insertForm(@RequestParam(value = "view", defaultValue = "user.insert", required = false) String view,
                             Locale l, Model map)
            throws Exception {
        map.addAttribute("applicationUser", new ApplicationUser());
        map.addAttribute("statuses", refFactory.createUserStatusList(l));

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
    @RequestMapping(value = "/administration/user/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "view", defaultValue = "user.list", required = false) String view,
                       @RequestParam(value = "name",
                                     required = false) String name, @RequestParam(value = "page", defaultValue = "1",
                                     required = false) Integer page, @RequestParam(value = "maxResults",
            defaultValue = "10", required = false) Integer maxResults, Model map) throws Exception {
        List<ApplicationUser> list  = administrationService.findApplicationUsers(name, page - 1, maxResults);
        Long             count = administrationService.findApplicationUserCount(name);

        map.addAttribute("users", list);
        map.addAttribute("count", count);
        map.addAttribute("page", page);
        map.addAttribute("maxResults", maxResults);

        float nrOfPages = (float) count / maxResults;

        map.addAttribute("maxPages", (int) (((nrOfPages > (int) nrOfPages) || (nrOfPages == 0.0))
                ? nrOfPages + 1
                : nrOfPages));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}", method = RequestMethod.GET)
    public String show(@PathVariable("userId") String userId, @RequestParam(value = "view", defaultValue = "user.show",
            required = false) String view, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
        map.addAttribute("accounts", administrationService.findAccounts(userId));
        map.addAttribute("players", administrationService.findPlayers(userId, null, null, null));
        map.addAttribute("userId", userId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param view view
     * @param user user
     * @param result result
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/form", method = RequestMethod.PUT)
    public String update(@PathVariable(value = "userId") String userId, @RequestParam(value = "view",
            defaultValue = "user.update", required = false) String view, @Valid ApplicationUser user, BindingResult result,
            Locale l, Model map)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        if (user == null) {
            throw new IllegalArgumentException("A user is required");
        }

        // admins shouldn't have to add captchas so we disable it before validating
        user.setKaptchaEnabled(false);

        if (log.isTraceEnabled()) {
            log.trace("Validating User entity before persisting: " + user);
        }

        userValidator.validate(user, result);

        if (!result.hasErrors()) {
            view = "redirect:/app/administration/user/" + user.getId();
            administrationService.mergeApplicationUser(user);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("statuses", refFactory.createUserStatusList(l));
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
    @RequestMapping(value = "/administration/user/{userId}/form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("userId") String userId, @RequestParam(value = "view",
            defaultValue = "user.update", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        map.addAttribute("applicationUser", administrationService.findApplicationUser(userId));
        map.addAttribute("statuses", refFactory.createUserStatusList(l));

        return view;
    }
}
