/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.administration;

//~--- non-JDK imports --------------------------------------------------------

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Oct 3, 2009
 * Time: 6:58:57 PM
 * Responsibility:
 */
@Controller
public class SecurityController {

    /**
     * Method description
     *
     *
     * @param view view
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/administration/security", method = RequestMethod.GET)
    public String show(@RequestParam(value = "view", defaultValue = "security.monitor", required = false) String view,
                       Model map)
            throws Exception {
        return view;
    }
}
