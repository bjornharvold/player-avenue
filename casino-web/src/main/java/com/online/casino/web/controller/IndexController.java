/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.bootstrap.BootStrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * <p>Title: PageController</p>
 * <p>Description: Dispatches pages.</p>
 *
 * @author Bjorn Harvold
 */
@Controller
public class IndexController {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(IndexController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final BootStrapperService bootStrapperService;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param bootStrapperService bootStrapperService
     */
    @Autowired
    public IndexController(BootStrapperService bootStrapperService) {
        this.bootStrapperService = bootStrapperService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String show() throws Exception {
        return "index";
    }
}
