/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.dto.AutoComplete;
import com.online.casino.domain.entity.PokerGame;
import com.online.casino.domain.entity.QueuedGambler;
import com.online.casino.domain.enums.GameStatus;
import com.online.casino.exception.GameException;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.FiniteStateMachineClientService;
import com.online.casino.service.PokerGameService;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import com.online.casino.web.WebConstants;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Sep 12, 2009
 * Time: 12:28:40 PM
 * Responsibility:
 */
@Controller
@SessionAttributes(types = PokerGame.class)
public class PokerGameController {

    /**
     * Field description
     */
    private static final Logger log = LoggerFactory.getLogger(PokerGameController.class);

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    private final AdministrationService administrationService;

    /**
     * Field description
     */
    private final FiniteStateMachineClientService finiteStateMachineClientService;

    /**
     * Field description
     */
    private final PokerGameService pokerGameService;

    /**
     * Field description
     */
    private final ReferenceDataFactory refFactory;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param refFactory            refFactory
     * @param pokerGameService      pokerGameService
     * @param finiteStateMachineClientService
     *                              finiteStateMachineClientService
     * @param administrationService administrationService
     */
    @Autowired
    public PokerGameController(ReferenceDataFactory refFactory, PokerGameService pokerGameService,
                               FiniteStateMachineClientService finiteStateMachineClientService,
                               AdministrationService administrationService) {
        this.refFactory = refFactory;
        this.pokerGameService = pokerGameService;
        this.finiteStateMachineClientService = finiteStateMachineClientService;
        this.administrationService = administrationService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param pokergameId id
     * @param casinoId    casinoId
     * @param view        view
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("pokergameId") String pokergameId, @PathVariable("casinoId") String casinoId,
                         @RequestParam(value = "view",
                                 required = false) String view) throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("An game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        PokerGame game = administrationService.findPokerGame(pokergameId);

        if (game != null) {
            game.setStatus(GameStatus.REMOVED);
        }

        if (StringUtils.isBlank(view)) {
            view = "redirect:/app/administration/casino/" + casinoId + "/game/list";
        }

        return view;
    }

