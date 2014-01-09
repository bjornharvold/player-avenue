/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap;

import javax.persistence.EntityManager;

/**
 * Created by Bjorn Harvold
 * Date: 3/22/11
 * Time: 8:14 PM
 * Responsibility:
 */
public class EntityManagerUtil {
    public static final ThreadLocal<EntityManager> ENTITY_MANAGER = new ThreadLocal<EntityManager>();

    /**
     * Returns a fresh EntityManager
     */
    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER.get();
    }
}
