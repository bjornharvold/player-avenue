/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Casino;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.utils.ReferenceDataFactory;
import com.online.casino.web.validator.CasinoValidator;
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
@SessionAttributes(types = Casino.class)
public class CasinoController {

    /** Field description */
    private static final Logger log = LoggerFactory.getLogger(CasinoController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final CasinoValidator casinoValidator;

    /** Field description */
    private final ReferenceDataFactory refFactory;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param refFactory refFactory
     * @param administrationService
     * @param casinoValidator
     */
    @Autowired
    public CasinoController(ReferenceDataFactory refFactory, AdministrationService administrationService,
                            CasinoValidator casinoValidator) {
        this.refFactory            = refFactory;
        this.administrationService = administrationService;
        this.casinoValidator       = casinoValidator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param view view
     * @param casinoId id
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "redirect:/app/administration/casino/list", required = false) String view) throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        administrationService.deleteCasino(casinoId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param view view
     * @param casino casino
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/form", method = RequestMethod.POST)
    public String insert(@RequestParam(value = "view", defaultValue = "casino.insert", required = false) String view,
                         @Valid Casino casino, BindingResult result, Model map, Locale l)
            throws Exception {
        if (casino == null) {
            throw new IllegalArgumentException("A casino is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Casino entity before persisting: " + casino);
        }

        casinoValidator.validate(casino, result);

        if (!result.hasErrors()) {
            casino = administrationService.persistCasino(casino);
            view   = "redirect:/app/administration/casino/" + casino.getId();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("statuses", refFactory.createUserStatusList(l));
            map.addAttribute("currencies", refFactory.createCurrencyList(l));
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
    @RequestMapping(value = "/administration/casino/form", method = RequestMethod.GET)
    public String insertForm(@RequestParam(value = "view", defaultValue = "casino.insert",
            required = false) String view, Locale l, Model map)
            throws Exception {
        Casino casino = new Casino();

        map.addAttribute("casino", casino);
        map.addAttribute("statuses", refFactory.createCasinoStatusList(l));
        map.addAttribute("currencies", refFactory.createCurrencyList(l));

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
    @RequestMapping(value = "/administration/casino/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "view", defaultValue = "casino.list", required = false) String view,
                       @RequestParam(value = "name",
                                     required = false) String name, @RequestParam(value = "page", defaultValue = "1",
                                     required = false) Integer page, @RequestParam(value = "maxResults",
            defaultValue = "10", required = false) Integer maxResults, Model map) throws Exception {
        List<Casino> list  = administrationService.findCasinos(name, page - 1, maxResults);
        Long         count = administrationService.findCasinoCount(name);

        map.addAttribute("casinos", list);
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
     * @param view view
     * @param casinoId casinoId
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}", method = RequestMethod.GET)
    public String show(@RequestParam(value = "view", defaultValue = "casino.show", required = false) String view,
                       @PathVariable("casinoId") String casinoId, Model map)
            throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        map.addAttribute("casino", administrationService.findCasino(casinoId));
        map.addAttribute("casinoId", casinoId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param view view
     * @param id id
     * @param casino casino
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{id}/form", method = RequestMethod.PUT)
    public String update(@RequestParam(value = "view", defaultValue = "casino.update", required = false) String view,
                         @PathVariable(value = "id") String id, @Valid Casino casino, BindingResult result, Model map,
                         Locale l)
            throws Exception {
        if (casino == null) {
            throw new IllegalArgumentException("A casino is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Casino entity before persisting: " + casino);
        }

        casinoValidator.validate(casino, result);

        if (!result.hasErrors()) {
            view = "redirect:/app/administration/casino/" + casino.getId();
            administrationService.mergeCasino(casino);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Validation failed: " + result);
            }

            map.addAttribute("statuses", refFactory.createCasinoStatusList(l));
            map.addAttribute("currencies", refFactory.createCurrencyList(l));
            map.addAttribute("casinoId", id);
        }

        return view;
    }

    /**
     * Method description
     *
     *
     * @param view view
     * @param id id
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{id}/form", method = RequestMethod.GET)
    public String updateForm(@RequestParam(value = "view", defaultValue = "casino.update",
            required = false) String view, @PathVariable("id") String id, Locale l, Model map)
            throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        map.addAttribute("casino", administrationService.findCasino(id));
        map.addAttribute("statuses", refFactory.createCasinoStatusList(l));
        map.addAttribute("currencies", refFactory.createCurrencyList(l));
        map.addAttribute("casinoId", id);

        return view;
    }
}
