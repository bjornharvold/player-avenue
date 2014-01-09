package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/10/30
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class GameObserver extends AbstractEntity implements Serializable {

    /**
     * Field description
     */
    private static final long serialVersionUID = 8513123269434475777L;

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @NotNull
    @Column(nullable = false)
    private String playerId;

    /**
     * Field description
     */
    @NotNull
    @Column(nullable = false)
    private String pokergameId;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     */
    public GameObserver() {
    }

    private GameObserver(String id) {
        this.id = id;
    }

    /**
     * Constructs ...
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     */
    public GameObserver(String pokergameId, String playerId) {
        this.pokergameId = pokergameId;
        this.playerId = playerId;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new GameObserver().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                    "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param playerId    playerId
     * @return
     */
    public static GameObserver findGameObserver(String pokergameId, String playerId) {
        GameObserver result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }
        if (StringUtils.isBlank(playerId)) {
            throw new IllegalArgumentException("playerId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();

            // what to return
            CriteriaQuery<GameObserver> criteria = builder.createQuery(GameObserver.class);

            // from
            Root<GameObserver> root = criteria.from(GameObserver.class);

            // what to select from root (needs to be the same value as what to return)
            criteria.select(root);

            // conditional statement
            criteria.where(builder.equal(root.get(GameObserver_.playerId), playerId),
                    builder.equal(root.get(GameObserver_.pokergameId), pokergameId));
            result = entityManager().createQuery(criteria).getSingleResult();

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
    public static GameObserver findGameObserver(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of GameObserver");
        }

        return entityManager().find(GameObserver.class, id);
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @return
     */
    public static Long findGameObserverCount(String pokergameId) {
        Long result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q =
                    entityManager().createQuery("select count(g) from GameObserver as g where g.pokergameId = :pokergameId");

            q.setParameter("pokergameId", pokergameId);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return (result == null)
                ? 0L
                : result;
    }

    /**
     * Method description
     *
     * @param pokergameId pokergameId
     * @param index       index
     * @param maxResults  maxResults
     * @return
     */
    public static List<GameObserver> findGameObservers(String pokergameId, Integer index, Integer maxResults) {
        List<GameObserver> result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select g from GameObserver as g where g.pokergameId = :pokergameId");

            q.setParameter("pokergameId", pokergameId);

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
     */
    @Transactional
    public void flush() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        this.entityManager.flush();
    }

    //~--- get methods --------------------------------------------------------


    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        GameObserver merged = this.entityManager.merge(this);

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
            GameObserver attached = this.entityManager.find(GameObserver.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new GameObserver(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPokergameId() {
        return pokergameId;
    }

    public void setPokergameId(String pokergameId) {
        this.pokergameId = pokergameId;
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

        builder.append("GameObserver [");
        builder.append(super.toString());

        if (pokergameId != null) {
            builder.append("pokergameId=");
            builder.append(pokergameId);
            builder.append(", ");
        }

        if (playerId != null) {
            builder.append("playerId=");
            builder.append(playerId);
        }

        builder.append("]");

        return builder.toString();
    }
}
