/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.security.SecureChannelHelper;
import com.online.casino.service.AdministrationService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 10/10/30
 * @author         Bjorn Harvold    
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
public abstract class AbstractFunctionalTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    protected AdministrationService administrationService;

    /**
     * Method description
     *
     */
    @Before
    public void createSecureChannel() {
        SecureChannelHelper.secureChannel();

        EntityManager em = entityManagerFactory.createEntityManager();
        TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(em));
    }

    /**
     * Method description
     *
     */
    @After
    public void destroySecureChannel() {
        EntityManagerHolder emHolder = (EntityManagerHolder)
                TransactionSynchronizationManager.unbindResource(entityManagerFactory);

        SecureChannelHelper.unsecureChannel();
    }

}
