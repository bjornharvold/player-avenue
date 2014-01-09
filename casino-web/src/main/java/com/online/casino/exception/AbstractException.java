/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by Bjorn Harvold
 * Date: 1/2/11
 * Time: 2:57 PM
 * Responsibility:
 */
public class AbstractException extends Exception {
    private static final long serialVersionUID = 2471133467485384146L;
    private String[] params;

    public AbstractException() {
        super();
    }

    public AbstractException(String msg) {
        super(msg);
    }

    public AbstractException(String msg, Throwable t) {
        super(msg, t);
    }

    public AbstractException(String msg, Throwable t, String... params) {
        super(msg, t);
        this.params = params;

    }

    public AbstractException(String key, String... params) {
        super(key);
        this.params = params;
    }

    public String[] getParams() {
        return params;
    }

	public String getTranslatedMessage() {
		return messageSource.getMessage(super.getMessage(), params, "Cannot find i18n exception string", Locale.ENGLISH);
	}
    
 // Spring IoC
	@Autowired
	private MessageSource messageSource;
}
