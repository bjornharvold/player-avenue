/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Gambler;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.FiniteStateMachineClientService;
import com.online.casino.service.PokerGameService;
import com.online.casino.service.fsm.event.CallEvent;
import com.online.casino.service.fsm.event.CheckEvent;
import com.online.casino.service.fsm.event.FoldEvent;
import com.online.casino.service.fsm.event.RaiseEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Oct 13, 2010
 * Time: 2:56:33 PM
 * Responsibility: Admin can control a game manually with these methods
 */
@Controller
public class HandController {

    /** Field description */
    private static final String ERRORS = "errors";

    /**
     * Field description
     */
    private static final Logger log = LoggerFactory.getLogger(HandController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final FiniteStateMachineClientService finiteStateMachineClientService;

    /**
     * Field description
     */
    private final PokerGameService pokerGameService;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param pokerGameService service
     * @param finiteStateMachineClientService
     * @param administrationService
     */
    @Autowired
    public HandController(PokerGameService pokerGameService,
                          FiniteStateMachineClientService finiteStateMachineClientService,
                          AdministrationService administrationService) {
        this.pokerGameService                = pokerGameService;
        this.finiteStateMachineClientService = finiteStateMachineClientService;
        this.administrationService           = administrationService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Calls the pot
     *
     * @param casinoId    casinoId
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param applicationUserId applicationUserId
     * @param gamblerId   gamblerId
     * @return Redirects to showHand()
     * @throws Exception exception
     */
    @RequestMapping(
            value = "/administration/casino/{casinoId}/game/{pokergameId}/hand/{handId}/user/{applicationUserId}/gambler/{gamblerId}/call",
            method = RequestMethod.GET)
    public String doCall(@PathVariable("casinoId") String casinoId, @PathVariable("pokergameId") String pokergameId,
                         @PathVariable("handId") String handId, @PathVariable("applicationUserId") String applicationUserId,
                         @PathVariable("gamblerId") String gamblerId)
            throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("A hand ID is required");
        }

        if (StringUtils.isBlank(applicationUserId)) {
            throw new IllegalArgumentException("A system user ID is required");
        }

        if (StringUtils.isBlank(gamblerId)) {
            throw new IllegalArgumentException("A gambler ID is required");
        }

        finiteStateMachineClientService.dispatchCallEvent(new CallEvent(pokergameId, handId, applicationUserId, gamblerId));

        return "redirect:/app/administration/casino/" + casinoId + "/game/" + pokergameId + "/hand/" + handId;
    }

