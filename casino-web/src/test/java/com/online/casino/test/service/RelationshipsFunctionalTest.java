/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.entity.*;
import com.online.casino.service.RelationshipService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility:
 */
public class RelationshipsFunctionalTest extends AbstractFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(RelationshipsFunctionalTest.class);

    @Autowired
    private RelationshipService relationshipService;

    @Test
    public void testRelationships() {
        log.info("Retrieving 4 players and testing relationship creation");

        Player player1 = administrationService.findPlayerByNickname("player7a");
        Player player2 = administrationService.findPlayerByNickname("player8a");
        Player player3 = administrationService.findPlayerByNickname("player9a");
        Player player4 = administrationService.findPlayerByNickname("player10a");
        assertNotNull("player1 is null", player1);
        assertNotNull("player2 is null", player2);
        assertNotNull("player3 is null", player3);
        assertNotNull("player4 is null", player4);
        
        log.info("Player retrieved successfully");
        
        log.info("Verifying that they don't have any friends yet");
        List<Player> player1Friends = relationshipService.findMyFriends(player1.getId(), null, null, null);
        Long player1FriendCount = relationshipService.findMyFriendsCount(player1.getId(), null);
        assertEquals("Player1 should not have any friends yet", 0L, player1FriendCount, 0);
        assertEquals("List size is incorrect", player1Friends.size(), player1FriendCount.intValue());
        
        List<Player> player2Friends = relationshipService.findMyFriends(player2.getId(), null, null, null);
        Long player2FriendCount = relationshipService.findMyFriendsCount(player2.getId(), null);
        assertEquals("player2 should not have any friends yet", 0L, player2FriendCount, 0);
        assertEquals("List size is incorrect", player2Friends.size(), player2FriendCount.intValue());
        
        List<Player> player3Friends = relationshipService.findMyFriends(player3.getId(), null, null, null);
        Long player3FriendCount = relationshipService.findMyFriendsCount(player3.getId(), null);
        assertEquals("player3 should not have any friends yet", 0L, player3FriendCount, 0);
        assertEquals("List size is incorrect", player3Friends.size(), player3FriendCount.intValue());
        
        List<Player> player4Friends = relationshipService.findMyFriends(player4.getId(), null, null, null);
        Long player4FriendCount = relationshipService.findMyFriendsCount(player4.getId(), null);
        assertEquals("player4 should not have any friends yet", 0L, player4FriendCount, 0);
        assertEquals("List size is incorrect", player4Friends.size(), player4FriendCount.intValue());
        
        log.info("Creating relationship between player 1 and player 2");
        relationshipService.createFriendRequest(player1.getId(), player2.getId());
        
        log.info("Creating relationship between player 1 and player 3");
        relationshipService.createFriendRequest(player1.getId(), player3.getId());
        
        log.info("Creating relationship between player 1 and player 4");
        relationshipService.createFriendRequest(player1.getId(), player4.getId());

        log.info("Player 2, 3 and 4 should now have a friend request in their list");
        
        List<Player> player1Requests = relationshipService.findMyFriendRequests(player1.getId(), null, null, null);
        Long player1RequestCount = relationshipService.findMyFriendRequestCount(player1.getId(), null);
        assertEquals("Friend request count is wrong", 0L, player1RequestCount, 0);
        assertEquals("List and count should be equal", player1Requests.size(), player1RequestCount.intValue());
        
        List<Player> player2Requests = relationshipService.findMyFriendRequests(player2.getId(), null, null, null);
        Long player2RequestCount = relationshipService.findMyFriendRequestCount(player2.getId(), null);
        assertEquals("Friend request count is wrong", 1L, player2RequestCount, 1);
        assertEquals("List and count should be equal", player2Requests.size(), player2RequestCount.intValue());
        
        List<Player> player3Requests = relationshipService.findMyFriendRequests(player3.getId(), null, null, null);
        Long player3RequestCount = relationshipService.findMyFriendRequestCount(player3.getId(), null);
        assertEquals("Friend request count is wrong", 1L, player3RequestCount, 1);
        assertEquals("List and count should be equal", player3Requests.size(), player3RequestCount.intValue());
        
        List<Player> player4Requests = relationshipService.findMyFriendRequests(player4.getId(), null, null, null);
        Long player4RequestCount = relationshipService.findMyFriendRequestCount(player4.getId(), null);
        assertEquals("Friend request count is wrong", 1L, player4RequestCount, 1);
        assertEquals("List and count should be equal", player4Requests.size(), player4RequestCount.intValue());
        
        log.info("All players have correct friend requests");
        
        log.info("Now player2 will accept the request");
        relationshipService.acceptFriendRequest(player1.getId(), player2.getId());
        
        log.info("Now player3 will ignore the request");
        relationshipService.ignoreFriendRequest(player1.getId(), player3.getId());
        
        log.info("Now player1 will remove the request to player4");
        relationshipService.removeFriendRequest(player1.getId(), player4.getId());
        
        log.info("All relationship actions are complete.");
        
        log.info("Player1 should have 1 friend");
        player1Friends = relationshipService.findMyFriends(player1.getId(), null, null, null);
        player1FriendCount = relationshipService.findMyFriendsCount(player1.getId(), null);
        assertEquals("Player1 should have friends", 1L, player1FriendCount, 0);
        assertEquals("List size is incorrect", player1Friends.size(), player1FriendCount.intValue());
        
        log.info("Player2 should have 1 friend");
        player2Friends = relationshipService.findMyFriends(player2.getId(), null, null, null);
        player2FriendCount = relationshipService.findMyFriendsCount(player2.getId(), null);
        assertEquals("Player2 should have friends", 1L, player2FriendCount, 0);
        assertEquals("List size is incorrect", player2Friends.size(), player2FriendCount.intValue());

        log.info("Player3 should not have any friends");
        player3Friends = relationshipService.findMyFriends(player3.getId(), null, null, null);
        player3FriendCount = relationshipService.findMyFriendsCount(player3.getId(), null);
        assertEquals("player3 should not have any friends yet", 0L, player3FriendCount, 0);
        assertEquals("List size is incorrect", player3Friends.size(), player3FriendCount.intValue());

        log.info("Player4 should not have any friends");
        player4Friends = relationshipService.findMyFriends(player4.getId(), null, null, null);
        player4FriendCount = relationshipService.findMyFriendsCount(player4.getId(), null);
        assertEquals("player4 should not have any friends yet", 0L, player4FriendCount, 0);
        assertEquals("List size is incorrect", player4Friends.size(), player4FriendCount.intValue());

        log.info("Relationship management tested successfully");
    }
}
