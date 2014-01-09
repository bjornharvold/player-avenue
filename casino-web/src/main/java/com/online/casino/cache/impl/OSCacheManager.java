/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.cache.impl;

import com.online.casino.cache.CacheException;
import com.online.casino.cache.CacheManager;
import com.online.casino.cache.CachedObjectWrapper;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: bjorn
 * Date: Jun 19, 2008
 * Time: 2:35:00 PM
 * Implmentation of caching using oscache (http://opensymphony.com/oscache)
 */
public class OSCacheManager implements CacheManager {
    private final static Log log = LogFactory.getLog(OSCacheManager.class);

    public void init() {
        log.info("OSCacheManager is enabled: " + enabled);
    }

    /**
     * Add object to cache specified by the given key
     *
     * @param key
     * @param value
     * @throws CacheException
     */
    public void putInCache(String[] groups, String key, Object value) throws CacheException {
        if (enabled) {
            if (StringUtils.isBlank(key)) {
                throw new CacheException("key cannot be null");
            }
            if (value == null) {
                throw new CacheException("value cannot be null");
            }

            try {
                key = createKey(key);
                if (log.isTraceEnabled()) {
                    log.trace("Adding object to cache with key: " + key + ", object is of type: " + value.getClass());
                }
                cacheAdministrator.putInCache(key, new CachedObjectWrapper(value), groups);
            } catch (Exception e) {
                cacheAdministrator.cancelUpdate(key);
                throw new CacheException("Could not update cache: " + e.getMessage(), e);
            }
        } else {
            log.info("Caching is disabled");
        }
    }


    public void putInCache(String key, Object value) throws CacheException {
        if (enabled) {
            if (StringUtils.isBlank(key)) {
                throw new CacheException("key cannot be null");
            }
            if (value == null) {
                throw new CacheException("value cannot be null");
            }

            try {
                key = createKey(key);
                if (log.isTraceEnabled()) {
                    log.trace("Adding object to cache with key: " + key + ", object is of type: " + value.getClass());
                }
                cacheAdministrator.putInCache(key, new CachedObjectWrapper(value));
            } catch (Exception e) {
                cacheAdministrator.cancelUpdate(key);
                throw new CacheException("Could not update cache: " + e.getMessage(), e);
            }
        } else {
            log.info("Caching is disabled");
        }
    }

    /**
     * Get object from cache specified by a given key
     *
     * @param key
     * @return
     * @throws CacheException
     */
    public Object getFromCache(String key) throws CacheException {
        Object result = null;
        boolean updated = false;

        if (enabled) {
            if (StringUtils.isBlank(key)) {
                throw new CacheException("key cannot be null");
            }

            try {
                key = createKey(key);
                CachedObjectWrapper cow = (CachedObjectWrapper) cacheAdministrator.getFromCache(key, cacheRefreshInSeconds);
                if (cow != null && !cow.getObject().equals(key)) {
                    result = cow.getObject();
                    updated = true;
                }

                if (log.isTraceEnabled()) {
                    if (result != null) {
                        log.trace("Object with key: " + key + " already exists in cache. Returning previously fetched item: " + result);
                    } else {
                        log.trace("Object with key: " + key + " does not exist in cache.");
                    }
                }
            } catch (NeedsRefreshException e) {
                // happens when cache is empty
            } finally {
                if (!updated) {
                    if (log.isTraceEnabled()) {
                        log.trace("There is no object in cache for key: " + key + ". Releasing handle.");
                    }
                    // essential that this is called when no object exists in cache
                    cacheAdministrator.cancelUpdate(key);
                }
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Caching is disabled");
            }
        }

        return result;
    }

    /**
     * removes all unwanted characters from the incoming key to be cached
     *
     * @param key
     * @return
     */
    public String createKey(String key) {
        key = key.replace(" ", "_");
        key = key.replace("(", "_");
        key = key.replace(")", "_");
        key = key.replace("+", "_");
        key = key.replace("!", "_");
        key = key.replace("#", "_");
        key = key.replace("$", "_");
        key = key.replace("@", "_");
        key = key.replace("&", "_");
        key = key.replace("*", "_");
        key = key.replace("?", "_");
        key = key.replace(";", "_");

        return key;
    }

    /**
     * Kills object in cache
     *
     * @param key
     * @throws CacheException
     */
    public void invalidateObject(String key) throws CacheException {
        if (enabled) {
            if (StringUtils.isBlank(key)) {
                throw new CacheException("key cannot be null");
            }

            if (log.isTraceEnabled()) {
                log.trace("Removing object with key: " + key);
            }

            cacheAdministrator.removeEntry(key);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Caching is disabled");
            }
        }
    }

    /**
     * Empties the cache
     *
     * @throws CacheException
     */
    public void flush(String[] groups) throws CacheException {

        for (String group : groups) {
            if (log.isTraceEnabled()) {
                log.trace("Flushing caches for group: " + group);
            }

            cacheAdministrator.flushGroup(group);
        }


    }


    public void flush() throws CacheException {
        cacheAdministrator.flushAll();
    }

    // Spring IoC
    @Autowired
    private GeneralCacheAdministrator cacheAdministrator;

    private Integer cacheRefreshInSeconds;
    private Boolean enabled;

    public void setCacheRefreshInSeconds(Integer cacheRefreshInSeconds) {
        this.cacheRefreshInSeconds = cacheRefreshInSeconds;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
