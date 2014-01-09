/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.impl;

import com.online.casino.service.MailService;
import com.online.casino.service.email.MailServiceException;
import com.online.casino.service.email.MyMimeMessagePreparator;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: Bjorn Harvold Date: Apr 23, 2007 Time: 5:04:33 PM
 */
@Service("mailService")
public class MailServiceImpl implements MailService {
    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    private final static String ACTIVATION_EMAIL = "activateUserEmail";
    private final static String RESET_PASSWORD_EMAIL = "resetPasswordEmail";
    private final JavaMailSender mailSender;
    private final SimpleMailMessage mailMessage;
    private final VelocityEngine velocityEngine;
    private final MyMimeMessagePreparator mimeMessagePreparator;
    private final MessageSource messageSource;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, SimpleMailMessage mailMessage,
                           VelocityEngine velocityEngine, MyMimeMessagePreparator mimeMessagePreparator,
                           MessageSource messageSource) {
        this.mailSender = mailSender;
        this.mailMessage = mailMessage;
        this.velocityEngine = velocityEngine;
        this.mimeMessagePreparator = mimeMessagePreparator;
        this.messageSource = messageSource;
    }

    @Override
    public void sendActivationEmail(String email, String activationId, Locale l) throws MailServiceException {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("to", email);
        map.put("activationId", activationId);
        map.put("subject", messageSource.getMessage("email.user.activation.subject", null, l));

        String host = messageSource.getMessage("site.host", null, l);
        String port = messageSource.getMessage("site.port", null, l);

        if (!StringUtils.equals("80", port)) {
            host = host + ":" + port;
        }

        map.put("host", host);

        sendMIMEEmail(ACTIVATION_EMAIL + "_" + l.getLanguage() + ".xml", map, null, null, null);
    }

    @Override
    public void sendPasswordReminderEmail(String email, String newPassword, Locale l) throws MailServiceException {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("to", email);
        map.put("password", newPassword);
        map.put("subject", messageSource.getMessage("email.user.password.reset.subject", null, l));

        sendPlainEmail(RESET_PASSWORD_EMAIL + "_" + l.getLanguage() + ".xml", map);
    }

    /**
     * Creates a MailMessage for you that you can then send Required keys in the
     * map are: key: to key: subject (is a message bundle key that will be
     * translated)
     *
     * @param template
     * @param params
     * @throws MailServiceException
     */
    public void sendPlainEmail(String template, Map<String, Object> params)
            throws MailServiceException {

        validate(template, params);

        // retrieve template
        try {
            prepareMailMessage(template, params);

            mailSender.send(mailMessage);

        } catch (VelocityException ex) {
            log.error("Problem retrieving template for email: " + ex.getMessage(), ex);
            throw new MailServiceException("error.velocity: " + ex.getMessage(), ex);
        }
    }

    /**
     * setup the mail message in preparation for sending
     *
     * @param template
     * @param params
     */
    private void prepareMailMessage(String template, Map<String, Object> params) {
        mailMessage.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, params));
        mailMessage.setTo((String) params.get("to"));
        mailMessage.setSubject((String) params.get("subject"));

        if (log.isDebugEnabled()) {
            log.debug("Initial email looks like this: ");
            log.debug(mailMessage.toString());
        }
    }

    /**
     * Sends a MIME email based on a velocity template and image assets with
     * (key: identifier, value: image name)
     *
     * @param template
     * @param params
     * @param imageAssets
     * @throws MailServiceException
     */
    public void sendMIMEEmail(String template, Map<String, Object> params,
                              Map<String, String> imageAssets, Map<String, String> attachments, Map<String, String> headers)
            throws MailServiceException {

        validate(template, params);

        try {
            mimeMessagePreparator.setVelocityEngine(velocityEngine);
            mimeMessagePreparator.setTemplate(template);
            mimeMessagePreparator.setParams(params);
            mimeMessagePreparator.setImageAssets(imageAssets);
            mimeMessagePreparator.setAttachments(attachments);
            mimeMessagePreparator.setHeaders(headers);

            mailSender.send(mimeMessagePreparator);
        } catch (VelocityException ex) {
            log.error("Problem retrieving template for email: " + ex.getMessage(), ex);
            throw new MailServiceException("error.notification.velocity: " + ex.getMessage(), ex);
        }
    }

    public Boolean isAvailable() {
        return true;
    }

    private void validate(String template, Map<String, Object> params)
            throws MailServiceException {
        if (StringUtils.isBlank(template)) {
            log.error("Missing template. Template cannot be null");
            throw new MailServiceException("error.missing.argument.exception",
                    "Missing template. Template cannot be null");
        }
        if (params == null) {
            log
                    .error("Missing params. Minimum fields needed are \"to\" and \"subject\"");
            throw new MailServiceException("error.missing.argument.exception",
                    "Missing params. Minimum fields needed are \"to\" and \"subject\"");
        } else if ((!params.containsKey("to") && mailMessage.getTo() == null)
                || (!params.containsKey("subject") && StringUtils
                .isBlank(mailMessage.getSubject()))) {
            log
                    .error("Missing values in map. Missing params. Minimum fields needed are \"to\" and \"subject\"");
            throw new MailServiceException(
                    "error.missing.argument.exception",
                    "Missing values in map. Missing params. Minimum fields needed are \"to\" and \"subject\"");
        }
    }

}
