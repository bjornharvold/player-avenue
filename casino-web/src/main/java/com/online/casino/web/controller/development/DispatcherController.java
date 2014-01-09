/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.development;

//~--- non-JDK imports --------------------------------------------------------

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Oct 16, 2010
 * Time: 3:57:16 PM
 * Responsibility: Dispatches any sort of tile definition
 */
@Controller
public class DispatcherController {

    /**
     * Method description
     *
     *
     * @param view view
     *
     * @return Return value
     */
    @RequestMapping(value = "/dispatcher", method = RequestMethod.GET)
    public String dispatch(@RequestParam(value = "view") String view) {
        return view;
    }
}
