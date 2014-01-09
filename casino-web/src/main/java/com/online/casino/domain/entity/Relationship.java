/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.RelationshipStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Relationship entity takes care of our social networking functions
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/11/03
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class Relationship extends AbstractEntity implements Serializable {

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(Relationship.class);

    /**
     * Field description
     */
    private static final long serialVersionUID = 5958105410547634094L;

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @ManyToOne(targetEntity = Player.class, optional = false)
    private Player requested;

    /**
     * Field description
     */
    @ManyToOne(targetEntity = Player.class, optional = false)
    private Player requester;

    /**
     * Field description
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private RelationshipStatus status;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     */
    public Relationship() {
    }

    private Relationship(String id) {
        this.id = id;
    }

    /**
     * Constructs ...
     *
     * @param requested requested
     * @param requester requester
     * @param status    status
     */
    public Relationship(Player requested, Player requester, RelationshipStatus status) {
        this.requested = requested;
        this.requester = requester;
        this.status = status;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     */
    public static void acceptFriendRequest(String requesterPlayerId, String requestedPlayerId) {
        if (StringUtils.isBlank(requesterPlayerId)) {
            throw new IllegalArgumentException("requesterPlayerId cannot be null");
        }
        if (StringUtils.isBlank(requestedPlayerId)) {
            throw new IllegalArgumentException("requestedPlayerId cannot be null");
        }

        Relationship relationship = findRelationshipRequest(requesterPlayerId, requestedPlayerId);

        if ((relationship != null) && !relationship.getStatus().equals(RelationshipStatus.ACCEPTED)) {
            relationship.setStatus(RelationshipStatus.ACCEPTED);
            relationship.merge();
        } else {
            log.warn("Cannot accept friend request. Reason: Status could already be ACCEPTED or no entry found for "
                    + "requester: " + requesterPlayerId + " and requested: " + requestedPlayerId);
        }
    }

    /**
     * Method description
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     */
    public static void createFriendRequest(String requesterPlayerId, String requestedPlayerId) {
        if (StringUtils.isBlank(requesterPlayerId)) {
            throw new IllegalArgumentException("requesterPlayerId cannot be null");
        }
        if (StringUtils.isBlank(requestedPlayerId)) {
            throw new IllegalArgumentException("requestedPlayerId cannot be null");
        }

        Relationship relationship = findRelationshipRequest(requesterPlayerId, requestedPlayerId);

        if (relationship != null) {
            throw new IllegalStateException("error.relationship.entry.exists");
        }

        Player requesterP = Player.findPlayer(requesterPlayerId);
        Player requestedP = Player.findPlayer(requestedPlayerId);

        if (requesterP == null || requestedP == null) {
            throw new IllegalStateException("error.relationship.missing.parties");
        }

        relationship = new Relationship(requestedP, requesterP, RelationshipStatus.AWAITING_RESPONSE);
        relationship.persist();
    }

    /**
     * Method description
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new Relationship().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                    "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     * @return Return value
     */
    public static Relationship findFriend(String requesterPlayerId, String requestedPlayerId) {
        Relationship result = null;

        if (StringUtils.isBlank(requesterPlayerId)) {
            throw new IllegalArgumentException("requesterPlayerId cannot be null");
        }
        if (StringUtils.isBlank(requestedPlayerId)) {
            throw new IllegalArgumentException("requestedPlayerId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Relationship> criteria = builder.createQuery(Relationship.class);

            // from
            Root<Relationship> root = criteria.from(Relationship.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // joining with the player table
            Join<Relationship, Player> requesterJoin = root.join(Relationship_.requester);
            Join<Relationship, Player> requestedJoin = root.join(Relationship_.requested);

            // conditional statement - find all records where I have either requested or accepted a friend's request
            criteria.where(builder.equal(root.get(Relationship_.status), RelationshipStatus.ACCEPTED),
                    builder.equal(requesterJoin.get(Player_.id), requesterPlayerId),
                    builder.equal(requestedJoin.get(Player_.id), requestedPlayerId));

            // this should throw an exception - otherwise we have an illegal state on our hands
            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param playerId playerId
     * @param nickname
     * @return Return value
     */
    public static Long findMyFriendsCount(String playerId, String nickname) {
        Long result = 0L;

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // from
            Root<Relationship> root = criteria.from(Relationship.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(builder.count(root));

            // joining with the player table
            Join<Relationship, Player> requesterJoin = root.join(Relationship_.requester);
            Join<Relationship, Player> requestedJoin = root.join(Relationship_.requested);

            Predicate equalStatus = builder.equal(root.get(Relationship_.status), RelationshipStatus.ACCEPTED);
            Predicate equalPlayerIds = builder.or(
                    builder.equal(requesterJoin.get(Player_.id), playerId),
                    builder.equal(requestedJoin.get(Player_.id), playerId)
            );
            Predicate likeNicknames = null;

            // conditional statement - find all records where I have either requested or accepted a friend's request
            if (StringUtils.isNotBlank(nickname)) {
                likeNicknames = builder.or(
                        builder.like(builder.lower(requesterJoin.get(Player_.nickname)), nickname.toLowerCase()),
                        builder.like(builder.lower(requestedJoin.get(Player_.nickname)), nickname.toLowerCase())
                );
            }

            if (likeNicknames != null) {
                criteria.where(equalStatus, equalPlayerIds, likeNicknames);
            } else {
                criteria.where(equalStatus, equalPlayerIds);
            }

            result = entityManager().createQuery(criteria).getSingleResult();

        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param playerId playerId
     * @return Return value
     */
    public static List<Player> findMyFriendRequests(String playerId, String nickname, Integer index, Integer maxResults) {
        return findRelationshipsByPlayerAndStatus(playerId, RelationshipStatus.AWAITING_RESPONSE, nickname, index, maxResults);
    }

    /**
     * Method description
     *
     * @param playerId playerId
     * @return Return value
     */
    public static Long findMyFriendRequestCount(String playerId, String nickname) {
        return findRelationshipCountByPlayerAndStatus(playerId, RelationshipStatus.AWAITING_RESPONSE, nickname);
    }

    /**
     * Returns a list of active relationships
     *
     * @param playerId   playerId
     * @param index      index
     * @param maxResults maxResults
     * @return Return value
     */
    public static List<Player> findMyFriends(String playerId, String nickname, Integer index, Integer maxResults) {
        List<Player> result = null;

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            List<Relationship> list = null;
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Relationship> criteria = builder.createQuery(Relationship.class);

            // from
            Root<Relationship> root = criteria.from(Relationship.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // joining with the player table
            Join<Relationship, Player> requesterJoin = root.join(Relationship_.requester);
            Join<Relationship, Player> requestedJoin = root.join(Relationship_.requested);

            Predicate equalStatus = builder.equal(root.get(Relationship_.status), RelationshipStatus.ACCEPTED);
            Predicate likeNicknames = null;
            Predicate equalPlayerIds = builder.or(
                    builder.equal(requesterJoin.get(Player_.id), playerId),
                    builder.equal(requestedJoin.get(Player_.id), playerId)
            );

            // conditional statement - find all records where I have either requested or accepted a friend's request
            if (StringUtils.isNotBlank(nickname)) {
                likeNicknames = builder.or(
                        builder.like(builder.lower(requesterJoin.get(Player_.nickname)), nickname.toLowerCase() + '%'),
                        builder.like(builder.lower(requestedJoin.get(Player_.nickname)), nickname.toLowerCase() + '%')
                );
            }

            if (likeNicknames != null) {
                criteria.where(equalStatus, equalPlayerIds, likeNicknames);
            } else {
                criteria.where(equalStatus, equalPlayerIds);
            }

            TypedQuery<Relationship> query = entityManager().createQuery(criteria);

            if ((index != null) && (maxResults != null)) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }

            list = query.getResultList();

            if (list != null) {
                result = new ArrayList<Player>(list.size());

                // we want to get the perso the player has a relationship with so the player entity which
                // does not have the playerId
                for (Relationship relationship : list) {
                    if (StringUtils.equals(relationship.getRequested().getId(), playerId)) {
                        result.add(relationship.getRequester());
                    } else {
                        result.add(relationship.getRequested());
                    }
                }
            }
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param id id
     * @return
     */
    public static Relationship findRelationship(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Relationship");
        }

        return entityManager().find(Relationship.class, id);
    }

    /**
     * Method description
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     * @return Return value
     */
    public static Relationship findRelationshipRequest(String requesterPlayerId, String requestedPlayerId) {
        Relationship result = null;

        if (StringUtils.isBlank(requesterPlayerId)) {
            throw new IllegalArgumentException("requesterPlayerId cannot be null");
        }
        if (StringUtils.isBlank(requestedPlayerId)) {
            throw new IllegalArgumentException("requestedPlayerId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Relationship> criteria = builder.createQuery(Relationship.class);

            // from
            Root<Relationship> root = criteria.from(Relationship.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // joining with the player table
            Join<Relationship, Player> requesterJoin = root.join(Relationship_.requester);
            Join<Relationship, Player> requestedJoin = root.join(Relationship_.requested);

            // conditional statement - find all records where I have either requested or accepted a friend's request
            criteria.where(builder.equal(requesterJoin.get(Player_.id), requesterPlayerId),
                    builder.equal(requestedJoin.get(Player_.id), requestedPlayerId));

            // this should throw an exception - otherwise we have an illegal state on our hands
            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     */
    @Transactional
    public void flush() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        this.entityManager.flush();
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return Return value
     */
    public Player getRequested() {
        return requested;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Player getRequester() {
        return requester;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public RelationshipStatus getStatus() {
        return status;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Set the status on the relationship entity to ignored
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     */
    public static void ignoreFriendRequest(String requesterPlayerId, String requestedPlayerId) {
        if (StringUtils.isBlank(requesterPlayerId)) {
            throw new IllegalArgumentException("requesterPlayerId cannot be null");
        }
        if (StringUtils.isBlank(requestedPlayerId)) {
            throw new IllegalArgumentException("requestedPlayerId cannot be null");
        }

        Relationship relationship = findFriend(requesterPlayerId, requestedPlayerId);

        if (relationship != null) {
            throw new IllegalStateException("Cannot ignore this friend request. It has already been approved.");
        }

        relationship = findRelationshipRequest(requesterPlayerId, requestedPlayerId);

        if (relationship == null) {
            throw new IllegalStateException("Cannot ignore this friend request. It does not exist.");
        }

        relationship.setStatus(RelationshipStatus.IGNORED);
        relationship.merge();
    }

    /**
     * Method description
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        Relationship merged = this.entityManager.merge(this);

        this.entityManager.flush();
        this.id = merged.getId();
    }

    /**
     * Method description
     */
    @Transactional
    public void persist() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        this.entityManager.persist(this);
    }

    /**
     * Method description
     */
    @Transactional
    public void remove() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Relationship attached = this.entityManager.find(Relationship.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new Relationship(id).remove();
    }

    /**
     * Method description
     *
     * @param requesterPlayerId requesterPlayerId
     * @param requestedPlayerId requestedPlayerId
     */
    public static void removeFriendRequest(String requesterPlayerId, String requestedPlayerId) {
        if (StringUtils.isBlank(requesterPlayerId)) {
            throw new IllegalArgumentException("requesterPlayerId cannot be null");
        }
        if (StringUtils.isBlank(requestedPlayerId)) {
            throw new IllegalArgumentException("requestedPlayerId cannot be null");
        }

        Relationship relationship = findRelationshipRequest(requesterPlayerId, requestedPlayerId);

        if (relationship == null) {
            throw new IllegalStateException("Cannot remove this friend request. It does not exist.");
        }

        relationship.remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param requested requested
     */
    public void setRequested(Player requested) {
        this.requested = requested;
    }

    /**
     * Method description
     *
     * @param requester requester
     */
    public void setRequester(Player requester) {
        this.requester = requester;
    }

    /**
     * Method description
     *
     * @param status status
     */
    public void setStatus(RelationshipStatus status) {
        this.status = status;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param playerId   playerId
     * @param status     status
     * @param name
     * @param index
     * @param maxResults
     * @return Return value
     */
    private static List<Player> findRelationshipsByPlayerAndStatus(String playerId, RelationshipStatus status, String name, Integer index, Integer maxResults) {
        List<Player> result = null;

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }

        try {
            List<Relationship> list = null;
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Relationship> criteria = builder.createQuery(Relationship.class);

            // from
            Root<Relationship> root = criteria.from(Relationship.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // joining with the player table
            Join<Relationship, Player> requestedJoin = root.join(Relationship_.requested);

            Predicate equalStatus = builder.equal(root.get(Relationship_.status), status);
            Predicate equalPlayerId = builder.equal(requestedJoin.get(Player_.id), playerId);
            Predicate likeNickname = null;

            if (StringUtils.isNotBlank(name)) {
                // conditional statement - find all records where I have either requested or accepted a friend's request
                likeNickname = builder.like(builder.lower(requestedJoin.get(Player_.nickname)), name.toLowerCase() + '%');
            }

            if (likeNickname != null) {
                criteria.where(equalStatus, equalPlayerId, likeNickname);
            } else {
                criteria.where(equalStatus, equalPlayerId);
            }

            TypedQuery<Relationship> query = entityManager().createQuery(criteria);

            if (index != null && maxResults != null) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }

            list = query.getResultList();

            if (list != null) {
                result = new ArrayList<Player>(list.size());

                // we want to get the perso the player has a relationship with so the player entity which
                // does not have the playerId
                for (Relationship relationship : list) {
                    if (StringUtils.equals(relationship.getRequested().getId(), playerId)) {
                        result.add(relationship.getRequester());
                    } else {
                        result.add(relationship.getRequested());
                    }
                }
            }
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    private static Long findRelationshipCountByPlayerAndStatus(String playerId, RelationshipStatus status, String name) {
        Long result = 0L;

        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }

        try {
            List<Relationship> list = null;
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // from
            Root<Relationship> root = criteria.from(Relationship.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(builder.count(root));

            // joining with the player table
            Join<Relationship, Player> requestedJoin = root.join(Relationship_.requested);

            Predicate equalStatus = builder.equal(root.get(Relationship_.status), status);
            Predicate equalPlayerId = builder.equal(requestedJoin.get(Player_.id), playerId);
            Predicate likeNickname = null;

            if (StringUtils.isNotBlank(name)) {
                // conditional statement - find all records where I have either requested or accepted a friend's request
                likeNickname = builder.like(builder.lower(requestedJoin.get(Player_.nickname)), name.toLowerCase() + '%');
            }

            if (likeNickname != null) {
                criteria.where(equalStatus, equalPlayerId, likeNickname);
            } else {
                criteria.where(equalStatus, equalPlayerId);
            }

            result = entityManager().createQuery(criteria).getSingleResult();

        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Relationship {");
        sb.append(super.toString());
        sb.append(", requested=").append(requested.getId());
        sb.append(", requester=").append(requester.getId());
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    public static Relationship instantiate(Player requested, Player requester, RelationshipStatus status) {
        return new Relationship(requested, requester, status);
    }
}
