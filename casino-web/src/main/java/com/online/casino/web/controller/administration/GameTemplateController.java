/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.GameTemplate;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.utils.ReferenceDataFactory;
import com.online.casino.web.validator.GameTemplateValidator;
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
 * Date: Sep 12, 2009
 * Time: 12:28:40 PM
 * Responsibility:
 */
@Controller
@SessionAttributes(types = GameTemplate.class)
public class GameTemplateController {

    /** Field description */
    private static final Logger log = LoggerFactory.getLogger(GameTemplateController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final GameTemplateValidator gameTemplateValidator;

    /** Field description */
    private final ReferenceDataFactory refFactory;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param refFactory refFactory
     * @param administrationService administrationService
     * @param gameTemplateValidator gameTemplateValidator
     */
    @Autowired
    public GameTemplateController(ReferenceDataFactory refFactory, AdministrationService administrationService,
                                  GameTemplateValidator gameTemplateValidator) {
        this.refFactory            = refFactory;
        this.administrationService = administrationService;
        this.gameTemplateValidator = gameTemplateValidator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param gameTemplateId gameTemplateId
     * @param view view
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/gametemplate/{gameTemplateId}",
                    method = RequestMethod.DELETE)
    public String delete(@PathVariable("casinoId") String casinoId,
                         @PathVariable("gameTemplateId") String gameTemplateId, @RequestParam(value = "view",
            required = false) String view) throws Exception {
        if (StringUtils.isBlank(gameTemplateId)) {
            throw new IllegalArgumentException("An game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        administrationService.deleteGameTemplate(gameTemplateId);

        if (StringUtils.isBlank(view)) {
            view = "redirect:/app/administration/casino/" + casinoId + "/gametemplate/list";
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param view view
     * @param game game
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/gametemplate/form", method = RequestMethod.POST)
    public String insert(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "gametemplate.insert", required = false) String view, @Valid GameTemplate game,
            BindingResult result, Model map, Locale l)
            throws Exception {
        if (game == null) {
            throw new IllegalArgumentException("A game is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating GameTemplate entity before persisting: " + game);
        }

        gameTemplateValidator.validate(game, result);

        if (!result.hasErrors()) {
            game.setAutoGenerated(Boolean.FALSE);
            game.setCasino(administrationService.findCasino(casinoId));
            game = administrationService.persistGameTemplate(game);
            view = "redirect:/app/administration/casino/" + casinoId + "/gametemplate/" + game.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("currencies", refFactory.createCurrencyList(l));
            map.addAttribute("devices", refFactory.createDeviceTypeList(l));
            map.addAttribute("limits", refFactory.createLimitTypeList(l));
            map.addAttribute("types", refFactory.createGameTypeList(l));
            map.addAttribute("roundTypes", refFactory.createRoundTypeList(l));
            map.addAttribute("stakes", administrationService.findStakes(casinoId, null, null));
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/gametemplate/form", method = RequestMethod.GET)
    public String insertForm(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "gametemplate.insert", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        GameTemplate gameTemplate = new GameTemplate();

        gameTemplate.setCasino(administrationService.findCasino(casinoId));
        map.addAttribute("gameTemplate", gameTemplate);
        map.addAttribute("currencies", refFactory.createCurrencyList(l));
        map.addAttribute("devices", refFactory.createDeviceTypeList(l));
        map.addAttribute("limits", refFactory.createLimitTypeList(l));
        map.addAttribute("types", refFactory.createGameTypeList(l));
        map.addAttribute("roundTypes", refFactory.createRoundTypeList(l));
        map.addAttribute("stakes", administrationService.findStakes(casinoId, null, null));
        map.addAttribute("casinoId", casinoId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param view view
     * @param page page
     * @param maxResults maxResults
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/gametemplate/list", method = RequestMethod.GET)
    public String list(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "gametemplate.list", required = false) String view, @RequestParam(value = "page",
            defaultValue = "1", required = false) Integer page, @RequestParam(value = "maxResults", defaultValue = "10",
            required = false) Integer maxResults, Model map) throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        List<GameTemplate> list  = administrationService.findGameTemplates(casinoId, page - 1, maxResults);
        Long               count = administrationService.findGameTemplateCount(casinoId);

        map.addAttribute("gametemplates", list);
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
     *
     * @param casinoId casinoId
     * @param gameTemplateId gameTemplateId
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/gametemplate/{gameTemplateId}",
                    method = RequestMethod.GET)
    public String show(@PathVariable("casinoId") String casinoId,
                       @PathVariable("gameTemplateId") String gameTemplateId, @RequestParam(value = "view",
            defaultValue = "gametemplate.show", required = false) String view, Model map) throws Exception {
        if (StringUtils.isBlank(gameTemplateId)) {
            throw new IllegalArgumentException("An game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        map.addAttribute("gameTemplate", administrationService.findGameTemplate(gameTemplateId));
        map.addAttribute("casinoId", casinoId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param gameTemplateId gameTemplateId
     * @param view view
     * @param game game
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/gametemplate/{gameTemplateId}/form",
                    method = RequestMethod.PUT)
    public String update(@PathVariable("casinoId") String casinoId,
                         @PathVariable("gameTemplateId") String gameTemplateId, @RequestParam(value = "view",
            defaultValue = "gametemplate.update", required = false) String view, @Valid GameTemplate game,
            BindingResult result, Model map, Locale l)
            throws Exception {
        if (StringUtils.isBlank(gameTemplateId)) {
            throw new IllegalArgumentException("An game ID is required");
        }

        if (game == null) {
            throw new IllegalArgumentException("A game is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating GameTemplate entity before persisting: " + game);
        }

        gameTemplateValidator.validate(game, result);

        if (!result.hasErrors()) {
            view = "redirect:/app/administration/casino/" + casinoId + "/gametemplate/" + gameTemplateId;
            administrationService.mergeGameTemplate(game);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("currencies", refFactory.createCurrencyList(l));
            map.addAttribute("devices", refFactory.createDeviceTypeList(l));
            map.addAttribute("limits", refFactory.createLimitTypeList(l));
            map.addAttribute("types", refFactory.createGameTypeList(l));
            map.addAttribute("roundTypes", refFactory.createRoundTypeList(l));
            map.addAttribute("stakes", administrationService.findStakes(casinoId, null, null));
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param gameTemplateId gameTemplateId
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/gametemplate/{gameTemplateId}/form",
                    method = RequestMethod.GET)
    public String updateForm(@PathVariable("casinoId") String casinoId,
                             @PathVariable("gameTemplateId") String gameTemplateId, @RequestParam(value = "view",
            defaultValue = "gametemplate.update", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(gameTemplateId)) {
            throw new IllegalArgumentException("An game ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        map.addAttribute("gameTemplate", administrationService.findGameTemplate(gameTemplateId));
        map.addAttribute("currencies", refFactory.createCurrencyList(l));
        map.addAttribute("devices", refFactory.createDeviceTypeList(l));
        map.addAttribute("limits", refFactory.createLimitTypeList(l));
        map.addAttribute("types", refFactory.createGameTypeList(l));
        map.addAttribute("roundTypes", refFactory.createRoundTypeList(l));
        map.addAttribute("stakes", administrationService.findStakes(casinoId, null, null));
        map.addAttribute("casinoId", casinoId);

        return view;
    }
}
