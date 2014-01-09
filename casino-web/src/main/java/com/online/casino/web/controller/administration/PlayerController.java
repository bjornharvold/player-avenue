/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Player;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.utils.ReferenceDataFactory;
import com.online.casino.web.validator.PlayerValidator;
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
 * Class description
 *
 *
 * @version        ${project.version}, 11/03/22
 * @author         Bjorn Harvold
 */
@Controller
@SessionAttributes(types = Player.class)
public class PlayerController {

    /** Field description */
    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

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
    public PlayerController(ReferenceDataFactory refFactory, AdministrationService administrationService) {
        this.refFactory            = refFactory;
        this.administrationService = administrationService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param playerId playerId
     * @param view view
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/player/{playerId}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("userId") String userId, @PathVariable("playerId") String playerId,
                         @RequestParam(value = "view",
                                       required = false) String view) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A player ID is required");
        }

        administrationService.deletePlayer(administrationService.findPlayer(playerId));
        view = "redirect:/app/administration/user/" + userId;

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param view view
     * @param player player
     * @param result result
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/player/form", method = RequestMethod.POST)
    public String insert(@PathVariable("userId") String userId, @RequestParam(value = "view",
            defaultValue = "player.insert", required = false) String view, @Valid Player player, BindingResult result,
            Locale l, Model map)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        if (player == null) {
            throw new IllegalArgumentException("A player is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Player entity before persisting: " + player);
        }

        player.setApplicationUser(administrationService.findApplicationUser(userId));
        new PlayerValidator(administrationService).validate(player, result);

        if (!result.hasErrors()) {
            player = administrationService.persistPlayer(player);
            view   = "redirect:/app/administration/user/" + userId + "/player/" + player.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("statuses", refFactory.createPlayerStatusList(l));
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
    @RequestMapping(value = "/administration/user/{userId}/player/form", method = RequestMethod.GET)
    public String insertForm(@PathVariable("userId") String userId, @RequestParam(value = "view",
            defaultValue = "player.insert", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        map.addAttribute("userId", userId);
        map.addAttribute("player", new Player());
        map.addAttribute("statuses", refFactory.createPlayerStatusList(l));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
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
    @RequestMapping(value = "/administration/user/{userId}/player/list", method = RequestMethod.GET)
    public String list(@PathVariable("userId") String userId, @RequestParam(value = "view", defaultValue = "user.list",
            required = false) String view, @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page",
                          defaultValue = "1", required = false) Integer page, @RequestParam(value = "maxResults",
                          defaultValue = "10",
            required = false) Integer maxResults, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        map.addAttribute("userId", userId);
        map.addAttribute("players", administrationService.findPlayers(userId, name, page - 1, maxResults));
        map.addAttribute("count", administrationService.findPlayerCount(userId, name));
        map.addAttribute("page", page);
        map.addAttribute("maxResults", maxResults);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param playerId playerId
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/player/{playerId}", method = RequestMethod.GET)
    public String show(@PathVariable("userId") String userId, @PathVariable("playerId") String playerId,
                       @RequestParam(value = "view",
                                     defaultValue = "player.show",
                                     required = false) String view, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A player ID is required");
        }

        map.addAttribute("userId", userId);
        map.addAttribute("playerId", playerId);
        map.addAttribute("player", administrationService.findPlayer(playerId));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param playerId playerId
     * @param view view
     * @param player player
     * @param result result
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/player/{playerId}/form", method = RequestMethod.PUT)
    public String update(@PathVariable("userId") String userId, @PathVariable("playerId") String playerId,
                         @RequestParam(value = "view",
                                       defaultValue = "player.update",
                                       required = false) String view, @Valid Player player, BindingResult result,
                                           Locale l, Model map)
            throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A player ID is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Player entity before persisting: " + player);
        }

        new PlayerValidator(administrationService).validate(player, result);

        if (!result.hasErrors()) {
            administrationService.mergePlayer(player);
            view = "redirect:/app/administration/user/" + userId + "/player/" + playerId;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("userId", userId);
            map.addAttribute("playerId", playerId);
            map.addAttribute("statuses", refFactory.createPlayerStatusList(l));
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param userId userId
     * @param playerId playerId
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/user/{userId}/player/{playerId}/form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("userId") String userId, @PathVariable("playerId") String playerId,
                             @RequestParam(value = "view",
            defaultValue = "player.update", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A player ID is required");
        }

        map.addAttribute("userId", userId);
        map.addAttribute("playerId", playerId);
        map.addAttribute("player", administrationService.findPlayer(playerId));
        map.addAttribute("statuses", refFactory.createPlayerStatusList(l));

        return view;
    }
}
