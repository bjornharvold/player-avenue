/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.cache;

/**
 * User: Bjorn Harvold
 * Date: Aug 25, 2009
 * Time: 8:57:43 PM
 * Responsibility:
 */
public class CacheException extends Exception {
	private static final long serialVersionUID = 2690143413893397542L;
	String[] params;

    public CacheException() {
        super();
    }

    public CacheException(String msg) {
        super(msg);
    }

    public CacheException(String msg, Throwable t) {
        super(msg, t);
    }

    public CacheException(String msg, Throwable t, String... params) {
        super(msg, t);
        this.params = params;

    }

    public CacheException(String key, String... params) {
        super(key);
        this.params = params;
    }

    public String[] getParams() {
        return params;
    }
}