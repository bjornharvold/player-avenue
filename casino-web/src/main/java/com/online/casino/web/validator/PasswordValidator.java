/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.service.AdministrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: bjorn Date: Sep 25, 2008 Time: 1:32:16 PM
 */
@Component
public class PasswordValidator implements Validator {
    private final AdministrationService administrationService;

    @Autowired
    public PasswordValidator(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public boolean supports(Class<?> clazz) {
		return ApplicationUser.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		ApplicationUser user = (ApplicationUser) target;

		if (!errors.hasErrors()) {
			if (StringUtils.isBlank(user.getNewPassword())) {
				errors.rejectValue("newPassword", "error.form.field.required", null, "Required field");
			}
			if (StringUtils.isBlank(user.getCurrentPassword())) {
				errors.rejectValue("currentPassword", "error.form.field.required", null, "Required field");
			}
			if (StringUtils.isBlank(user.getPasswordConfirm())) {
				errors.rejectValue("passwordConfirm", "error.form.field.required", null, "Required field");
			}
			// check for existing password match
			if (StringUtils.isNotBlank(user.getCurrentPassword()) && !administrationService.isPasswordMatch(user.getPassword(), user.getCurrentPassword())) {
				errors.rejectValue("currentPassword", "error.password.current.match", null, "Current password is not correct");
			}

			// check for password match
			if (StringUtils.isNotBlank(user.getNewPassword()) && StringUtils.isNotBlank(user.getPasswordConfirm())) {
				if (!StringUtils.equals(user.getNewPassword(), user.getPasswordConfirm())) {
					errors.rejectValue("passwordConfirm", "error.password.mismatch", null, "Passwords are not the same");
				}
			}

			// check that new password doesn't match old password
			if (StringUtils.isNotBlank(user.getCurrentPassword()) && StringUtils.isNotBlank(user.getNewPassword()) && StringUtils.isNotBlank(user.getPasswordConfirm())) {
				if (StringUtils.equals(user.getNewPassword(), user.getPasswordConfirm()) && StringUtils.equals(user.getNewPassword(), user.getCurrentPassword())) {
					errors.rejectValue("currentPassword", "error.password.current.matches.new", null, "New password is the same as the old one");
				}
			}
		}
	}
}