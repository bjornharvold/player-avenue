/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap.impl;

import com.online.casino.bootstrap.BootStrapperService;
import com.online.casino.bootstrap.Bootstrapper;
import com.online.casino.bootstrap.BootstrapperException;
import com.online.casino.security.SecureChannelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:17:07 AM
 */
public class OnStartupBootStrapperService implements BootStrapperService {
    private final static Logger log = LoggerFactory.getLogger(OnStartupBootStrapperService.class);
    private final EntityManagerFactory entityManagerFactory;
    private Boolean enabled = null;
    private Boolean complete = false;
    private List<Bootstrapper> bootstrappers = null;

    public OnStartupBootStrapperService(EntityManagerFactory emf) {
        this.entityManagerFactory = emf;
    }

    @Override
    public void init() {

        if (enabled) {

            // bind entityManager to current thread
            if (bootstrappers != null && bootstrappers.size() > 0) {

                try {
                    // set a fake user in the security context
                    SecureChannelHelper.secureChannel();
                    // bind an entityManager to the session the same way Spring does it in OpenEntityManagerInViewFilter
                    EntityManager em = entityManagerFactory.createEntityManager();
                    TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));

                    for (Bootstrapper dc : bootstrappers) {

                        log.info("Creating data with: " + dc.toString());
                        dc.create();
                        log.info("Success: " + dc.toString());

                    }

                    complete = true;

                } catch (BootstrapperException e) {
                    log.error("Error creating data! " + e.getMessage(), e);
                } finally {
                    EntityManagerHolder emHolder = (EntityManagerHolder)
                            TransactionSynchronizationManager.unbindResource(entityManagerFactory);
                    EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
                    SecureChannelHelper.unsecureChannel();
                }


            }
        } else {
            log.info("OnStartupBootStrapperService is currently disabled. Check application.properties file for property: 'data.creation.enabled'.");
        }
    }

    @Override
    public Boolean isComplete() {
        return complete;
    }

    @PreDestroy
    public void destroy() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setBootstrappers(List<Bootstrapper> bootstrappers) {
        this.bootstrappers = bootstrappers;
    }
}
