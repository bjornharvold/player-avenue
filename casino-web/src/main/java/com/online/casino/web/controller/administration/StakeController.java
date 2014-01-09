/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.Stake;
import com.online.casino.service.AdministrationService;
import com.online.casino.web.validator.StakeValidator;
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
import java.math.BigDecimal;
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
@SessionAttributes(types = Stake.class)
public class StakeController {

    /** Field description */
    private static final Logger log = LoggerFactory.getLogger(StakeController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    /** Field description */
    private final StakeValidator stakeValidator;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param administrationService administrationService
     * @param stakeValidator stakeValidator
     */
    @Autowired
    public StakeController(AdministrationService administrationService, StakeValidator stakeValidator) {
        this.administrationService = administrationService;
        this.stakeValidator        = stakeValidator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param stakeId stakeId
     * @param view view
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/stake/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("casinoId") String casinoId, @PathVariable("stakeId") String stakeId,
                         @RequestParam(value = "view",
                                       required = false) String view) throws Exception {
        if (StringUtils.isBlank(stakeId)) {
            throw new IllegalArgumentException("A stake ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        administrationService.deleteStake(stakeId);
        view = "redirect:/app/administration/casino/" + casinoId + "/stake/list";

        return view;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param view view
     * @param stake stake
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/stake/form", method = RequestMethod.POST)
    public String insert(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "stake.insert", required = false) String view, @Valid Stake stake, BindingResult result,
            Model map, Locale l)
            throws Exception {
        if (stake == null) {
            throw new IllegalArgumentException("A stake is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Stake entity before persisting: " + stake);
        }

        stakeValidator.validate(stake, result);

        if (!result.hasErrors()) {
            stake.setCasino(administrationService.findCasino(casinoId));
            stake = administrationService.persistStake(stake);
            view  = "redirect:/app/administration/casino/" + casinoId + "/stake/list";
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
     * @param casinoId casinoId
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/stake/form", method = RequestMethod.GET)
    public String insertForm(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "stake.insert", required = false) String view, Model map) throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        map.addAttribute("stake", new Stake());
        map.addAttribute("casino", administrationService.findCasino(casinoId));
        map.addAttribute("casinoId", casinoId);

        return view;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param view view
     * @param hilo hilo
     * @param page page
     * @param maxResults maxResults
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/stake/list", method = RequestMethod.GET)
    public String list(@PathVariable("casinoId") String casinoId, @RequestParam(value = "view",
            defaultValue = "stake.list", required = false) String view, @RequestParam(value = "name",
            required = false) BigDecimal hilo, @RequestParam(value = "page",
            defaultValue = "1", required = false) Integer page, @RequestParam(value = "maxResults", defaultValue = "10",
            required = false) Integer maxResults, Model map) throws Exception {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        List<Stake> list  = administrationService.findStakes(casinoId, hilo, page - 1, maxResults);
        Long        count = administrationService.findStakeCount(casinoId, hilo);

        map.addAttribute("stakes", list);
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
     * @param stakeId stakeId
     * @param view view
     * @param stake stake
     * @param result result
     * @param map map
     * @param l l
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/stake/{stakeId}/form", method = RequestMethod.PUT)
    public String update(@PathVariable("casinoId") String casinoId, @PathVariable("stakeId") String stakeId,
                         @RequestParam(value = "view",
                                       defaultValue = "stake.update",
                                       required = false) String view, @Valid Stake stake, BindingResult result,
                                           Model map, Locale l)
            throws Exception {
        if (stake == null) {
            throw new IllegalArgumentException("A stake is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        if (StringUtils.isBlank(stakeId)) {
            throw new IllegalArgumentException("A stake ID is required");
        }

        if (log.isTraceEnabled()) {
            log.trace("Validating Stake entity before persisting: " + stake);
        }

        stakeValidator.validate(stake, result);

        if (!result.hasErrors()) {
            view = "redirect:/app/administration/casino/" + casinoId + "/stake/list";
            administrationService.mergeStake(stake);
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
     * @param stakeId stakeId
     * @param casinoId casinoId
     * @param view view
     * @param l l
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/casino/{casinoId}/stake/{stakeId}/form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("stakeId") String stakeId, @PathVariable("casinoId") String casinoId,
                             @RequestParam(value = "view",
            defaultValue = "stake.update", required = false) String view, Locale l, Model map) throws Exception {
        if (StringUtils.isBlank(stakeId)) {
            throw new IllegalArgumentException("An stake ID is required");
        }

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("A casino ID is required");
        }

        map.addAttribute("stake", administrationService.findStake(stakeId));
        map.addAttribute("casinoId", casinoId);
        map.addAttribute("stakeId", stakeId);

        return view;
    }
}
