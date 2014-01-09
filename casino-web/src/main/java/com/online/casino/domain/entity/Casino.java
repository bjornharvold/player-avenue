package com.online.casino.domain.entity;

import com.online.casino.domain.enums.CasinoStatus;
import com.online.casino.domain.enums.Currency;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity

@Configurable
@SuppressWarnings("unchecked")
public class Casino extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = -5691925497406806638L;
	
	private final static String defaultCasinoName = "Las Vegas";

	@NotNull
	@Column(nullable = false)
	private String name; 

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CasinoStatus status;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency;

	// how many games should be created from a specific template when new games are needed
	@NotNull
	@Column(nullable = false)
	private Integer gameBuffer;

	// how many empty games there should be before new ones need to be created
	@NotNull
	@Column(nullable = false)
	private Integer emptyGameMinimumLimit;

	// how many empty games are allowed before the system should delete some
	@NotNull
	@Column(nullable = false)
	private Integer emptyGameMaximumLimit;

	// how many games should be deleted when the maximum level of empty games has been reached
	@NotNull
	@Column(nullable = false)
	private Integer deleteGamesOnCheck;

    public Casino() {
    }

    private Casino(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CasinoStatus getStatus() {
        return status;
    }

    public void setStatus(CasinoStatus status) {
        this.status = status;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Integer getGameBuffer() {
        return gameBuffer;
    }

    public void setGameBuffer(Integer gameBuffer) {
        this.gameBuffer = gameBuffer;
    }

    public Integer getEmptyGameMinimumLimit() {
        return emptyGameMinimumLimit;
    }

    public void setEmptyGameMinimumLimit(Integer emptyGameMinimumLimit) {
        this.emptyGameMinimumLimit = emptyGameMinimumLimit;
    }

    public Integer getEmptyGameMaximumLimit() {
        return emptyGameMaximumLimit;
    }

    public void setEmptyGameMaximumLimit(Integer emptyGameMaximumLimit) {
        this.emptyGameMaximumLimit = emptyGameMaximumLimit;
    }

    public Integer getDeleteGamesOnCheck() {
        return deleteGamesOnCheck;
    }

    public void setDeleteGamesOnCheck(Integer deleteGamesOnCheck) {
        this.deleteGamesOnCheck = deleteGamesOnCheck;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Casino [");
		builder.append(super.toString());
		if (currency != null) {
			builder.append("currency=");
			builder.append(currency);
			builder.append(", ");
		}
		if (deleteGamesOnCheck != null) {
			builder.append("deleteGamesOnCheck=");
			builder.append(deleteGamesOnCheck);
			builder.append(", ");
		}
		if (emptyGameMaximumLimit != null) {
			builder.append("emptyGameMaximumLimit=");
			builder.append(emptyGameMaximumLimit);
			builder.append(", ");
		}
		if (emptyGameMinimumLimit != null) {
			builder.append("emptyGameMinimumLimit=");
			builder.append(emptyGameMinimumLimit);
			builder.append(", ");
		}
		if (gameBuffer != null) {
			builder.append("gameBuffer=");
			builder.append(gameBuffer);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (status != null) {
			builder.append("status=");
			builder.append(status);
		}
		builder.append("]");
		return builder.toString();
	}

	public static Casino findDefaultCasino() {
		return findCasinoByName(defaultCasinoName);
	}

	public static Casino findCasinoByName(String name) {
		Casino result = null;

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name cannot be null");
        }

		try {
			Query q = entityManager().createQuery("select c from Casino as c where c.name = :name");
			q.setParameter("name", name);

			result = (Casino) q.getSingleResult();
		} catch (EmptyResultDataAccessException e) {}

		return result;
	}

	public static List<Casino> findCasinos(String name, Integer index, Integer maxResults) {
		List<Casino> result = null;

        try {
            CriteriaBuilder     builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<Casino> criteria = builder.createQuery(Casino.class);
            Root<Casino>   root     = criteria.from(Casino.class);

            criteria.select(root);

            if (StringUtils.isNotBlank(name)) {
                criteria.where(builder.like(builder.lower(root.get(Casino_.name)), name.toLowerCase() + "%"));
            }

            criteria.orderBy(builder.asc(root.get(Casino_.name)));

            TypedQuery<Casino> query = entityManager().createQuery(criteria);
            if (index != null && maxResults != null) {
				query.setFirstResult(index * maxResults);
				query.setMaxResults(maxResults);
			}
            result = query.getResultList();
        } catch (EmptyResultDataAccessException e) {}

		return result;
	}

	public static Long findCasinoCount(String name) {
		Long result = null;

        try {
            CriteriaBuilder     builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Casino>   root     = criteria.from(Casino.class);

            criteria.select(builder.count(root));

            if (StringUtils.isNotBlank(name)) {
                criteria.where(builder.like(builder.lower(root.get(Casino_.name)), name.toLowerCase() + "%"));
            }

            result = entityManager().createQuery(criteria).getSingleResult();
        } catch (EmptyResultDataAccessException e) {}

		return result;
	}

	public static List<Casino> findCasinoByCurrencies(List<Currency> currencies) {
		List<Casino> result = null;

        if (currencies == null) {
            throw new IllegalArgumentException("currencies cannot be null");
        }

        try {
            CriteriaBuilder     builder  = entityManager().getCriteriaBuilder();
            CriteriaQuery<Casino> criteria = builder.createQuery(Casino.class);
            Root<Casino>   root     = criteria.from(Casino.class);

            criteria.select(root);

            criteria.where(root.get(Casino_.currency).in(currencies));

            criteria.orderBy(builder.asc(root.get(Casino_.name)));

            TypedQuery<Casino> query = entityManager().createQuery(criteria);

            result = query.getResultList();
        } catch (EmptyResultDataAccessException e) {}

		return result;
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
            Casino attached = this.entityManager.find(Casino.class, this.id);
            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new Casino(id).remove();
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Casino merged = this.entityManager.merge(this);
        this.entityManager.flush();
        this.id = merged.getId();
    }

    public static final EntityManager entityManager() {
        EntityManager em = new Casino().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static Casino findCasino(String id) {
        if (id == null) {
            throw new IllegalArgumentException("An identifier is required to retrieve an instance of Casino");
        }
        return entityManager().find(Casino.class, id);
    }

    public static List<Casino> findAllCasinos() {    
        return entityManager().createQuery("select o from Casino o").getResultList();
    }
}