    /**
     * Checks the pot
     *
     * @param casinoId    casinoId
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param applicationUserId applicationUserId
     * @param gamblerId   gamblerId
     * @return Redirects to showHand()
     * @throws Exception exception
     */
    @RequestMapping(
            value = "/administration/casino/{casinoId}/game/{pokergameId}/hand/{handId}/user/{applicationUserId}/gambler/{gamblerId}/check",
            method = RequestMethod.GET)
    public String doCheck(@PathVariable("casinoId") String casinoId, @PathVariable("pokergameId") String pokergameId,
                          @PathVariable("handId") String handId, @PathVariable("applicationUserId") String applicationUserId,
                          @PathVariable("gamblerId") String gamblerId)
            throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("A hand ID is required");
        }

        if (StringUtils.isBlank(applicationUserId)) {
            throw new IllegalArgumentException("A system user ID is required");
        }

        if (StringUtils.isBlank(gamblerId)) {
            throw new IllegalArgumentException("A gambler ID is required");
        }

        finiteStateMachineClientService.dispatchCheckEvent(new CheckEvent(pokergameId, handId, applicationUserId, gamblerId));

        return "redirect:/app/administration/casino/" + casinoId + "/game/" + pokergameId + "/hand/" + handId;
    }

    /**
     * Folds
     *
     * @param casinoId    casinoId
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param applicationUserId applicationUserId
     * @param gamblerId   gamblerId
     * @return Redirects to showHand()
     * @throws Exception exception
     */
    @RequestMapping(
            value = "/administration/casino/{casinoId}/game/{pokergameId}/hand/{handId}/user/{applicationUserId}/gambler/{gamblerId}/fold",
            method = RequestMethod.GET)
    public String doFold(@PathVariable("casinoId") String casinoId, @PathVariable("pokergameId") String pokergameId,
                         @PathVariable("handId") String handId, @PathVariable("applicationUserId") String applicationUserId,
                         @PathVariable("gamblerId") String gamblerId)
            throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("A hand ID is required");
        }

        if (StringUtils.isBlank(applicationUserId)) {
            throw new IllegalArgumentException("A system user ID is required");
        }

        if (StringUtils.isBlank(gamblerId)) {
            throw new IllegalArgumentException("A gambler ID is required");
        }

        finiteStateMachineClientService.dispatchFoldEvent(new FoldEvent(pokergameId, handId, applicationUserId, gamblerId));

        return "redirect:/app/administration/casino/" + casinoId + "/game/" + pokergameId + "/hand/" + handId;
    }

    /**
     * Raises
     *
     * @param casinoId    casinoId
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param applicationUserId applicationUserId
     * @param gamblerId   gamblerId
     * @param bet bet
     * @return Redirects to showHand()
     * @throws Exception exception
     */
    @RequestMapping(
            value = "/administration/casino/{casinoId}/game/{pokergameId}/hand/{handId}/user/{applicationUserId}/gambler/{gamblerId}/raise",
            method = RequestMethod.POST)
    public String doRaise(@PathVariable("casinoId") String casinoId, @PathVariable("pokergameId") String pokergameId,
                          @PathVariable("handId") String handId, @PathVariable("applicationUserId") String applicationUserId,
                          @PathVariable("gamblerId") String gamblerId, @RequestParam(value = "bet",
            required = true) BigDecimal bet) throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("A hand ID is required");
        }

        if (StringUtils.isBlank(applicationUserId)) {
            throw new IllegalArgumentException("A system user ID is required");
        }

        if (StringUtils.isBlank(gamblerId)) {
            throw new IllegalArgumentException("A gambler ID is required");
        }

        finiteStateMachineClientService.dispatchRaiseEvent(new RaiseEvent(pokergameId, handId, applicationUserId, gamblerId, bet));

        return "redirect:/app/administration/casino/" + casinoId + "/game/" + pokergameId + "/hand/" + handId;
    }

    /**
     * Shows the hand page
     *
     * @param casinoId    casinoId
     * @param pokergameId pokergameId
     * @param handId      handId
     * @param map         map
     * @param session session
     * @return Returns the tiles definition for hand
     * @throws Exception exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/game/{pokergameId}/hand/{handId}",
                    method = RequestMethod.GET)
    public String showHand(@PathVariable("casinoId") String casinoId, @PathVariable("pokergameId") String pokergameId,
                           @PathVariable("handId") String handId, Model map, HttpSession session)
            throws Exception {
        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("A game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(handId)) {
            throw new IllegalArgumentException("A hand ID is required");
        }

        map.addAttribute("casinoId", casinoId);
        map.addAttribute("pokergameId", pokergameId);
        map.addAttribute("handId", handId);
        map.addAttribute("casino", administrationService.findCasino(casinoId));
        map.addAttribute("hand", pokerGameService.findHand(handId));
        map.addAttribute("pokerGame", administrationService.findPokerGame(pokergameId));
        map.addAttribute("progressable", pokerGameService.isProgressable(handId));
        map.addAttribute("endgame", pokerGameService.isEndGame(handId));
        map.addAttribute("pot", pokerGameService.findPotSize(handId));
        map.addAttribute("bets", pokerGameService.findBetsByHand(handId));

        Gambler currentGambler = pokerGameService.findCurrentGambler(handId);

        // retrieve potential session errors
        if ((session != null) && (session.getAttribute(ERRORS) != null)) {
            map.addAttribute(ERRORS, session.getAttribute(ERRORS));
            session.removeAttribute(ERRORS);
        }

        if (currentGambler != null) {
            map.addAttribute("checkable", pokerGameService.isCheckable(currentGambler.getId()));
            map.addAttribute("callable", pokerGameService.isCallable(currentGambler.getId()));
            map.addAttribute("raisable", pokerGameService.isRaiseable(currentGambler.getId()));
            map.addAttribute("currentGamblerId", currentGambler.getId());
        }

        // below is all gambler related
        List<Gambler> gamblers = pokerGameService.findGamblers(handId);

        if ((gamblers != null) && !gamblers.isEmpty()) {
            map.addAttribute("gamblers", gamblers);

            Map<String, BigDecimal> balance = new HashMap<String, BigDecimal>();
            Map<String, BigDecimal> bet     = new HashMap<String, BigDecimal>();

            for (Gambler gambler : gamblers) {

                // grab gambler balances
                balance.put(gambler.getId(), pokerGameService.findBalance(pokergameId, gambler.getPlayer().getId()));
                bet.put(gambler.getId(), pokerGameService.findBetAmount(handId, gambler.getPlayer().getId()));
            }

            map.addAttribute("balance", balance);
            map.addAttribute("bet", bet);
        }

        return "game.hand";
    }
}
