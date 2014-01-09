/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold. 
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap.impl;

import com.online.casino.bootstrap.Bootstrapper;
import com.online.casino.bootstrap.BootstrapperException;
import com.online.casino.domain.entity.ApplicationRight;
import com.online.casino.domain.enums.SystemRightType;
import com.online.casino.service.AdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * Inserts required rights into the system
 */
@SuppressWarnings("unchecked")
public class SystemRightBootstrapper extends AbstractBootstrapper implements Bootstrapper {
    private final static Logger log = LoggerFactory.getLogger(SystemRightBootstrapper.class);
    private static int populated = 0;
    private static int omitted = 0;
    private final AdministrationService administrationService;

    public SystemRightBootstrapper(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
    public void create() throws BootstrapperException {

        processCreation();

        log.info("Populated " + populated + " rights in db");
        log.info("Omitted " + omitted + " rights from db. Already exists.");
    }

    private void processCreation() throws BootstrapperException {
        try {

            List<ApplicationRight> dbList = new ArrayList<ApplicationRight>();

        try {

            for (SystemRightType right : SystemRightType.values()) {
                ApplicationRight tmp = administrationService.findSystemRightByStatusCode(right);

                if (tmp == null) {
                    dbList.add(new ApplicationRight(right));
                    populated++;
                } else {
                    log.info("ApplicationRight already exists with status code: " + right.name());
                    omitted++;
                }
            }

            // ready for save
            if (dbList.size() > 0) {
                for (ApplicationRight right : dbList) {
                    administrationService.persistSystemRight(right);
                }
            }
        } catch (Exception e) {
            throw new BootstrapperException(e.getMessage(), e);
        }

        } catch (Exception e) {
            throw new BootstrapperException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "SystemRightBootstrapper";
    }
}