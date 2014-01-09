/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino;

import com.online.casino.exception.AbstractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Configurable
public class UserException extends AbstractException {
	private static final long serialVersionUID = -3202910061311582616L;

    public UserException() {
        super();
    }

    public UserException(String msg) {
        super(msg);
    }

    public UserException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserException(String msg, Throwable t, String... params) {
        super(msg, t, params);

    }

    public UserException(String key, String... params) {
        super(key, params);
    }
}