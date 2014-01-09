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
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: bjorn Date: Sep 25, 2008 Time: 1:32:16 PM
 */
@Component
public class UserValidator implements Validator {
    private final AdministrationService administrationService;

    @Autowired
    public UserValidator(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public boolean supports(Class<?> clazz) {
		return ApplicationUser.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		ApplicationUser user = (ApplicationUser) target;

		if (!errors.hasErrors()) {
			// check for username availability
			if (user.getId() == null && administrationService.findApplicationUserByUsername(user.getUsername()) != null) {
				errors.rejectValue("username", "error.username.taken", null, "Username is already taken");
			} else if (user.getId() != null && administrationService.isApplicationUserUniqueByUsername(user.getId(), user.getUsername()) != null) {
				errors.rejectValue("username", "error.username.taken", null, "Username is already taken");
			}

			// check for email availability
			if (user.getId() == null && administrationService.findApplicationUserByEmail(user.getEmail()) != null) {
				errors.rejectValue("email", "error.email.taken", null, "E-mail is already taken");
			} else if (user.getId() != null && administrationService.isApplicationUserUniqueByEmail(user.getId(), user.getEmail()) != null) {
				errors.rejectValue("email", "error.email.taken", null, "E-mail is already taken");
			}

			if (user.getId() == null) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "error.form.field.required", null, "Required field");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "error.form.field.required", null, "Required field");

				// check for password match
				if (StringUtils.isNotBlank(user.getNewPassword()) && StringUtils.isNotBlank(user.getPasswordConfirm())) {
					if (!StringUtils.equals(user.getNewPassword(), user.getPasswordConfirm())) {
						errors.rejectValue("passwordConfirm", "error.password.mismatch", null, "Passwords are not the same");
					}
				}
			}

			if (user.isKaptchaEnabled()) {
				if (StringUtils.isBlank(user.getKaptcha())) {
					errors.rejectValue("kaptcha", "error.kaptcha.failed", null, "Captcha failed");
				} else if (!StringUtils.equals(user.getKaptcha(), user.getRequiredKaptcha())) {
					errors.rejectValue("kaptcha", "error.kaptcha.mismatch", null, "Kaptcha is wrong");
				}
			}
		}
	}
}