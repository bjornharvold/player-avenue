/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 * User: bjorn
 * Date: Sep 23, 2008
 * Time: 5:56:18 PM
 */
public class ImageFileUploadValidator extends FileUploadValidator implements Validator {
    private final static String JPEG = "jpg";
    private final static String PNG = "png";
    private final static String GIF = "gif";

    public ImageFileUploadValidator() {
        super(new String[]{JPEG, PNG, GIF});
    }

    public boolean supports(Class clazz) {
        return MultipartFile.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
    }
}