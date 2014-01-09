/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.entity;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity

@Configurable
@SuppressWarnings("unchecked")
public class ApplicationUserRole extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 7896822477203593673L;

	@NotNull
	@ManyToOne(optional = false, targetEntity = ApplicationUser.class)
	@JoinColumn
	private ApplicationUser applicationUser;

	@NotNull
	@ManyToOne(optional = false, targetEntity = ApplicationRole.class)
	@JoinColumn
	private ApplicationRole applicationRole;

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    public ApplicationRole getApplicationRole() {
        return applicationRole;
    }

    public void setApplicationRole(ApplicationRole applicationRole) {
        this.applicationRole = applicationRole;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationUserRole [");
		builder.append(super.toString());
		if (applicationRole != null) {
			builder.append("applicationRole=");
			builder.append(applicationRole.getId());
			builder.append(", ");
		}
		if (applicationUser != null) {
			builder.append("applicationUser=");
			builder.append(applicationUser.getId());
		}
		builder.append("]");
		return builder.toString();
	}

	public ApplicationUserRole(){}

	private ApplicationUserRole(String id){
        this.id = id;
    }

	public ApplicationUserRole(ApplicationUser user, ApplicationRole level) {
		this.applicationUser = user;
		this.applicationRole = level;
	}

	public static List<ApplicationUserRole> findApplicationUserAccessLevels(String userId, Integer index, Integer maxResults) {
		List<ApplicationUserRole> result = null;

		try {
			Query q = entityManager().createQuery("select s from ApplicationUserRole as s where s.applicationUser.id = :userId order by s.applicationRole.name asc");
			q.setParameter("userId", userId);

			if (index != null && maxResults != null) {
				q.setFirstResult(index * maxResults);
				q.setMaxResults(maxResults);
			}

			result = q.getResultList();
		} catch (EmptyResultDataAccessException e) {}

		return result;
	}

	public static Long findApplicationUserAccessLevelCount(String userId) {
		Long result = null;

		try {
			Query q = entityManager().createQuery("select count(s) from ApplicationUserRole as s where s.applicationUser.id = :userId");
			q.setParameter("userId", userId);

			result = (Long) q.getSingleResult();
		} catch (EmptyResultDataAccessException e) {}

		return result;
	}

	public static ApplicationUserRole findApplicationUserAccessLevel(String userId, String accessLevelId) {
		ApplicationUserRole result = null;

		try {
			Query q = entityManager().createQuery("select s from ApplicationUserRole as s where s.applicationUser.id = :userId and s.applicationRole.id = :accessLevelId");
			q.setParameter("userId", userId);
			q.setParameter("accessLevelId", accessLevelId);

			result = (ApplicationUserRole) q.getSingleResult();
		} catch (EmptyResultDataAccessException e) {}

		return result;
	}

	public static List<ApplicationUserRole> findApplicationUserAccessLevelsByAccessLevel(String accessLevelId) {
		List<ApplicationUserRole> result = null;

		try {
			Query q = entityManager().createQuery("select s from ApplicationUserRole as s where s.applicationRole.id = :accessLevelId");
			q.setParameter("accessLevelId", accessLevelId);

			result = q.getResultList();
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
            ApplicationUserRole attached = this.entityManager.find(ApplicationUserRole.class, this.id);
            this.entityManager.remove(attached);
        }
    }

    public static void remove(String id) {
        new ApplicationUserRole(id).remove();
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ApplicationUserRole merged = this.entityManager.merge(this);
        this.entityManager.flush();
        this.id = merged.getId();
    }

    public static final EntityManager entityManager() {
        EntityManager em = new ApplicationUserRole().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static ApplicationUserRole findApplicationUserAccessLevel(String id) {
        if (id == null) throw new IllegalArgumentException("An identifier is required to retrieve an instance of ApplicationUserRole");
        return entityManager().find(ApplicationUserRole.class, id);
    }

    public static ApplicationUserRole newApplicationUserRole(ApplicationUser user, ApplicationRole applicationRole) {
        return new ApplicationUserRole(user, applicationRole);
    }
}
