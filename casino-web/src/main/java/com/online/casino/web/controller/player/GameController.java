/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.player;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.dto.GameFinder;
import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.security.SpringSecurityHelper;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.PokerGameService;
import com.online.casino.web.utils.ReferenceDataFactory;
import com.online.casino.web.validator.GameFinderValidator;
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
import java.util.Map;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Oct 11, 2009
 * Time: 11:52:47 AM
 * Responsibility: Helps the player join the game they want by finding a list of suitable games based on user criteria
 */
@Controller
@SessionAttributes(types = GameFinder.class)
public class GameController {

    /** Field description */
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final PokerGameService gameService;

    /** Field description */
    private final ReferenceDataFactory refFactory;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param refFactory refFactory
     * @param gameService gameService
     * @param administrationService administrationService
     */
    @Autowired
    public GameController(ReferenceDataFactory refFactory, PokerGameService gameService,
                          AdministrationService administrationService) {
        this.refFactory            = refFactory;
        this.gameService           = gameService;
        this.administrationService = administrationService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Will return a list of search results of active games that match the player's search criteria
     *
     *
     * @param casinoId casinoId
     * @param view
     * @param gameFinder
     * @param result
     * @param l
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/player/casino/{casinoId}/game/find", method = RequestMethod.POST)
    public String listSearchResults(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            required = false, defaultValue = "player.game.finder") String view, @Valid GameFinder gameFinder,
            BindingResult result, Locale l, Model map)
            throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating GameFinder entity before initializing game search: " + gameFinder);
        }

        new GameFinderValidator().validate(gameFinder, result);

        ApplicationUser user   = SpringSecurityHelper.getSecurityContextPrincipal();
        String     userId = user.getId();

        if (!result.hasErrors()) {
            Map<PokerGame, List<Gambler>> list = gameService.findPokerGamesAndGamblers(casinoId, gameFinder.getType(),
                                                     gameFinder.getStakeId(), 0, 3);

            map.addAttribute("players", administrationService.findPlayers(userId, null, null, null));
            map.addAttribute("games", list);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }
        }

        map.addAttribute("stakes", administrationService.findStakes(casinoId, null, null));
        map.addAttribute("types", refFactory.createGameTypeList(l));
        map.addAttribute("casinoId", casinoId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     * @param playerId playerId
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/{playerId}/game/{pokergameId}/watch", method = RequestMethod.GET)
    public String watchGame(@PathVariable("pokergameId") String pokergameId,
                            @PathVariable("playerId") String playerId, Model map) throws Exception {
        String view = "player.game.observe";
        
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A pokergame ID is required");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A player ID is required");
        }

        map.addAttribute("playerId", playerId);
        map.addAttribute("pokergameId", pokergameId);

        return view;
    }

    /**
     * Displays a form for finding games the player wants to play
     *
     *
     * @param casinoId casinoId
     * @param map
     * @param l
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/player/casino/{casinoId}/game/find", method = RequestMethod.GET)
    public String showForm(@PathVariable("casinoId") String casinoId, Model map, Locale l) throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        String result = "player.game.finder";

        map.addAttribute("gameFinder", new GameFinder());
        map.addAttribute("types", refFactory.createGameTypeList(l));
        map.addAttribute("casinoId", casinoId);
        map.addAttribute("stakes", administrationService.findStakes(casinoId, null, null));

        return result;
    }
}
