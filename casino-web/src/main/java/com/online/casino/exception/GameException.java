/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.exception;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSource;

@Configurable
public class GameException extends AbstractException {
	private static final long serialVersionUID = -3202910061311582616L;

    public GameException() {
        super();
    }

    public GameException(String msg) {
        super(msg);
    }

    public GameException(String msg, Throwable t) {
        super(msg, t);
    }

    public GameException(String msg, Throwable t, String... params) {
        super(msg, t, params);
    }

    public GameException(String key, String... params) {
        super(key, params);
    }
}