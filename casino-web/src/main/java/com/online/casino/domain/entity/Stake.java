package com.online.casino.domain.entity;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
import java.math.BigDecimal;
import java.util.List;

@Entity

@Configurable
@SuppressWarnings("unchecked")
public class Stake extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = -4620058355204262150L;

    @ManyToOne(optional = false, targetEntity = Casino.class)
    @JoinColumn
    private Casino casino;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal high;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal low;

    public Stake() {
    }

    private Stake(String id) {
        this.id = id;
    }

    public Casino getCasino() {
        return casino;
    }

    public void setCasino(Casino casino) {
        this.casino = casino;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Stake [");
        builder.append(super.toString());
        if (casino != null) {
            builder.append("casino=");
            builder.append(casino.getId());
            builder.append(", ");
        }
        if (high != null) {
            builder.append("high=");
            builder.append(high);
            builder.append(", ");
        }
        if (low != null) {
            builder.append("low=");
            builder.append(low);
        }
        builder.append("]");
        return builder.toString();
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(high);
        sb.append(" / ");
        sb.append(low);

        return sb.toString();
    }

    public static Stake findStakeByHighLow(String casinoId, BigDecimal high, BigDecimal low) {
        Stake result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }
        if (high == null) {
            throw new IllegalArgumentException("high cannot be null");
        }
        if (low == null) {
            throw new IllegalArgumentException("low cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select s from Stake as s where s.casino.id = :casinoId and s.high = :high and s.low = :low");
            q.setParameter("casinoId", casinoId);
            q.setParameter("high", high);
            q.setParameter("low", low);

            result = (Stake) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    public static Long findStakeCount(String casinoId) {
        Long result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        try {
            Query q = entityManager().createQuery("select count(s) from Stake as s where s.casino.id = :casinoId");
            q.setParameter("casinoId", casinoId);

            result = (Long) q.getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    public static Long findStakeCount(String casinoId, BigDecimal hilo) {
        Long result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Stake> root = criteria.from(Stake.class);

            criteria.select(builder.count(root));

            Join<Stake, Casino> casinoJoin = root.join(Stake_.casino);
            Predicate equalCasinoId = builder.equal(casinoJoin.get(Casino_.id), casinoId);
            Predicate equalHighOrLow = null;

            if (hilo != null) {
                equalHighOrLow = builder.or(
                        builder.equal(root.get(Stake_.high), hilo),
                        builder.equal(root.get(Stake_.low), hilo)
                );
            }

            if (equalHighOrLow != null) {
                criteria.where(equalCasinoId, equalHighOrLow);
            } else {
                criteria.where(equalCasinoId);
            }

            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    public static List<Stake> findStakes(String casinoId, BigDecimal hilo, Integer index, Integer maxResults) {
        List<Stake> result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();
            CriteriaQuery<Stake> criteria = builder.createQuery(Stake.class);
            Root<Stake> root = criteria.from(Stake.class);

            criteria.select(root);

            Join<Stake, Casino> casinoJoin = root.join(Stake_.casino);
            Predicate equalCasinoId = builder.equal(casinoJoin.get(Casino_.id), casinoId);
            Predicate equalHighOrLow = null;

            if (hilo != null) {
                equalHighOrLow = builder.or(
                        builder.equal(root.get(Stake_.high), hilo),
                        builder.equal(root.get(Stake_.low), hilo)
                );
            }

            if (equalHighOrLow != null) {
                criteria.where(equalCasinoId, equalHighOrLow);
            } else {
                criteria.where(equalCasinoId);
            }

            criteria.orderBy(builder.desc(root.get(Stake_.high)));

            TypedQuery<Stake> query = entityManager().createQuery(criteria);
            if (index != null && maxResults != null) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }

            result = query.getResultList();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    public static List<Stake> findStakes(String casinoId, Integer index, Integer maxResults) {
        List<Stake> result = null;

        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        try {
            CriteriaBuilder builder = entityManager().getCriteriaBuilder();
            CriteriaQuery<Stake> criteria = builder.createQuery(Stake.class);
            Root<Stake> root = criteria.from(Stake.class);

            criteria.select(root);

            Join<Stake, Casino> casinoJoin = root.join(Stake_.casino);
            Predicate equalCasinoId = builder.equal(casinoJoin.get(Casino_.id), casinoId);
            criteria.where(equalCasinoId);
            criteria.orderBy(builder.desc(root.get(Stake_.high)));

            TypedQuery<Stake> query = entityManager().createQuery(criteria);
            if (index != null && maxResults != null) {
                query.setFirstResult(index * maxResults);
                query.setMaxResults(maxResults);
            }

            result = query.getResultList();
        } catch (EmptyResultDataAccessException e) {
        }

        return result;
    }

    public static void deleteStakesForCasino(String casinoId) {
        if (StringUtils.isBlank(casinoId)) {
            throw new IllegalArgumentException("casinoId cannot be null");
        }

        Query q = entityManager().createQuery("delete from Stake as s where s.casino.id = :id");
        q.setParameter("id", casinoId);

        q.executeUpdate();
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Stake attached = this.entityManager.find(Stake.class, this.id);
            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new Stake(id).remove();
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Stake merged = this.entityManager.merge(this);
        this.entityManager.flush();
        this.id = merged.getId();
    }

    public static final EntityManager entityManager() {
        EntityManager em = new Stake().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static Stake findStake(String id) {
        if (id == null) throw new IllegalArgumentException("An identifier is required to retrieve an instance of Stake");
        return entityManager().find(Stake.class, id);
    }
}
