package com.online.casino.domain.entity;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.GameStatus;
import com.online.casino.domain.enums.GameType;
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
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 10/10/23
 * @author         Bjorn Harvold
 */
@Entity
@Configurable
@SuppressWarnings("unchecked")
public class PokerGame extends AbstractEntity implements Serializable {

    /** Field description */
    private static final long serialVersionUID = -7396291521384387801L;

    //~--- fields -------------------------------------------------------------

    /** Field description */
    @NotNull
    @Column(nullable = false)
    private Boolean autoGenerated;

    /** Field description */
    @Size(min = 1, max = 40)
    @NotEmpty
    @NotNull
    @Column(nullable = false)
    private String gameName;

    /** Field description */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private GameStatus status;

    /** Field description */
    @NotNull
    @ManyToOne(optional = false, targetEntity = GameTemplate.class)
    @JoinColumn
    private GameTemplate template;

    public PokerGame() {
    }

    private PokerGame(String id) {
        this.id = id;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return
     */
    public static final EntityManager entityManager() {
        EntityManager em = new PokerGame().entityManager;

        if (em == null) {
            throw new IllegalStateException(
                "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }

        return em;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     *
     * @return
     */
    public static Long findAutoGeneratedGameCount(String casinoId) {
        Long result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select count(pg) from PokerGame as pg where pg.template.casino.id = :casinoId and pg.autoGenerated = true and pg.status = :status");

            q.setParameter("casinoId", casinoId);
            q.setParameter("status", GameStatus.ACTIVE);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Returns a casino entity for which the pokergame belongs
     *
     *
     * @param pokergameId pokergameId
     *
     * @return Casino
     */
    public static Casino findCasinoByPokerGame(String pokergameId) {
        Casino result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select pg.template.casino from PokerGame as pg where pg.id = :pokergameId");

            q.setParameter("pokergameId", pokergameId);
            result = (Casino) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * TODO This needs to be thought of again
     *
     *
     * @param gameTemplateId gameTemplateId
     *
     * @return
     */
    public static Long findEmptyPokerGameCountForGameTemplate(String gameTemplateId) {
        Long result = null;

        if (StringUtils.isBlank(gameTemplateId)) {
            throw new IllegalArgumentException("gameTemplateId cannot be null");
        }

        /*
         * try {
         *
         *   // Hibernate criteria does not support outer joins so have to write it in HQL instead
         *   Query q = entityManager().createQuery("select count(distinct pg.id) as result from PokerGame as pg "
         *                 + "left outer join pg.gamblers as g " + "inner join pg.template as t "
         *                 + "where (pg.autoGenerated = true " + "and t.id = :templateId " + "and g = null "
         *                 + "and pg.status = :status)");
         *
         *   q.setParameter("templateId", gameTemplateId);
         *   q.setParameter("status", GameStatusCd.ACTIVE);
         *   result = (Long) q.getSingleResult();
         * } catch (EmptyResultDataAccessException e) {}
         */
        return result;
    }

    /**
     * TODO This needs to be thought of again
     *
     *
     * @param gameTemplateId gameTemplateId
     * @param gamesToDelete gamesToDelete
     *
     * @return
     */
    public static List<PokerGame> findLatestEmptyPokerGamesByGameTemplate(String gameTemplateId, Integer gamesToDelete) {
        List<PokerGame> result = null;

        if (StringUtils.isBlank(gameTemplateId)) {
            throw new IllegalArgumentException("gameTemplateId cannot be null");
        }
        if (gamesToDelete == null) {
            throw new IllegalArgumentException("gamesToDelete cannot be null");
        }

        /*
         * try {
         *
         *   // Hibernate criteria does not support outer joins so have to write it in HQL instead
         *   Query q = entityManager().createQuery("select pg from PokerGame as pg "
         *                 + "left outer join pg.gamblers as g " + "inner join pg.template as t "
         *                 + "where (pg.autoGenerated = true " + "and t.id = :templateId " + "and g = null "
         *                 + "and pg.status = :status) " + "order by pg.lastUpdate asc");
         *
         *   q.setParameter("templateId", gameTemplateId);
         *   q.setParameter("status", GameStatusCd.ACTIVE);
         *   q.setMaxResults(gamesToDelete);
         *   q.setFirstResult(0);
         *   result = q.getResultList();
         * } catch (EmptyResultDataAccessException e) {}
         */
        return result;
    }

    /**
     * Method description
     *
     *
     * @param pokergameId pokergameId
     *
     * @return
     */
    public static BigDecimal findMinimumRaiseAmount(String pokergameId) {
        BigDecimal result = null;

        if (StringUtils.isBlank(pokergameId)) {
            throw new IllegalArgumentException("pokergameId cannot be null");
        }

        try {
            Query q = entityManager().createQuery(
                          "select pg.template.stake.high from PokerGame pg where pg.id = :pokergameId");

            q.setParameter("pokergameId", pokergameId);
            result = (BigDecimal) q.getSingleResult();
            result = result.multiply(new BigDecimal(2));
        } catch (EmptyResultDataAccessException e) {}

        return (result == null)
               ? new BigDecimal(0)
               : result;
    }

    /**
     * Method description
     *
     *
     * @param id id
     *
     * @return
     */
    public static PokerGame findPokerGame(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of PokerGame");
        }

        return entityManager().find(PokerGame.class, id);
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param name name
     *
     * @return
     */
    public static Long findPokerGameCount(String casinoId, String name) {
        Long result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        try {
            CriteriaBuilder     builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<PokerGame>   root     = criteria.from(PokerGame.class);

            criteria.select(builder.count(root));

            Predicate equalStatus = builder.equal(root.get(PokerGame_.status), GameStatus.ACTIVE);
            Predicate equalCasinoId = builder.equal(root.get(PokerGame_.id), casinoId);
            Predicate likeName = null;

            if (StringUtils.isNotBlank(name)) {
                likeName = builder.like(builder.lower(root.get(PokerGame_.gameName)), name.toLowerCase() + "%");
            }

            if (likeName != null) {
                criteria.where(equalCasinoId, equalStatus, likeName);
            } else {
                criteria.where(equalCasinoId, equalStatus);
            }

            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param type type
     * @param stakeId stakeId
     *
     * @return
     */
    public static Long findPokerGameCount(String casinoId, GameType type, String stakeId) {
        Long result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (StringUtils.isBlank(stakeId)) {
            throw new IllegalArgumentException("stakeId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select count(pg) from PokerGame as pg where pg.template.casino.id = :casinoId and pg.template.stake.id = :stakeId and pg.template.type = :type and pg.status = :status order by pg.autoGenerated asc");

            q.setParameter("casinoId", casinoId);
            q.setParameter("stakeId", stakeId);
            q.setParameter("type", type);
            q.setParameter("status", GameStatus.ACTIVE);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public static List<Object[]> findPokerGameCount() {
        List<Object[]> result = null;

        try {
            Query q =
                entityManager().createQuery(
                    "select c.id as casinoId, c.name as casinoName, count(p.id) as gameCount from PokerGame as p "
                    + "join p.template.casino as c " + "where p.status = :status " + "group by c.id,c.name");

            q.setParameter("status", GameStatus.ACTIVE);
            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param gameTemplateId gameTemplateId
     *
     * @return
     */
    public static Long findPokerGameCountForGameTemplate(String gameTemplateId) {
        Long result = null;

        if (StringUtils.isBlank(gameTemplateId)) {
            throw new IllegalArgumentException("gameTemplateId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select count(pg) from PokerGame as pg where pg.template.id = :templateId and pg.status = :status");

            q.setParameter("templateId", gameTemplateId);
            q.setParameter("status", GameStatus.ACTIVE);
            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param index index
     * @param maxResults maxResults
     *
     * @return
     */
    public static List<PokerGame> findPokerGames(String casinoId, Integer index, Integer maxResults) {
        List<PokerGame> result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select pg from PokerGame as pg where pg.template.casino.id = :casinoId and pg.status = :status order by pg.autoGenerated asc");

            q.setParameter("casinoId", casinoId);
            q.setParameter("status", GameStatus.ACTIVE);

            if ((index != null) && (maxResults != null)) {
                q.setFirstResult(index * maxResults);
                q.setMaxResults(maxResults);
            }

            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param types types
     * @param index index
     * @param maxResults maxResults
     *
     * @return
     */
    public static List<PokerGame> findPokerGames(String casinoId, List<GameType> types, Integer index,
            Integer maxResults) {
        List<PokerGame> result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        try {
            CriteriaBuilder     builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<PokerGame> criteria = builder.createQuery(PokerGame.class);
            Root<PokerGame>   root     = criteria.from(PokerGame.class);

            criteria.select(root);

            Join<PokerGame, GameTemplate> templateJoin = root.join(PokerGame_.template);
            Join<GameTemplate, Casino> casinoJoin = templateJoin.join(GameTemplate_.casino);

            Predicate equalCasino = builder.equal(casinoJoin.get(Casino_.id), casinoId);
            Predicate equalStatus = builder.equal(root.get(PokerGame_.status), GameStatus.ACTIVE);
            Predicate inTypes = null;

            if (types != null) {
                inTypes = templateJoin.get(GameTemplate_.type).in(types);
            }

            if (inTypes != null) {
                criteria.where(equalCasino, inTypes);
            } else {
                criteria.where(equalCasino);
            }

            criteria.orderBy(builder.asc(root.get(PokerGame_.autoGenerated)));

            TypedQuery<PokerGame> query = entityManager().createQuery(criteria);

            if ((index != null) && (maxResults != null)) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }

            result = query.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
     *
     * @param casinoId casinoId
     * @param type type
     * @param stakeId stakeId
     * @param index index
     * @param maxResults maxResults
     *
     * @return
     */
    public static List<PokerGame> findPokerGames(String casinoId, GameType type, String stakeId, Integer index,
            Integer maxResults) {
        List<PokerGame> result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (StringUtils.isBlank(stakeId)) {
            throw new IllegalArgumentException("stakeId cannot be null");
        }

        try {
            Query q =
                entityManager().createQuery(
                    "select pg from PokerGame as pg where pg.template.casino.id = :casinoId and pg.template.stake.id = :stakeId and pg.template.type = :type and pg.status = :status order by pg.autoGenerated asc");

            q.setParameter("casinoId", casinoId);
            q.setParameter("stakeId", stakeId);
            q.setParameter("type", type);
            q.setParameter("status", GameStatus.ACTIVE);

            if ((index != null) && (maxResults != null)) {
                q.setFirstResult(index * maxResults);
                q.setMaxResults(maxResults);
            }

            result = q.getResultList();
        } catch (EmptyResultDataAccessException e) {}

        return result;
    }

    /**
     * Method description
     *
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
     *
     * @return
     */
    public Boolean getAutoGenerated() {
        return autoGenerated;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public GameTemplate getTemplate() {
        return template;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     */
    @Transactional
    public void merge() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        PokerGame merged = this.entityManager.merge(this);

        this.entityManager.flush();
        this.id = merged.getId();
    }

    /**
     * Method description
     *
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
     *
     */
    @Transactional
    public void remove() {
        if (this.entityManager == null) {
            this.entityManager = entityManager();
        }

        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            PokerGame attached = this.entityManager.find(PokerGame.class, this.id);

            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new PokerGame(id).remove();
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param autoGenerated autoGenerated
     */
    public void setAutoGenerated(Boolean autoGenerated) {
        this.autoGenerated = autoGenerated;
    }

    /**
     * Method description
     *
     *
     * @param gameName gameName
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Method description
     *
     *
     * @param status status
     */
    public void setStatus(GameStatus status) {
        this.status = status;
    }

    /**
     * Method description
     *
     *
     * @param template template
     */
    public void setTemplate(GameTemplate template) {
        this.template = template;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("PokerGame [");
        builder.append(super.toString());

        if (autoGenerated != null) {
            builder.append("autoGenerated=");
            builder.append(autoGenerated);
            builder.append(", ");
        }

        if (gameName != null) {
            builder.append("name=");
            builder.append(gameName);
            builder.append(", ");
        }

        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }

        if (template != null) {
            builder.append("template=");
            builder.append(template.getId());
        }

        builder.append("]");

        return builder.toString();
    }
}
