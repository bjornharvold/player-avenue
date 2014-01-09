/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: Bjorn Harvold
 * Date: Dec 2, 2009
 * Time: 11:55:13 PM
 * Responsibility:
 */
public class DataLoader {
    private final static Logger log = LoggerFactory.getLogger(DataLoader.class);

    public DataLoader(String[] args) {

        if (args != null) {
            log.info("DataLoader initialized with args:");
            for (String arg : args) {
                log.info(arg);
            }
        } else {
            log.info("DataLoader initialized");
        }

        new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
    }

    public static void main(String[] args) {
        new DataLoader(args);
    }
}
