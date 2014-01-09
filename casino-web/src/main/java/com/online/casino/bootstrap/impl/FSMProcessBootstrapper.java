/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap.impl;

import com.online.casino.bootstrap.Bootstrapper;
import com.online.casino.bootstrap.BootstrapperException;
import com.online.casino.service.AdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 * Created by Bjorn Harvold
 * Date: 12/29/10
 * Time: 2:54 PM
 * Responsibility: This will resume all FSM processes that are currently dormant
 */
public class FSMProcessBootstrapper extends AbstractBootstrapper implements Bootstrapper {
    private final static Logger log = LoggerFactory.getLogger(FSMProcessBootstrapper.class);
    private final AdministrationService administrationService;

    public FSMProcessBootstrapper(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Transactional
    @Override
    public void create() throws BootstrapperException {

		try {
            log.info("Reloading fsm process state models");
            administrationService.reinitializeFiniteStateProcesses();
		} catch (Exception e) {
			throw new BootstrapperException(e.getMessage(), e);
		}
    }

    @Override
	public String toString() {
		return "FSMProcessBootstrapper";
	}
}
