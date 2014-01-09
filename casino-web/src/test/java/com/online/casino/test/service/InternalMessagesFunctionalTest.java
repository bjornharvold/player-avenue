/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.InternalMessage;
import com.online.casino.domain.entity.Player;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.RelationshipService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility:
 */
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class InternalMessagesFunctionalTest extends AbstractFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(InternalMessagesFunctionalTest.class);

    @Autowired
    private RelationshipService relationshipService;


    @Test
    public void testInternalMessages() {
        log.info("Sending messages from one player to another");

        Player player1 = administrationService.findPlayerByNickname("player9a");
        Player player2 = administrationService.findPlayerByNickname("player10a");
        assertNotNull("player3 is null", player1);
        assertNotNull("player4 is null", player2);

        log.info("Player retrieved successfully");

        log.info("First we want to verify that player1 and player2 do not yet have any messages in their inbox");
        List<InternalMessage> player1Messages = relationshipService.findInternalMessages(player1.getApplicationUser().getId());
        Long player1MessageCount = relationshipService.findInternalMessageCount(player1.getApplicationUser().getId());
        assertEquals("player1 should not have any messages yet", 0L, player1MessageCount, 0);
        assertEquals("List size is incorrect", player1Messages.size(), player1MessageCount.intValue());

        List<InternalMessage> player2Messages = relationshipService.findInternalMessages(player2.getApplicationUser().getId());
        Long player2MessageCount = relationshipService.findInternalMessageCount(player2.getApplicationUser().getId());
        assertEquals("player2 should not have any messages yet", 0L, player2MessageCount, 0);
        assertEquals("List size is incorrect", player2Messages.size(), player2MessageCount.intValue());

        log.info("Now player1 sends a message to player2");
        InternalMessage im = new InternalMessage(player2, player1, "Test");
        relationshipService.persistInternalMessage(im);

        assertNotNull("IM should not be null", im);
        assertNotNull("IM id should not be null", im.getId());

        log.info("Now player2 should have a message in his inbox");
        player2Messages = relationshipService.findInternalMessages(player2.getApplicationUser().getId());
        player2MessageCount = relationshipService.findInternalMessageCount(player2.getApplicationUser().getId());
        assertEquals("player2 should have one message", 1L, player2MessageCount, 0);
        assertEquals("List size is incorrect", player2Messages.size(), player2MessageCount.intValue());

        log.info("Now we retrieve the individual message");
        im = relationshipService.findInternalMessage(player2Messages.get(0).getId());
        assertNotNull("InternalMessage should not be null", im);
        assertEquals("InternalMessages should not have been read yet", false, im.getViewed());

        log.info("Marking the message as read");
        im.setViewed(true);
        relationshipService.mergeInternalMessage(im);
        im = relationshipService.findInternalMessage(im.getId());
        assertNotNull("InternalMessage should not be null", im);
        assertEquals("InternalMessages should have been read yet", true, im.getViewed());

        log.info("Removing the message");
        relationshipService.removeInternalMessage(im);

        player2Messages = relationshipService.findInternalMessages(player2.getApplicationUser().getId());
        player2MessageCount = relationshipService.findInternalMessageCount(player2.getApplicationUser().getId());
        assertEquals("player2 should not have any messages yet", 0L, player2MessageCount, 0);
        assertEquals("List size is incorrect", player2Messages.size(), player2MessageCount.intValue());
        log.info("InternalMessages tested successfully");
    }
}
