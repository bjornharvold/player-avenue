/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service;

import com.online.casino.domain.entity.InternalMessage;
import com.online.casino.domain.entity.InternalSystemMessage;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.Relationship;

import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 4/3/11
 * Time: 11:55 AM
 * Responsibility:
 */
public interface RelationshipService {
    List<Player> findMyFriends(String playerId, String name, Integer index, Integer maxResults);
    Long findMyFriendsCount(String playerId, String name);
    List<Player> findMyFriendRequests(String playerId, String name, Integer index, Integer maxResults);
    Long findMyFriendRequestCount(String playerId, String name);
    Relationship persistRelationship(Relationship relationship);

    List<InternalMessage> findInternalMessages(String applicationUserId);
    Long findInternalMessageCount(String applicationUserId);
    void persistInternalMessage(InternalMessage entity);
    void mergeInternalMessage(InternalMessage entity);
    InternalMessage findInternalMessage(String id);
    void removeInternalMessage(InternalMessage entity);

    List<InternalSystemMessage> findInternalSystemMessages(String applicationUserId);
    Long findInternalSystemMessageCount(String applicationUserId);
    void persistInternalSystemMessage(InternalSystemMessage entity);
    InternalSystemMessage findInternalSystemMessage(String id);
    void mergeInternalSystemMessage(InternalSystemMessage entity);
    void removeInternalSystemMessage(InternalSystemMessage entity);

    void createFriendRequest(String requesterPlayerId, String requestedPlayerId);
    void acceptFriendRequest(String requesterPlayerId, String requestedPlayerId);
    void ignoreFriendRequest(String requesterPlayerId, String requestedPlayerId);
    void removeFriendRequest(String requesterPlayerId, String requestedPlayerId);
}
