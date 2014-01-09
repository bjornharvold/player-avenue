/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.cache;


/**
 * User: Bjorn Harvold
 * Date: Nov 13, 2009
 * Time: 9:40:04 PM
 * Responsibility:
 */
public interface CacheManager {
    void putInCache(String[] groups, String key, Object value) throws CacheException;

    void putInCache(String key, Object value) throws CacheException;

    Object getFromCache(String key) throws CacheException;

    void flush(String[] groups) throws CacheException;

    void flush() throws CacheException;

    void invalidateObject(String key) throws CacheException;
}
