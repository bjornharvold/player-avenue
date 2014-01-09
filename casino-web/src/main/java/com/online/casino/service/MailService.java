/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service;

import com.online.casino.service.email.MailServiceException;

import java.util.Locale;
import java.util.Map;

/**
 * User: Bjorn Harvold
 * Date: Apr 24, 2007
 * Time: 11:50:44 AM
 */
public interface MailService {
    void sendActivationEmail(String email, String activationId, Locale l) throws MailServiceException;

    void sendPlainEmail(String template, Map<String, Object> params) throws MailServiceException;

    void sendMIMEEmail(String template, Map<String, Object> params, Map<String, String> imageAssets,
                       Map<String, String> attachments, Map<String, String> headers) throws MailServiceException;

    Boolean isAvailable();

    public void sendPasswordReminderEmail(String email, String newPassword, Locale l) throws MailServiceException;
}
