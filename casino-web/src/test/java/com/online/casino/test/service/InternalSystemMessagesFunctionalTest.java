/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.InternalSystemMessage;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.enums.InternalSystemMessageSender;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.RelationshipService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility:
 */
public class InternalSystemMessagesFunctionalTest extends AbstractFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(InternalSystemMessagesFunctionalTest.class);

    @Autowired
    private RelationshipService relationshipService;
    
    @Test
    public void testInternalSystemMessages() {
        log.info("Sending messages from a system admin to a player");

        Player player1 = administrationService.findPlayerByNickname("player10a");
        assertNotNull("player4 is null", player1);
        
        log.info("Player retrieved successfully");
        
        log.info("First we want to verify that player1 and player1 do not yet have any messages in their inbox");
        List<InternalSystemMessage> player1Messages = relationshipService.findInternalSystemMessages(player1.getApplicationUser().getId());
        Long player1MessageCount = relationshipService.findInternalSystemMessageCount(player1.getApplicationUser().getId());
        assertEquals("player1 should not have any messages yet", 0L, player1MessageCount, 0);
        assertEquals("List size is incorrect", player1Messages.size(), player1MessageCount.intValue());
        
        log.info("Now player1 sends a message to player1");
        InternalSystemMessage im = new InternalSystemMessage(player1.getApplicationUser(), InternalSystemMessageSender.SYSTEM_ADMIN, "Test");
        relationshipService.persistInternalSystemMessage(im);
        assertNotNull("IM should not be null", im);
        assertNotNull("IM id should not be null", im.getId());

        log.info("Now player1 should have a message in his inbox");
        player1Messages = relationshipService.findInternalSystemMessages(player1.getApplicationUser().getId());
        player1MessageCount = relationshipService.findInternalSystemMessageCount(player1.getApplicationUser().getId());
        assertEquals("player1 should not have any messages yet", 1L, player1MessageCount, 0);
        assertEquals("List size is incorrect", player1Messages.size(), player1MessageCount.intValue());

        log.info("Now we retrieve the individual message");
        im = relationshipService.findInternalSystemMessage(player1Messages.get(0).getId());
        assertNotNull("InternalSystemMessage should not be null", im);
        assertEquals("InternalSystemMessages should not have been read yet", false, im.getViewed());

        log.info("Marking the message as read");
        im.setViewed(true);
        relationshipService.mergeInternalSystemMessage(im);
        im = relationshipService.findInternalSystemMessage(im.getId());
        assertNotNull("InternalSystemMessage should not be null", im);
        assertEquals("InternalSystemMessages should have been read yet", true, im.getViewed());

        log.info("Removing the message");
        relationshipService.removeInternalSystemMessage(im);

        player1Messages = relationshipService.findInternalSystemMessages(player1.getApplicationUser().getId());
        player1MessageCount = relationshipService.findInternalSystemMessageCount(player1.getApplicationUser().getId());
        assertEquals("player1 should not have any messages yet", 0L, player1MessageCount, 0);
        assertEquals("List size is incorrect", player1Messages.size(), player1MessageCount.intValue());
        log.info("InternalSystemMessages tested successfully");
    }
}