    /**
     * Method description
     *
     * @param view     view
     * @param casinoId casinoId
     * @param game     game
     * @param result   result
     * @param map      map
     * @param l        l
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/form", method = RequestMethod.POST)
    public String insert(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "game.insert", required = false) String view, @Valid PokerGame game, BindingResult result,
                         Model map, Locale l)
            throws Exception {
        if (game == null) {
            throw new IllegalArgumentException("A game is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating PokerGame entity before persisting: " + game);
        }

        if (!result.hasErrors()) {
            game = administrationService.persistPokerGame(game);
            view = "redirect:/app/administration/casino/" + casinoId + "/game/" + game.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("statuses", refFactory.createGameStatusList(l));
            map.addAttribute("gametemplates", administrationService.findGameTemplates(casinoId, null, null));
            map.addAttribute("casinos", administrationService.findAllCasinos());
            map.addAttribute("casinoId", casinoId);
        }

        return view;
    }

    /**
     * Method description
     *
     * @param view     view
     * @param casinoId casinoId
     * @param l        l
     * @param map      map
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/form", method = RequestMethod.GET)
    public String insertForm(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "game.insert", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        PokerGame game = new PokerGame();

        // set some default values
        game.setAutoGenerated(true);
        map.addAttribute("pokerGame", game);
        map.addAttribute("statuses", refFactory.createGameStatusList(l));
        map.addAttribute("gametemplates", administrationService.findGameTemplates(casinoId, null, null));
        map.addAttribute("casinos", administrationService.findAllCasinos());
        map.addAttribute("casinoId", casinoId);

        return view;
    }

    /**
     * Method description
     *
     * @param casinoId        casinoId
     * @param pokergameId     pokergameId
     * @param queuedGamblerId gamblerId
     * @param map             map
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}/queuedgambler/{queuedGamblerId}",
            method = RequestMethod.DELETE)
    public String leaveGame(@PathVariable("casinoId") String casinoId, @PathVariable("pokergameId") String pokergameId,
                            @PathVariable("queuedGamblerId") String queuedGamblerId, Model map)
            throws Exception {
        String view;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(queuedGamblerId)) {
            throw new IllegalArgumentException("A queued gambler ID is required");
        }

        try {
            pokerGameService.doRemoveQueuedGambler(queuedGamblerId);

            // success view
            view = "redirect:/app/administration/casino/" + casinoId + "/game/" + pokergameId;
        } catch (GameException ex) {
            List<String> errors = new ArrayList<String>();

            errors.add(ex.getTranslatedMessage());
            map.addAttribute(WebConstants.ERRORS, errors);
            map.addAttribute("casinoId", casinoId);
            map.addAttribute("pokergameId", pokergameId);
            map.addAttribute("playerId", queuedGamblerId);
            view = show("game.show", pokergameId, casinoId, map);
        }

        return view;
    }

    /**
     * Method description
     *
     * @param view       view
     * @param name       name
     * @param page       page
     * @param maxResults maxResults
     * @param casinoId   casinoId
     * @param map        map
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/list", method = RequestMethod.GET)
    public String list(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "game.list", required = false) String view, @RequestParam(value = "name",
            required = false) String name, @RequestParam(value = "page",
            defaultValue = "1", required = false) Integer page, @RequestParam(value = "maxResults", defaultValue = "10",
            required = false) Integer maxResults, Model map) throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        Long count = administrationService.findPokerGameCount(casinoId, name);

        map.addAttribute("games", administrationService.findPokerGames(casinoId, page - 1, maxResults));
        map.addAttribute("count", count);
        map.addAttribute("page", page);
        map.addAttribute("maxResults", maxResults);
        map.addAttribute("casino", administrationService.findCasino(casinoId));
        map.addAttribute("casinoId", casinoId);

        float nrOfPages = (float) count / maxResults;

        map.addAttribute("maxPages", (int) (((nrOfPages > (int) nrOfPages) || (nrOfPages == 0.0))
                ? nrOfPages + 1
                : nrOfPages));

        return view;
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param casinoId    casinoId
     * @param playerId    playerId
     * @param map         map
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}/player/{playerId}/observe",
            method = RequestMethod.GET)
    public String observeGame(@PathVariable("casinoId") String casinoId,
                              @PathVariable("pokergameId") String pokergameId,
                              @PathVariable("playerId") String playerId, Model map)
            throws Exception {
        String view = "observer.data";

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A player ID is required");
        }

        // start observing game - this will be very short-lived
        pokerGameService.doWatchGame(pokergameId, playerId);
        map.addAttribute("observers", pokerGameService.findGameObservers(pokergameId, null, null));

        return view;
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param casinoId    casinoId
     * @param term        term
     * @return JSON Object
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}/findplayer",
            method = RequestMethod.GET)
    public
    @ResponseBody
    List<AutoComplete> playerSearch(@PathVariable("pokergameId") String pokergameId,
                                    @PathVariable("casinoId") String casinoId, @RequestParam(value = "term",
                    required = true) String term) throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        return administrationService.autoCompletePlayersByNickname(term, 0, 10);
    }

    /**
     * Adds the player to the game queue
     *
     * @param casinoId     casinoId
     * @param pokergameId  pokergameId
     * @param playerId     playerId
     * @param buyin        buyin
     * @param seatNumber   seatNumber
     * @param mustHaveSeat mustHaveSeat
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{gameId}/user/{applicationUserId}/player/{playerId}/queue",
            method = RequestMethod.POST)
    public String queueGambler(@PathVariable("casinoId") String casinoId,
                               @PathVariable("gameId") String pokergameId,
                               @PathVariable("applicationUserId") String applicationUserId,
                               @PathVariable("playerId") String playerId,
                               @RequestParam(value = "buyin", required = true) BigDecimal buyin,
                               @RequestParam(value = "seat", required = true) Integer seatNumber,
                               @RequestParam(value = "mustHaveSeat",
                                       required = true) Boolean mustHaveSeat) throws Exception {
        String view;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A pokergame ID is required");
        }

        if (StringUtils.isBlank(applicationUserId)) {
            throw new IllegalArgumentException("A system user ID is required");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A player ID is required");
        }

        if (buyin == null) {
            throw new IllegalArgumentException("The player needs a buyin defined");
        }

        finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(pokergameId, applicationUserId, playerId, buyin, mustHaveSeat, seatNumber));

        // success view
        view = "redirect:/app/administration/casino/" + casinoId + "/game/" + pokergameId;

        return view;
    }

    /**
     * Method description
     *
     * @param view        view
     * @param nickname    nickname
     * @param page        page
     * @param maxResults  maxResults
     * @param pokergameId pokergameId
     * @param casinoId    casinoId
     * @param map         map
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}/setup/players",
            method = RequestMethod.GET)
    public String searchNonGamblers(@PathVariable("pokergameId") String pokergameId,
                                    @PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
                    defaultValue = "game.setup.player", required = false) String view, @RequestParam(value = "nickname",
                    required = true) String nickname, @RequestParam(value = "page", defaultValue = "1",
                    required = false) Integer page, @RequestParam(value = "maxResults",
                    defaultValue = "10", required = false) Integer maxResults, Model map) throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        Long count = administrationService.findPlayersByNicknameCount(nickname);

        map.addAttribute("players", administrationService.findPlayersByNickname(nickname, page - 1, maxResults));
        map.addAttribute("page", page);
        map.addAttribute("maxResults", maxResults);

        float nrOfPages = (float) count / maxResults;

        map.addAttribute("maxPages", (int) (((nrOfPages > (int) nrOfPages) || (nrOfPages == 0.0))
                ? nrOfPages + 1
                : nrOfPages));
        map.addAttribute("casinoId", casinoId);
        map.addAttribute("pokergameId", pokergameId);

        return view;
    }

    /**
     * Method description
     *
     * @param view        view
     * @param pokergameId pokergameId
     * @param casinoId    casinoId
     * @param map         map
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}", method = RequestMethod.GET)
    public String show(@PathVariable("casinoId") String casinoId, @PathVariable("pokergameId") String pokergameId,
                       @RequestParam(value = "view",
                               defaultValue = "game.show",
                               required = false) String view, Model map) throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        PokerGame game = administrationService.findPokerGame(pokergameId);

        map.addAttribute("casinoId", casinoId);
        map.addAttribute("pokergameId", pokergameId);
        map.addAttribute("pokerGame", game);
        map.addAttribute("hands", pokerGameService.findHandsByGame(pokergameId));
        map.addAttribute("queuedGamblerCount", pokerGameService.findQueuedGamblerCount(pokergameId));
        map.addAttribute("observers", pokerGameService.findGameObservers(pokergameId, null, null));

        List<QueuedGambler> gamblers = pokerGameService.findLatestQueuedGamblers(pokergameId,
                game.getTemplate().getMaxPlayers() * 2);

        if ((gamblers != null) && !gamblers.isEmpty()) {
            map.addAttribute("queuedGamblers", gamblers);

            Map<String, BigDecimal> gameBalance = new HashMap<String, BigDecimal>();
            Map<String, BigDecimal> accountBalance = new HashMap<String, BigDecimal>();

            for (QueuedGambler gambler : gamblers) {

                // grab gambler balances
                gameBalance.put(gambler.getId(), pokerGameService.findBalance(pokergameId, gambler.getPlayerId()));
                accountBalance.put(gambler.getId(),
                        pokerGameService.findAccountBalance(pokergameId, gambler.getPlayerId()));
            }

            map.addAttribute("gameBalance", gameBalance);
            map.addAttribute("accountBalance", accountBalance);
        }

        return view;
    }

    /**
     * Method description
     *
     * @param casinoId    casinoId
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @param map         map
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{gameId}/player/{playerId}/queue",
            method = RequestMethod.GET)
    public String showQueueForm(@PathVariable("casinoId") String casinoId, @PathVariable("gameId") String pokergameId,
                                @PathVariable("playerId") String playerId, Model map)
            throws Exception {
        String view = "queued.gambler.form";

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("A player ID is required");
        }

        map.addAttribute("casinoId", casinoId);
        map.addAttribute("pokergameId", pokergameId);
        map.addAttribute("playerId", playerId);
        map.addAttribute("player", administrationService.findPlayer(playerId));
        map.addAttribute("seats", pokerGameService.findSeatNumbers(pokergameId));
        map.addAttribute("balance", pokerGameService.findAccountBalance(pokergameId, playerId));

        return view;
    }

    /**
     * Method description
     *
     * @param casinoId    casinoId
     * @param pokergameId pokergameId
     * @param observerId  observerId
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}/observer/{observerId}",
            method = RequestMethod.DELETE)
    public String stopObserving(@PathVariable("casinoId") String casinoId,
                                @PathVariable("pokergameId") String pokergameId,
                                @PathVariable("observerId") String observerId)
            throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(observerId)) {
            throw new IllegalArgumentException("An observer ID is required");
        }

        pokerGameService.doUnwatchGame(observerId);

        return "redirect:/app/administration/casino/" + casinoId + "/game/" + pokergameId;
    }

    /**
     * Method description
     *
     * @param view        view
     * @param pokergameId pokergameId
     * @param casinoId    casinoId
     * @param game        game
     * @param result      result
     * @param map         map
     * @param l           l
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}/form", method = RequestMethod.PUT)
    public String update(@PathVariable("casinoId") String casinoId, @PathVariable("pokergameId") String pokergameId,
                         @RequestParam(value = "view",
                                 defaultValue = "game.update",
                                 required = false) String view, @Valid PokerGame game, BindingResult result,
                         Model map, Locale l)
            throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (game == null) {
            throw new IllegalArgumentException("A game is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating PokerGame entity before persisting: " + game);
        }

        if (!result.hasErrors()) {
            view = "redirect:/app/administration/casino/" + casinoId + "/game/" + pokergameId;
            administrationService.mergePokerGame(game);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("statuses", refFactory.createGameStatusList(l));
            map.addAttribute("gametemplates", administrationService.findGameTemplates(casinoId, null, null));
            map.addAttribute("casinos", administrationService.findAllCasinos());
            map.addAttribute("casinoId", casinoId);
            map.addAttribute("pokergameId", pokergameId);
        }

        return view;
    }

    /**
     * Method description
     *
     * @param view        view
     * @param pokergameId pokergameId
     * @param casinoId    casinoId
     * @param l           l
     * @param map         map
     * @return Tile definition
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}/form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("casinoId") String casinoId,
                             @PathVariable("pokergameId") String pokergameId, @RequestParam(value = "view",
                    defaultValue = "game.update", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("An game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        map.addAttribute("pokerGame", administrationService.findPokerGame(pokergameId));
        map.addAttribute("statuses", refFactory.createGameStatusList(l));
        map.addAttribute("gametemplates", administrationService.findGameTemplates(casinoId, null, null));
        map.addAttribute("casinos", administrationService.findAllCasinos());
        map.addAttribute("casinoId", casinoId);
        map.addAttribute("pokergameId", pokergameId);

        return view;
    }
}
