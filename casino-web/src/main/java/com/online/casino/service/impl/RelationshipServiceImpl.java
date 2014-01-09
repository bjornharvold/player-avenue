/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service.impl;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.InternalMessage;
import com.online.casino.domain.entity.InternalSystemMessage;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.Relationship;
import com.online.casino.service.RelationshipService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 4/3/11
 * Time: 11:54 AM
 * Responsibility:
 */
@Service("relationshipService")
public class RelationshipServiceImpl implements RelationshipService {

    /**
     * Method description
     *
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     */
    @Override
    public void acceptFriendRequest(String requesterPlayerId, String requestedPlayerId) {
        Relationship.acceptFriendRequest(requesterPlayerId, requestedPlayerId);
    }

    /**
     * Method description
     *
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     */
    @Override
    public void createFriendRequest(String requesterPlayerId, String requestedPlayerId) {
        Relationship.createFriendRequest(requesterPlayerId, requestedPlayerId);
    }

    /**
     * Method description
     *
     *
     * @param id id
     *
     * @return Return value
     */
    @Override
    public InternalMessage findInternalMessage(String id) {
        return InternalMessage.findInternalMessage(id);
    }

    /**
     * Method description
     *
     *
     * @param applicationUserId applicationUserId
     *
     * @return Return value
     */
    @Override
    public Long findInternalMessageCount(String applicationUserId) {
        return InternalMessage.findInternalMessageCount(applicationUserId);
    }

    /**
     * Method description
     *
     *
     * @param applicationUserId applicationUserId
     *
     * @return Return value
     */
    @Override
    public List<InternalMessage> findInternalMessages(String applicationUserId) {
        return InternalMessage.findInternalMessages(applicationUserId);
    }

    /**
     * Method description
     *
     *
     * @param id id
     *
     * @return Return value
     */
    @Override
    public InternalSystemMessage findInternalSystemMessage(String id) {
        return InternalSystemMessage.findInternalSystemMessage(id);
    }

    /**
     * Method description
     *
     *
     * @param applicationUserId applicationUserId
     *
     * @return Return value
     */
    @Override
    public Long findInternalSystemMessageCount(String applicationUserId) {
        return InternalSystemMessage.findInternalSystemMessageCount(applicationUserId);
    }

    /**
     * Method description
     *
     *
     * @param applicationUserId applicationUserId
     *
     * @return Return value
     */
    @Override
    public List<InternalSystemMessage> findInternalSystemMessages(String applicationUserId) {
        return InternalSystemMessage.findInternalSystemMessages(applicationUserId);
    }

    /**
     * Method description
     *
     * @param playerId playerId
     * @param name     name
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_RELATIONSHIP')")
    @Override
    public Long findMyFriendRequestCount(String playerId, String name) {
        return Relationship.findMyFriendRequestCount(playerId, name);
    }

    /**
     * Method description
     *
     * @param playerId   playerId
     * @param name       name
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_RELATIONSHIP')")
    @Override
    public List<Player> findMyFriendRequests(String playerId, String name, Integer index, Integer maxResults) {
        return Relationship.findMyFriendRequests(playerId, name, index, maxResults);
    }

    /**
     * Method description
     *
     * @param playerId   playerId
     * @param name       name
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_RELATIONSHIP')")
    @Override
    public List<Player> findMyFriends(String playerId, String name, Integer index, Integer maxResults) {
        return Relationship.findMyFriends(playerId, name, index, maxResults);
    }

    /**
     * Method description
     *
     * @param playerId playerId
     * @param name     name
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_READ_RELATIONSHIP')")
    @Override
    public Long findMyFriendsCount(String playerId, String name) {
        return Relationship.findMyFriendsCount(playerId, name);
    }

    /**
     * Method description
     *
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     */
    @Override
    public void ignoreFriendRequest(String requesterPlayerId, String requestedPlayerId) {
        Relationship.ignoreFriendRequest(requesterPlayerId, requestedPlayerId);
    }

    /**
     * Method description
     *
     *
     * @param entity entity
     */
    @Override
    public void mergeInternalMessage(InternalMessage entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     *
     * @param entity entity
     */
    @Override
    public void mergeInternalSystemMessage(InternalSystemMessage entity) {
        entity.merge();
    }

    /**
     * Method description
     *
     *
     * @param entity entity
     */
    @Override
    public void persistInternalMessage(InternalMessage entity) {
        entity.persist();
    }

    /**
     * Method description
     *
     *
     * @param entity entity
     */
    @Override
    public void persistInternalSystemMessage(InternalSystemMessage entity) {
        entity.persist();
    }

    /**
     * Method description
     *
     * @param entity entity
     *
     * @return Return value
     */
    @PreAuthorize("hasRole('RIGHT_WRITE_RELATIONSHIP')")
    @Override
    public Relationship persistRelationship(Relationship entity) {
        entity.persist();

        return entity;
    }

    /**
     * Method description
     *
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     */
    @Override
    public void removeFriendRequest(String requesterPlayerId, String requestedPlayerId) {
        Relationship.removeFriendRequest(requesterPlayerId, requestedPlayerId);
    }

    /**
     * Method description
     *
     *
     * @param entity entity
     */
    @Override
    public void removeInternalMessage(InternalMessage entity) {
        entity.remove();
    }

    /**
     * Method description
     *
     *
     * @param entity entity
     */
    @Override
    public void removeInternalSystemMessage(InternalSystemMessage entity) {
        entity.remove();
    }
}
