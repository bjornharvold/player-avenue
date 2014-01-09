/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.player;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.enums.PlayerStatus;
import com.online.casino.security.SpringSecurityHelper;
import com.online.casino.service.AdministrationService;
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
public class PersonaController {

    /** Field description */
    private static final Logger log = LoggerFactory.getLogger(PersonaController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final PlayerValidator playerValidator;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param administrationService administrationService
     * @param playerValidator playerValidator
     */
    @Autowired
    public PersonaController(AdministrationService administrationService, PlayerValidator playerValidator) {
        this.administrationService = administrationService;
        this.playerValidator       = playerValidator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param playerId playerId
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/persona/{playerId}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("playerId") String playerId) throws Exception {
        if (playerId == null) {
            throw new IllegalArgumentException("An Identifier is required");
        }

        administrationService.findPlayer(playerId).remove();

        return "redirect:/app/player/persona/list";
    }

    /**
     * Method description
     *
     *
     * @param player player
     * @param result result
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/persona/form", method = RequestMethod.POST)
    public String insert(@Valid Player player, BindingResult result) throws Exception {
        String view = "player.persona.insert";

        if (player == null) {
            throw new IllegalArgumentException("A player is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Player entity before persisting: " + player);
        }

        ApplicationUser user = SpringSecurityHelper.getSecurityContextPrincipal();

        player.setApplicationUser(user);
        player.setStatus(PlayerStatus.ACTIVE);
        playerValidator.validate(player, result);

        if (!result.hasErrors()) {
            player = administrationService.persistPlayer(player);
            view = "redirect:/app/player/persona/" + player.getId();
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
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/persona/form", method = RequestMethod.GET)
    public String insertForm(@RequestParam(value = "view", defaultValue = "player.persona.insert",
            required = false) String view, Locale l, Model map)
            throws Exception {
        map.addAttribute("player", new Player());

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
    @RequestMapping(value = "/player/persona/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "view", defaultValue = "player.persona.list",
                                     required = false) String view, @RequestParam(value = "name",
            required = false) String name, @RequestParam(value = "page", defaultValue = "1",
            required = false) Integer page, @RequestParam(value = "maxResults",
            defaultValue = "10", required = false) Integer maxResults, Model map) throws Exception {
        ApplicationUser user   = SpringSecurityHelper.getSecurityContextPrincipal();
        String     userId = user.getId();

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
     * @param playerId playerId
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/persona/{playerId}", method = RequestMethod.GET)
    public String show(@PathVariable("playerId") String playerId, Model map) throws Exception {
        final String view = "player.persona.show";

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        map.addAttribute("player", administrationService.findPlayer(playerId));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param playerId playerId
     * @param player player
     * @param result result
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/persona/{playerId}/form", method = RequestMethod.PUT)
    public String update(@PathVariable("playerId") String playerId, @Valid Player player, BindingResult result)
            throws Exception {
        String view = "player.persona.update";

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A user ID is required");
        }

        if (player == null) {
            throw new IllegalArgumentException("A player is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Player entity before persisting: " + player);
        }

        playerValidator.validate(player, result);

        if (!result.hasErrors()) {
            administrationService.mergePlayer(player);
            view = "redirect:/app/player/persona/" + playerId;
        }

        {
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
     * @param playerId playerId
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/persona/{playerId}/form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("playerId") String playerId, Locale l, Model map) throws Exception {
        final String view = "player.persona.update";

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("An ID is required");
        }

        map.addAttribute("player", administrationService.findPlayer(playerId));

        return view;
    }
}
