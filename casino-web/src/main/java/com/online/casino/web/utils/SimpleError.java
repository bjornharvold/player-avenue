/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold. 
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.utils;

/**
 * User: Bjorn Harvold
 * Date: Oct 13, 2010
 * Time: 3:19:42 PM
 * Responsibility:
 */
public class SimpleError {
    private String message;
    private String[] params;

    public SimpleError(String message, String ... params) {
        this.message = message;
        this.params = params;
    }

    public String getMessage() {
        return message;
    }

    public String[] getParams() {
        return params;
    }
}
