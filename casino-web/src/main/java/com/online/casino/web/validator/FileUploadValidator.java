/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 * User: Bjorn Harvold
 * Date: Oct 27, 2008
 * Time: 5:04:31 PM
 * Description:
 */
public class FileUploadValidator implements Validator {
    private String[] allowedFileExtensions;

    public FileUploadValidator(String[] allowedFileExtensions) {
        this.allowedFileExtensions = allowedFileExtensions;
    }

    public boolean supports(Class clazz) {
        return MultipartFile.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        MultipartFile mf = (MultipartFile) target;
        boolean match = false;

        if (!mf.isEmpty()) {

            if (allowedFileExtensions != null) {
                for (String extension : allowedFileExtensions) {
                    if (StringUtils.endsWithIgnoreCase(mf.getOriginalFilename(), extension)) {
                        match = true;
                    }
                }
            }

            if (!match) {
                StringBuilder sb = new StringBuilder();
                for (String extension : allowedFileExtensions) {
                    sb.append(extension);
                    sb.append(", ");
                }

                sb.delete(sb.length() - 2, sb.length() - 1);
                errors.rejectValue("name", "error.form.file.extension", new String[]{sb.toString()}, "Required " + sb.toString() + " file");
            }

        }
    }
}
