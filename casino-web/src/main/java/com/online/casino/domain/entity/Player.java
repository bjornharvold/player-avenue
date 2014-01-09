package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.dto.AutoComplete;
import com.online.casino.domain.enums.PlayerStatus;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/11/02
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class Player extends AbstractEntity implements Serializable {

    /**
     * Field description
     */
    private static final long serialVersionUID = -8658833768902505843L;

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private PlayerStatus status = PlayerStatus.ACTIVE;

    /**
     * Field description
     */
    @Column
    private String avatarUrl;

    /**
     * Field description
     */
    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String nickname;

    /**
     * Field description
     */
    @ManyToOne(optional = false, targetEntity = ApplicationUser.class)
    @JoinColumn
    private ApplicationUser applicationUser;

    public Player() {
    }

    private Player(String id) {
        this.id = id;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param nickname   nickname
     * @param index      index
     * @param maxResults maxResults
     * @return
     */
    public static List<AutoComplete> autoCompletePlayersByNickname(String nickname, Integer index, Integer maxResults) {
        List<AutoComplete> result = null;

        if (StringUtils.isBlank(nickname)) {
            throw new IllegalArgumentException("nickname cannot be null");
        }

        // grab criteria builder
        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();
            CriteriaQuery<AutoComplete> criteria = builder.createQuery(AutoComplete.class);
            Root<Player> root = criteria.from(Player.class);
            criteria.select(
                    builder.construct(
                            AutoComplete.class,
                            root.get(Player_.id),
                            root.get(Player_.nickname)
                    )
            );

            criteria.where(builder.like(root.get(Player_.nickname), nickname + "%"));
            TypedQuery<AutoComplete> query = entityManager().createQuery(criteria);

            if (index != null && maxResults != null) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }

            result = query.getResultList();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;

    }

    /**
     * Method description
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new Player().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                    "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     * @param id id
     * @return
     */
    public static Player findPlayer(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Player");
        }

        return entityManager().find(Player.class, id);
    }

    /**
     * Method description
     *
     * @param nickname nickname
     * @return
     */
    public static Player findPlayerByNickname(String nickname) {
        Player result = null;

        if (StringUtils.isBlank(nickname)) {
            throw new IllegalArgumentException("nickname cannot be null");
        }

        try {
            // grab criteria builder
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // create a criteria for Player entity
            CriteriaQuery<Player> criteria = builder.createQuery(Player.class);

            // this is the query root
            Root<Player> root = criteria.from(Player.class);

            criteria.where(builder.equal(builder.lower(root.get(Player_.nickname)), nickname.toLowerCase()));
            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param userId userId
     * @param name   name
     * @return
     */
    public static Long findPlayerCount(String userId, String name) {
        Long result = 0L;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        try {
            // grab criteria builder
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // create a criteria for Player entity
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // this is the query root
            Root<Player> root = criteria.from(Player.class);

            criteria.select(builder.count(root));

            Join<Player, ApplicationUser> playerUser = root.join(Player_.applicationUser);
            Predicate equalUserId = builder.equal(playerUser.get(ApplicationUser_.id), userId);

            if (StringUtils.isNotBlank(name)) {
                Predicate likeNickname = builder.like(builder.lower(root.get(Player_.nickname)), name.toLowerCase() + '%');
                criteria.where(likeNickname, equalUserId);
            } else {
                criteria.where(equalUserId);
            }

            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param userId     userId
     * @param name       name
     * @param index      index
     * @param maxResults maxResults
     * @return
     */
    public static List<Player> findPlayers(String userId, String name, Integer index, Integer maxResults) {
        List<Player> result = null;

        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        try {
            // grab criteria builder
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // create a criteria for Player entity
            CriteriaQuery<Player> criteria = builder.createQuery(Player.class);

            // this is the query root
            Root<Player> root = criteria.from(Player.class);

            Join<Player, ApplicationUser> playerUser = root.join(Player_.applicationUser);
            Predicate equalUserId = builder.equal(playerUser.get(ApplicationUser_.id), userId);

            if (StringUtils.isNotBlank(name)) {
                Predicate likeNickname = builder.like(builder.lower(root.get(Player_.nickname)), name.toLowerCase() + '%');
                criteria.where(likeNickname, equalUserId);
            } else {
                criteria.where(equalUserId);
            }

            Query q = entityManager().createQuery(criteria);

            if ((index != null) && (maxResults != null)) {
                q.setFirstResult(index * maxResults);
                q.setMaxResults(maxResults);
            }

            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Method description
     *
     * @param nickname nickname
     * @return
     */
    public static List<Player> findPlayersByNickname(String nickname) {
        List<Player> result = null;

        if (StringUtils.isBlank(nickname)) {
            throw new IllegalArgumentException("nickname cannot be null");
        }

        try {

            // grab criteria builder
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // create a criteria for Player entity
            CriteriaQuery<Player> criteria = builder.createQuery(Player.class);

            // this is the query root
            Root<Player> root = criteria.from(Player.class);

            // we want to select what we are returning
            criteria.select(root);

            // conditional statement - player needs to have a nickname like specified
            criteria.where(builder.like(builder.lower(root.get(Player_.nickname)), nickname + "%"));

            result = entityManager().createQuery(criteria).getResultList();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Returns a list of all players who are NOT playing the particular game
     *
     * @param nickname   player nickname
     * @param index      index
     * @param maxResults maxResults
     * @return Returns a list of player entities
     */
    public static List<Player> findPlayersByNickname(String nickname, Integer index, Integer maxResults) {
        List<Player> result = null;

        if (StringUtils.isBlank(nickname)) {
            throw new IllegalArgumentException("nickname cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Player> criteria = builder.createQuery(Player.class);

            // from
            Root<Player> root = criteria.from(Player.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // conditional statement - player needs to have a nickname like specified
            criteria.where(builder.like(builder.lower(root.get(Player_.nickname)), nickname + "%"));

            // create the query
            TypedQuery<Player> query = entityManager().createQuery(criteria);

            if (index != null && maxResults != null) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }

            result = query.getResultList();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    /**
     * Same as above only returns the count
     *
     * @param nickname nickname
     * @return
     */
    public static Long findPlayersByNicknameCount(String nickname) {
        Long result = 0L;

        if (StringUtils.isBlank(nickname)) {
            throw new IllegalArgumentException("nickname cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

            // from
            Root<Player> root = criteria.from(Player.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(builder.count(root));

            // conditional statement - player needs to have a nickname like specified
            criteria.where(builder.like(builder.lower(root.get(Player_.nickname)), nickname + "%"));

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
     * @return
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Method description
     *
     * @return
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Method description
     *
     * @return
     */
    public PlayerStatus getStatus() {
        return status;
    }

    /**
     * Method description
     *
     * @return
     */
    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        Player merged = this.entityManager.merge(this);

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
            Player attached = this.entityManager.find(Player.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new Player(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param email email
     */
    public void setAvatarUrl(String email) {
        this.avatarUrl = email;
    }

    /**
     * Method description
     *
     * @param nickname nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Method description
     *
     * @param status status
     */
    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    /**
     * Method description
     *
     * @param applicationUser applicationUser
     */
    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Player [");
        builder.append(super.toString());

        if (avatarUrl != null) {
            builder.append("avatarUrl=");
            builder.append(avatarUrl);
            builder.append(", ");
        }

        if (nickname != null) {
            builder.append("nickname=");
            builder.append(nickname);
            builder.append(", ");
        }

        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }

        if (applicationUser != null) {
            builder.append("applicationUser=");
            builder.append(applicationUser.getId());
        }

        builder.append("]");

        return builder.toString();
    }
}
