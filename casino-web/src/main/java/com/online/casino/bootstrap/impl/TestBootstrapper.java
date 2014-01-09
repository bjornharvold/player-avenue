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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * Inserts required rights into the system
 */
@SuppressWarnings("unchecked")
public class TestBootstrapper extends AbstractBootstrapper implements Bootstrapper {
    private final static Logger log = LoggerFactory.getLogger(TestBootstrapper.class);
    private final AdministrationService administrationService;
    private final EntityManagerFactory entityManagerFactory;

    public TestBootstrapper(AdministrationService administrationService, EntityManagerFactory entityManagerFactory) {
        this.administrationService = administrationService;
        this.entityManagerFactory = entityManagerFactory;
    }

    public void create() throws BootstrapperException {
        create1();
//        create2();
    }

    public void create2() throws BootstrapperException {
        log.info("This is a test bootstrapper and should not be in production code!!");
        EntityManager em = null;
        ApplicationRight sr = new ApplicationRight();
        sr.setStatusCode(SystemRightType.RIGHT_READ_BET);

        ApplicationRight tmp = administrationService.findSystemRightByStatusCode(sr.getStatusCode());

        try {
            if (tmp == null) {
                em = entityManagerFactory.createEntityManager();
                EntityTransaction tx = em.getTransaction();
                tx.begin();

                em.persist(sr);

                tx.commit();
                em.close();

                tmp = administrationService.findSystemRightByStatusCode(sr.getStatusCode());

                if (tmp != null) {
                    log.info("Test Bootstrapper persisted successfully");
                } else {
                    log.error("Test Bootstrapper FAILED");
                }
            } else {
                log.info("Test right already exists in the db");
            }
        } finally {
            try {
                if (em != null && em.isOpen())
                    em.close();
            } catch (Throwable t) {
                log.error("While closing an EntityManager", t);
            }
        }
    }

    public void create1() throws BootstrapperException {
        ApplicationRight sr = new ApplicationRight();
        sr.setStatusCode(SystemRightType.RIGHT_READ_BET);

        ApplicationRight tmp = administrationService.findSystemRightByStatusCode(sr.getStatusCode());

        if (tmp == null) {
            administrationService.persistSystemRight(sr);
            sr.flush();
            tmp = administrationService.findSystemRightByStatusCode(sr.getStatusCode());

            if (tmp != null) {
                log.info("Test Bootstrapper persisted successfully");
            } else {
                log.error("Test Bootstrapper FAILED");
            }
        } else {
            log.info("Test right already exists in the db");
        }

        if (tmp != null) {
            log.info("Test Bootstrapper persisted successfully");
        }

    }

    @Override
    public String toString() {
        return "TestBootstrapper";
    }
}